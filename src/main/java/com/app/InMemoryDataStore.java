package com.app;

import com.app.recordtransaction.model.Transaction;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class InMemoryDataStore {

    //TODO Should make this an interface so easier to replace

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
