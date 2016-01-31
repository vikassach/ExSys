package cs

import java.util.concurrent.ConcurrentHashMap

import scala.collection.JavaConversions._

/**
 * @author Vikas Sachdeva
 */
object Exchange {

  val ricMarketMap = new ConcurrentHashMap[String, Market]() // Map of RIC to Markets

  def addMarket(ric: String, market: Market): Unit = ricMarketMap.putIfAbsent(ric, market)
  
  def getMarket(ric: String): Option[Market] = Option(ricMarketMap.getOrDefault(ric, null))

  
  def process(newOrder: Order): Unit = {
    getMarket(newOrder.ric) match {
      case Some(market) =>
        market.synchronized { market.process(newOrder) }
      case None =>
        println ("Please add market for this RIC : " + newOrder.ric);
    }
  }
  
  def executedTrades(ric: String) : Int =  getMarket(ric) match {
      case Some(market) =>
        market.executedTrades
      case None =>
        0
    }

  def openInterest(ric: String, trdDir: TrdDir): Long = getMarket(ric) match {
      case Some(market) =>
        market.openInterest(trdDir)
      case None =>
        0L
    }

    
  def avgExecPrice(ric: String):Option[BigDecimal] =    getMarket(ric) match {
      case Some(market) =>
        Option(ricMarketMap(ric).avgExecPrice)
      case None =>
        Option(null)
    }
    
  def executedQty(ric: String, user:String):Long = getMarket(ric) match {
      case Some(market) =>
        market.executedQty(user)
      case None =>
        0L
    }
}