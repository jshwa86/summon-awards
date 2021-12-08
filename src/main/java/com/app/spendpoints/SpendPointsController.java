package com.app.spendpoints;

import com.app.InMemoryDataStore;
import com.app.model.InputValidationFailedResponse;
import com.app.recordtransaction.model.Transaction;
import com.app.spendpoints.model.SpendPointsRequest;
import com.app.spendpoints.model.SpendPointsResponse;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("")
public class SpendPointsController {

    @Inject
    InMemoryDataStore inMemoryDataStore;

    @POST
    @Path("/{userIdentifier}/spend")
    public RestResponse<List<SpendPointsResponse>> retrievePayerPointBalanceSummary(@RestPath @Pattern(regexp = "^[A-Za-z0-9]+$", message = "userIdentifier must be alphanumeric") String userIdentifier,
                                                                                    @Valid SpendPointsRequest spendPointsRequest) {

        List<Transaction> transactionList = inMemoryDataStore.retrieveAssociatedTransactions(userIdentifier);


        /* Apply the following filtering & sorting to the transaction list:
         * - Remove any transactions from our list of transactions where the points have already been spent
         * - Sort ordered from oldest to newest transactions
         */
        List<Transaction> sortedAndFilteredTransactionList = transactionList.stream()
                //Ignore any transactions where the points have already been spent:
                .filter(transaction -> transaction.getUnspentPoints() != 0)
                //Sort the transactions on timestamp:
                .sorted(Comparator.comparing(Transaction::getTimestamp))
                .collect(Collectors.toList());

        //Before we do any detailed work, make sure that the user has enough points to spend for this request in the first place
        Integer totalAmountOfPointsToSpend = spendPointsRequest.getPoints();
        Integer availablePoints = sortedAndFilteredTransactionList.stream().mapToInt(Transaction::getUnspentPoints).sum();
        if(availablePoints < totalAmountOfPointsToSpend){
            throw new UnsupportedOperationException("Request to spend " + totalAmountOfPointsToSpend + " points cannot be fulfilled as there are only "+totalAmountOfPointsToSpend+" points available to spend");
        }

        Integer totalPointsSpentSoFar = 0;
        Map<String,Integer> spentPointsMap = new HashMap<>();
        for(Transaction aTransactionWithPoints : sortedAndFilteredTransactionList){
            if(aTransactionWithPoints.getUnspentPoints() <= (totalAmountOfPointsToSpend - totalPointsSpentSoFar)){
                //The points available from this transaction can be fully consumed:
                updateRunningTotal(spentPointsMap,aTransactionWithPoints.getPayer(),aTransactionWithPoints.getUnspentPoints());
                totalPointsSpentSoFar += aTransactionWithPoints.getUnspentPoints();
                aTransactionWithPoints.setSpentPoints(aTransactionWithPoints.getPoints());
            }else{
                //The points available from this transaction will bring us to the amount we want to pay,
                //but there will be some leftover
                int pointsToSpendFromThisTransaction = totalAmountOfPointsToSpend - totalPointsSpentSoFar;
                totalPointsSpentSoFar = totalAmountOfPointsToSpend;
                updateRunningTotal(spentPointsMap,aTransactionWithPoints.getPayer(),pointsToSpendFromThisTransaction);
                aTransactionWithPoints.setSpentPoints(aTransactionWithPoints.getSpentPoints() + pointsToSpendFromThisTransaction);
            }
            if(totalPointsSpentSoFar == totalAmountOfPointsToSpend){
                //We've accounted for all the points to spend so we are done
                break;
            }
        }

        return RestResponse.ok(
              spentPointsMap.entrySet().stream().map(entry -> new SpendPointsResponse(entry.getKey(),entry.getValue()*-1)).collect(Collectors.toList())
        );

    }

    private void updateRunningTotal(Map<String,Integer> spentPointsMap, String payer, Integer points){
        if(spentPointsMap.containsKey(payer)){
            spentPointsMap.put(payer,spentPointsMap.get(payer)+points);
        }else{
            spentPointsMap.put(payer,points);
        }
    }

}
