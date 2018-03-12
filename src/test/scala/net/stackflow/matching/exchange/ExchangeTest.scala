package net.stackflow.matching.exchange

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

class ExchangeTest extends TestKit(ActorSystem("ExchangeSpec")) with ImplicitSender
  with FlatSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "Exchange actor" should "have no orders after start" in {
    val exchange = TestActorRef[Exchange](Exchange.props())
    exchange.underlyingActor.orders shouldBe empty
  }

  it should "receive orders" in {
    val exchange = TestActorRef[Exchange](Exchange.props())
    exchange ! Exchange.AddOrder(Manager.order("id", "s", "A", 15, 89))
    exchange.underlyingActor.orders should have size 1
  }

}
