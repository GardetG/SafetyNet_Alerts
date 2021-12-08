package com.safetynet.alerts.controller;

import com.safetynet.alerts.exception.ResourceAlreadyExistsException;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Class handling exceptions thrown by Service and HTTP response.
 */
@ControllerAdvice
public class GlobalControllerExceptionHandler {

  private static final Logger LOGGER = LoggerFactory
          .getLogger(GlobalControllerExceptionHandler.class);

  /**
   * Handle ConstraintViolationException thrown when invalid parameter are
   * provided.
   * 

   * @param ex instance of the exception
   * @return HTTP 400 response with informations on invalid parameters
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<List<String>> handleParameterValidationExceptions(
          ConstraintViolationException ex) {
    List<String> errors = new ArrayList<>();
    ex.getConstraintViolations().forEach(error -> {
      String errorMessage = error.getMessage();
      errors.add(errorMessage);
    });
    LOGGER.info("Response : 400 invalid parameter {}", errors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
  }

  /**
   * Handle MethodArgumentNotValidException thrown when trying to deserialize an
   * invalid object.
   * 

   * @param ex instance of the exception
   * @return HTTP 422 response with informations on invalid fields
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(
          MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    LOGGER.info("Response : 422 invalid DTO");
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errors);
  }

  /**
   * Handle ResourceNotFoundException thrown when the resource can't be found.
   * 

   * @param ex instance of the exception
   * @return HTTP 404 response with informations on resource
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<String> handleNotFoundExceptions(ResourceNotFoundException ex) {
    String error = ex.getMessage();
    LOGGER.info("Response : 404 {}", error);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  /**
   * Handle ResourceNotFoundException thrown when the resource to create already
   * exists.
   * 

   * @param ex instance of the exception
   * @return HTTP 409 response with informations on resource
   */
  @ExceptionHandler(ResourceAlreadyExistsException.class)
  public ResponseEntity<String> handleAlreadyExistsExceptions(ResourceAlreadyExistsException ex) {
    String error = ex.getMessage();
    LOGGER.info("Response: 409 {}", error);
    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }
}
