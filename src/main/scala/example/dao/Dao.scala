package example.dao

import example.model.{CommentsTable, PaginatedResult, PostsTable, UsersTable}
import slick.lifted.TableQuery

import scala.concurrent.Future

trait Dao[T] {

  lazy val usersTblQ = TableQuery[UsersTable]
  lazy val postsTblQ = TableQuery[PostsTable]
  lazy val commentsTblQ = TableQuery[CommentsTable]

  def find(id: Long): Future[Option[T]]

  def insert(newVal: T): Future[Int]

  def update(updVal: T): Future[Int]

  def delete(id: Long): Future[Int]

  //if userId is None - return all records
  def findEntitiesForUser(userId: Option[Long], limit: Int, offset: Int): Future[PaginatedResult[T]]
}

