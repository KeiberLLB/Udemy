package com.keiber.junitapp.ejemplotest.junit5_app.exceptions;

public class DineroInsuficienteException extends RuntimeException {
  public DineroInsuficienteException(String message) {
    super(message);
  }
}
