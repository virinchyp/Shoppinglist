package de.kerron.shoppinglist.server.service;

import javax.jws.WebMethod;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Impl Klasse des Rest-Service
 */
@ApplicationPath("/shoppinglist")
public class ShoppinglistService {

    @WebMethod
    @Produces(MediaType.TEXT_PLAIN)
    @POST
    @Path("/create")
    public Response createEntry() {
        return Response.ok("huhu", MediaType.TEXT_PLAIN).build();
    }

    @WebMethod
    @Produces(MediaType.TEXT_PLAIN)
    @POST
    @Path("/delete")
    public Response deleteEntry() {
        return null;
    }

    @WebMethod
    @Produces(MediaType.TEXT_PLAIN)
    @POST
    @Path("/update")
    public Response updateEntry() {
        return null;
    }
}
