package example.swagger


import com.github.swagger.akka.SwaggerHttpService
import com.github.swagger.akka.model.Info
import example.MyMain
import example.actor.RestActor


//http://localhost:8009/api-docs/swagger.json
object SwaggerDocService extends SwaggerHttpService {
  override def apiClasses: Set[Class[_]] = Set(classOf[RestActor])

  override val host = s"${MyMain.restInterface}:${MyMain.restPort}" //the url of api, not swagger's json endpoint
  override val apiDocsPath = "api-docs" //where will be the swagger-json endpoint exposed
  override val info = Info(description = "Rest services documentation") //provides license and other description details
  override val unwantedDefinitions = Seq("Function1", "Function1RequestContextFutureRouteResult")
}
