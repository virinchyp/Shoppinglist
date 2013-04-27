package de.kerron.shoppinglist.server.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jws.WebMethod;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Impl Klasse des Rest-Service
 */
@Path("/shoppinglist")
public class ShoppinglistService {

    final static Logger LOG = Logger.getLogger(ShoppinglistService.class.getName());

    @WebMethod
    @Produces(MediaType.TEXT_PLAIN)
    @POST
    @Path("/create")
    public Response createEntry(final String id, final String description, final String quantityValue,
            final String quantityUnit, final String list) {
        Response response = null;
        if (null == id || null == description || null == quantityValue || null == quantityUnit || null == list) {
            response = Response.status(Status.BAD_REQUEST).build();
        }
        // TODO: implement me
        response = Response.ok().build();
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info(String.format("Entry with id %s created successfully", id));
        }
        return response;
    }

    @WebMethod
    @Produces(MediaType.TEXT_PLAIN)
    @POST
    @Path("/delete")
    public Response deleteEntry(final String id) {
        Response response = null;
        if (null == id) {
            response = Response.status(Status.BAD_REQUEST).build();
        }
        // TODO: implement me
        response = Response.ok().build();
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info(String.format("Entry with id %s removed successfully", id));
        }
        return response;
    }

    @WebMethod
    @Produces(MediaType.TEXT_PLAIN)
    @POST
    @Path("/update")
    public Response updateEntry(final String id, final String description, final String quantityValue,
            final String quantityUnit) {
        Response response = null;
        if (null == id || null == description || null == quantityValue || null == quantityUnit) {
            response = Response.status(Status.BAD_REQUEST).build();
        }
        // TODO: implement me
        response = Response.ok().build();
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info(String.format("Entry with id %s updated successfully", id));
        }
        return response;
    }

    @Produces(MediaType.TEXT_PLAIN)
    @GET
    public String test() {
        return "blubbi blub blubb";
    }
}
