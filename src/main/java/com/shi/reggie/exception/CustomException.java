package com.shi.reggie.exception;

import java.io.Serial;

public class CustomException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 2438951379764605127L;

    public CustomException() {
    }

    public CustomException(String message) {
        super(message);
    }
}
