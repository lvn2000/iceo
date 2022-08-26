package example.actor

import akka.pattern._
import akka.testkit.TestKit
import example.TestInit
import example.model._
import org.scalatest.Suites
import org.scalatest.matchers.should.Matchers
import org.slf4j.LoggerFactory
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery
import scala.concurrent.Await

class DBManagerActorTest extends Suites with Matchers with TestInit {
  private val logger = LoggerFactory.getLogger(this.getClass)

  val dbManager = system.actorOf(DBManagerActor.props(), "dbmanager-actor")

  val fakeUserId = 999999
  val testEmail = "test@email.com"

  val nameUserClass = User.getClass.getName

  lazy val usersTblQ = TableQuery[UsersTable]

  def getTestUser = Await.result(dbSlick.run {
    usersTblQ.filter(_.email === testEmail).result
  }.map(_.headOption).mapTo[Option[User]]
    , timeToWait).getOrElse(User(id = fakeUserId, email = testEmail))

  var testUserModel = getTestUser

  def testFindOne(): Unit = {
    val result = Await.result(dbManager ? FindOne(testUserModel.id, nameUserClass), timeToWait)
    assert(result.isInstanceOf[ResultDBManager] && result.asInstanceOf[ResultDBManager] == ResultDBManager(None, Some(testUserModel), None))
  }

  def testInsert(): Unit = {
    val result = Await.result(dbManager ? Insert(testUserModel), timeToWait)
    assert(result.isInstanceOf[ResultDBManager] && result.asInstanceOf[ResultDBManager].countRecord == Some(1))
  }

  def testDelete(): Unit = {
    val result = Await.result(dbManager ? Delete(testUserModel.id, nameUserClass), timeToWait)
    assert(result.isInstanceOf[ResultDBManager] && result.asInstanceOf[ResultDBManager].countRecord == Some(1))
  }

  def testUpdate(): Unit = {
    val result = Await.result(dbManager ? Update(testUserModel), timeToWait)
    assert(result.isInstanceOf[ResultDBManager] && result.asInstanceOf[ResultDBManager].countRecord == Some(1))
  }

  try {

    if (testUserModel.id == fakeUserId) {
      testInsert()
      testUserModel = getTestUser
    }

    testFindOne()
    testUpdate()
    testDelete()

  } finally {
    TestKit.shutdownActorSystem(system)
    systemTest.shutdown()
  }

}
