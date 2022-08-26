package example.actor

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.pipe
import akka.stream.ActorMaterializer
import akka.util.Timeout
import example.dao.{DaoComment, DaoPost, DaoUser}
import example.model._
//import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

class DBManagerActor(implicit val materializer: ActorMaterializer, db: Database, execContext: ExecutionContext) extends Actor with ActorLogging {

  implicit private val system = context.system
  implicit private val executionContext = system.dispatcher
  implicit private val askTimeout: Timeout = 30.seconds

  lazy val daoUser = DaoUser(execContext, db)
  lazy val daoPost = DaoPost(execContext, db)
  lazy val daoComment = DaoComment(execContext, db)

  val usrNameClass = User.getClass.getName
  val postNameClass = Post.getClass.getName
  val commentNameClass = Comment.getClass.getName

  override def postStop() = {
  }

  override def receive: Receive = {
    case u: Update =>
      val res = u.newModel match {
        case uu: User => daoUser.update(uu).map(cnt => ResultDBManager(countRecord = Some(cnt)))
        case pp: Post => daoPost.update(pp).map(cnt => ResultDBManager(countRecord = Some(cnt)))
        case cu: Comment => daoComment.update(cu).map(cnt => ResultDBManager(countRecord = Some(cnt)))
        case _ => Future.successful(ResultDBManager(message = Some("Unknown type of model data")))
      }
      res pipeTo sender()
    case i: Insert =>
      val res = i.newModel match {
        case uu: User => daoUser.insert(uu).map(cnt => ResultDBManager(countRecord = Some(cnt)))
        case pp: Post => daoPost.insert(pp).map(cnt => ResultDBManager(countRecord = Some(cnt)))
        case cu: Comment => daoComment.insert(cu).map(cnt => ResultDBManager(countRecord = Some(cnt)))
        case _ => Future.successful(ResultDBManager(message = Some("Unknown type of model data")))
      }
      res pipeTo sender()
    case d: Delete =>
      val res = d.className match {
        case `usrNameClass` => daoUser.delete(d.id).map(cnt => ResultDBManager(countRecord = Some(cnt)))
        case `postNameClass` => daoPost.delete(d.id).map(cnt => ResultDBManager(countRecord = Some(cnt)))
        case `commentNameClass` => daoComment.delete(d.id).map(cnt => ResultDBManager(countRecord = Some(cnt)))
        case _ => Future.successful(ResultDBManager(message = Some("Unknown type of model data")))
      }
      res pipeTo sender()
    case f: FindOne =>
      val res = f.className match {
        case `usrNameClass` => daoUser.find(f.id).map(m => ResultDBManager(model = m))
        case `postNameClass` => daoPost.find(f.id).map(m => ResultDBManager(model = m))
        case `commentNameClass` => daoComment.find(f.id).map(m => ResultDBManager(model = m))
        case _ => Future.successful(ResultDBManager(message = Some("Unknown type of model data")))
      }
      res pipeTo sender()

    case _ => ResultDBManager(message = Some("Unknown message"))
  }

}

object DBManagerActor {
  def props()(implicit materializer: ActorMaterializer, db: Database, execContext: ExecutionContext) = Props(classOf[DBManagerActor], materializer, db, execContext)
}