package com.app.model;

import java.util.List;

public class RecordTransactionResponse {

    private boolean success;
    private List<String> errors;

    private RecordTransactionResponse(){}

    public static RecordTransactionResponse success(){
        RecordTransactionResponse recordTransactionResponse = new RecordTransactionResponse();
            recordTransactionResponse.setSuccess(true);
            recordTransactionResponse.setErrors(null);
        return recordTransactionResponse;
    }

    public static RecordTransactionResponse error(List<String> errors){
        RecordTransactionResponse recordTransactionResponse = new RecordTransactionResponse();
            recordTransactionResponse.setSuccess(false);
            recordTransactionResponse.setErrors(errors);
        return recordTransactionResponse;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
