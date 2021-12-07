package com.app;

import com.app.model.InputValidationFailedResponse;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
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

}
