package com.app;

import com.app.model.RecordTransactionResponse;
import com.app.model.Transaction;
import org.jboss.resteasy.reactive.RestResponse;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Controller {

    @Inject
    Validator validator;

    @POST
    public RestResponse<RecordTransactionResponse> recordTransaction(Transaction transaction){
        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        if (violations.isEmpty()) {
            return RestResponse.status(RestResponse.Status.CREATED,RecordTransactionResponse.success());
        }else{
            return RestResponse.status(Response.Status.BAD_REQUEST,
                    RecordTransactionResponse.error(violations.stream()
                                                    .map(transactionConstraintViolation -> transactionConstraintViolation.getMessage())
                                                    .collect(Collectors.toList()))
                                                    );
        }

    }

}
