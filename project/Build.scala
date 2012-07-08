import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

  val appName = "blog"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "net.debasishg" % "redisclient_2.9.1" % "2.5",
    "com.novus" %% "salat-core" % "0.0.8-SNAPSHOT",
    "com.google.inject" % "guice" % "3.0",
    "com.tristanhunt" %% "knockoff" % "0.8.0-16"
  
  )

  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    resolvers ++= Seq(
      "Maven repository" at "http://morphia.googlecode.com/svn/mavenrepo/",
      "MongoDb Java Driver Repository" at "http://repo1.maven.org/maven2/org/mongodb/mongo-java-driver/",
      "repo.novus rels" at "http://repo.novus.com/releases/",
      "repo.novus snaps" at "http://repo.novus.com/snapshots/"))

}
