/*s
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.thorsten.shopping.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author thorsten.elfert@gmail.com
 */
@Entity
@XmlRootElement
@Table(uniqueConstraints=@UniqueConstraint(columnNames="REGID"))
public class GcmClient implements Serializable
{
    
  private static final long serialVersionUID = 1L;
   
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private Long id;
   
   @NotNull
   private String regId;


   @Override
   public int hashCode()
   {
      int hash = 7;
      hash = 53 * hash + Objects.hashCode(this.regId);
      return hash;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
      {
         return true;
      }
      if (obj == null)
      {
         return false;
      }
      if (getClass() != obj.getClass())
      {
         return false;
      }
      final GcmClient other = (GcmClient) obj;
      if (!Objects.equals(this.regId, other.regId))
      {
         return false;
      }
      return true;
   }

   @Override
   public String toString()
   {
      return "GcmClient{" + "id=" + id + ", regId=" + regId + '}';
   }

 
   public String getRegId()
   {
      return regId;
   }

   public void setRegId(String regId)
   {
      this.regId = regId;
   }

   public Long getId()
   {
      return id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

}
