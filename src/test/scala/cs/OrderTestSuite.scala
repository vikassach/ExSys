package cs

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * @author Vikas Sachdeva
 */
@RunWith(classOf[JUnitRunner])
class OrderTestSuite extends FunSuite{
  
  private def ts = System.nanoTime()
  
  test("Order equals compares all fields") {
    val ts0 = ts
    val orderActual = new Order(BuyTrd, "AAPL", 1000L, 23.3, "User", ts0)
    val orderExpected = new Order(BuyTrd, "AAPL", 1000L, 23.3, "User", ts0)
    assert(orderActual == orderExpected, 
        "Actual Order was : " + orderActual + " : Expected was: " + orderExpected)
  }
  
  
  test("Order equals compares all fields : Negative") {
    val orderActual = new Order(BuyTrd, "AAPL", 1000L, 23.3, "User1", ts)
    val orderExpected = new Order(BuyTrd, "AAPL", 1001L, 23.3, "User2", ts)
    assert(orderActual != orderExpected, 
        "Actual Order was : " + orderActual + " : Expected was: " + orderExpected)
  }
  
  
  test("Orders match if they have opposing directions, matching RICs and quantities, and if the sell price is less than or equal to the buy price") {
    val orderBuy = new Order(BuyTrd, "AAPL", 1000L, 23.3, "User1", ts)
    val orderSell = new Order(SellTrd, "AAPL", 1000L, 23.3, "User2", ts)
    assert(orderBuy.matchTrd(orderSell) == true, 
        "Actual Order was : " + orderBuy + " : Expected was: " + orderSell)
  }
  
  
  test("Orders match when seller is selling at a price lesser than buyer is willing to pay") {
    val orderBuy = new Order(BuyTrd, "AAPL", 1000L, 23.3, "User1", ts)
    val orderSell = new Order(SellTrd, "AAPL", 1000L, 23.1, "User2", ts)
    assert(orderBuy.matchTrd(orderSell) == true, 
        "Matching Buy Order : " + orderBuy + " : to Sell Order : " + orderSell)
    assert(orderSell.matchTrd(orderBuy) == true, 
        "Matching Sell Order : " + orderBuy + " : to Buy Order : " + orderSell)
  }
  
  
  test("Orders don't match when selling price > than buyer is willing to pay") {
    val orderBuy = new Order(BuyTrd, "AAPL", 1000L, 23.1, "User1", ts)
    val orderSell = new Order(SellTrd, "AAPL", 1000L, 23.5, "User2", ts)
    assert(orderBuy.matchTrd(orderSell) == false, 
        "Matching Buy Order : " + orderBuy + " : to Sell Order : " + orderSell)
    assert(orderSell.matchTrd(orderBuy) == false, 
        "Matching Sell Order : " + orderBuy + " : to Buy Order : " + orderSell)
  }
  
  
  test("Orders don't match when they are in same direction") {
    val orderBuy = new Order(BuyTrd, "AAPL", 1000L, 23.3, "User1", ts)
    val orderSell = new Order(SellTrd, "AAPL", 1000L, 23.3, "User2", ts)
    assert(orderBuy.matchTrd(orderBuy) == false)
    assert(orderSell.matchTrd(orderSell) == false)
  }
  
  
  test("Order.isBuy returns true for Buy trade and false for Sell trade") {
    val orderBuy = new Order(BuyTrd, "AAPL", 1000L, 23.3, "User1", ts)
    val orderSell = new Order(SellTrd, "AAPL", 1000L, 23.3, "User2", ts)
    assert(orderBuy.isBuy == true)
    assert(orderSell.isBuy == false)
  }
  
}