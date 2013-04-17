/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.tattletale.core;

import java.io.Serializable;

/**
 * Location
 *
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 */
public class Location implements Serializable, Comparable<Location>
{
   /** SerialVersionUID */
   static final long serialVersionUID = 5772882935036035107L;

   /** The filename */
   private String filename;

   /** Version */
   private String version;

   /**
    * Constructor
    *
    * @param filename The filename
    * @param version  The version
    */
   public Location(String filename, String version)
   {
      this.filename = filename;
      this.version = version;
   }

   /**
    * Get the filename
    *
    * @return The value
    */
   public String getFilename()
   {
      return filename;
   }

   /**
    * Get the version
    *
    * @return The value
    */
   public String getVersion()
   {
      return version;
   }

   /**
    * Comparable
    *
    * @param l The other location
    * @return The compareTo value
    */
   public int compareTo(Location l)
   {
      int result = filename.compareTo(l.getFilename());

      if (result == 0)
      {
         result = (version != null ? version.compareTo(l.getVersion()) : 0);
      }

      return result;
   }

   /**
    * Equals
    *
    * @param obj The other object
    * @return True if equals; otherwise false
    */
   public boolean equals(Object obj)
   {
      if (obj == null || !(obj instanceof Location))
      {
         return false;
      }

      Location l = (Location) obj;

      return filename.equals(l.getFilename()) && (version != null ? version.equals(l.getVersion()) : true);
   }

   /**
    * Hash code
    *
    * @return The hash code
    */
   public int hashCode()
   {
      int hash = 7;

      hash += 31 * filename.hashCode();

      if (version != null)
      {
         hash += 31 * version.hashCode();
      }

      return hash;
   }

   /**
    * String representation
    *
    * @return The string
    */
   public String toString()
   {
      StringBuffer sb = new StringBuffer();
      String newline = System.getProperty("line.separator");

      sb.append(getClass().getName()).append("(").append(newline);
      sb.append("filename=").append(filename).append(newline);
      sb.append("version=").append(version).append(newline).append(")");

      return sb.toString();
   }
}
