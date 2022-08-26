package example.dao

import example.model._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}


class DaoUser(implicit context: ExecutionContext, db: Database) extends Dao[User] {

  override def findEntitiesForUser(userId: Option[Long] = None, limit: Int, offset: Int) = db.run {

    def getUsers = if (userId.isEmpty) usersTblQ else usersTblQ.filter(_.id === userId.get)

    for {
      users <- getUsers.sortBy(_.email)
        .drop(offset).take(limit)
        .result
      numberOfUsers <- getUsers.length.result
    } yield PaginatedResult[User](
      totalCount = numberOfUsers,
      entities = users.toList,
      hasNextPage = numberOfUsers - (offset + limit) > 0
    )
  }

  override def find(id: Long): Future[Option[User]] = db.run {
    usersTblQ.filter(_.id === id).result
  }.map(_.headOption).mapTo[Option[User]]

  override def update(newVal: User) = db.run {
    usersTblQ.filter(_.id === newVal.id)
      .map(_.email).update(newVal.email)
  }

  override def delete(id: Long) = db.run {
    usersTblQ.filter(_.id === id).delete
  }

  override def insert(newVal: User): Future[Int] = db.run {
    usersTblQ += newVal
  }
}

object DaoUser {
  def apply(implicit context: ExecutionContext, db: Database) = new DaoUser()
}


