package homebudget.persist;

import homebudget.domain.Budget;

public interface ActiveBudgetRepository {
	
	public Budget getActiveBudget(String username);
	
	public void setActiveBudget(String username, Budget budget);
	
	public void removeActiveBudget(String username);
	
}
