package org.keiber.test.springboot.app.controllers;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.keiber.test.springboot.app.models.Cuenta;
import org.keiber.test.springboot.app.models.TransaccionDTO;
import org.springframework.beans.factory.annotation.Autowired;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CuentaControllerWebTestClientTest {

  private ObjectMapper objectMapper;

  @Autowired
  private WebTestClient client;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  @Order(1)
  void testTransferir() throws JsonProcessingException {

    // Given
    TransaccionDTO dto = new TransaccionDTO();
    dto.setCuentaOrigenId(1L);
    dto.setCuentaDestinoId(2L);
    dto.setMonto(new BigDecimal("100"));
    dto.setBancoId(1L);

    Map<String, Object> response = new HashMap<>();
    response.put("date", LocalDate.now().toString());
    response.put("status", "OK");
    response.put("mensaje", "Transferencia realizada con éxito");
    response.put("transacción", dto);

    // When
    client.post().uri("/api/cuentas/transferir")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(dto)
        .exchange()

        // Then
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        // si al expectBody no se le pasa el tipo de dato, devuelve byte
        .expectBody(String.class)
        .consumeWith(respuesta -> {
          try {
            String jsonStr = respuesta.getResponseBody();
            JsonNode json = objectMapper.readTree(jsonStr);
            assertEquals("Transferencia realizada con éxito", json.path("mensaje").asText());
            assertEquals(1L, json.path("transacción").path("cuentaOrigenId").asLong());
            assertEquals(LocalDate.now().toString(), json.path("date").asText());
            assertEquals("100", json.path("transacción").path("monto").asText());
          } catch (IOException e) {
            e.printStackTrace();
          }
        })// jsonPath exige que el expectBody sea de tipo byte
    /*
     * .jsonPath("$.mensaje").isNotEmpty()
     * .jsonPath("$.mensaje").value(is("Transferencia realizada con éxito"))
     * .jsonPath("$.mensaje").value(valor ->
     * assertEquals("Transferencia realizada con éxito", valor))
     * .jsonPath("$.mensaje").isEqualTo("Transferencia realizada con éxito")
     * .jsonPath("$.transacción.cuentaOrigenId").isEqualTo(dto.getCuentaOrigenId())
     * .jsonPath("$.date").isEqualTo(LocalDate.now().toString())
     * .json(objectMapper.writeValueAsString(response))
     */;
  }

  @Test
  @Order(2)
  void testDetalle() throws JsonProcessingException {
    Cuenta cuenta = new Cuenta(1L, "Andrés", new BigDecimal("900"));

    client.get().uri("/api/cuentas/1").exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.persona").isEqualTo("Andrés")
        .jsonPath("$.saldo").isEqualTo(900)
        .json(objectMapper.writeValueAsString(cuenta));
  }

  @Test
  @Order(3)
  void testDetalle2() {
    client.get().uri("/api/cuentas/2").exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(Cuenta.class)
        .consumeWith(response -> {
          Cuenta cuenta = response.getResponseBody();
          assertNotNull(cuenta);
          assertEquals("Jhon", cuenta.getPersona());
          assertEquals("2100.00", cuenta.getSaldo().toPlainString());
        });
  }

  @Test
  @Order(4)
  void testListar() {
    client.get().uri("/api/cuentas").exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.[0].persona").isEqualTo("Andrés")
        .jsonPath("$.[0].id").isEqualTo(1)
        .jsonPath("$.[0].saldo").isEqualTo(900)
        .jsonPath("$.[1].persona").isEqualTo("Jhon")
        .jsonPath("$.[1].id").isEqualTo(2)
        .jsonPath("$.[1].saldo").isEqualTo(2100)
        .jsonPath("$").isArray()
        .jsonPath("$").value(hasSize(2));
  }

  @Test
  @Order(5)
  void testListar2() {
    client.get().uri("/api/cuentas").exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Cuenta.class)
        .consumeWith(response -> {
          List<Cuenta> cuentas = response.getResponseBody();
          assertNotNull(cuentas);
          assertEquals(2, cuentas.size());
          assertEquals(1L, cuentas.get(0).getId());
          assertEquals("Andrés", cuentas.get(0).getPersona());
          assertEquals(900., cuentas.get(0).getSaldo().intValue());
          assertEquals(2L, cuentas.get(1).getId());
          assertEquals("Jhon", cuentas.get(1).getPersona());
          assertEquals("2100.00", cuentas.get(1).getSaldo().toPlainString());
        })
        .hasSize(2)
        .value(hasSize(2));
  }

  @Test
  @Order(6)
  void testGuardar() {

    // Given
    Cuenta cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));

    // When
    client.post().uri("/api/cuentas").contentType(MediaType.APPLICATION_JSON)
        .bodyValue(cuenta)
        .exchange()
        // Then
        .expectStatus().isCreated()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.id").isEqualTo(3)
        .jsonPath("$.persona").isEqualTo("Pepe")
        .jsonPath("$.persona").value(is("Pepe"))
        .jsonPath("$.saldo").isEqualTo(3000);
  }

  @Test
  @Order(7)
  void testGuardar2() {

    // Given
    Cuenta cuenta = new Cuenta(null, "Maria", new BigDecimal("4000"));

    // When
    client.post().uri("/api/cuentas").contentType(MediaType.APPLICATION_JSON)
        .bodyValue(cuenta)
        .exchange()
        // Then
        .expectStatus().isCreated()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(Cuenta.class)
        .consumeWith(response -> {
          Cuenta c = response.getResponseBody();
          assertNotNull(c);
          assertEquals(4L, c.getId());
          assertEquals("Maria", c.getPersona());
          assertEquals(4000., c.getSaldo().intValue());
        });
  }

  @Test
  @Order(8)
  void testEliminar() {
    client.get().uri("/api/cuentas").exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Cuenta.class)
        .hasSize(4);
    // When
    client.delete().uri("/api/cuentas/3").exchange()
        // Then
        .expectStatus().isNoContent()
        .expectBody().isEmpty();

    client.get().uri("/api/cuentas").exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Cuenta.class)
        .hasSize(3);

    client.get().uri("/api/cuentas/3").exchange()
        // .expectStatus().is5xxServerError()
        .expectStatus().isNotFound()
        .expectBody().isEmpty();
  }
}
