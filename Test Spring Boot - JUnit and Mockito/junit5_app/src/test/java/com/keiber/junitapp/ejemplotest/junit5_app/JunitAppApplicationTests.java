package com.keiber.junitapp.ejemplotest.junit5_app;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;

// el static es indispensable
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import com.keiber.junitapp.ejemplotest.junit5_app.exceptions.DineroInsuficienteException;
import com.keiber.junitapp.ejemplotest.junit5_app.models.Banco;
import com.keiber.junitapp.ejemplotest.junit5_app.models.Cuenta;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

// @BeforeAll (se ejecuta antes de cualquiera)
// @AfterAll (se ejecuta despues de cualquiera)
// los dos anteriores "envuelven" el test
// @BeforeEach (se ejecuta antes de cada test)
// @AfterEach (se ejecuta despues de cada test)

// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
// indica que este test se va a ejecutar con instancia de clase
// por defecto viene PER_METHOD
// PER_CLASS maneja una sola instancia de la clase test para todos los metodos, una instancia en comun me permite quitar los static
// no se recomienda
class JunitAppApplicationTests {

  Cuenta cuenta;

  private TestInfo testInfo;
  private TestReporter testReporter;

  @BeforeEach
    // este metodo se ejecuta antes de cada test
  void initMetodoTest(TestInfo testInfo, TestReporter testReporter) {
    this.cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
    this.testInfo = testInfo;
    this.testReporter = testReporter;
    System.out.println("iniciando el metodo");
    testReporter.publishEntry("Ejecutando: " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().orElse(null).getName() +
      " con las etiquetas " + testInfo.getTags());
  }

  @AfterEach
    // este metodo se ejecuta despues de cada test
  void tearDown() {
    System.out.println("finalizando el metodo de prueba");
  }

  @BeforeAll
  static void beforeAll() {
    System.out.println("inicializando el test");
  }

  @AfterAll
  static void afterAll() {
    System.out.println("finalizando el test");
  }

  @Tag("cuenta")
  @Nested
  @DisplayName("probando atributos de la cuenta corriente")
  class CuentaTestNombreSaldo {

    @Test
    // @Disabled
    // deshabilitar el test, no se ejecuta
    // en los reportes muestra que un test no se ejecutó
    // comentarlo omite cualquier registro del mismo, por tal motivo lo mejor es
    // usar etiquetas
    @DisplayName("el nombre")
    void testNombreCuenta() {
      testReporter.publishEntry(testInfo.getTags().toString());
      if (testInfo.getTags().contains("cuenta")) {
        testReporter.publishEntry("Hacer algo con la etiqueta cuenta");
      }

      //fail();
      // forzar el error
      // cuenta.setPersona("Andres");
      String esperado = "Andres";
      String real = cuenta.getPersona();
      // el mensaje de error se pasa en expresion lambda para que el mensaje no se
      // construya inmediatamente, solo se construira y creara el string del error
      // cuando sea necesario
      assertNotNull(real, () -> "la cuenta no puede ser nula");
      assertEquals(esperado, real,
        () -> "el nombre de la cuenta no es el que se esperaba: se esperaba: " + esperado + " sin embargo fue "
          + real);
      assertEquals("Andres", real, "nombre cuenta esperada debe ser igual a la real");
    }

    @Test
    void testSaldoCuenta() {
      assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
      // este test aplica para determinar que un valor NO sea negativo
      assertNotNull(cuenta.getSaldo());
      assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
      assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testRefenciaCuenta() {
      cuenta = new Cuenta("John Doe", new BigDecimal("8900.9997"));
      Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.9997"));
      // se le pasa primero el valor de comparacion y de segundo el valor "real"
      // assertNotEquals(cuenta2, cuenta);
      assertEquals(cuenta2, cuenta);
    }

  }

  @Nested
  class CuentaOperacionesTest {
    @Tag("cuenta")
    @Test
    void testDebitoCuenta() {
      cuenta.debito(new BigDecimal(100));
      assertNotNull(cuenta.getSaldo());
      assertEquals(900, cuenta.getSaldo().intValue());
      assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testCreditoCuenta() {
      cuenta.credito(new BigDecimal(100));
      assertNotNull(cuenta.getSaldo());
      assertEquals(1100, cuenta.getSaldo().intValue());
      assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
    }

    @Tag("cuenta")
    @Tag("manejoErroresCuenta")
    @Test
    void testDineroInsuficienteExceptionCuenta() {
      // Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
      // el primer dato que pasamos es la funcion de exceptions que deseamos testear
      // el segundo valor, en este caso un funcion Lambda donde se creará el ambiente
      // de prueba
      Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
        cuenta.debito(new BigDecimal(1500));
      });
      String actual = exception.getMessage();
      String esperado = "Dinero Insuficiente";
      assertEquals(esperado, actual);
    }

