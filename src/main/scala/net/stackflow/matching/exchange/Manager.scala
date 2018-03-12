package net.stackflow.matching.exchange

import java.util.UUID

object Manager {

  case class Account(id: String,
                     balance: Int,
                     stocks: Map[String, Int])

  def account(id: String, balance: Int, a: Int, b: Int, c: Int, d: Int): Account = {
    Account(id, balance, Map("A" -> a, "B" -> b, "C" -> c, "D" -> d))
  }

  case class Order(id: String,
                   accountId: String,
                   typeId: String,
                   stockId: String,
                   price: Int,
                   count: Int,
                   code: String)

  def order(accountId: String, typeId: String, stockId: String, price: Int, count: Int): Order = {
    Order(UUID.randomUUID().toString, accountId, typeId, stockId, price, count, s"$stockId:$price:$count")
  }

}
