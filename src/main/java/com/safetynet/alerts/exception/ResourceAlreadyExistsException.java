package com.safetynet.alerts.exception;

/**
 * Exception thrown when trying to create an already existing resource.
 */
public class ResourceAlreadyExistsException extends Exception {

  private static final long serialVersionUID = 1L;

  public ResourceAlreadyExistsException(String s) {
    super(s);
  }
}
