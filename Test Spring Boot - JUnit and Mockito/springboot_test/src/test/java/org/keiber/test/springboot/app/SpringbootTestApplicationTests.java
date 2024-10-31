package org.keiber.test.springboot.app;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.keiber.test.springboot.app.Datos.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keiber.test.springboot.app.exceptions.DineroInsuficienteException;
import org.keiber.test.springboot.app.models.Banco;
import org.keiber.test.springboot.app.models.Cuenta;
import org.keiber.test.springboot.app.repositories.BancoRepository;
import org.keiber.test.springboot.app.repositories.CuentaRepository;
import org.keiber.test.springboot.app.services.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class SpringbootTestApplicationTests {

  // anotación de mockito
  //@Mock
  // anotación de SpringBoot
  @MockBean
  CuentaRepository cuentaRepository;

  // anotación de mockito
  //@Mock
  // anotación de SpringBoot
  @MockBean
  BancoRepository bancoRepository;

  // anotación de mockito
  //@InjectMocks

  // Para usar la anotación de SpringBoot se debe volver un componente el CuentaServiceImp con ->
  // la anotación de SpringBoot @Service
  // anotación de SpringBoot
  @Autowired // se usa para inyectar un componente dentro de otro componente
    // La ventaja es que se puede anotar la interface y no la implementación
    CuentaService service;
  // Para usar las anotaciones de mockito service debe ser una implementación
  // en el @BeforeEach se usa como está abajo ->
//  CuentaService service;

  @BeforeEach
  void setUp() {
//    cuentaRepository = mock(CuentaRepository.class);
//    bancoRepository = mock(BancoRepository.class);
//    service = new CuentaServiceImpl(cuentaRepository, bancoRepository);
//		Datos.CUENTA_001.setSaldo(new BigDecimal("1000"));
//		Datos.CUENTA_002.setSaldo(new BigDecimal("2000"));
//		Datos.BANCO.setTotalTransferencias(0);
  }

  //Ctrl + shift + Fn + F10 = iniciar test

  @Test
	void contextLoads() {
    when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());
    when(cuentaRepository.findById(2L)).thenReturn(crearCuenta002());
    when(bancoRepository.findById(1L)).thenReturn(crearBanco());

    BigDecimal saldoOrigen = service.revisarSaldo(1L);
    BigDecimal saldoDestino = service.revisarSaldo(2L);
    assertEquals("1000", saldoOrigen.toPlainString());
    assertEquals("2000", saldoDestino.toPlainString());

    service.transferir(1L, 2L, new BigDecimal("100"), 1L);
    saldoOrigen = service.revisarSaldo(1L);
    saldoDestino = service.revisarSaldo(2L);

    assertEquals("900", saldoOrigen.toPlainString());
    assertEquals("2100", saldoDestino.toPlainString());

    int total = service.revisarTotalTransferencias(1L);
    assertEquals(1, total);

    verify(cuentaRepository, times(3)).findById(1L);
    verify(cuentaRepository, times(3)).findById(2L);
    verify(cuentaRepository, times(2)).save(any(Cuenta.class));

    verify(bancoRepository, times(2)).findById(1L);
    verify(bancoRepository).save(any(Banco.class));

    verify(cuentaRepository, times(6)).findById(anyLong());
    verify(cuentaRepository, never()).findAll();
  }

  @Test
  void contextLoads2() {
    when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());
    when(cuentaRepository.findById(2L)).thenReturn(crearCuenta002());
    when(bancoRepository.findById(1L)).thenReturn(crearBanco());

    BigDecimal saldoOrigen = service.revisarSaldo(1L);
    BigDecimal saldoDestino = service.revisarSaldo(2L);
    assertEquals("1000", saldoOrigen.toPlainString());
    assertEquals("2000", saldoDestino.toPlainString());

    assertThrows(DineroInsuficienteException.class, () -> {
      service.transferir(1L, 2L, new BigDecimal("1200"), 1L);
    });


    saldoOrigen = service.revisarSaldo(1L);
    saldoDestino = service.revisarSaldo(2L);

    assertEquals("1000", saldoOrigen.toPlainString());
    assertEquals("2000", saldoDestino.toPlainString());

    int total = service.revisarTotalTransferencias(1L);
    assertEquals(0, total);

    verify(cuentaRepository, times(3)).findById(1L);
    verify(cuentaRepository, times(2)).findById(2L);
    verify(cuentaRepository, never()).save(any(Cuenta.class));

    verify(bancoRepository, times(1)).findById(1L);
    verify(bancoRepository, never()).save(any(Banco.class));

    verify(cuentaRepository, times(5)).findById(anyLong());
    verify(cuentaRepository, never()).findAll();
  }

  @Test
  void contextLoads3() {
    when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());

    Cuenta cuenta1 = service.findById(1L);
    Cuenta cuenta2 = service.findById(1L);

    //verifica si dos valores son el mismo objeto
    assertSame(cuenta1, cuenta2);
    assertTrue(cuenta1.equals(cuenta2));
    assertTrue(cuenta1 == cuenta2);
    assertEquals("Andrés", cuenta1.getPersona());
    assertEquals("Andrés", cuenta2.getPersona());
    verify(cuentaRepository, times(2)).findById(1L);
  }

  @Test
  void testFindAll() {
    // Given
    List<Cuenta> cuentas = Arrays.asList(crearCuenta001().orElseThrow(), crearCuenta002().orElseThrow());
    when(cuentaRepository.findAll()).thenReturn(cuentas);

    // When
    List<Cuenta> allCuentas = service.findAll();

    // Then
    assertEquals(2, allCuentas.size());
    assertTrue(cuentas.contains(crearCuenta002().orElseThrow()));
    assertFalse(allCuentas.isEmpty());

    verify(cuentaRepository).findAll();
  }

  @Test
  void testSave() {

    // Given
    Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("1000"));
    when(cuentaRepository.save(any())).then(invocation ->{
      Cuenta c = invocation.getArgument(0);
      c.setId(3L);
      return c;
    });
    // When
    Cuenta cuenta = service.save(cuentaPepe);
    // Then
    assertEquals(3L, cuenta.getId());
    assertEquals("Pepe", cuenta.getPersona());
    assertEquals("1000", cuenta.getSaldo().toPlainString());

    verify(cuentaRepository).save(any());
  }
}
