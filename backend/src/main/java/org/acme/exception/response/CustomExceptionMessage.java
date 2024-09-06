package org.acme.exception.response;

public class CustomExceptionMessage {
    private String message;

    public String getMessage() {
        return message;
    }

    public CustomExceptionMessage(String message) {
        this.message = message;
    }
}
