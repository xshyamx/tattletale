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
package org.jboss.tattletale.reporting.classloader;

import java.io.File;
import java.util.SortedSet;

import org.jboss.tattletale.core.Archive;
import org.jboss.tattletale.core.Location;

/**
 * A classloader structure class that represents the JBoss Application Server 5.x
 *
 * @author <a href="mailto:jesper.pedersen@jboss.org">Jesper Pedersen</a>
 */
public class JBossAS5ClassLoaderStructure extends JBossASClassLoaderStructure
{
   /** Constructor */
   public JBossAS5ClassLoaderStructure()
   {
   }

   /**
    * Can one archive see the other
    * @param from The from archive
    * @param to   The to archive
    * @return True if from can see to; otherwise false
    * @see ClassLoaderStructure#isVisible(Archive, Archive)
    */
   public boolean isVisible(Archive from, Archive to)
   {
      final SortedSet<Location> fromLocations = from.getLocations();
      final SortedSet<Location> toLocations = to.getLocations();

      for (Location fromLocation : fromLocations)
      {
         String fromPath = fromLocation.getFilename();

         int fIdx = fromPath.indexOf(from.getName());
         String fp = fromPath.substring(0, fIdx);
         fp = stripPrefix(fp);

         if (!fp.startsWith("docs"))
         {
            for (Location toLocation : toLocations)
            {
               String toPath = toLocation.getFilename();

               int tIdx = toPath.indexOf(to.getName());
               String tp = toPath.substring(0, tIdx);
               tp = stripPrefix(tp);

               // Same directory
               if (fp.equals(tp))
               {
                  return true;
               }

               // bin and client can only see same directory
               if (!fp.startsWith("bin") && !fp.startsWith("client"))
               {
                  // Top-level bin, lib and common is always visible
                  if (tp.startsWith("bin") || tp.startsWith("lib") || tp.startsWith("common"))
                  {
                     return true;
                  }

                  if (fp.startsWith("lib") || fp.startsWith("common"))
                  {
                     // A sub-directory can see higher level or bin
                     if (fp.startsWith(tp) || tp.startsWith("bin"))
                     {
                        return true;
                     }
                  }
                  else
                  {
                     // Exclude client from target
                     if (!tp.startsWith("client"))
                     {
                        // A sub-directory can see higher level
                        if (fp.startsWith(tp))
                        {
                           return true;
                        }

                        // server/xxx/lib directories can only see same directory at this point
                        if (!fp.endsWith("lib" + File.separator))
                        {
                           int deploy = fp.indexOf("deploy");
                           int deployers = fp.indexOf("deployers");

                           // server/xxx/deploy
                           if (deploy != -1 && deployers == -1)
                           {
                              String config = fp.substring(0, deploy);

                              // server/xxx/lib
                              if (tp.equals(config + "lib" + File.separator))
                              {
                                 return true;
                              }

                              // server/xxx/deployers
                              if (tp.startsWith(config + "deployers" + File.separator))
                              {
                                 return true;
                              }
                           }
                           else if (deployers != -1)
                           {
                              String config = fp.substring(0, deployers);

                              // server/xxx/lib
                              if (tp.equals(config + "lib" + File.separator))
                              {
                                 return true;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return false;
   }

   /**
    * Strip prefix
    *
    * @param input The input string
    * @return The result
    */
   private String stripPrefix(String input)
   {
      int idx = input.indexOf("bin");
      if (idx != -1)
      {
         return input.substring(idx);
      }

      idx = input.indexOf("client");
      if (idx != -1)
      {
         return input.substring(idx);
      }

      idx = input.indexOf("common");
      if (idx != -1)
      {
         return input.substring(idx);
      }

      idx = input.indexOf("docs");
      if (idx != -1)
      {
         return input.substring(idx);
      }

      idx = input.indexOf("server");
      if (idx != -1)
      {
         return input.substring(idx);
      }

      idx = input.indexOf("lib");
      if (idx != -1)
      {
         return input.substring(idx);
      }

      return input;
   }
}
