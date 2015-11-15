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

/**
 * 
 */
@Stateless
@Path("/shoppingitems")
public class ShoppingItemEndpoint
{
   @PersistenceContext(unitName = "shopping-persistence-unit")
   private EntityManager em;

   @POST
   @Consumes("application/xml")
   public Response create(ShoppingItem entity)
   {
      em.persist(entity);
      return Response.created(UriBuilder.fromResource(ShoppingItemEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
   }

   @DELETE
   @Path("/{id:[0-9][0-9]*}")
   public Response deleteById(@PathParam("id") Long id)
   {
      ShoppingItem entity = em.find(ShoppingItem.class, id);
      if (entity == null)
      {
         return Response.status(Status.NOT_FOUND).build();
      }
      em.remove(entity);
      return Response.noContent().build();
   }

   @GET
   @Path("/{id:[0-9][0-9]*}")
   @Produces("application/xml")
   public Response findById(@PathParam("id") Long id)
   {
      TypedQuery<ShoppingItem> findByIdQuery = em.createQuery("SELECT DISTINCT s FROM ShoppingItem s WHERE s.id = :entityId ORDER BY s.id", ShoppingItem.class);
      findByIdQuery.setParameter("entityId", id);
      ShoppingItem entity;
      try
      {
         entity = findByIdQuery.getSingleResult();
      }
      catch (NoResultException nre)
      {
         entity = null;
      }
      if (entity == null)
      {
         return Response.status(Status.NOT_FOUND).build();
      }
      return Response.ok(entity).build();
   }

   @GET
   @Produces("application/xml")
   public List<ShoppingItem> listAll()
   {
      final List<ShoppingItem> results = em.createQuery("SELECT DISTINCT s FROM ShoppingItem s ORDER BY s.id", ShoppingItem.class).getResultList();
      return results;
   }

   @PUT
   @Path("/{id:[0-9][0-9]*}")
   @Consumes("application/xml")
   public Response update(ShoppingItem entity)
   {
      entity = em.merge(entity);
      return Response.noContent().build();
   }
}