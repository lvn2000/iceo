package example.actor

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}

import scala.util.Random

class ActorSystemTest(name: String) extends TestKit(ActorSystem(name)) with ImplicitSender {

  def this() = this(s"TestSystem${Random.nextInt(5)}")

  def shutdown(): Unit = system.terminate()
}
