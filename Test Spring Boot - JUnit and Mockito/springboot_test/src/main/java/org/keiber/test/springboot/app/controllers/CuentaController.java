package org.keiber.test.springboot.app.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.keiber.test.springboot.app.models.Cuenta;
import org.keiber.test.springboot.app.models.TransaccionDTO;
import org.keiber.test.springboot.app.services.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

  @Autowired
  private CuentaService cuentaService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<Cuenta> listar() {
    return cuentaService.findAll();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Cuenta guardar(@RequestBody Cuenta cuenta) {
    return cuentaService.save(cuenta);
  }

  @Operation(summary = "Obtiene el detalle de una cuenta por ID")
  @GetMapping("/{id}")

  public ResponseEntity<Cuenta> detalle(@PathVariable Long id) {
    Cuenta cuenta = null;
    try {
      cuenta = cuentaService.findById(id);
    } catch (NoSuchElementException e) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(cuenta);
  }

  @Operation(summary = "Realiza una transferencia entre cuentas")
  @PostMapping("/transferir")
  public ResponseEntity<?> transferir(@RequestBody TransaccionDTO dto) {
    cuentaService.transferir(dto.getCuentaOrigenId(),
        dto.getCuentaDestinoId(),
        dto.getMonto(),
        dto.getBancoId());

    Map<String, Object> response = new HashMap<>();
    response.put("date", LocalDate.now().toString());
    response.put("status", "OK");
    response.put("mensaje", "Transferencia realizada con éxito");
    response.put("transacción", dto);

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void eliminar(@PathVariable Long id) {
  cuentaService.deleteById(id);
  }

}
