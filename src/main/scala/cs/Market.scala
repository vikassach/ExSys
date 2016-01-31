package cs

import java.util.concurrent.PriorityBlockingQueue

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._

// Represents Trades Executed in the system
sealed class TradeExec(val oldOrd: Order, val newOrd: Order)

class Market {

  val bids = new PriorityBlockingQueue[Order](1000, new BidComparator()) // Open Buy requests
  val asks = new PriorityBlockingQueue[Order](1000, new AskComparator()) // Open Sell requests

  val trades = new ListBuffer[TradeExec] // Trades executed


  /*
   * Processes new trade requests by checking for a matching request of opposite 
   * trade direction.
   * If matching request is found trade is executed by removing matching request from
   * Open requests and adding it along with new order to trades executed list
   * If matching request is not found new order is added to open requests of its type
   */
  def process(newOrder: Order) = {
    this.synchronized{
      val (trds, oppTrds) =
        if (newOrder.isBuy) (bids, asks)
        else (asks, bids)

      val matchingOrder = oppTrds.toList.find { x => x.matchTrd(newOrder) }
      if (matchingOrder.isDefined)
        executeTrd(matchingOrder.get, newOrder, oppTrds)
      else
        trds.add(newOrder)

//      println("Remaining Bids : " + bids.size + " : Asks : " + asks.size + " : Trades : " + trades.size)
    }
  }

  private def executeTrd(order: Order, newOrder: Order, trds: PriorityBlockingQueue[Order]) = {
    trds.remove(order)
    trades += new TradeExec(order, newOrder)
  }
  

  // Open interest is the total quantity of all open orders for the given direction
  def openInterest(trdDir: TrdDir): Long = {
    val trds = if (Buy.equals(trdDir.dir)) bids else asks

    return trds.toList.map(_.qty).foldLeft(0L)(_ + _)
  }

  
  // average price per unit of all executions
  def avgExecPrice(): BigDecimal = trades.map { x => x.newOrd.price }.sum / trades.size

  
  /*
   * Executed quantity is the sum of quantities of executed orders for the given RIC and user.
   * The quantity of sell orders should be negated
   */
  def executedQty(user: String): Long = {
    val usersOrder = trades.filter { x => x.newOrd.user.equals(user) }.map { _.newOrd } ++
      trades.filter { x => x.oldOrd.user.equals(user) }.map { _.oldOrd }

    val sumOfBuyOrders = usersOrder.filter { _.isBuy }.map(_.qty).sum
    val sumOfSellOrders = usersOrder.filter { !_.isBuy }.map(_.qty).sum

    sumOfBuyOrders - sumOfSellOrders
  }

  
  def executedTrades = {
    trades.size
  }
}