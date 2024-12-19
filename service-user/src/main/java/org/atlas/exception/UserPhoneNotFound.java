package org.atlas.exception;

public class UserPhoneNotFound extends RuntimeException {

    public UserPhoneNotFound(String message) {
        super(message);
    }
}
