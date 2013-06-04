Tattletale analyzes Java classes in archives (JAR, WAR, RAR, EAR) and
builds HTML reports that

* Identify runtime (bytecode) dependencies on a class level
* Find missing classes from the classpath
* Spot if a class/package is located in multiple JAR files
* Spot if the same archive is located in different places
* List what each archive requires and provides
* Verify the SerialVersionUID of a class
* Find similar archives that have different version numbers
* Find archives without a (valid) version number
* Find unused archives
* Identify sealed/signed archives
* Locate a class in an archive
* Show the OSGi status of your project
* Identify blacklisted API usage
