name := "TDP"

version := "1.0"

//maintainer := "Julian Gonzalez <juliangonzalezdosreis@gmail.com>"

scalaVersion := "2.11.8"

val sparkVersion = "1.6.1"

val repositories = Seq(
  ("confluent" at "http://packages.confluent.io/maven/").withAllowInsecureProtocol(true),
  Resolver.sonatypeRepo("public")
)

libraryDependencies ++= Seq(

  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % "2.0.0",
  "org.apache.spark" %% "spark-mllib" % "2.0.0",

  "org.apache.kafka" % "kafka_2.11" % "0.9.0.0",
  "org.apache.kafka" % "connect-api" % "0.9.0.0",
  //"org.apache.kafka" % "connect-transforms" % "0.9.0.0",
  "org.apache.kafka" % "connect-json" % "0.9.0.0",
  "org.apache.kafka" % "connect-runtime" % "0.9.0.1",
  "org.apache.kafka" % "connect-file" % "0.9.0.0" % "provided",

  "io.confluent" % "kafka-avro-serializer" % "1.0",

  "com.agoda" % "kafka-jdbc-connector_2.11" % "1.2.0",

  "com.typesafe" % "config" % "1.3.1",
  "com.typesafe.akka" % "akka-actor_2.11" % "2.4.2"

)

lazy val root = (project in file(".")).
  settings(resolvers ++= repositories)