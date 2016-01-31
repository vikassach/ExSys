package cs;

import java.util.Comparator;

public class AskComparator  implements Comparator<Order> {

	@Override
	public int compare(Order o1, Order o2) {
		int result = o1.price().compare(o2.price());
		if (result != 0) return result; 
				
		return new Long(o1.timeStamp()).compareTo(new Long(o2.timeStamp()));
	}

}
