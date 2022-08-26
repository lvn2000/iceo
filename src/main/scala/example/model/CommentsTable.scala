package example.model

import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._

class CommentsTable(tag: Tag) extends Table[Comment](tag, "comments") {

  def id    = column[Long]("id", O.AutoInc )
  def userId    = column[Long]("user_id"  )
  def postId    = column[Long]("post_id"  )
  def body = column[String]("body")

  def * = (id, userId, postId, body) <> (Comment.tupled, Comment.unapply)
}
