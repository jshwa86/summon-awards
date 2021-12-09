package com.app.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Schema(description = "This object is returned any time a request to an operation fails. It contains feedback information detailing why the operation could not be completed.")
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
