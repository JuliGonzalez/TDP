name := "TDP"

version := "1.0.0"
//maintainer := "Julian Gonzalez <juliangonzalezdosreis@gmail.com>"
scalaVersion := "2.11.8"

val sparkVersion = "1.6.1"

resolvers += "confluent" at "https://packages.confluent.io/maven/"

libraryDependencies ++= Dependencies.Compile.kafkaConnect