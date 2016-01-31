package cs;

import java.util.Comparator;

public class BidComparator implements Comparator<Order> {

	@Override
	public int compare(Order o1, Order o2) {
		int result = o2.price().compare(o1.price());
		if (result != 0) return result; 
				
		return new Long(o1.timeStamp()).compareTo(new Long(o2.timeStamp()));
	}

}
