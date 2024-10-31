package org.keiber.test.springboot.app.repositories;

import org.keiber.test.springboot.app.models.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
  @Query("select c from Cuenta c where c.persona=?1")
  Optional<Cuenta> findByPersona(String persona);

//  List<Cuenta> findAll();
//  Optional<Cuenta> findById(Long id);
//  void update(Cuenta cuenta);
}
