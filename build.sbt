name := "TDP"

version := "1.0"

scalaVersion := "2.11.8"

val sparkVersion = "1.6.1"

libraryDependencies ++= Seq(

  "org.apache.spark" %% "spark-core" % sparkVersion,

  "org.apache.spark" %% "spark-streaming" % sparkVersion,

  "org.apache.spark" %% "spark-sql" % "2.0.0",

  "org.apache.spark" %% "spark-mllib" % "2.0.0",

  "org.apache.kafka" % "kafka_2.11" % "1.1.1"
)