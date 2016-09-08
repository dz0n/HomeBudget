package homebudget.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import homebudget.domain.AccessLevel;
import homebudget.domain.Budget;
import homebudget.persist.AccessToBudgetRepository;

@Service
public class AccessToBudgetService {
	@Autowired
	private AccessToBudgetRepository accessToBudgetRepository;
	
	public void addAccessToBudget(Budget budget, String username, AccessLevel level) {
		accessToBudgetRepository.addAccessToBudget(budget, username, level);
	}
	
	public void removeAccessToBudget(Budget budget, String username) {
		accessToBudgetRepository.removeAccessToBudget(budget, username);
	}
	
	public Map<Long, AccessLevel> getIdsOfAccessesToBudgets(String username) {
		return accessToBudgetRepository.getIdsOfAccessesToBudgets(username);
	}
	
	public Map<String, AccessLevel> getHiddenIdsOfAccessesToBudgets(String username) {
		return accessToBudgetRepository.getHiddenIdsOfAccessesToBudgets(username);
	}

	public AccessLevel getAccessLevel(Budget budget, String username) {
		return accessToBudgetRepository.getAccessLevel(budget, username);
	}
	
	public Map<String, AccessLevel> getAccessLevelsToBudget(Budget budget) {
		return accessToBudgetRepository.getAccessLevelsToBudget(budget);
	}
}
