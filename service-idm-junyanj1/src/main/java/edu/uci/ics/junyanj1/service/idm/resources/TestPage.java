package edu.uci.ics.junyanj1.service.idm.resources;

import edu.uci.ics.junyanj1.service.idm.logger.ServiceLogger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("test")
public class TestPage {
    @Path("hello")
    @GET
    public Response hello() {
        ServiceLogger.LOGGER.info("Hello!");
        return Response.status(Response.Status.OK).build();
    }
}
