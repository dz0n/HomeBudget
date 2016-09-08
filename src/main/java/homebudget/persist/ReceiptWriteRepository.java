package homebudget.persist;

import homebudget.domain.Budget;
import homebudget.domain.Receipt;

public interface ReceiptWriteRepository {
	
	public Receipt save(Receipt receipt);
	
	public Receipt update(Receipt receipt);
	
	public void delete(Receipt receipt);
	
	public int deleteAll(Budget budget);
	
}
