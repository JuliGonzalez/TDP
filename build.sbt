name := "TDP"

version := "1.0.0"
//maintainer := "Julian Gonzalez <juliangonzalezdosreis@gmail.com>"
scalaVersion := "2.11.8"


val sparkVersion = "1.6.1"

resolvers += "confluent" at "https://packages.confluent.io/maven/"
resolvers += Resolver.bintrayRepo("cakesolutions", "maven")


libraryDependencies ++= Dependencies.Compile.kafkaConnect

dependencyOverrides ++= {
  Seq(
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.6.7.1",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.7",
    "com.fasterxml.jackson.core" % "jackson-core" % "2.6.7"
  )
}


mainClass in (Compile, run) := Some("sparkML.UseRandomForestModel")
mainClass in (Compile, packageBin) := Some("sparkML.UseRandomForestModel")
mainClass in assembly := Some("sparkML.UseRandomForestModel")
test in assembly := {}
assemblyOption in assembly := (assemblyOption in assembly).value.copy(cacheUnzip = false)
assemblyJarName in assembly := "randomForest.jar"
