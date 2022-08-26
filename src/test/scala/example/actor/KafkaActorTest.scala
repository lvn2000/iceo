package example.actor


import example.model.{ConsumeMessageRequest, ConsumeMessageResponse, ProduceMessageRequest}
import example.{Hello, TestInit}
import org.apache.kafka.clients.producer.ProducerRecord
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.slf4j.LoggerFactory
import akka.pattern.{ask, pipe}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class KafkaActorTest extends AnyFlatSpec with Matchers with TestInit {

  private val logger = LoggerFactory.getLogger(this.getClass)

  val testKey = "testKey"
  val testValue = "testValue"

  val kafkaTestActor = system.actorOf(KafkaActor.props()(kafkaProps, kafkaTopic), "kafka-test-actor")

  "Sending of message operation into topic" should "return" in {
    val result = Await.result((kafkaTestActor ? ProduceMessageRequest(messages = Vector(new ProducerRecord(kafkaTopic, 1, testKey, testValue)))), Duration.Inf)
    Hello.greeting shouldEqual "hello"
  }

  "Receiving messages operation from topic" should "return" in {
    val result = Await.result(kafkaTestActor ? ConsumeMessageRequest, Duration.Inf)
    result.isInstanceOf[ConsumeMessageResponse] shouldEqual true
  }



}
