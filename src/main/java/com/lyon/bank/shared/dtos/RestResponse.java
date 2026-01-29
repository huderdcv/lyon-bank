package com.lyon.bank.shared.dtos;

import java.time.LocalDateTime;

public record RestResponse<T>(
  String success,
  String message,
  T data,
  LocalDateTime timestamp
) {
  public static <T> RestResponse<T> success(String message, T data){
    return new RestResponse<>("SUCCESS", message, data, LocalDateTime.now());
  }

  public static <T> RestResponse<T> success(String message){
    return new RestResponse<>("SUCCESS", message, null, LocalDateTime.now());
  }
}
