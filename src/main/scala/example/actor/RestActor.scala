package example.actor

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.util.Timeout
import example.dao.{DaoComment, DaoPost, DaoUser}
import example.model.Formatter._
import example.model.{Comment, Post, User}
import example.swagger.SwaggerDocService
import io.swagger.v3.oas.annotations.media.{Content, Schema}
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.{Operation, Parameter}
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.{GET, Path, Produces}
import slick.jdbc.PostgresProfile.api._
import spray.json.enrichAny

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

class RestActor(host: String, port: Int)(implicit val materializer: ActorMaterializer, db: Database, execContext: ExecutionContext) extends Actor with ActorLogging {

  implicit private val system = context.system
  implicit private val executionContext = system.dispatcher
  implicit private val askTimeout: Timeout = 30.seconds

  lazy val daoUser = DaoUser(execContext, db)
  lazy val daoPost = DaoPost(execContext, db)
  lazy val daoComment = DaoComment(execContext, db)

  log.info("Start REST controller")

  override def receive: Receive = Actor.emptyBehavior

  private val route = get {
    getUsers ~
      getPosts ~
      getComments ~
      SwaggerDocService.routes
  }

  private val bindingFuture: Future[Http.ServerBinding] = Http().bindAndHandle(route, host, port)
  bindingFuture.map { binding =>
    log.info(s"REST interface bound to ${binding.localAddress}")
  } recover { case ex =>
    log.error(s"REST interface could not bind to $host:$port", ex.getMessage)
  }

  override def postStop() = {
    log.info(s"Unbinding of $host:$port")
    bindingFuture.flatMap(_.unbind())
  }

  @GET
  @Path("users/limit/{limit}/offset/{offset}")
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(method = "GET", summary = "Return list of users", parameters = Array(
    new Parameter(name = "limit", in = ParameterIn.PATH, required = true),
    new Parameter(name = "offset", in = ParameterIn.PATH, required = true)
  ), description = "Return list of all users. Example of url http://0.0.0.0:8009/users/limit/5/offset/1",
    responses = Array(
      new ApiResponse(responseCode = "200", description = "Users response",
        content = Array(new Content(schema = new Schema(implementation = classOf[User])))),
      new ApiResponse(responseCode = "500", description = "Internal server error"))
  )
  def getUsers = {
    path("users" / "limit" / Segment / "offset" / Segment) { (limit, offset) =>
      onSuccess(
        daoUser.findEntitiesForUser(None, limit.toInt, offset.toInt).map(pgs =>
          HttpResponse(
            status = StatusCodes.OK,
            entity = HttpEntity(pgs.toJson.toString)
          )
        )
      ) {
        case resp: HttpResponse =>
          log.info(resp.toString())
          complete(resp)
        case _ => complete(StatusCodes.InternalServerError)
      }
    }
  }

  @GET
  @Path("posts/id_user/{user_id}/limit/{limit}/offset/{offset}")
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(method = "GET", summary = "Return list of posts", parameters = Array(
    new Parameter(name = "user_id", in = ParameterIn.PATH, required = true),
    new Parameter(name = "limit", in = ParameterIn.PATH, required = true),
    new Parameter(name = "offset", in = ParameterIn.PATH, required = true)
  ), description = "Return list of posts for user (using id of user). Example of url http://0.0.0.0:8009/posts/id_user/1/limit/5/offset/1",
    responses = Array(
      new ApiResponse(responseCode = "200", description = "Posts response",
        content = Array(new Content(schema = new Schema(implementation = classOf[Post])))),
      new ApiResponse(responseCode = "500", description = "Internal server error"))
  )
  def getPosts = {
    path("posts" / "id_user" / Segment / "limit" / Segment / "offset" / Segment) { (id_user, limit, offset) =>
      onSuccess(
        daoPost.findEntitiesForUser(Some(id_user.toInt), limit.toInt, offset.toInt).map(pgs =>
          HttpResponse(
            status = StatusCodes.OK,
            entity = HttpEntity(pgs.toJson.toString)
          )
        )
      ) {
        case resp: HttpResponse =>
          log.info(resp.toString())
          complete(resp)
        case _ => complete(StatusCodes.InternalServerError)
      }
    }
  }

  @GET
  @Path("comments/id_user/{user_id}/limit/{limit}/offset/{offset}")
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(method = "GET", summary = "Return list of comments", parameters = Array(
    new Parameter(name = "user_id", in = ParameterIn.PATH, required = true),
    new Parameter(name = "limit", in = ParameterIn.PATH, required = true),
    new Parameter(name = "offset", in = ParameterIn.PATH, required = true)
  ), description = "Return list of comments for user (using id of user). Example of url http://0.0.0.0:8009/comments/id_user/1/limit/5/offset/1",
    responses = Array(
      new ApiResponse(responseCode = "200", description = "Posts response",
        content = Array(new Content(schema = new Schema(implementation = classOf[Comment])))),
      new ApiResponse(responseCode = "500", description = "Internal server error"))
  )
  def getComments = {
    path("comments" / "id_user" / Segment / "limit" / Segment / "offset" / Segment) { (id_user, limit, offset) =>
      onSuccess(
        daoComment.findEntitiesForUser(Some(id_user.toInt), limit.toInt, offset.toInt).map(pgs =>
          HttpResponse(
            status = StatusCodes.OK,
            entity = HttpEntity(pgs.toJson.toString)
          )
        )
      ) {
        case resp: HttpResponse =>
          log.info(resp.toString())
          complete(resp)
        case _ => complete(StatusCodes.InternalServerError)
      }
    }

  }
}

object RestActor {
  def props()(host: String, port: Int)(implicit materializer: ActorMaterializer, db: Database, execContext: ExecutionContext) = Props(classOf[RestActor], host, port, materializer, db, execContext)
}