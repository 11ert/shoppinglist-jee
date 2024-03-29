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

import de.thorsten.shopping.model.ShoppingItem;
import java.util.logging.Logger;
import javax.enterprise.event.Event;

/**
 * Backing bean for ShoppingItem entities.
 * <p>
 * This class provides CRUD functionality for all ShoppingItem entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class ShoppingItemBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving ShoppingItem entities
    */

   private Long id;

   @Inject
   private Logger log;

   @Inject
   private Event<ShoppingItem> shoppingItemEventSrc;

   
   public Long getId()
   {
      return this.id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   private ShoppingItem shoppingItem;

   public ShoppingItem getShoppingItem()
   {
      return this.shoppingItem;
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
         this.shoppingItem = this.example;
      }
      else
      {
         this.shoppingItem = findById(getId());
      }
   }

   public ShoppingItem findById(Long id)
   {

      return this.entityManager.find(ShoppingItem.class, id);
   }

   /*
    * Support updating and deleting ShoppingItem entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.shoppingItem);
            log.info("ShoppingItemBEan vor fire");
            this.shoppingItemEventSrc.fire(this.shoppingItem);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.shoppingItem);
            return "view?faces-redirect=true&id=" + this.shoppingItem.getId();
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
         ShoppingItem deletableEntity = findById(getId());

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
    * Support searching ShoppingItem entities with pagination
    */

   private int page;
   private long count;
   private List<ShoppingItem> pageItems;

   private ShoppingItem example = new ShoppingItem();

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

   public ShoppingItem getExample()
   {
      return this.example;
   }

   public void setExample(ShoppingItem example)
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
      Root<ShoppingItem> root = countCriteria.from(ShoppingItem.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<ShoppingItem> criteria = builder.createQuery(ShoppingItem.class);
      root = criteria.from(ShoppingItem.class);
      TypedQuery<ShoppingItem> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(
            getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<ShoppingItem> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      String name = this.example.getName();
      if (name != null && !"".equals(name))
      {
         predicatesList.add(builder.like(root.<String> get("name"), '%' + name + '%'));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<ShoppingItem> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back ShoppingItem entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<ShoppingItem> getAll()
   {

      CriteriaQuery<ShoppingItem> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(ShoppingItem.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(ShoppingItem.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final ShoppingItemBean ejbProxy = this.sessionContext.getBusinessObject(ShoppingItemBean.class);

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

            return String.valueOf(((ShoppingItem) value).getId());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private ShoppingItem add = new ShoppingItem();

   public ShoppingItem getAdd()
   {
      return this.add;
   }

   public ShoppingItem getAdded()
   {
      ShoppingItem added = this.add;
      this.add = new ShoppingItem();
      return added;
   }
}