package net.stackflow.matching.exchange

import java.io.{File, PrintWriter}

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging

import scala.collection.mutable

object Exchange {

  trait Request

  case class AddAccount(account: Manager.Account) extends Request

  case class AddOrder(order: Manager.Order) extends Request

  case object PrintOrders extends Request

  case object PrintAccounts extends Request

  def props(accounts: Map[String, Manager.Account] = Map.empty): Props = Props(new Exchange(accounts))

}

class Exchange(_accounts: Map[String, Manager.Account]) extends Actor with LazyLogging {

  var orders: mutable.Map[String, Manager.Order] = mutable.Map()

  def receive: Receive = receive(_accounts)

  def receive(accounts: Map[String, Manager.Account]): Receive = {
    case Exchange.AddAccount(account: Manager.Account) =>
      context.become(receive(accounts + (account.id -> account)))

    case Exchange.AddOrder(order: Manager.Order) =>
      orders.find {
        case (_, _order) =>
          _order.accountId != order.accountId && _order.typeId != order.typeId && _order.code == order.code
      } match {
        case Some((id, _order)) =>
          val accountOpt = accounts.find(_._1 == order.accountId)
          val _accountOpt = accounts.find(_._1 == _order.accountId)
          (accountOpt, _accountOpt) match {
            case (Some((_, account)), Some((_, _account))) =>
              val balance = (if (order.typeId == "b") -1 else 1) * (order.count * order.price)
              val count = (if (order.typeId == "b") 1 else -1) * order.count
              val a = account.copy(
                balance = account.balance + balance,
                stocks = account.stocks.map { stock =>
                  if (stock._1 == order.stockId) {
                    (stock._1, stock._2 + count)
                  } else {
                    stock
                  }
                }
              )
              val _a = _account.copy(
                balance = _account.balance - balance,
                stocks = _account.stocks.map { stock =>
                  if (stock._1 == order.stockId) {
                    (stock._1, stock._2 - count)
                  } else {
                    stock
                  }
                }
              )
              orders.remove(id)
              context.become(receive(accounts.updated(a.id, a).updated(_a.id, _a)))

            case _ =>
              orders.update(order.id, order)
          }

        case None =>
          orders.update(order.id, order)
      }

    case Exchange.PrintAccounts =>
      val writer = new PrintWriter(new File("exchange.txt"))
      accounts.values.toSeq.sortBy(a => a.id) foreach { account =>
        writer.write(s"${account.id}\t${account.balance}\t")
        writer.write(account.stocks.values.mkString("\t"))
        writer.write("\n")
      }
      writer.close()
  }

}
