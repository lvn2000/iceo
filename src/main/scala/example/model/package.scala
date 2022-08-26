package example

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.{ProducerRecord, RecordMetadata}


package object model {


  sealed trait Model

  case class PaginatedResult[T](
                                 totalCount: Int,
                                 entities: List[T],
                                 hasNextPage: Boolean
                               ) extends Model

  case class User(id: Long, email: String) extends Model

  case class Post(id: Long, user_id: Long, title: String) extends Model

  case class Comment(id: Long, user_id: Long, post_id: Long, body: String) extends Model

  //Messages for working with data

  sealed trait DMManager extends Model

  case class Update(newModel: Model) extends DMManager

  case class Insert(newModel: Model) extends DMManager

  case class Delete(id: Long, className: String) extends DMManager // className should be value [Model].getClass.getName

  case class FindOne(id: Long, className: String) extends DMManager // className should be value [Model].getClass.getName

  case class ResultDBManager(countRecord: Option[Int] = None, model: Option[Model] = None, message: Option[String] = None) extends DMManager


  //Kafka messages for actor

  sealed trait KafkaMessage extends Model

  case class ProduceMessageRequest(messages: Vector[ProducerRecord[String, String]]) extends KafkaMessage

  case class ProduceMessageResponse(messages: Vector[RecordMetadata]) extends KafkaMessage

  case object ConsumeMessageRequest extends KafkaMessage

  case class ConsumeMessageResponse(records: Vector[ConsumerRecord[Nothing, Nothing]]) extends KafkaMessage
}
