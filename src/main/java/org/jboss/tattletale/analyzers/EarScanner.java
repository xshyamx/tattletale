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

package org.jboss.tattletale.analyzers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.jboss.tattletale.core.Archive;
import org.jboss.tattletale.core.EarArchive;
import org.jboss.tattletale.core.Location;
import org.jboss.tattletale.profiles.Profile;

/**
 * Scanner type that will be used to make scan calls on .ear files.
 *
 * @author Navin Surtani
 */
public class EarScanner extends AbstractScanner
{
   /** Field pattern */
   private final String pattern;

   /**
    * Constructor
    * @param pattern select matching entries
    */
   public EarScanner(String pattern) {
      this.pattern = pattern;
   }

   /**
    * Scan a .ear archive
    * @param file -  The file to be scanned.
    * @return the archive
    * @throws IOException - if there is a problem with the file parameter
    * @see org.jboss.tattletale.analyzers.ArchiveScanner#scan(File)
    */
   public Archive scan(File file)
   {
      return this.scan(file, null, null, null);
   }

   /**
    * Scan a .ear archive
    * @param ear         The ear file
    * @param gProvides   The global provides map
    * @param known       The set of known archives
    * @param blacklisted The set of black listed packages
    * @return the archive
    * @throws IOException - if there is a problem with the file parameter
    * @see org.jboss.tattletale.analyzers.ArchiveScanner#scan(File, Map<String,SortedSet<String>>, List<Profile>, Set<String>)
    */
   public Archive scan(File ear, Map<String, SortedSet<String>> gProvides, List<Profile> known,
                       Set<String> blacklisted)
   {
      if (null == ear || !ear.exists())
      {
         return null;
      }

      EarArchive earArchive = null;
      final List<Archive> subArchiveList = new ArrayList<Archive>();
      final ArchiveScanner jarScanner = new JarScanner();
      final ArchiveScanner warScanner = new WarScanner(pattern);
      JarFile earFile = null;
      final String name = ear.getName();
      try
      {
         final String canonicalPath = ear.getCanonicalPath();
         final Extractor xt = new Extractor(ear, pattern);
         xt.extract();
         earFile = xt.getArchive();
         final File extractedDir = xt.getTarget();

         Integer classVersion = null;
         final SortedSet<String> requires = new TreeSet<String>();
         final SortedMap<String, Long> provides = new TreeMap<String, Long>();
         final SortedSet<String> profiles = new TreeSet<String>();
         final SortedMap<String, SortedSet<String>> classDependencies = new TreeMap<String, SortedSet<String>>();
         final SortedMap<String, SortedSet<String>> packageDependencies = new TreeMap<String, SortedSet<String>>();
         final SortedMap<String, SortedSet<String>> blacklistedDependencies = new TreeMap<String, SortedSet<String>>();
         List<String> lSign = null;

         final Enumeration<JarEntry> earEntries = earFile.entries();

         while (earEntries.hasMoreElements())
         {
            JarEntry earEntry = earEntries.nextElement();
            String entryName = earEntry.getName();
            InputStream entryStream = null;

            if (entryName.endsWith(".class"))
            {
               try
               {
                  entryStream = earFile.getInputStream(earEntry);
                  classVersion = scanClasses(entryStream, blacklisted, known, classVersion, provides, requires,
                                             profiles, classDependencies, packageDependencies, blacklistedDependencies);
               }
               catch (Exception openException)
               {
                  openException.printStackTrace();
               }
               finally
               {
                  if (null != entryStream)
                  {
                     entryStream.close();
                  }
               }
            }
            else if (entryName.contains("META-INF") && entryName.endsWith(".SF"))
            {
               InputStream is = null;
               try
               {
                  is = earFile.getInputStream(earEntry);

                  InputStreamReader isr = new InputStreamReader(is);
                  LineNumberReader lnr = new LineNumberReader(isr);

                  if (null == lSign)
                  {
                     lSign = new ArrayList<String>();
                  }

                  String line = lnr.readLine();
                  while (null != line)
                  {
                     lSign.add(line);
                     line = lnr.readLine();
                  }
               }
               catch (Exception ie)
               {
                  // Ignore
               }
               finally
               {
                  try
                  {
                     if (null != is)
                     {
                        is.close();
                     }
                  }
                  catch (IOException ioe)
                  {
                     // Ignore
                  }
               }
            }
            else if (entryName.endsWith(".jar"))
            {
               File jarFile = new File(extractedDir.getCanonicalPath(), entryName);
               Archive jarArchive = jarScanner.scan(jarFile, gProvides, known, blacklisted);
               if (null != jarArchive)
               {
                  subArchiveList.add(jarArchive);
               }
            }
            else if (entryName.endsWith(".war") || entryName.endsWith(".rar"))
            {
               File warFile = new File(extractedDir.getCanonicalPath(), entryName);
               Archive warArchive = warScanner.scan(warFile, gProvides, known, blacklisted);
               if (null != warArchive)
               {
                  subArchiveList.add(warArchive);
               }
            }
         }

         if (0 == provides.size() && 0 == subArchiveList.size())
         {
            return null;
         }

         String version = null;
         List<String> lManifest = null;
         final Manifest manifest = earFile.getManifest();

         if (null != manifest)
         {
            version = super.versionFromManifest(manifest);
            lManifest = super.readManifest(manifest);
         }

         final Location location = new Location(canonicalPath, version);

         // Obtain the class version if it is null. In other words, if there aren't any .class files in a
         // WEB-INF/classes directory. This would get the class version from the first archive in the list of sub
         // archives.
         if (subArchiveList.size() > 0 && null == classVersion)
         {
            classVersion = subArchiveList.get(0).getVersion();
         }
         if (null == classVersion)
         {
            classVersion = Integer.valueOf(0);
         }

         earArchive = new EarArchive(name, classVersion, lManifest, lSign, requires, provides, classDependencies,
                                     packageDependencies, blacklistedDependencies, location, subArchiveList);
         super.addProfilesToArchive(earArchive, profiles);

         for (String provide : provides.keySet())
         {
            if (null != gProvides)
            {
               SortedSet<String> ss = gProvides.get(provide);
               if (null == ss)
               {
                  ss = new TreeSet<String>();
               }
               ss.add(earArchive.getName());
               gProvides.put(provide, ss);
            }
            requires.remove(provide);
         }
      }
      catch (IOException ioe)
      {
         ioe.printStackTrace();
      }
      catch (Exception e)
      {
         System.err.println("Scan: " + e.getMessage());
         e.printStackTrace(System.err);
      }
      finally
      {
         try
         {
            if (null != earFile)
            {
               earFile.close();
            }
         }
         catch (IOException closeException)
         {
            // No op
         }
      }
      return earArchive;
   }
}
