package org.keiber.test.springboot.app.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.keiber.test.springboot.app.models.Cuenta;
import org.keiber.test.springboot.app.models.TransaccionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CuentaControllerTestRestTemplateTest {

  @LocalServerPort
  private int port;

  @Autowired
  private RestTemplate client;

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    client.setErrorHandler(new DefaultResponseErrorHandler() {
      @Override
      public void handleError(@SuppressWarnings("null") ClientHttpResponse response) throws IOException {
        // No lanzar excepciones para códigos de error
      }
    });
  }

  @Test
  @Order(1)
  void testTransferir() throws JsonMappingException, JsonProcessingException {
    // Given
    TransaccionDTO dto = new TransaccionDTO();
    dto.setMonto(new BigDecimal("100"));
    dto.setCuentaOrigenId(1L);
    dto.setCuentaDestinoId(2L);
    dto.setBancoId(1L);

    ResponseEntity<String> response = client
        .postForEntity(crearUri("/api/cuentas/transferir"), dto, String.class);
    String json = response.getBody();
    System.out.println(json);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
    assertNotNull(json);
    assertTrue(json.contains("Transferencia realizada con éxito"));
    assertTrue(json.contains("{\"date\":\"2024-10-11\",\"transacción\":{\"cuentaOrigenId\":1,\"cuentaDestinoId\":2,\"monto\":100,\"bancoId\":1},\"mensaje\":\"Transferencia realizada con éxito\",\"status\":\"OK\"}"));

    JsonNode jsonNode = objectMapper.readTree(json);
    assertEquals("Transferencia realizada con éxito", jsonNode.path("mensaje").asText());
    assertEquals(LocalDate.now().toString(), jsonNode.path("date").asText());
    assertEquals("100", jsonNode.path("transacción").path("monto").asText());
    assertEquals(1L, jsonNode.path("transacción").path("cuentaOrigenId").asLong());

    Map<String, Object> response2 = new HashMap<>();
    response2.put("date", LocalDate.now().toString());
    response2.put("status", "OK");
    response2.put("mensaje", "Transferencia realizada con éxito");
    response2.put("transacción", dto);

    assertEquals(objectMapper.writeValueAsString(response2), json);
  }

  @Test
  @Order(2)
  void testDetalle() {
    ResponseEntity<Cuenta> respuesta = client.getForEntity(crearUri("/api/cuentas/1"), Cuenta.class);
    Cuenta cuenta = respuesta.getBody();
    assertEquals(HttpStatus.OK, respuesta.getStatusCode());
    assertNotNull(cuenta);
    assertEquals(MediaType.APPLICATION_JSON, respuesta.getHeaders().getContentType());
    assertEquals("Andrés", cuenta.getPersona());
    assertEquals(1L, cuenta.getId());
    assertEquals("900.00", cuenta.getSaldo().toPlainString());
    assertEquals(new Cuenta(1L, "Andrés", new BigDecimal("900.00")), cuenta);
  }

  @Test
  @Order(3)
  void testListar() throws JsonMappingException, JsonProcessingException {
    ResponseEntity<Cuenta[]> respuesta = client.getForEntity(crearUri("/api/cuentas"), Cuenta[].class);
    List<Cuenta> cuentas = Arrays.asList(respuesta.getBody());
    assertEquals(HttpStatus.OK, respuesta.getStatusCode());
    assertNotNull(cuentas);
    assertEquals(MediaType.APPLICATION_JSON, respuesta.getHeaders().getContentType());
    assertEquals(2, cuentas.size());
    assertEquals(new Cuenta(1L, "Andrés", new BigDecimal("900.00")), cuentas.get(0));
    assertEquals(new Cuenta(2L, "Jhon", new BigDecimal("2100.00")), cuentas.get(1));

    assertEquals(1L, cuentas.get(0).getId());
    assertEquals("Andrés", cuentas.get(0).getPersona());
    assertEquals("900.00", cuentas.get(0).getSaldo().toPlainString());
    assertEquals(2L, cuentas.get(1).getId());
    assertEquals("Jhon", cuentas.get(1).getPersona());
    assertEquals("2100.00", cuentas.get(1).getSaldo().toPlainString());

    JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(cuentas));
    assertEquals(2, json.size());
    assertEquals(1L, json.get(0).path("id").asLong());
    assertEquals("Andrés", json.get(0).path("persona").asText());
    assertEquals("900.0", json.get(0).path("saldo").asText());
    assertEquals(2L, json.get(1).path("id").asLong());
    assertEquals("Jhon", json.get(1).path("persona").asText());
    assertEquals("2100.0", json.get(1).path("saldo").asText());
  }

  @Test
  @Order(4)
  void testGuardar() throws JsonMappingException, JsonProcessingException {
    Cuenta cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));
    ResponseEntity<Cuenta> respuesta = client.postForEntity(crearUri("/api/cuentas"), cuenta, Cuenta.class);
    Cuenta cuentaGuardada = respuesta.getBody();
    assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
    assertNotNull(cuentaGuardada);
    assertEquals(MediaType.APPLICATION_JSON, respuesta.getHeaders().getContentType());
    assertEquals(3L, cuentaGuardada.getId());
    assertEquals("Pepe", cuentaGuardada.getPersona());
    assertEquals("3000", cuentaGuardada.getSaldo().toPlainString());

    JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(cuentaGuardada));
    assertEquals(3, json.path("id").asLong());
    assertEquals("Pepe", json.path("persona").asText());
    assertEquals("3000", json.path("saldo").asText());
  }

  @Test
  @Order(5)
  void testEliminar() throws JsonMappingException, JsonProcessingException {
    ResponseEntity<Cuenta[]> respuesta = client.getForEntity(crearUri("/api/cuentas"), Cuenta[].class);
    List<Cuenta> cuentas = Arrays.asList(respuesta.getBody());
    assertEquals(3, cuentas.size());

    // client.delete(crearUri("/api/cuentas/3"));
    Map<String, Long> pathVariable = new HashMap<>();
    pathVariable.put("id", 3L);
    ResponseEntity<Void> respuestaDelete = client.exchange(crearUri("/api/cuentas/{id}"), HttpMethod.DELETE, null,
            Void.class, pathVariable);
    assertEquals(HttpStatus.NO_CONTENT, respuestaDelete.getStatusCode());
    assertFalse(respuestaDelete.hasBody());

    respuesta = client.getForEntity(crearUri("/api/cuentas"), Cuenta[].class);
    cuentas = Arrays.asList(respuesta.getBody());
    assertEquals(2, cuentas.size());

    ResponseEntity<Cuenta> respuestaDetalle = client.getForEntity(crearUri("/api/cuentas/3"), Cuenta.class);
    assertEquals(HttpStatus.NOT_FOUND, respuestaDetalle.getStatusCode());
    assertFalse(respuestaDetalle.hasBody());
  }

  private String crearUri(String uri) {
    return "http://localhost:" + port + uri;
  }
}