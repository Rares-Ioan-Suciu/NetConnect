package com.netconnect.Applications.UserApp.Exception;

public class InvalidEmailException extends RuntimeException {
  public InvalidEmailException(String message) {
    super(message);
  }
}
