package homebudget.persist;

import java.util.List;

import homebudget.domain.CategoryTransfer;
import homebudget.domain.Budget;

public interface CategoryTransferRepository {
	
	public CategoryTransfer save(CategoryTransfer categoryTransfer);
	
	public void delete(CategoryTransfer categoryTransfer);
	
	public int deleteAll(Budget budget);
	
	public CategoryTransfer update(CategoryTransfer categoryTransfer);
	
	public CategoryTransfer getById(long id);
	
	public List<CategoryTransfer> getTransfers(Budget budget, int start, int max);
	
	public int count(Budget budget);
	
	public int count();
	
}
