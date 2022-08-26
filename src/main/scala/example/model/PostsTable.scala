package example.model

import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._


class PostsTable(tag: Tag) extends Table[Post](tag, "posts") {

  def id    = column[Long]("id", O.AutoInc )
  def userId    = column[Long]("user_id"  )
  def title = column[String]("title")

  def * = (id, userId, title) <> (Post.tupled, Post.unapply)

}
