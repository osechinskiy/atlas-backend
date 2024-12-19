package org.atlas.rest.exception;

public class UserUnAuthorized extends RuntimeException {
  public UserUnAuthorized(String message) {
    super(message);
  }
}
