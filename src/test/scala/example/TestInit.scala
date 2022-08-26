package example

import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import example.actor.ActorSystemTest
import org.apache.kafka.clients.consumer.ConsumerConfig
import slick.jdbc.PostgresProfile.api._

import java.util.Properties
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration

trait TestInit {

  implicit val timeToWait = Duration(10, TimeUnit.SECONDS)
  implicit val timeout = Timeout(10, TimeUnit.SECONDS)

  implicit val systemTest = new ActorSystemTest()
  implicit val system = systemTest.system
  implicit val ex = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val testDB = "db.test"
  val config = ConfigFactory.parseResources("defaults.conf")
  implicit val dbSlick = Database.forConfig(path = testDB,  config = config)

  val kafkaProps: Properties = new Properties()

  kafkaProps.put("bootstrap.servers", config.getString("kafka.server"))
  kafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  kafkaProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  kafkaProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer") //ByteArrayDeserializer
  kafkaProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  kafkaProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test")
  kafkaProps.put("acks", "all")

  val kafkaTopic = config.getString("kafka.topic")


}
