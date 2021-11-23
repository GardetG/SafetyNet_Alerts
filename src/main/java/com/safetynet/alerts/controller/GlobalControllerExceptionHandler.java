package com.safetynet.alerts.controller;

import com.safetynet.alerts.exception.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Class handling exceptions throw in Controllers and HTTP response.
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
  public ResponseEntity<List<String>> handleValidation2Exceptions(ConstraintViolationException ex) {
    List<String> errors = new ArrayList<>();
    ex.getConstraintViolations().forEach(error -> {
      String errorMessage = error.getMessage();
      errors.add(errorMessage);
    });
    LOGGER.error("Can't process input : invalid parameter {}", errors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
  }

  /**
   * Handle ResourceNotFoundException thrown when the looked for resource can't
   * be found.
   * 

   * @param ex instance of the exception
   * @return HTTP 400 response with informations on invalid parameters
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<String> handleValidation2Exceptions(ResourceNotFoundException ex) {
    String error =  ex.getMessage();
    LOGGER.error("Can't find the resource : {}", error);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }
}
