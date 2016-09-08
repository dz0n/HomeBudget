package homebudget.persist;

import java.util.Map;

import homebudget.domain.AccessLevel;
import homebudget.domain.Budget;

public interface AccessToBudgetRepository {
	
	public void addAccessToBudget(Budget budget, String username, AccessLevel level);
	
	public void removeAccessToBudget(Budget budget, String username);
	
	public Map<Long, AccessLevel> getIdsOfAccessesToBudgets(String username);
	
	public Map<String, AccessLevel> getHiddenIdsOfAccessesToBudgets(String username);
	
	public AccessLevel getAccessLevel(Budget budget, String username);
	
	public Map<String, AccessLevel> getAccessLevelsToBudget(Budget budget);
	
}
