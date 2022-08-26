import sbt._

object Dependencies {

  lazy val test = "org.scalatest" %% "scalatest" % "3.2.10" % "test->compile"

  object akkaHttp {
    val version = "10.2.9"
    val logbackVersion = "1.2.10"
    val akkaStreamVersion = "2.6.19"
    lazy val http = "com.typesafe.akka" %% "akka-http" % version
    lazy val sprayJSon = "com.typesafe.akka" %% "akka-http-spray-json" % version
    lazy val logBack = "ch.qos.logback" % "logback-classic" % logbackVersion
    lazy val actorTyped = "com.typesafe.akka" %% "akka-actor-typed" % akkaStreamVersion
    lazy val stream = "com.typesafe.akka" %% "akka-stream" % akkaStreamVersion
    lazy val actorTestKitTyped = "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaStreamVersion % Test
    lazy val httpTestKit = "com.typesafe.akka" %% "akka-http-testkit" % version % Test
  }

  // https://github.com/aseigneurin/kafka-streams-scala
  object kafka {
    val kafka_streams_version = "0.2.1"
    val kafka_streams_query_version = "0.1.1"
    val kafka_stream = "com.lightbend" %% "kafka-streams-scala" % kafka_streams_version
    val kafka_stream_query = "com.lightbend" %% "kafka-streams-query" % kafka_streams_query_version
  }
  object slick {
    val version = "3.3.3"
    val slick =  "com.typesafe.slick" %% "slick" % version
    val typeSafe = "com.typesafe.slick" %% "slick-hikaricp" % version
    val nop = "org.slf4j" % "slf4j-nop" % "1.7.36"
    val postgreSql = "org.postgresql" % "postgresql" % "42.4.0"
  }

  //  val kafkaClient = "org.apache.kafka" % "kafka-clients" % "3.2.0"

  object swagger {
    val version = "2.8.0"
    val akka_http = "com.github.swagger-akka-http" %% "swagger-akka-http" % version
    val scala_module = "com.github.swagger-akka-http" %% "swagger-scala-module" % "2.6.0"
    val rs_api = "jakarta.ws.rs" % "jakarta.ws.rs-api" % "3.1.0"
    val jaxrs2_jakarta = "io.swagger.core.v3" % "swagger-jaxrs2-jakarta" % "2.2.0"
  }

}

