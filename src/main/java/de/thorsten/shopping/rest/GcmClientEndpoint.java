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
import de.thorsten.shopping.model.GcmClient;

/**
 * 
 */
@Stateless
@Path("/gcmclients")
public class GcmClientEndpoint
{
   @PersistenceContext(unitName = "shopping-persistence-unit")
   private EntityManager em;

   @POST
   @Consumes("application/xml")
   public Response create(GcmClient entity)
   {
      em.persist(entity);
      return Response.created(UriBuilder.fromResource(GcmClientEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
   }

   @DELETE
   @Path("/{id:[0-9][0-9]*}")
   public Response deleteById(@PathParam("id") Long id)
   {
      GcmClient entity = em.find(GcmClient.class, id);
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
      TypedQuery<GcmClient> findByIdQuery = em.createQuery("SELECT DISTINCT g FROM GcmClient g WHERE g.id = :entityId ORDER BY g.id", GcmClient.class);
      findByIdQuery.setParameter("entityId", id);
      GcmClient entity;
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
   public List<GcmClient> listAll()
   {
      final List<GcmClient> results = em.createQuery("SELECT DISTINCT g FROM GcmClient g ORDER BY g.id", GcmClient.class).getResultList();
      return results;
   }

   @PUT
   @Path("/{id:[0-9][0-9]*}")
   @Consumes("application/xml")
   public Response update(GcmClient entity)
   {
      entity = em.merge(entity);
      return Response.noContent().build();
   }
}