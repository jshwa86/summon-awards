package com.app.recordtransaction;

import com.app.InMemoryDataStore;
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

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("")
public class RecordTransactionController {

    @Inject
    InMemoryDataStore inMemoryDataStore;

    @POST
    @Path("/{userIdentifier}/recordTransaction")
    public RestResponse<Void> recordTransaction(@RestPath @Pattern(regexp = "^[A-Za-z0-9]+$", message = "userIdentifier must be alphanumeric") String userIdentifier,
                                                                     @Valid Transaction transaction){
            inMemoryDataStore.store(userIdentifier,transaction);
            return RestResponse.status(RestResponse.Status.CREATED);
    }

}
