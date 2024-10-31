package org.keiber.test.springboot.app;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.keiber.test.springboot.app.models.Cuenta;
import org.keiber.test.springboot.app.repositories.CuentaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@DataJpaTest
public class IntegrationJpaTest {
  @Autowired
  CuentaRepository cuentaRepository;

  // @BeforeEach
  // void setUp() {
  // cuentaRepository.save(new Cuenta(1L, "Andrés", new BigDecimal("1000")));
  // cuentaRepository.save(new Cuenta(2L, "Jhon", new BigDecimal("2000")));
  // }

  @Test
  void testFindById() {
    Optional<Cuenta> cuenta = cuentaRepository.findById(1L);
    assertTrue(cuenta.isPresent());
    assertEquals("Andrés", cuenta.orElseThrow().getPersona());
  }

  @Test
  void testFindByPersona() {
    Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Andrés");
    assertTrue(cuenta.isPresent());
    assertEquals("Andrés", cuenta.orElseThrow().getPersona());
    assertEquals("1000.00", cuenta.orElseThrow().getSaldo().toPlainString());
  }

  @Test
  void testFindByPersonaThrowException() {
    Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Rod");
    assertThrows(NoSuchElementException.class, cuenta::orElseThrow);
    assertFalse(cuenta.isPresent());
  }

  @Test
  void testFindAll() {
    List<Cuenta> cuentas = cuentaRepository.findAll();
    assertFalse(cuentas.isEmpty());
    assertEquals(2, cuentas.size());
  }

  @Test
  void testSave() {
    // Given
    Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));

    // When
    Cuenta cuenta = cuentaRepository.save(cuentaPepe);
    // Cuenta cuenta = cuentaRepository.findById(save.getId()).orElseThrow();
    // Cuenta cuenta = cuentaRepository.findById(3L).orElseThrow();

    // Then
    assertEquals("Pepe", cuentaPepe.getPersona());
    assertEquals("3000", cuentaPepe.getSaldo().toPlainString());
    assertEquals(3L, cuenta.getId());
    assertEquals(3, cuentaRepository.findAll().size());
  }

  @Test
  void testUpdate() {
    // Given
    Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));

    // When
    Cuenta cuenta = cuentaRepository.save(cuentaPepe);
    // Cuenta cuenta = cuentaRepository.findById(save.getId()).orElseThrow();
    // Cuenta cuenta = cuentaRepository.findById(3L).orElseThrow();

    // Then
    assertEquals("Pepe", cuenta.getPersona());
    assertEquals("3000", cuenta.getSaldo().toPlainString());
    // assertEquals(3L, cuenta.getId());

    // When
    cuenta.setSaldo(new BigDecimal("3800"));
    Cuenta cuentaActualizada = cuentaRepository.save(cuenta);

    // Then
    assertEquals("Pepe", cuentaActualizada.getPersona());
    assertEquals("3800", cuentaActualizada.getSaldo().toPlainString());
  }

  @Test
  void testDelete() {
    Cuenta cuenta = cuentaRepository.findById(2L).orElseThrow();
    assertEquals("Jhon", cuenta.getPersona());

    cuentaRepository.delete(cuenta);
    assertThrows(NoSuchElementException.class, () -> cuentaRepository.findByPersona("Jhon").orElseThrow());
    assertEquals(1, cuentaRepository.findAll().size());
  }
}
