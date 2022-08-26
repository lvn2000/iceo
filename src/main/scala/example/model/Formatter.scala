package example.model

import spray.json.{RootJsonFormat, _}

object Formatter extends DefaultJsonProtocol {

  implicit val userFormat = jsonFormat2(User.apply)
  implicit val postFormat = jsonFormat3(Post.apply)
  implicit val commentFormat = jsonFormat4(Comment.apply)

  implicit object paginatedFormatUser extends RootJsonFormat[PaginatedResult[User]] {
    def write(paginated: PaginatedResult[User]): JsValue = {
      JsObject("totalCount" -> JsNumber(paginated.totalCount),
        "entities" -> JsArray(paginated.entities.map(_.toJson).toVector),
        "hasNextPage" -> JsBoolean(paginated.hasNextPage)
      )
    }

    override def read(json: JsValue): PaginatedResult[User] = read(json)
  }

  implicit object paginatedFormatPost extends RootJsonFormat[PaginatedResult[Post]] {
    def write(paginated: PaginatedResult[Post]): JsValue = {
      JsObject("totalCount" -> JsNumber(paginated.totalCount),
        "entities" -> JsArray(paginated.entities.map(_.toJson).toVector),
        "hasNextPage" -> JsBoolean(paginated.hasNextPage)
      )
    }

    override def read(json: JsValue): PaginatedResult[Post] = read(json)
  }

  implicit object paginatedFormatComment extends RootJsonFormat[PaginatedResult[Comment]] {
    def write(paginated: PaginatedResult[Comment]): JsValue = {
      JsObject("totalCount" -> JsNumber(paginated.totalCount),
        "entities" -> JsArray(paginated.entities.map(_.toJson).toVector),
        "hasNextPage" -> JsBoolean(paginated.hasNextPage)
      )
    }

    override def read(json: JsValue): PaginatedResult[Comment] = read(json)
  }


}
