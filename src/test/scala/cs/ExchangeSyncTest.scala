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
class ExchangeSyncTest extends FunSuite {

  private def ts = System.nanoTime()

  val orders = new Market

  test("Exchange is able to handle 100 parallel processes") {
    Exchange.addMarket("RIC5", new Market)
    val runner = new Runnable() {
      def run() = {
        createSellOrders(10, "RIC5")
        createBuyOrders(10, "RIC5")
      }
    }

    val executor = Executors.newCachedThreadPool();

    for (i <- 1 to 100)
      executor.submit(runner)

    executor.shutdown()
    executor.awaitTermination(1, TimeUnit.DAYS)

    val bidsRemaining = Exchange.getMarket("RIC5").get.bids.size
    val asksRemaining = Exchange.getMarket("RIC5").get.asks.size
    assert(bidsRemaining == asksRemaining, "bidsRemaining : " + bidsRemaining + " : should == asksRemaining : " + asksRemaining)
    
    val trdsExecuted = Exchange.executedTrades("RIC5")
    assert(trdsExecuted == (1000 - bidsRemaining), "Actual : " + trdsExecuted + " : Expected : " + (1000 - bidsRemaining))
  }



  test("Exchange is able to handle 100 parallel processes for multiple RICs") {

    def createRunner(ric: String): Runnable = {
      Exchange.addMarket(ric, new Market)
      new Runnable() {
        def run() = {
          createSellOrders(10, ric)
          createBuyOrders(10, ric)
        }
      }
    }

    val executor = Executors.newCachedThreadPool();

    for (i <- 1 to 100) {
      executor.submit(createRunner("RICd" + i))
    }

    executor.shutdown()
    executor.awaitTermination(1, TimeUnit.DAYS)


    for (i <- 1 to 100) {
      val ric = "RICd" + i
      val bidsRemaining = Exchange.getMarket(ric).get.bids.size
      val asksRemaining = Exchange.getMarket(ric).get.asks.size
      val trdsExecuted = Exchange.executedTrades(ric)

//      println("ric : " + ric + " : bidsRemaining : " + bidsRemaining + " : asksRemaining : " + asksRemaining + " : Actual : " + trdsExecuted)
      assert(bidsRemaining == asksRemaining, "bidsRemaining : " + bidsRemaining + " : should == asksRemaining : " + asksRemaining)

      assert(trdsExecuted == (10 - bidsRemaining), "Actual : " + trdsExecuted + " : Expected : " + (10 - bidsRemaining))
    }
  }

  private def createBuyOrders(num: Int, ric: String): Unit = {
    for (i <- 1 to num)
      Exchange.process(new Order(BuyTrd, ric, 1000L, 23.0 + (i % 4), "User" + i % 4, ts))
  }

  private def createSellOrders(num: Int, ric: String): Unit = {
    for (i <- 1 to num)
      Exchange.process(new Order(SellTrd, ric, 1000L, 23.0 + (i % 4), "User" + i % 4, ts))
  }
}