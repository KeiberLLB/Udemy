package org.keiber.appmockito.ejemplos.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keiber.appmockito.ejemplos.Datos;
import org.keiber.appmockito.ejemplos.models.Examen;

import org.keiber.appmockito.ejemplos.repositories.ExamenRepository;
import org.keiber.appmockito.ejemplos.repositories.ExamenRepositoryImpl;
import org.keiber.appmockito.ejemplos.repositories.PreguntaRepository;
import org.keiber.appmockito.ejemplos.repositories.PreguntaRepositoryImpl;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {

  //Given
  @Mock
  ExamenRepositoryImpl repository;

  @Mock
  PreguntaRepositoryImpl preguntaRepository;

  @InjectMocks
  ExamenServiceImpl service;

  @Captor
  ArgumentCaptor<Long> captor;

  @BeforeEach
  void setUp() {
    // También serían en given ->
    // es lo mismo de arriba, pero de forma implícita

    //MockitoAnnotations.openMocks(this);
    //repository = mock(ExamenRepositoryImpl.class);
    //preguntaRepository = mock(PreguntaRepositoryImpl.class);
    //service = new ExamenServiceImpl(repository, preguntaRepository);
  }

  @Test
  void findExamenPorNombre() {
    // no se puede simular métodos privados, estáticos o final en Mockito
    when(repository.findAll()).thenReturn(Datos.EXAMENES);
    Optional<Examen> examen = service.findExamenPorNombre("Matemáticas");
    assertTrue(examen.isPresent());
    assertEquals(5L, examen.orElseThrow().getId());
    assertEquals("Matemáticas", examen.get().getNombre());
    // control + shift + Fn + F10 ejecuta el test.
  }

  @Test
  void findExamenPorNombreListaVacia() {
    // no se puede simular métodos privados, estáticos o final en Mockito
    List<Examen> datos = Collections.emptyList();

    when(repository.findAll()).thenReturn(datos);
    Optional<Examen> examen = service.findExamenPorNombre("Matemáticas");
    assertFalse(examen.isPresent());
    // control + shift + Fn + F10 ejecuta el test.
  }

  @Test
  void testPreguntasExamen() {
    when(repository.findAll()).thenReturn(Datos.EXAMENES);
    when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
    Examen examen = service.findExamenPorNombreConPreguntas("Matemáticas");
    assertEquals(5, examen.getPreguntas().size());
    assertTrue(examen.getPreguntas().contains("aritmética"));
  }

  @Test
  void testPreguntasExamenVerify() {
    when(repository.findAll()).thenReturn(Datos.EXAMENES);
    when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
    Examen examen = service.findExamenPorNombreConPreguntas("Matemáticas");
    assertEquals(5, examen.getPreguntas().size());
    assertTrue(examen.getPreguntas().contains("aritmética"));
    // verifica que los métodos sean invocados (usados)
    verify(repository).findAll();
    verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
  }

  @Test
  void testNoExisteExamenVerify() {

    //Given
    when(repository.findAll()).thenReturn(Collections.emptyList());
    when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

    //When
    Examen examen = service.findExamenPorNombreConPreguntas("Matemáticas");

    //Then
    assertNull(examen);
    // verifica que los métodos sean invocados (usados)
    verify(repository).findAll();
    verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
  }

  @Test
  void testGuardarExamen() {
    //Given = Son las precondiciones en nuestro entorno de prueba
    Examen newExamen = Datos.EXAMEN;
    newExamen.setPreguntas(Datos.PREGUNTAS);
    when(repository.guardar(any(Examen.class))).then(new Answer<Examen>() {

      Long secuencia = 8L;

      @Override
      public Examen answer(InvocationOnMock invocationOnMock) throws Throwable {
        Examen examen = invocationOnMock.getArgument(0);
        examen.setId(secuencia++);
        return examen;
      }
    });

    //When = Llamamos a nuestra acción en nuestro entorno de prueba
    Examen examen = service.guardar(newExamen);

    //Then = Verificamos que nuestra acción produzca el resultado esperado
    assertNotNull(examen.getId());
    assertEquals(8L, examen.getId());
    assertEquals("Física", examen.getNombre());

    verify(repository).guardar(any(Examen.class));
    verify(preguntaRepository).guardarVarias(anyList());
  }

  @Test
  void testManejoException() {
    when(repository.findAll()).thenReturn(Datos.EXAMENES_ID_NULL);
    when(preguntaRepository.findPreguntasPorExamenId(isNull())).thenThrow(IllegalArgumentException.class);
    Exception exception = assertThrows(IllegalArgumentException.class, () -> service.findExamenPorNombreConPreguntas("Matemáticas"));
    assertEquals(IllegalArgumentException.class, exception.getClass());
    verify(repository).findAll();
    verify(preguntaRepository).findPreguntasPorExamenId(isNull());
  }

  @Test
  void testArgumentMatcher() {
    when(repository.findAll()).thenReturn(Datos.EXAMENES);
    when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
    service.findExamenPorNombreConPreguntas("Matemáticas");

    verify(repository).findAll();
    verify(preguntaRepository).findPreguntasPorExamenId(argThat(arg -> arg != null && arg.equals(5L))); // arg hace referencia a argumento
    //haciendo referencia en este caso al id
//    verify(preguntaRepository).findPreguntasPorExamenId(eq(5L));
//    verify(preguntaRepository).findPreguntasPorExamenId(argThat(arg -> arg != null && arg >= 5L));
  }

  @Test
  void testArgumentMatcher2() {
    when(repository.findAll()).thenReturn(Datos.EXAMENES_ID_NEGATIVOS);
    when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
    service.findExamenPorNombreConPreguntas("Matemáticas");

    verify(repository).findAll();
    verify(preguntaRepository).findPreguntasPorExamenId(argThat(new MiArgsMatchers()));
  }

  public static class MiArgsMatchers implements ArgumentMatcher<Long> {

    private Long argument;

    @Override
    public boolean matches(Long argument) {
      this.argument = argument;
      return argument != null && argument > 0;
    }

    @Override
    public Class<?> type() {
      return ArgumentMatcher.super.type();
    }

    @Override
    public String toString() {
      return "es para un mensaje personalizado de error "
        + "que imprime mockito en caso de que falle el test "
        + argument + " debe ser un entero positivo";
    }
  }

  @Test
  void testArgumentCaptor() {
    when(repository.findAll()).thenReturn(Datos.EXAMENES);
    when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
    service.findExamenPorNombreConPreguntas("Matemáticas");

    //se hará con anotaciones al principio
//    ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
    verify(preguntaRepository).findPreguntasPorExamenId(captor.capture());
    assertEquals(5L, captor.getValue());
  }

  @Test
  void testDoThorow() {
    //when lo usamos para simular comportamientos que no son posibles con Mockito
    //y también cuando el método retorna un objeto de cualquier tipo; ejemplo ->
    // no se puede usar con un Void.
    //when(preguntaRepository.findPreguntasPorExamenId(45L)).thenThrow(IllegalArgumentException.class);

    //doThrow lo usamos para los métodos Void
    Examen examen = Datos.EXAMEN;
    examen.setPreguntas(Datos.PREGUNTAS);
    doThrow(IllegalArgumentException.class).when(preguntaRepository).guardarVarias(anyList());

    assertThrows(IllegalArgumentException.class, () -> service.guardar(examen));
  }

  @Test
  void testDoAnswer() {
    when(repository.findAll()).thenReturn(Datos.EXAMENES);
//    when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
    doAnswer(invocationOnMock -> {
      Long id = invocationOnMock.getArgument(0);
      return id == 5L ? Datos.PREGUNTAS : Collections.emptyList();
    }).when(preguntaRepository).findPreguntasPorExamenId(anyLong());

    Examen examen = service.findExamenPorNombreConPreguntas("Matemáticas");
    assertEquals(5, examen.getPreguntas().size());
    assertTrue(examen.getPreguntas().contains("aritmética"));
    assertEquals(5L, examen.getId());
    assertEquals("Matemáticas", examen.getNombre());

    //verify = verifica que el método se ha llamado con el argumento esperado
    verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
  }

  @Test
  void testDoAnswerGuardarExamen() {
    //Given = Son las precondiciones en nuestro entorno de prueba
    Examen newExamen = Datos.EXAMEN;
    newExamen.setPreguntas(Datos.PREGUNTAS);

    doAnswer(new Answer<Examen>() {
      Long secuencia = 8L;

      @Override
      public Examen answer(InvocationOnMock invocationOnMock) throws Throwable {
        Examen examen = invocationOnMock.getArgument(0);
        examen.setId(secuencia++);
        return examen;
      }
    }).when(repository).guardar(any(Examen.class));

    //When = Llamamos a nuestra acción en nuestro entorno de prueba
    Examen examen = service.guardar(newExamen);

    //Then = Verificamos que nuestra acción produzca el resultado esperado
    assertNotNull(examen.getId());
    assertEquals(8L, examen.getId());
    assertEquals("Física", examen.getNombre());

    verify(repository).guardar(any(Examen.class));
    verify(preguntaRepository).guardarVarias(anyList());
  }

  @Test
  void testDoCallRealMethod() {
    when(repository.findAll()).thenReturn(Datos.EXAMENES);
//    when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

    doCallRealMethod().when(preguntaRepository).findPreguntasPorExamenId(anyLong());
    Examen examen = service.findExamenPorNombreConPreguntas("Matemáticas");
    assertEquals(5L, examen.getId());
    assertEquals("Matemáticas", examen.getNombre());

  }

  @Test
  void testSpy() {
    ExamenRepository examenRepository = spy(ExamenRepositoryImpl.class);
    PreguntaRepository preguntaRepository = spy(PreguntaRepositoryImpl.class);
    ExamenService examenService = new ExamenServiceImpl(examenRepository, preguntaRepository);

    List<String> preguntas = Arrays.asList("aritmética", "geometría", "cálculo");
//    when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);
    doReturn(preguntas).when(preguntaRepository).findPreguntasPorExamenId(anyLong());

    Examen examen = examenService.findExamenPorNombreConPreguntas("Matemáticas");
    assertEquals(5L, examen.getId());
    assertEquals("Matemáticas", examen.getNombre());
    assertEquals(3, examen.getPreguntas().size());
    assertTrue(examen.getPreguntas().contains("aritmética"));

    verify(examenRepository).findAll();
    verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
  }

  //verificar el orden en el que se ejecutan los métodos mock
  @Test
  void testOrdenDeInvocaciones() {
    when(repository.findAll()).thenReturn(Datos.EXAMENES);

    service.findExamenPorNombreConPreguntas("Matemáticas");
    service.findExamenPorNombreConPreguntas("Lenguaje");

    // Ctrl + Alt + v = se crea una variable
    InOrder inOrder = inOrder(preguntaRepository);
    inOrder.verify(preguntaRepository).findPreguntasPorExamenId(5L);
    inOrder.verify(preguntaRepository).findPreguntasPorExamenId(6L);
  }

  @Test
  void testOrdenDeInvocaciones2() {
    when(repository.findAll()).thenReturn(Datos.EXAMENES);

    service.findExamenPorNombreConPreguntas("Matemáticas");
    service.findExamenPorNombreConPreguntas("Lenguaje");

    // Ctrl + Alt + v = se crea una variable
    // Ctrl + d = clona una linea de código
    InOrder inOrder = inOrder(repository, preguntaRepository);
    inOrder.verify(repository).findAll();
    inOrder.verify(preguntaRepository).findPreguntasPorExamenId(5L);

    inOrder.verify(repository).findAll();
    inOrder.verify(preguntaRepository).findPreguntasPorExamenId(6L);
  }

  @Test
  void testNumeroInvocaciones() {
    when(repository.findAll()).thenReturn(Datos.EXAMENES);
    service.findExamenPorNombreConPreguntas("Matemáticas");

    verify(preguntaRepository).findPreguntasPorExamenId(5L);
    //como segundo argumento en el verify
    //times es el número de veces que se invoca el mock
    verify(preguntaRepository, times(1)).findPreguntasPorExamenId(5L);
    //atLeast(1) es el mínimo número de veces que se invoca el mock
    verify(preguntaRepository, atLeast(1)).findPreguntasPorExamenId(5L);
    //atMost(1) es el máximo número de veces que se invoca el mock
    verify(preguntaRepository, atMost(1)).findPreguntasPorExamenId(5L);
    //atMostOnce() es el número máximo de veces que se invoca el mock, incluyendo 0
    verify(preguntaRepository, atMostOnce()).findPreguntasPorExamenId(5L);
    //atLeastOnce() es el número mínimo de veces que se invoca el mock, incluyendo 0
    verify(preguntaRepository, atLeastOnce()).findPreguntasPorExamenId(5L);
  }

  @Test
  void testNumeroInvocaciones2() {
    when(repository.findAll()).thenReturn(Datos.EXAMENES);
    service.findExamenPorNombreConPreguntas("Matemáticas");

//    verify(preguntaRepository).findPreguntasPorExamenId(5L);
    //como segundo argumento en el verify
    //times es el número de veces que se invoca el mock
    verify(preguntaRepository, times(2)).findPreguntasPorExamenId(5L);
    //atLeast(1) es el mínimo número de veces que se invoca el mock
    verify(preguntaRepository, atLeast(1)).findPreguntasPorExamenId(5L);
    //atMost(1) es el máximo número de veces que se invoca el mock
    verify(preguntaRepository, atMost(2)).findPreguntasPorExamenId(5L);
    //atMostOnce() es el número máximo de veces que se invoca el mock, incluyendo 0
//    verify(preguntaRepository, atMostOnce()).findPreguntasPorExamenId(5L);
    //atLeastOnce() es el número mínimo de veces que se invoca el mock, incluyendo 0
    verify(preguntaRepository, atLeastOnce()).findPreguntasPorExamenId(5L);
  }

  @Test
  void testNumeroInvocaciones3() {
    when(repository.findAll()).thenReturn(Collections.emptyList());
    service.findExamenPorNombreConPreguntas("Matemáticas");

    verify(preguntaRepository, never()).findPreguntasPorExamenId(5L);
    verifyNoInteractions(preguntaRepository);

    verify(repository).findAll();
    verify(repository, times(1)).findAll();
    verify(repository, atLeast(1)).findAll();
    verify(repository, atLeastOnce()).findAll();
    verify(repository, atMost(1)).findAll();
    verify(repository, atMostOnce()).findAll();
  }

}