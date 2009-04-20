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
package org.jboss.tattletale.reporting;

import org.jboss.tattletale.Version;
import org.jboss.tattletale.core.Archive;
import org.jboss.tattletale.core.ArchiveTypes;
import org.jboss.tattletale.core.Location;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;

/**
 * Blacklisted report
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 */
public class BlackListedReport extends Report
{
   /** NAME */
   private static final String NAME = "Black listed";

   /** DIRECTORY */
   private static final String DIRECTORY = "blacklisted";

   /**
    * Constructor
    * @param archives The archives
    */
   public BlackListedReport(SortedSet<Archive> archives)
   {
      super(ReportSeverity.ERROR, archives);
   }

   /**
    * Get the name of the report
    * @return The name
    */
   public String getName()
   {
      return NAME;
   }

   /**
    * Get the name of the directory
    * @return The directory
    */
   public String getDirectory()
   {
      return DIRECTORY;
   }

   /**
    * Generate the report(s)
    * @param outputDirectory The top-level output directory
    */
   public void generate(String outputDirectory)
   {
      try
      {
         File output = new File(outputDirectory, DIRECTORY);
         output.mkdirs();

         FileWriter fw = new FileWriter(output.getAbsolutePath() + File.separator +  "index.html");
         BufferedWriter bw = new BufferedWriter(fw, 8192);
         bw.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">" + Dump.NEW_LINE);
         bw.write("<html>" + Dump.NEW_LINE);
         bw.write("<head>" + Dump.NEW_LINE);
         bw.write("  <title>" + Version.FULL_VERSION + ": " + NAME + "</title>" + Dump.NEW_LINE);
         bw.write("  <meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">" + Dump.NEW_LINE);
         bw.write("  <link rel=\"stylesheet\" type=\"text/css\" href=\"../style.css\">" + Dump.NEW_LINE);
         bw.write("</head>" + Dump.NEW_LINE);
         bw.write("<body>" + Dump.NEW_LINE);
         bw.write(Dump.NEW_LINE);

         bw.write("<h1>" + NAME + "</h1>" + Dump.NEW_LINE);

         bw.write("<a href=\"../index.html\">Main</a>" + Dump.NEW_LINE);
         bw.write("<p>" + Dump.NEW_LINE);

         bw.write("<table>" + Dump.NEW_LINE);

         bw.write("  <tr>" + Dump.NEW_LINE);
         bw.write("     <th>Archive</th>" + Dump.NEW_LINE);
         bw.write("     <th>Usage</th>" + Dump.NEW_LINE);
         bw.write("  </tr>" + Dump.NEW_LINE);

         boolean odd = true;

         Iterator<Archive> it = archives.iterator();
         while (it.hasNext())
         {
            Archive a = it.next();

            if (a.getType() == ArchiveTypes.JAR)
            {
               boolean include = false;

               if (a.getBlackListedDependencies() != null && a.getBlackListedDependencies().size() > 0)
               {
                  include = true;
                  status = ReportStatus.RED;
               }

               if (include)
               {
                  if (odd)
                  {
                     bw.write("  <tr class=\"rowodd\">" + Dump.NEW_LINE);
                  }
                  else
                  {
                     bw.write("  <tr class=\"roweven\">" + Dump.NEW_LINE);
                  }
                  bw.write("     <td><a href=\"../jar/" + a.getName() + ".html\">" + a.getName() + "</a></td>" + Dump.NEW_LINE);
                  bw.write("     <td>");

                  bw.write("       <table>" + Dump.NEW_LINE);
            
                  Iterator<Map.Entry<String, SortedSet<String>>> blit = a.getBlackListedDependencies().entrySet().iterator();
                  while (blit.hasNext())
                  {
                     Map.Entry<String, SortedSet<String>> entry = blit.next();

                     String pkg = entry.getKey();
                     SortedSet<String> blpkgs = entry.getValue();

                     bw.write("      <tr>" + Dump.NEW_LINE);
                     
                     bw.write("        <td>" + pkg + "</td>" + Dump.NEW_LINE);

                     bw.write("        <td>");

                     for (String blp : blpkgs)
                     {
                        bw.write(blp + "<br>");
                     }

                     bw.write("</td>" + Dump.NEW_LINE);
            
                     bw.write("      </tr>" + Dump.NEW_LINE);
                  }
               
                  bw.write("       </table>" + Dump.NEW_LINE);

                  bw.write("</td>" + Dump.NEW_LINE);
                  bw.write("  </tr>" + Dump.NEW_LINE);
                  
                  odd = !odd;
               }
            }
         }

         bw.write("</table>" + Dump.NEW_LINE);

         bw.write(Dump.NEW_LINE);
         bw.write("<p>" + Dump.NEW_LINE);
         bw.write("<hr>" + Dump.NEW_LINE);
         bw.write("Generated by: <a href=\"http://www.jboss.org/projects/tattletale\">" + Version.FULL_VERSION + "</a>" + Dump.NEW_LINE);
         bw.write(Dump.NEW_LINE);
         bw.write("</body>" + Dump.NEW_LINE);
         bw.write("</html>" + Dump.NEW_LINE);

         bw.flush();
         bw.close();
      }
      catch (Exception e)
      {
         System.err.println("NoVersionReport: " + e.getMessage());
         e.printStackTrace(System.err);
      }
   }
}