package example.swagger

import akka.http.scaladsl.model.StatusCodes.PermanentRedirect
import com.github.swagger.akka.SwaggerHttpService
import example.MyMain

trait SwaggerHttpWithUiService extends SwaggerHttpService {

  val swaggerUiRoute = {
    pathPrefix(apiDocsPath) {
      val pathInit = removeTrailingSlashIfNecessary(apiDocsPath)
      redirect(s"https://petstore.swagger.io/?url=http://${MyMain.restInterface}:${MyMain.restPort}/$pathInit/swagger.json", PermanentRedirect)
    }
  }

  override val routes = super.routes ~ swaggerUiRoute

  private def removeTrailingSlashIfNecessary(path: String): String =
    if(path.endsWith("/")) removeTrailingSlashIfNecessary(path.substring(0, path.length - 1)) else path

}
