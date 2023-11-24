import java.util.concurrent.CompletionStage;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;

@Path("/test")
public class TestResource {

    @Inject
    @Channel("test")
    Emitter<String> emitter;

    @Inject
    BlobServiceClient blobServiceClient;

    @Path("/{id}")
    @GET
    public CompletionStage<Response> checkExistence(@PathParam("id") String id) {
        // can be reproduced with simple completion stage
        //return (CompletionStage<Response>) CompletableFuture.completedStage(id)CompletableFuture.completedStage(id)

        // or via reactive messaging
        return (CompletionStage<Response>) emitter.send(id)
                .thenApply(msg -> {
                    BlobContainerClient blobContainerClient = blobServiceClient
                            .createBlobContainerIfNotExists("azure-storage-blob");
                    BlobClient blobClient = blobContainerClient.getBlobClient(id);
                    if (blobClient.exists()) {
                        return Response.status(Response.Status.OK).build();
                    }
                    return Response.status(Response.Status.NOT_FOUND).build();
                });
    }

}
