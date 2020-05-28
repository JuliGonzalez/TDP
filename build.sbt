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
assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
assemblyJarName in assembly := "randomForest.jar"

assemblyMergeStrategy in assembly := {
  case PathList("org","aopalliance", xs @ _*) => MergeStrategy.last
  case PathList("javax", "inject", xs @ _*) => MergeStrategy.last
  case PathList("javax", "servlet", xs @ _*) => MergeStrategy.last
  case PathList("javax", "activation", xs @ _*) => MergeStrategy.last
  case PathList("org", "apache", xs @ _*) => MergeStrategy.last
  case PathList("com", "google", xs @ _*) => MergeStrategy.last
  case PathList("com", "esotericsoftware", xs @ _*) => MergeStrategy.last
  case PathList("com", "codahale", xs @ _*) => MergeStrategy.last
  case PathList("com", "yammer", xs @ _*) => MergeStrategy.last
  case "about.html" => MergeStrategy.rename
  case "META-INF/ECLIPSEF.RSA" => MergeStrategy.last
  case "META-INF/mailcap" => MergeStrategy.last
  case "META-INF/mimetypes.default" => MergeStrategy.last
  case "plugin.properties" => MergeStrategy.last
  case "log4j.properties" => MergeStrategy.last
  case "git.properties"  => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}