package net.stackflow.matching.exchange

import org.scalatest._

class ManagerTest extends FlatSpec with Matchers {

  "Account" should "be created with balance & stocks" in {
    val id = "id"
    val balance = 101
    val a = 12
    val b = 23
    val c = 34
    val d = 45

    val account = Manager.account(id, balance, a, b, c, d)

    account.stocks should have size 4
    account.id shouldEqual id
    account.balance should be (balance)
    account.stocks should contain key "A"
    account.stocks.get("C") shouldBe Some(c)
  }

  "Order" should "be created with code" in {
    val accountId = "id"
    val typeId = "s"
    val stockId = "A"
    val price = 23
    val count = 12

    val order = Manager.order(accountId, typeId, stockId, price, count)

    order.accountId shouldEqual accountId
    order.typeId should be (typeId)
    order.code shouldEqual s"$stockId:$price:$count"
  }

}
