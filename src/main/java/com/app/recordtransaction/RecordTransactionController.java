package com.app.recordtransaction;

import com.app.InMemoryDataStore;
import com.app.payerpointbalancesummary.PayerPointBalanceSummaryController;
import com.app.recordtransaction.model.Transaction;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("")
public class RecordTransactionController {

    @Inject
    InMemoryDataStore inMemoryDataStore;
    @Inject
    PayerPointBalanceSummaryController pointBalanceSummaryController;

    @POST
    @Path("/{userIdentifier}/recordTransaction")
    public RestResponse<Void> recordTransaction(@RestPath @Pattern(regexp = "^[A-Za-z0-9]+$", message = "userIdentifier must be alphanumeric") String userIdentifier,
                                                                     @Valid Transaction transaction){
            // If the request wants to add a transaction with negative points, we need to make sure
            // that the payer this transaction is tied to already had enough points to cover the negative points.
            // I.e. if the payer has 200 points right now and we are trying to add -300 points, that isn't valid because
            // after the operation the payer would have a negative point balance, which is not allowed
            if(transaction.getPoints() < 0){
                Map<String,Integer> payerPointBalanceSummary = pointBalanceSummaryController.retrievePayerPointBalanceSummary(userIdentifier);
                if(
                        //If the payer doesn't have any balance yet...
                        !payerPointBalanceSummary.containsKey(transaction.getPayer())
                        ||
                        //If the payer's point balance after adding this transaction would be less than zero...
                        (payerPointBalanceSummary.get(transaction.getPayer()) - transaction.getSpentPoints() < 0)
                ){
                    throw new UnsupportedOperationException("Cannot add transaction because it would cause the payer's point balance to become negative.");
                }

            }

            inMemoryDataStore.store(userIdentifier,transaction);
            return RestResponse.status(RestResponse.Status.CREATED);
    }

}
