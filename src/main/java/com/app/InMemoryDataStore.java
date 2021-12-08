package com.app;

import com.app.controllers.recordtransaction.model.Transaction;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
/**
 * This is used to store transactions in memory for this demo application.
 * In the real world a more durable external datastore would be used, but this is just a demonstration so this is fine.
 * Be aware that any data stored here will be lost when the application is restarted.
 */
public class InMemoryDataStore {

    private Map<String, List<Transaction>> data = new HashMap<>();

    public void store(String userIdentifier, Transaction transaction){
        if(thisIsTheFirstTransactionForThisUser(userIdentifier)){
            data.put(userIdentifier,new ArrayList<>());
        }
        data.get(userIdentifier).add(transaction);
    }

    private boolean thisIsTheFirstTransactionForThisUser(String userIdentifier){
        return !data.containsKey(userIdentifier);
    }

    public List<Transaction> retrieveAssociatedTransactions(String userIdentifier){
        return data.get(userIdentifier);
    }
}
