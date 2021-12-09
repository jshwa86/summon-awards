package com.app.controllers.payerpointbalancesummary;

import com.app.InMemoryDataStore;
import com.app.controllers.recordtransaction.model.Transaction;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;

import javax.inject.Inject;
import javax.validation.constraints.Pattern;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.summingInt;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("")
public class PayerPointBalanceSummaryController {

    @Inject
    InMemoryDataStore inMemoryDataStore;

    @GET
    @Path("/{userIdentifier}")
    @Operation(summary="Retrieve payer point balance",
            operationId = "Retrieve payer point balance",
            description = "This operation retrieves a summary of the current point balances associated to each payer for a given user.")
    @APIResponses({
            @APIResponse(responseCode = "200",description = "Success",content = @Content(example = "{\"UNILEVER\":200,\"BLUE MOON\":1000,\"DANNON\":1600}")),
            @APIResponse(responseCode = "400",description = "Input validation error",content = @Content(example = "{\"errors\":[\"userIdentifier must be alphanumeric.\"]}"))
    })
    public RestResponse<Map<String, Integer>> retrievePayerPointBalanceSummaryRestOperation(@RestPath @Pattern(regexp = "^[A-Za-z0-9]+$", message = "userIdentifier must be alphanumeric") String userIdentifier){
        return RestResponse.status(RestResponse.Status.OK,
                retrievePayerPointBalanceSummary(userIdentifier));
    }

    public Map<String, Integer> retrievePayerPointBalanceSummary(String userIdentifier){
        List<Transaction> recordedTransactions = inMemoryDataStore.retrieveAssociatedTransactions(userIdentifier);
        if(recordedTransactions == null){
            return new HashMap<>();
        }
        return  recordedTransactions.stream()
                        .collect(
                                //Group results by payer:
                                Collectors.groupingBy(Transaction::getPayer
                                //For each distinct payer, sum up the available points and use that as the result
                                ,summingInt(transaction -> transaction.getUnspentPoints())));
    }


}
