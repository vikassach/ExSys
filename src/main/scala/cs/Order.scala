package cs

sealed trait Dir 
case object Buy extends Dir
case object Sell extends Dir

sealed class TrdDir(val dir: Dir, val oppDir: Dir) 
case object BuyTrd extends TrdDir(Buy, Sell)
case object SellTrd extends TrdDir(Sell, Buy)

class Order(val trdDir: TrdDir, val ric: String, val qty: Long, 
    val price: BigDecimal, val user: String, val timeStamp: Long) {

  override def toString = "Order[" +
    "\t trdDir=" + trdDir +
    "\t ric=" + ric +
    "\t qty=" + qty +
    "\t price=" + price +
    "\t user=" + user +
    "\t timeStamp=" + timeStamp +
    "]"

  override def equals(o: Any) = o match {
    case that: Order => that.trdDir.equals(this.trdDir) &&
      that.ric.equals(this.ric) &&
      that.qty.equals(this.qty) &&
      that.price.equals(this.price) &&
      that.user.equals(this.user) &&
      that.timeStamp.equals(this.timeStamp)
    case _ => false
  }

  /*
   * Two orders match if they have opposing directions, matching RICs and quantities, and if the
   * sell price is less than or equal to the buy price
   */
  def matchTrd(o: Any) = o match {
    case that: Order => that.trdDir.oppDir.equals(this.trdDir.dir) &&
      that.ric.equals(this.ric) &&
      that.qty.equals(this.qty) &&
      ((that.trdDir.equals(SellTrd) && that.price <= this.price) ||
        (that.trdDir.equals(BuyTrd) && that.price >= this.price))
    case _ => false
  }
  
  def isBuy = this.trdDir.dir.equals(Buy)

}
