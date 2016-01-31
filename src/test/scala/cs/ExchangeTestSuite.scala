package cs

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * @author Vikas Sachdeva
 */
@RunWith(classOf[JUnitRunner])
class ExchangeTestSuite extends FunSuite {
  
  private def ts = System.nanoTime()

  test("Exchange is able to process orders and calculate open interest") {
    createMarket("AAPL")
    create4BuyOrders("AAPL")
    var orderActual = Exchange.openInterest("AAPL", BuyTrd)
    assert(orderActual == 3900L, "Actual 1: " + orderActual + " : Expected : " + 3900L)
  }

  test("Exchange can calculate average execution price") {
    createMarket("RIC1")
    create4BuyOrders("RIC1")
    create4SellOrders("RIC1")

    var execPriceActual = Exchange.avgExecPrice("RIC1")
    assert(execPriceActual.get == BigDecimal(23.2), "Actual : " + execPriceActual.get + " : Expected : " + 23.2)
  }

  test("Exchange can calculate executed quantity for specific user") {
    createMarket("RIC2")
    create4BuyOrders("RIC2")
    create4SellOrders("RIC2")

    var execPriceActual = Exchange.executedQty("RIC2", "User1")
    assert(execPriceActual == 900L, "Actual : " + execPriceActual + " : Expected : " + 900L)
  }

  test("Exchange is able to process 100000 orders and calculate open interest") {
    createMarket("RIC4")
    createBuyOrders(100000, "RIC4")
    var orderActual = Exchange.openInterest("RIC4", BuyTrd)
    assert(orderActual == 100000000L, "Actual : " + orderActual + " : Expected : " + 100000000L)
  }


  def createMarket(s: String): Unit = Exchange.addMarket(s, new Market)

  private def create4BuyOrders(ric: String): Unit = {
    Exchange.process(new Order(BuyTrd, ric, 1000L, 23.3, "User1", ts))
    Exchange.process(new Order(BuyTrd, ric, 1000L, 23.2, "User2", ts))
    Exchange.process(new Order(BuyTrd, ric, 900L, 23.1, "User1", ts))
    Exchange.process(new Order(BuyTrd, ric, 1000L, 23.2, "User2", ts))
  }

  private def create4SellOrders(ric: String): Unit = {
    Exchange.process(new Order(SellTrd, ric, 1000L, 23.3, "User1", ts))
    Exchange.process(new Order(SellTrd, ric, 1000L, 23.2, "User4", ts))
    Exchange.process(new Order(SellTrd, ric, 900L, 23.1, "User3", ts))
    Exchange.process(new Order(SellTrd, ric, 1000L, 23.2, "User4", ts))
  }

  private def createBuyOrders(num: Int, ric: String): Unit = {
    for (i <- 1 to num)
      Exchange.process(new Order(BuyTrd, ric, 1000L, 23.0 + (i % 4), "User" + i % 4, ts))
  }
}