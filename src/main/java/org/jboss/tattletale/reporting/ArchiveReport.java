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

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.jboss.tattletale.core.Archive;

/**
 * Represents an archive report (JAR, WAR, EAR, ...)
 *
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 */
public abstract class ArchiveReport extends AbstractReport
{
   /** The archive */
   protected final Archive archive;

   /**
    * Constructor
    * @param id       The report id
    * @param severity The severity
    * @param archive  The archive
    */
   protected ArchiveReport(String id, ReportSeverity severity, Archive archive)
   {
      super(id, severity, archive.getName(), id);
      this.archive = archive;
   }

   /**
    * Get the name of the report
    * @return The name
    * @see org.jboss.tattletale.reporting.Report#getName()
    */
   public String getName()
   {
      return archive.getName();
   }

   /**
    * Method join.
    * @param input SortedSet<String>
    * @param joiner String
    * @return String
    */
   protected String join(SortedSet<String> input, String joiner)
   {
      if (null == input)
      {
         return "";
      }
      return join(new ArrayList<String>(input),joiner);
   }

   /**
    * Method join.
    * @param input List<String>
    * @param joiner String
    * @return String
    */
   protected String join(List<String> input, String joiner)
   {
      if (null == input || 0 == input.size())
      {
         return "";
      }
      if (null == joiner)
      {
         joiner = "";
      }
      final StringBuffer list = new StringBuffer();
      for (String m : input)
      {
         list.append(m).append(joiner);
      }
      list.setLength(list.length() - joiner.length());
      return list.toString();
   }
}
