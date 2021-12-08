package com.app.payerpointbalancesummary;

import com.app.InMemoryDataStore;
import com.app.recordtransaction.model.Transaction;
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
                        .collect(Collectors.groupingBy(Transaction::getPayer
                                ,summingInt(transaction -> transaction.getPoints() - transaction.getSpentPoints())));
    }


}
