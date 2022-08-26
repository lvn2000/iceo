package example.dao

import example.model.{Comment, PaginatedResult}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}


class DaoComment(implicit context: ExecutionContext, db: Database) extends Dao[Comment] {

  override def findEntitiesForUser(userId: Option[Long], limit: Int, offset: Int) = db.run {

    def getComments = if (userId.isEmpty) commentsTblQ else commentsTblQ.filter(_.userId === userId)

    for {
      comments <- getComments.sortBy(_.postId)
        .drop(offset).take(limit)
        .result
      numberOfComments <- getComments.length.result
    } yield PaginatedResult[Comment](
      totalCount = numberOfComments,
      entities = comments.toList,
      hasNextPage = numberOfComments - (offset + limit) > 0
    )
  }

  override def find(id: Long): Future[Option[Comment]] = db.run {
    commentsTblQ.filter(_.id === id).result
  }.map(_.headOption).mapTo[Option[Comment]]

  override def update(newVal: Comment): Future[Int] = db.run {
    commentsTblQ.filter(_.id === newVal.id)
      .map(c => (c.userId, c.postId, c.body)).update(newVal.user_id, newVal.post_id, newVal.body)
  }

  override def delete(id: Long): Future[Int] = db.run {
    commentsTblQ.filter(_.id === id).delete
  }

  override def insert(newVal: Comment): Future[Int] = db.run {
    commentsTblQ += newVal
  }
}

object DaoComment {
  def apply(implicit context: ExecutionContext, db: Database) = new DaoComment()
}
