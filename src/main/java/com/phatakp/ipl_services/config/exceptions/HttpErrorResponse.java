package com.phatakp.ipl_services.config.exceptions;

import lombok.Getter;

import java.util.List;
import java.util.Map;


@Getter
public class HttpErrorResponse {
    private String message;
    private int status;
    private List<Map<String, String>> generalErrors;

    public static HttpErrorResponse of(String message, int status, List<Map<String, String>> generalErrors) {
        HttpErrorResponse response = of(message, status);
        response.generalErrors = generalErrors;
        return response;
    }

    public static HttpErrorResponse of(String message, int status) {
        HttpErrorResponse response = new HttpErrorResponse();
        response.message = message;
        response.status = status;
        return response;
    }
}
