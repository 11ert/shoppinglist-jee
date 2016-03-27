package de.thorsten.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.thorsten.shopping.model.GcmClient;

/**
 * Backing bean for GcmClient entities.
 * <p>
 * This class provides CRUD functionality for all GcmClient entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class GcmClientBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving GcmClient entities
    */

   private Long id;

   public Long getId()
   {
      return this.id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   private GcmClient gcmClient;

   public GcmClient getGcmClient()
   {
      return this.gcmClient;
   }

   @Inject
   private Conversation conversation;

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   public String create()
   {

      this.conversation.begin();
      return "create?faces-redirect=true";
   }

   public void retrieve()
   {

      if (FacesContext.getCurrentInstance().isPostback())
      {
         return;
      }

      if (this.conversation.isTransient())
      {
         this.conversation.begin();
      }

      if (this.id == null)
      {
         this.gcmClient = this.example;
      }
      else
      {
         this.gcmClient = findById(getId());
      }
   }

   public GcmClient findById(Long id)
   {

      return this.entityManager.find(GcmClient.class, id);
   }

   /*
    * Support updating and deleting GcmClient entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.gcmClient);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.gcmClient);
            return "view?faces-redirect=true&id=" + this.gcmClient.getId();
         }
      }
      catch (Exception e)
      {
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
         return null;
      }
   }

   public String delete()
   {
      this.conversation.end();

      try
      {
         GcmClient deletableEntity = findById(getId());

         this.entityManager.remove(deletableEntity);
         this.entityManager.flush();
         return "search?faces-redirect=true";
      }
      catch (Exception e)
      {
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
         return null;
      }
   }

   /*
    * Support searching GcmClient entities with pagination
    */

   private int page;
   private long count;
   private List<GcmClient> pageItems;

   private GcmClient example = new GcmClient();

   public int getPage()
   {
      return this.page;
   }

   public void setPage(int page)
   {
      this.page = page;
   }

   public int getPageSize()
   {
      return 10;
   }

   public GcmClient getExample()
   {
      return this.example;
   }

   public void setExample(GcmClient example)
   {
      this.example = example;
   }

   public void search()
   {
      this.page = 0;
   }

   public void paginate()
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

      // Populate this.count

      CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
      Root<GcmClient> root = countCriteria.from(GcmClient.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<GcmClient> criteria = builder.createQuery(GcmClient.class);
      root = criteria.from(GcmClient.class);
      TypedQuery<GcmClient> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(
            getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<GcmClient> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      String regId = this.example.getRegId();
      if (regId != null && !"".equals(regId))
      {
         predicatesList.add(builder.like(root.<String> get("regId"), '%' + regId + '%'));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<GcmClient> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back GcmClient entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<GcmClient> getAll()
   {

      CriteriaQuery<GcmClient> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(GcmClient.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(GcmClient.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final GcmClientBean ejbProxy = this.sessionContext.getBusinessObject(GcmClientBean.class);

      return new Converter()
      {

         @Override
         public Object getAsObject(FacesContext context,
               UIComponent component, String value)
         {

            return ejbProxy.findById(Long.valueOf(value));
         }

         @Override
         public String getAsString(FacesContext context,
               UIComponent component, Object value)
         {

            if (value == null)
            {
               return "";
            }

            return String.valueOf(((GcmClient) value).getId());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private GcmClient add = new GcmClient();

   public GcmClient getAdd()
   {
      return this.add;
   }

   public GcmClient getAdded()
   {
      GcmClient added = this.add;
      this.add = new GcmClient();
      return added;
   }
}