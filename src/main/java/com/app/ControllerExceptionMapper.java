package com.app;

import com.app.model.InputValidationFailedResponse;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.stream.Collectors;

public class ControllerExceptionMapper {

    /*
     * This method catches any exceptions thrown as a result of input validation rules not being met,
     * and reformats the response into a 'InputValidationFailedResponse' to communicate back to the caller
     * what was wrong
     */
    @ServerExceptionMapper
    public RestResponse<InputValidationFailedResponse> mapException(ConstraintViolationException constraintViolationException) {
        return RestResponse.status(Response.Status.BAD_REQUEST,
                new InputValidationFailedResponse(constraintViolationException.getConstraintViolations().stream()
                        .map(transactionConstraintViolation -> transactionConstraintViolation.getMessage())
                        .collect(Collectors.toList()))
        );
    }

    /*
     * This method catches any 'UnsupportedOperationException' that are thrown when a controller executes.
     * This is used to abort operations when illegal conditions are detected, such as requests to spend more points
     * than the user has.
     */
    @ServerExceptionMapper
    public RestResponse<InputValidationFailedResponse> mapException(UnsupportedOperationException unsupportedOperationException) {
        return RestResponse.status(Response.Status.BAD_REQUEST,
                new InputValidationFailedResponse(Collections.singletonList(unsupportedOperationException.getMessage()))
        );
    }

}
