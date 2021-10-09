package edu.uci.ics.junyanj1.service.movies.resources;

import edu.uci.ics.junyanj1.service.movies.core.MovieRecords;
import edu.uci.ics.junyanj1.service.movies.logger.ServiceLogger;
import edu.uci.ics.junyanj1.service.movies.models.DeleteResponseModel;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("delete")
public class DeletePage {
    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRequest(@Context HttpHeaders headers,
                                  @PathParam("id") String id) {
        ServiceLogger.LOGGER.info("Received request to delete movie record.");

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        ServiceLogger.LOGGER.info("email: " + email);
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);
        ServiceLogger.LOGGER.info("transactionID: " + transactionID);
        ServiceLogger.LOGGER.info("id: " + id);

        DeleteResponseModel responseModel;

        int privilege_result = MovieRecords.checkPrivilegeFromIDM(email,3);
        if (privilege_result < 0){
            responseModel = DeleteResponseModel.deleteResponseModelFactory(-1);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
        } else if (privilege_result == 0) {
            responseModel = DeleteResponseModel.deleteResponseModelFactory(141);
            return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
        } else {
            Boolean deleted = MovieRecords.deleteMovieByIdFromDb(id);
            if (deleted == null) {
                responseModel = DeleteResponseModel.deleteResponseModelFactory(241);
                return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
            } else if (deleted) {
                responseModel = DeleteResponseModel.deleteResponseModelFactory(240);
                return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
            } else {
                responseModel = DeleteResponseModel.deleteResponseModelFactory(242);
                return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
            }
        }
    }
}
