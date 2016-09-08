package homebudget.persist;

import java.util.List;

import homebudget.domain.Budget;

public interface BudgetRepository {
	
	public Budget save(Budget budget);
	
	public Budget update(Budget budget);
	
	public void delete(Budget budget);
	
	public List<Budget> getBudgets(String username);
	
	public Budget getBudgetById(long id);
	
	public int count();
	
}
