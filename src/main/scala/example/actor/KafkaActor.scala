package example.actor

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.pattern.pipe
import akka.util.Timeout
import example.model.{ConsumeMessageRequest, ConsumeMessageResponse, ProduceMessageRequest, ProduceMessageResponse}
import org.apache.kafka.clients.consumer.{ConsumerRecord, KafkaConsumer}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}

import java.util.Properties
import scala.collection.JavaConverters._
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Future, blocking}
import scala.util.Try


class KafkaActor(props: Properties, topic: String) extends Actor with ActorLogging {

  implicit private val system: ActorSystem = context.system
  implicit private val executionContext = system.dispatcher
  implicit private val askTimeout: Timeout = 30.seconds

  val producer = new KafkaProducer[String, String](props)
  val consumer = new KafkaConsumer(props)
  private val topics = List(topic)

  consumer.subscribe(topics.asJava)

  override def postStop() = {
    consumer.close()
    producer.close()
  }

  override def receive: Receive = {
    case prod: ProduceMessageRequest => sendMessageToTopic(prod.messages).getOrElse(Future.successful(Vector.empty[RecordMetadata]))
      .map(ProduceMessageResponse(_)) pipeTo sender()
    case ConsumeMessageRequest => getMessagesFromTopics.getOrElse(Future.successful(Vector.empty[ConsumerRecord[Nothing, Nothing]]))
      .map(ConsumeMessageResponse(_)) pipeTo sender()
    case _ =>
  }

  def sendMessageToTopic(records: Vector[ProducerRecord[String, String]]): Try[Future[Vector[RecordMetadata]]] = {

    Try {
      Future.sequence(
        records.map(record => {
          val metadata = producer.send(record)

          log.info(s"sent record(key=%s value=%s) " +
            "meta(partition=%d, offset=%d)\n",
            record.key(), record.value(),
            metadata.get().partition(),
            metadata.get().offset())
          producer.close()
          Vector(metadata.get())
          scala.concurrent.Future {
            blocking {
              metadata.get()
            }
          }
        }
        ))
    }
  }

  def getMessagesFromTopics: Try[Future[Vector[ConsumerRecord[Nothing, Nothing]]]] = {

    Try {
      val records = consumer.poll(10)

      for (record <- records.asScala) {
        log.info("Topic: " + record.topic() +
          ",Key: " + record.key() +
          ",Value: " + record.value() +
          ", Offset: " + record.offset() +
          ", Partition: " + record.partition())
      }
      Future.successful(records.asScala.toVector)
    }
  }

}

object KafkaActor {
  def props()(props: Properties, topic: String) = Props(classOf[KafkaActor], props, topic)
}