package com.safetynet.alerts.exception;

/**
 * Exception thrown when looking for a not found resource.
 */
public class ResourceAlreadyExistsException extends Exception {

  private static final long serialVersionUID = 1L;

  public ResourceAlreadyExistsException(String s) {
    super(s);
  }
}
