package com.safetynet.alerts.exception;

/**
 * Exception thrown when looking for a not found resource.
 */
public class ResourceNotFoundException extends Exception {

  private static final long serialVersionUID = 1L;

  public ResourceNotFoundException(String s) {
    super(s);
  }
}
