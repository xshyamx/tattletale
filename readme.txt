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

Command line invocation
-----------------------

java -jar tattletale.jar [-title=\<title>] [-components[=\<regex>]] [-exclude=\<excludes>] <input>[#\<input>]* [\<output-directory>]

* -title puts *\<title>* on report index page
* -components tells tattletale to analyze nested archives (ear, war,
   rar) on a component level; *\<regex>* is used to group class files
   in classes directory by matching their (POSIX/SUS-style) paths,
   for instance, "com|org/\w+"
* -exclude removes archives with names or paths matching
   comma-separated list *\<excludes>* from analysis
* *\<input>* may be a directory containing Java archives or an archive

If *\<output-directory>* is not provided, current directory is used.
