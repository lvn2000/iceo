package example.actor

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.util.Timeout
import com.lightbend.kafka.scala.streams.DefaultSerdes.{longSerde, stringSerde}
import com.lightbend.kafka.scala.streams.ImplicitConversions.{consumedFromSerde, producedFromSerde, serializedFromSerde}
import com.lightbend.kafka.scala.streams.{KTableS, StreamsBuilderS}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.streams.KafkaStreams


import java.util.Properties
import java.util.regex.Pattern
import scala.concurrent.duration.DurationInt


class KafkaStreamActor(props: Properties, topic: String) extends Actor with ActorLogging {

  implicit private val system: ActorSystem = context.system
  implicit private val executionContext = system.dispatcher
  implicit private val askTimeout: Timeout = 30.seconds
  private val topics = List(topic)

  val builder = new StreamsBuilderS
  val textLines = builder.stream[String, String](topic)
  val streams = new KafkaStreams(builder.build, props)


  override def preStart() = {
    log.info("Kafka stream start..")
    streams.start()
  }

  override def postStop() = {
    log.info("Kafka stream stop..")
    streams.close()
  }


  override def receive: Receive = {
    //    case prod: ProduceMessageRequest => sendMessageToTopic(prod.messages).getOrElse(Future.successful(Vector.empty[RecordMetadata]))
    //      .map(ProduceMessageResponse(_)) pipeTo sender()
    //    case ConsumeMessageRequest => getMessagesFromTopics.getOrElse(Future.successful(Vector.empty[ConsumerRecord[Nothing, Nothing]]))
    //      .map(ConsumeMessageResponse(_)) pipeTo sender()
    case _ =>
  }

  def sendMessageToTopic(records: Vector[ProducerRecord[String, String]]) = {

    //    Try {
    //      Future.sequence(
    //        records.map(record => {
    //          val metadata = producer.send(record)
    //
    //          log.info(s"sent record(key=%s value=%s) " +
    //            "meta(partition=%d, offset=%d)\n",
    //            record.key(), record.value(),
    //            metadata.get().partition(),
    //            metadata.get().offset())
    //          producer.close()
    //          Vector(metadata.get())
    //          scala.concurrent.Future {
    //            blocking {
    //              metadata.get()
    //            }
    //          }
    //        }
    //        ))
    //    }
  }

  def getMessagesFromTopics = {


    val pattern = Pattern.compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS)

    val wordCounts: KTableS[String, Long] =
      textLines.flatMapValues(v => pattern.split(v.toLowerCase))
        .groupBy((k, v) => v)
        .count()

    wordCounts.toStream.to(topic)  //, Produced.`with`(stringSerde, longSerde)

    streams.start()


    //    Try {
    //      val records = consumer.poll(10)
    //
    //      for (record <- records.asScala) {
    //        log.info("Topic: " + record.topic() +
    //          ",Key: " + record.key() +
    //          ",Value: " + record.value() +
    //          ", Offset: " + record.offset() +
    //          ", Partition: " + record.partition())
    //      }
    //      Future.successful(records.asScala.toVector)
    //    }
  }

}

object KafkaStreamActor {
  def props()(props: Properties, topic: String) = Props(classOf[KafkaStreamActor], props, topic)
}