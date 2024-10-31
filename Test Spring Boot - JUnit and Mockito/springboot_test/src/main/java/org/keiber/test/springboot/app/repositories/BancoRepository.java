package org.keiber.test.springboot.app.repositories;

import org.keiber.test.springboot.app.models.Banco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BancoRepository extends JpaRepository<Banco, Long> {
//  List<Banco> findAll();

//  Banco findById(Long id);

//  void update(Banco banco);
}
