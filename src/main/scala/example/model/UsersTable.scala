package example.model

import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._


class UsersTable(tag: Tag) extends Table[User](tag, "users") {

  def id    = column[Long]("id", O.AutoInc )
  def email = column[String]("email")

  def * = (id, email) <> (User.tupled, User.unapply)
}
