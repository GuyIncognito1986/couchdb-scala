resolvers += Resolver.sonatypeRepo("releases")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "latest.integration")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "latest.integration")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "latest.integration")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "latest.integration")