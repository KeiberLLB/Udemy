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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTestImplSpyTest {

  //Given
  @Spy
  ExamenRepositoryImpl repository;

  @Spy
  PreguntaRepositoryImpl preguntaRepository;

  @InjectMocks
  ExamenServiceImpl service;

  @Test
  void testSpy() {
    List<String> preguntas = Arrays.asList("aritmética", "geometría", "cálculo");
//    when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);
    doReturn(preguntas).when(preguntaRepository).findPreguntasPorExamenId(anyLong());

    Examen examen = service.findExamenPorNombreConPreguntas("Matemáticas");
    assertEquals(5L, examen.getId());
    assertEquals("Matemáticas", examen.getNombre());
    assertEquals(3, examen.getPreguntas().size());
    assertTrue(examen.getPreguntas().contains("aritmética"));

    verify(repository).findAll();
    verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
  }
}