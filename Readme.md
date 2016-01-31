Solution Description
====================
This solution implements an exchange system which matches orders on stocks.
It is implemented in scala but java's concurrent api.
It has following main components:


Exchange
--------
Is a facade to this system and provides following four main functionalities

* Add a new Market in exchange (for RIC) if none exists
* Delegate processing of order and queries related to RIC to specific Market

It maintains a map of each type of stocks and its orders. It also synchronizes access to orders


Market
------
Maintains record of open requests and trades executed for a particular stock.
It contains two PriorityBlockingQueues for buy & sell orders and a list of trades executed.
* Buy orders are sorted with with descending price and ascending timestamp (BidComparator)
* Sell orders are sorted with with ascending price and ascending timestamp (AskComparator)

Each trade execution (TradeExec) is a pair of two orders in opposite direction 
with buy price >= sell price

Market is specific to RIC (Reuters Information Code) and provides following functionality:

* Add/Process an Order
* Open Interest for Direction
* Average Execution price
* Executed Quantity for a User


Order
-----
Models request from user with following details:
* Trade Direction (contains main and opposite direction)
* RIC
* Quantity
* Price
* User
* TimeStamp (when Order was created)

This class also contains functionality for matching trades.


Tests
-----

### ExchangeSyncTestSuite
* Exchange is able to handle 100 parallel processes
* Exchange is able to handle 100 parallel processes for multiple RICs

### ExchangeTestSuite
* Exchange is able to process orders and calculate open interest
* Exchange can calculate average execution price
* Exchange can calculate executed quantity for specific user
* Exchange is able to process 100000 orders and calculate open interest 

### MarketTestSuite 
* Market maintains buy orders with descending price and ascending timestamp
* Market maintains sell orders with ascending price and ascending timestamp
* Market adds an order to its list of trades only if matching order is not found
* Market is able to calculate open interest for Buy orders
* Market is able to calculate open interest for Sell orders
* Market can calculate average execution price
* Market can calculate executed quantity for specific user

### OrderTestSuite
* Order equals compares all fields
* Order equals compares all fields : Negative
* Orders match if they have opposing directions, matching RICs and quantities, and if the sell price is less than or equal to the buy price
* Orders match when seller is selling at a price lesser than buyer is willing to pay
* Orders don't match when selling price > than buyer is willing to pay
* Orders don't match when they are in same direction
* Order.isBuy returns true for Buy trade and false for Sell trade