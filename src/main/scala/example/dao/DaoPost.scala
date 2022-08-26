package example.dao

import example.model._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}


class DaoPost(implicit context: ExecutionContext, db: Database) extends Dao[Post] {

  override def findEntitiesForUser(userId: Option[Long], limit: Int, offset: Int) = db.run {

    def getPosts = if (userId.isEmpty) postsTblQ else postsTblQ.filter(_.id === userId.get)

    for {
      posts <- getPosts.sortBy(_.title)
        .drop(offset).take(limit)
        .result
      numberOfPosts <- getPosts.length.result
    } yield PaginatedResult[Post](
      totalCount = numberOfPosts,
      entities = posts.toList,
      hasNextPage = numberOfPosts - (offset + limit) > 0
    )
  }

  override def find(id: Long): Future[Option[Post]] = db.run {
    postsTblQ.filter(_.id === id).result
  }.map(_.headOption).mapTo[Option[Post]]

  override def update(newVal: Post): Future[Int] = db.run {
    postsTblQ.filter(_.id === newVal.id)
      .map(p => (p.userId, p.title)).update(newVal.user_id, newVal.title)
  }

  override def delete(id: Long): Future[Int] = db.run {
    postsTblQ.filter(_.id === id).delete
  }

  override def insert(newVal: Post): Future[Int] = db.run {
    postsTblQ += newVal
  }
}

object DaoPost {
  def apply(implicit context: ExecutionContext, db: Database) = new DaoPost()
}
