package cs


import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * @author Vikas Sachdeva
 */
@RunWith(classOf[JUnitRunner])
class MarketTestSuite extends FunSuite {
  
  private def ts = System.nanoTime()
  
  test("Market maintains buy orders with descending price and ascending timestamp") {
    val order1 = new Order(BuyTrd, "AAPL", 1000L, 23.3, "User", ts)
    val order2 = new Order(BuyTrd, "AAPL", 1000L, 23.2, "User", ts)
    val order3 = new Order(BuyTrd, "AAPL", 1000L, 23.1, "User", ts)
    val order4 = new Order(BuyTrd, "AAPL", 1000L, 23.2, "User", ts)
    
    val market = new Market
    
    market.process(order1)
    market.process(order4)
    market.process(order2)
    market.process(order3)
    
    var orderActual = market.bids.poll()
    assert(orderActual == order1, "Actual : " + orderActual + " : Expected was: " + order1)

    orderActual = market.bids.poll()
    assert(orderActual == order2, "Actual : " + orderActual + " : Expected was: " + order2)

    orderActual = market.bids.poll()
    assert(orderActual == order4, "Actual : " + orderActual + " : Expected was: " + order4)
    
    orderActual = market.bids.poll()
    assert(orderActual == order3, "Actual : " + orderActual + " : Expected was: " + order3)
    
    assert(market.bids.size == 0)
  }
  
  
  test("Market maintains sell orders with ascending price and ascending timestamp") {
    val order1 = new Order(SellTrd, "AAPL", 1000L, 23.3, "User", ts)
    val order2 = new Order(SellTrd, "AAPL", 1000L, 23.2, "User", ts)
    val order3 = new Order(SellTrd, "AAPL", 1000L, 23.1, "User", ts)
    val order4 = new Order(SellTrd, "AAPL", 1000L, 23.2, "User", ts)
    
    val market = new Market
    
    market.process(order1)
    market.process(order4)
    market.process(order2)
    market.process(order3)
    
    var orderActual = market.asks.poll()
    assert(orderActual == order3, "Actual : " + orderActual + " : Expected was: " + order3)

    orderActual = market.asks.poll()
    assert(orderActual == order2, "Actual : " + orderActual + " : Expected was: " + order2)
    
    orderActual = market.asks.poll()
    assert(orderActual == order4, "Actual : " + orderActual + " : Expected was: " + order4)
    
    orderActual = market.asks.poll()
    assert(orderActual == order1, "Actual : " + orderActual + " : Expected was: " + order1)
    
    assert(market.asks.size == 0)
  }
  
    
  test("Market adds an order to its list of trades only if matching order is not found") {
    val order1 = new Order(BuyTrd, "AAPL", 1000L, 23.3, "User", ts)
    val order2 = new Order(BuyTrd, "AAPL", 1000L, 23.3, "User", ts)
    
    val market = new Market
    create4BuyOrders(market)
    market.process(order1)
    market.process(order2)
    
    val newOrder = new Order(SellTrd, "AAPL", 1000L, 23.2, "User", ts)
    market.process(newOrder)
 
    var orderActual = market.bids.poll()
    assert(orderActual == order2, "Actual 1: " + orderActual + " : Expected : " + order2)
    
    var trdEx = market.trades.head
    assert(trdEx.oldOrd == order1, "Actual 2: " + trdEx.oldOrd + " : Expected : " + order1)
    assert(trdEx.newOrd == newOrder, "Actual 3: " + trdEx.newOrd + " : Expected : " + newOrder)
  }
  

  test("Market is able to calculate open interest for Buy orders") {
    val market = new Market
    create4BuyOrders(market)
    val openInt = market.openInterest(BuyTrd)
    assert(openInt == 3900L, "Actual : " + openInt + " : Expected : " + 3900L)
  }
  
  
  test("Market is able to calculate open interest for Sell orders") {
    val market = new Market
    create4SellOrders(market)
    val openInt = market.openInterest(SellTrd)
    assert(openInt == 3900L, "Actual : " + openInt + " : Expected : " + 3900L)
  }  
  
  
  test("Market can calculate average execution price") {
    val market = new Market
    create4BuyOrders(market)
    create4SellOrders(market)
        
    var execPriceActual = market.avgExecPrice()
    assert(execPriceActual == BigDecimal(23.2), "Actual : " + execPriceActual + " : Expected : " + 23.2)
  }
  
  
  test("Market can calculate executed quantity for specific user") {
    val market = new Market
    create4BuyOrders(market)
    create4SellOrders(market)
    
    var execPriceActual = market.executedQty("User1")
    assert(execPriceActual == 900L, "Actual : " + execPriceActual + " : Expected : " + 900L)
  }
  
  
  private def create4BuyOrders(market:Market):Unit = {
    val order1 = new Order(BuyTrd, "AAPL", 1000L, 23.3, "User1", ts)
    val order2 = new Order(BuyTrd, "AAPL", 1000L, 23.2, "User2", ts)
    val order3 = new Order(BuyTrd, "AAPL", 900L, 23.1, "User1", ts)
    val order4 = new Order(BuyTrd, "AAPL", 1000L, 23.2, "User2", ts)
    
    market.process(order1)
    market.process(order4)
    market.process(order2)
    market.process(order3)
  }
  
  private def create4SellOrders(market:Market):Unit = {
    val order1 = new Order(SellTrd, "AAPL", 1000L, 23.3, "User1", ts)
    val order2 = new Order(SellTrd, "AAPL", 1000L, 23.2, "User4", ts)
    val order3 = new Order(SellTrd, "AAPL", 900L, 23.1, "User3", ts)
    val order4 = new Order(SellTrd, "AAPL", 1000L, 23.2, "User4", ts)
        
    market.process(order1)
    market.process(order4)
    market.process(order2)
    market.process(order3)
  }

}