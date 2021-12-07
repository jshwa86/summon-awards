package com.app.model;

import java.util.List;

public class InputValidationFailedResponse {

    private List<String> errors;

    private InputValidationFailedResponse() {}

    public InputValidationFailedResponse(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

}
