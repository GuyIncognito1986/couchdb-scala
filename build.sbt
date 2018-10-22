name := "couchdb-scala"

version := "1.0.2"
scalaVersion := "2.12.7"
description := "A purely functional Scala client for CouchDB based on work by ibm guys (Anton Beloglazov and Ermyas Abebe)"
publishMavenStyle := true
licenses := Seq("The Apache Software License, Version 2.0"
  -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))
publishTo := sonatypePublishTo.value
sonatypeProfileName := "io.github.guyincognito1986"
test in assembly := {}
organization := "io.github.guyincognito1986"
import xerial.sbt.Sonatype._
sonatypeProjectHosting := Some(GitHubHosting("guyincognito1986", "couchdb-scala", "Anton.Semzy@gmail.com"))

libraryDependencies ++= Seq(
  "org.scalaz"                  %% "scalaz-core"                 % "7.2.26",
  "org.scalaz"                  %% "scalaz-concurrent"           % "7.2.26",
  "org.scalaz"                  %% "scalaz-effect"               % "7.2.26",
  "org.http4s"                  %% "http4s-core"                 % "0.17.6",
  "org.http4s"                  %% "http4s-client"               % "0.17.6",
  "org.http4s"                  %% "http4s-blaze-client"         % "0.17.6",
  "com.lihaoyi"                 %% "upickle"                     % "0.4.4",
  "com.github.julien-truffaut"  %% "monocle-core"                % "1.3.2",
  "com.github.julien-truffaut"  %% "monocle-macro"               % "1.3.2",
  "org.log4s"                   %% "log4s"                       % "1.4.0",
  "ch.qos.logback"              %  "logback-classic"             % "1.1.7",
  "org.specs2"                  %% "specs2-core"                 % "3.8.9" % "test",
  "org.typelevel"               %% "scalaz-specs2"               % "0.5.2"  % "test",
  "org.scalacheck"              %% "scalacheck"                  % "1.14.0" % "test",
  "org.scalaz"                  %% "scalaz-scalacheck-binding"   % "7.2.26-scalacheck-1.14"  % "test"
)

testFrameworks := Seq(TestFrameworks.Specs2, TestFrameworks.ScalaCheck)

parallelExecution in Test := false

unmanagedSourceDirectories in Compile += baseDirectory.value / "examples" / "src" / "main" / "scala"

initialCommands in console := "import scalaz._, Scalaz._, com.ibm.couchdb._"

initialCommands in console in Test := "import scalaz._, Scalaz._, scalacheck.ScalazProperties._, " +
                                      "scalacheck.ScalazArbitrary._,scalacheck.ScalaCheckBinding._"

logBuffered := false

publishArtifact in Test := false

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case "reference.conf" => MergeStrategy.concat
  case _ => MergeStrategy.last
}

//pomExtra := {
//    <developers>
//      <developer>
//        <id>GuyIncognito1986</id>
//        <name>Anton Semenov</name>
//        <email>anton.semzy@gmail.com</email>
//        <url>http://www.direct-trader.com</url>
//      </developer>
//    </developers>
//}