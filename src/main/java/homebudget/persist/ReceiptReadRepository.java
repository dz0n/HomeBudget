package homebudget.persist;

import java.util.Date;
import java.util.List;

import homebudget.domain.Account;
import homebudget.domain.Budget;
import homebudget.domain.Category;
import homebudget.domain.Receipt;
import homebudget.domain.Subcategory;

public interface ReceiptReadRepository {
	
	public int count();
	
	public int count(Budget budget);
	
	public int count(Category category);
	
	public int count(Subcategory subcategory);
	
	public int count(Account account);
	
	public Receipt getById(long id);
	
	public List<Receipt> getReceipts(Budget budget, int start, int max);
	
	public List<Receipt> getReceipts(Category category, int start, int max);
	
	public List<Receipt> getReceipts(Subcategory subcategory, int start, int max);
	
	public List<Receipt> getReceipts(Account account, int start, int max);
	
	public Date getFirstReceiptDate(Budget budget);
}
