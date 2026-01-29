package com.lyon.bank.shared.exceptions;

import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
  // --CUSTOM EXCEPTIONS
  @ExceptionHandler(ResourceNotFoundException.class)
  public ProblemDetail notFoundResource(ResourceNotFoundException ex) {
    return buildProblemDetail(HttpStatus.NOT_FOUND, "Not found resource", ex.getMessage());
  }

  @ExceptionHandler(DuplicateResourceException.class)
  public ProblemDetail duplicateResource(DuplicateResourceException ex) {
    return buildProblemDetail(HttpStatus.CONFLICT, "Conflicts in data", ex.getMessage());
  }

  // -- DEFENSIVE ERROR. VALIDATIONS STANDARD EXCEPTIONS
  @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class, SecurityException.class})
  public ProblemDetail handleBadRequest(RuntimeException ex) {
    return buildProblemDetail(HttpStatus.BAD_REQUEST, "Invalid request", ex.getMessage());
  }

  // -- CATCH ALL THE REMAINING ONES
  @ExceptionHandler(Exception.class)
  public ProblemDetail handleGeneralError(Exception ex) {
    //log.error("Unexpected internal error", ex);
    return buildProblemDetail(
      HttpStatus.INTERNAL_SERVER_ERROR,
      "Internal server error",
      "An unexpected error occurred. Please contact support"
    );
  }

  // -- JAKARTA VALIDATIONS
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
    MethodArgumentNotValidException ex,
    HttpHeaders headers,
    HttpStatusCode status,
    WebRequest request
  ) {
    // create the ProblemDetail
    ProblemDetail problem = buildProblemDetail(
      HttpStatus.BAD_REQUEST,
      "Input validation errors",
      "Data validations has failed"
    );

    // extract the errors dynamically
    Map<String, String> errors = ex.getBindingResult()
      .getFieldErrors()
      .stream()
      .collect(Collectors.toMap(
        FieldError::getField,
        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value",
        (existing, replacement) -> existing
      ));

    // add the errors to ProblemDetail
    problem.setProperty("errors", errors);

    // answer
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);

  }

  // -- HELPERS
  private ProblemDetail buildProblemDetail(HttpStatus status, String title, String detail) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
    problem.setTitle(title);
    problem.setProperty("timestamp", LocalDateTime.now());
    // more properties
    return problem;
  }
}
