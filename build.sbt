name := """gddev-play"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  evolutions,
  "com.twitter" % "hbc-twitter4j" % "2.2.0",
  "org.jooq" % "jool" % "0.9.7",
  javaJpa,
  "org.hibernate" % "hibernate-entitymanager" % "4.3.9.Final"
)

PlayKeys.externalizeResources := false

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
