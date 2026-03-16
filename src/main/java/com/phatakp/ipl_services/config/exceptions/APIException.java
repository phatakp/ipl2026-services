package com.phatakp.ipl_services.config.exceptions;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
public class APIException extends RuntimeException {
    private String message;
    private int status = 400;
    private Map<String, String> errors;

    public static APIException unAuthorized(String action) {
        return APIException.builder()
                .message("You do not have permission to "+ action)
                .status(403)
                .build();
    }

    public static APIException notFound(String message) {
        return APIException.builder()
                .message(message)
                .status(404)
                .build();
    }

    public static APIException alreadyPresent(String message) {
        return APIException.builder()
                .message(message)
                .status(400)
                .build();
    }

    public static APIException invalidData(String message) {
        return APIException.builder()
                .message(message)
                .status(400)
                .build();
    }

    public static APIException invalidFileFormat() {
        return APIException.builder()
                .message("File is not csv format")
                .status(400)
                .build();
    }
}