    @Tag("cuenta")
    @Tag("banco")
    @Test
    void testTransferirDineroCuentas() {
      Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
      Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));
      Banco banco = new Banco();
      banco.setNombre("Banco del Estado");
      banco.transferir(cuenta2, cuenta1, new BigDecimal(500));
      assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
      assertEquals("3000", cuenta1.getSaldo().toPlainString());
    }

    @Test
    void testClonAnterior() {
      Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
      Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));

      Banco banco = new Banco();
      banco.addCuenta(cuenta1);
      banco.addCuenta(cuenta2);

      banco.setNombre("Banco del Estado");
      banco.transferir(cuenta2, cuenta1, new BigDecimal(500));

      assertAll(() -> assertEquals("1000.8989", cuenta2.getSaldo().toPlainString()),
        () -> assertEquals("3000", cuenta1.getSaldo().toPlainString()),
        () -> assertEquals(2, banco.getCuentas().size()), () -> {
          assertEquals("Banco del Estado", cuenta1.getBanco().getNombre());
        }, () -> assertEquals("Andres", banco.getCuentas().stream()
          .filter(c -> c.getPersona()
            .equals("Andres"))
          .findFirst().get().getPersona())
/*
					, () ->
						assertTrue(banco.getCuentas().stream()
								.filter(c -> c.getPersona()
										.equals("Andres"))
								.findFirst().isPresent());
*/
        , () -> assertTrue(banco.getCuentas().stream()
          .anyMatch(c -> c.getPersona()
            .equals("Jhon Doe"))));
    }

  }

  @Nested
  class SistemaOperativoTest {

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testSoloWindows() {
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    void testSoloLinuxMac() {
    }

    @Test
    @DisabledOnOs(OS.MAC)
    void testNoMac() {
    }

  }

  @Nested
  class JavaVersion {

    @Test
    @EnabledOnJre(JRE.JAVA_8)
    void testSoloJava8() {
    }

    @Test
    @EnabledOnJre(JRE.JAVA_15)
    void testSoloJava15() {
    }

    @Test
    @DisabledOnJre(JRE.JAVA_15)
    void testNoJava15() {
    }

  }

  @Nested
  class SistemPropertiesTest {

    @Test
    void imprimirSystemProperties() {
      Properties properties = System.getProperties();
      properties.forEach((k, v) -> System.out.println(k + ":" + v));
    }

    @Test
    // en matches, el valor debe ser exactamente igual, para similitudes se debe
    // usar una expresion regular
    // ejemplo expresion regular ".*15.*"
    @EnabledIfSystemProperty(named = "java.version", matches = "15.0.1")
    void testJavaVersion() {
    }

    @Test
    @DisabledIfSystemProperty(named = "os.arch", matches = ".*64.*")
    void testSolo64() {
    }

    @Test
    @EnabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
    void testNo64() {
    }

    @Test
    @EnabledIfSystemProperty(named = "user.name", matches = "leand")
    void testUsuario() {
    }

    @Test
    @EnabledIfSystemProperty(named = "ENV", matches = "dev")
    void testDev() {
    }

  }

  @Nested
  class VariablesAmbiente {

    @Test
    void imprimirVariablesAmbiente() {
      System.getenv().forEach((k, v) -> System.out.println(k + ":" + v));
      Map<String, String> getenv = System.getenv();
      getenv.forEach((k, v) -> System.out.println(k + " = " + v));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "USERPROFILE", matches = ".*leand.*")
    void testJavaHome() {
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "4")
    void testProcesadores() {
    }

    @Test
    void testSaldoCuentaDev() {
      boolean esDev = "NUMBER_OF_PROCESSORS".equals(System.getProperty("4"));
      assumeTrue(esDev);
      // si se cumple o no el assumeTrue se ejecutan el resto de pruebas
      assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
      // este test aplica para determinar que un valor NO sea negativo
      assertNotNull(cuenta.getSaldo());
      assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
      assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testSaldoCuentaDev2() {
      boolean esDev = "NUMBER_OF_PROCESSORS".equals(System.getProperty("4"));
      assumingThat(esDev, this::execute);
    }

    private void execute() {
      // si se cumple o no el assumingThat se ejecutan el resto de pruebas
      assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
      // este test aplica para determinar que un valor NO sea negativo
      assertNotNull(cuenta.getSaldo());
      assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
      assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }
  }

  @DisplayName("Probando debito cuenta repetir!")
  @RepeatedTest(value = 5, name = "{displayName} - Repetición numero {currentRepetition} de {totalRepetitions}")
  void testDebitoCuentaRepetir(RepetitionInfo info) {
    if (info.getCurrentRepetition() == 3) {
      System.out.println("estamos en la repetición " + info.getCurrentRepetition());
    }
    cuenta.debito(new BigDecimal(100));
    assertNotNull(cuenta.getSaldo());
    assertEquals(900, cuenta.getSaldo().intValue());
    assertEquals("900.12345", cuenta.getSaldo().toPlainString());
  }

  @Tag("pruebasParametrizadas")
  @Nested
  class PruebasParametrizadasTest {
    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @ValueSource(strings = {"100", "200", "300", "500", "700", "1000.12345"})
    void testDebitoCuentaValueSource(String monto) {
      cuenta.debito(new BigDecimal(monto));
      assertNotNull(cuenta.getSaldo());
      assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @CsvSource({"1,100", "2,200", "3,300", "4,500", "5,700", "6,1000.12345"})
    void testDebitoCuentaCsvSource(String index, String monto) {
      System.out.println(index + " -> " + monto);
      cuenta.debito(new BigDecimal(monto));
      assertNotNull(cuenta.getSaldo());
      assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @CsvSource({"200,100,John,Andres", "250,200,Pepe,Pepe", "300,300,Maria,maria", "510,500,Pepa,Pepa", "700,700,Lucas,Luca", "1000.12345,1000.12345,cata,Cata"})
    void testDebitoCuentaCsvSource2(String saldo, String monto, String esperado, String actual) {
      System.out.println(saldo + " -> " + monto);
      cuenta.setSaldo(new BigDecimal(saldo));
      cuenta.debito(new BigDecimal(monto));
      cuenta.setPersona(actual);

      assertNotNull(cuenta.getPersona());
      assertNotNull(cuenta.getSaldo());
      assertEquals(esperado, actual);
      assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @CsvFileSource(resources = "/data.csv")
    void testDebitoCuentaCsvFileSource(String monto) {
      cuenta.debito(new BigDecimal(monto));
      assertNotNull(cuenta.getSaldo());
      assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @CsvFileSource(resources = "/data2.csv")
    void testDebitoCuentaCsvFileSource2(String saldo, String monto, String esperado, String actual) {
      System.out.println(saldo + " -> " + monto);
      cuenta.setSaldo(new BigDecimal(saldo));
      cuenta.debito(new BigDecimal(monto));
      cuenta.setPersona(actual);

      assertNotNull(cuenta.getPersona());
      assertNotNull(cuenta.getSaldo());
      assertEquals(esperado, actual);
      assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }
  }

  @Tag("pruebasParametrizadas")
  @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
  @MethodSource("montoList")
  void testDebitoCuentaMethodSource(String monto) {
    cuenta.debito(new BigDecimal(monto));
    assertNotNull(cuenta.getSaldo());
    assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
  }

  static List<String> montoList() {
    return Arrays.asList("100", "200", "300", "500", "700", "1000.12345");
  }

  @Nested
  @Tag("timeOut")
  class EjemploTimeOutTest {
    @Test
    @Timeout(1)
      // el valor que recibe es en segundos
    void pruebaTimeout() throws InterruptedException {
      TimeUnit.MILLISECONDS.sleep(900);
    }

    @Test
    @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
      // el valor que recibe es en segundos
    void pruebaTimeout2() throws InterruptedException {
      TimeUnit.MILLISECONDS.sleep(900);
    }

    @Test
    void testTimeoutAssertions() {
      assertTimeout(Duration.ofSeconds(5), () -> TimeUnit.MILLISECONDS.sleep(4000));
    }
  }
}