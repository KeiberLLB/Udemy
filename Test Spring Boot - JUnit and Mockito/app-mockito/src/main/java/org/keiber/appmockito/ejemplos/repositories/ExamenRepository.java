package org.keiber.appmockito.ejemplos.repositories;

import org.keiber.appmockito.ejemplos.models.Examen;

import java.util.List;

public interface ExamenRepository {
  Examen guardar(Examen examen);

  List<Examen> findAll();

}
