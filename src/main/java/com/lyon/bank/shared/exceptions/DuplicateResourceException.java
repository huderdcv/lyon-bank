package com.lyon.bank.shared.exceptions;

public class DuplicateResourceException extends RuntimeException{
  public DuplicateResourceException (String message){
    super(message);
  }
}
