package org.keiber.appmockito.ejemplos.repositories;

import org.keiber.appmockito.ejemplos.Datos;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PreguntaRepositoryImpl implements PreguntaRepository {
  @Override
  public List<String> findPreguntasPorExamenId(Long id) {
    System.out.println("PreguntaRepositoyImpl.findPreguntasPorExamenId");
    try {
      TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return Datos.PREGUNTAS;
  }

  @Override
  public void guardarVarias(List<String> preguntas) {
    System.out.println("PreguntaRepositoyImpl.guardarVarias");

  }
}
