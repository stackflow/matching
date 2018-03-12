package net.stackflow.matching

import akka.actor.ActorSystem
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import net.stackflow.matching.exchange.{Exchange, Manager}

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.io.Source

object App extends App with LazyLogging {

  implicit val as: ActorSystem = ActorSystem("matching")
  implicit val ec: ExecutionContextExecutor = as.dispatcher
  implicit val timeout: Timeout = Timeout(1.seconds)

  val exchange = as.actorOf(Exchange.props())

  val clientSource = Source.fromFile(getClass.getResource("/data/clients.txt").toURI)
  clientSource.getLines() map { line => line.split("\t") } foreach { args =>
    val account = Manager.account(args(0), args(1).toInt, args(2).toInt, args(3).toInt, args(4).toInt, args(5).toInt)
    exchange ! Exchange.AddAccount(account)
  }
  clientSource.close

  val orderSource = Source.fromFile(getClass.getResource("/data/orders.txt").toURI)
  orderSource.getLines() map { line => line.split("\t") } foreach { args =>
    val order = Manager.order(args(0), args(1), args(2), args(3).toInt, args(4).toInt)
    exchange ! Exchange.AddOrder(order)
  }
  orderSource.close

  exchange ! Exchange.PrintAccounts

}
