package de.thorsten.shopping.rest;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import de.thorsten.shopping.model.ShoppingItem;
import java.util.logging.Logger;
import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 *
 */
@Stateless
@Path("/shoppingitems")
public class ShoppingItemEndpoint {

    @Inject
    private Logger log;

    @PersistenceContext(unitName = "shopping-persistence-unit")
    private EntityManager em;

    @Inject
    private Event<ShoppingItem> shoppingItemEventSrc;

    @POST
    @Consumes("application/json")
    public Response create(ShoppingItem entity) {
        try {

            log.info("Registering " + entity.getName());
            em.persist(entity);
            shoppingItemEventSrc.fire(entity);

            return Response.created(UriBuilder.fromResource(ShoppingItemEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();

        }
    }

    @DELETE
    @Path("/{id:[0-9][0-9]*}")
    public Response deleteById(@PathParam("id") Long id) {
        ShoppingItem entity = em.find(ShoppingItem.class, id);
        if (entity == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        em.remove(entity);
        return Response.noContent().build();
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces("application/json")
    public Response findById(@PathParam("id") Long id) {
        TypedQuery<ShoppingItem> findByIdQuery = em.createQuery("SELECT DISTINCT s FROM ShoppingItem s WHERE s.id = :entityId ORDER BY s.id", ShoppingItem.class);
        findByIdQuery.setParameter("entityId", id);
        ShoppingItem entity;
        try {
            entity = findByIdQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        if (entity == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(entity).build();
    }

    @GET
    @Produces("application/json")
    public List<ShoppingItem> listAll() {
        final List<ShoppingItem> results = em.createQuery("SELECT DISTINCT s FROM ShoppingItem s ORDER BY s.id", ShoppingItem.class).getResultList();
        return results;
    }

    @PUT
    @Path("/{id:[0-9][0-9]*}")
    @Consumes("application/json")
    public Response update(ShoppingItem entity) {
        entity = em.merge(entity);
        return Response.noContent().build();
    }
}
