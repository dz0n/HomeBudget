package homebudget.config;

import java.io.Serializable;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import homebudget.domain.AccessLevel;
import homebudget.domain.Budget;
import homebudget.persist.JdbcAccessToBudgetForPermissionEvaluatorRepository;

@Component
public class BudgetPermissionEvaluator implements PermissionEvaluator {
	@Autowired
	private JdbcAccessToBudgetForPermissionEvaluatorRepository accessToBudgetRepository;
	
	@Override
	public boolean hasPermission(Authentication authentication, Object target, Object minimumAccessLevel) {
		if(target==null)
			return true;
		if(target instanceof Budget) {
			Budget budget = (Budget) target;
			if(budget.getUser()==null)
				return false;
			if(budget.getUser().getUsername()==null)
				return false;
			
			String username = authentication.getName();
			if(budget.getUser().getUsername().equals(username))
				return true;
			
			Map<Long, AccessLevel> ids = accessToBudgetRepository.getIdsOfAccessesToBudgets(username);
			if(!ids.containsKey(budget.getId()))
				return false;
			
			Integer minimumAccessLevelInteger;
			if(minimumAccessLevel instanceof AccessLevel) {
				minimumAccessLevelInteger = ((AccessLevel) minimumAccessLevel).ordinal();
			} else {
				minimumAccessLevelInteger = (Integer) minimumAccessLevel;
			}
			
			AccessLevel accessLevel = ids.get(budget.getId());
			if(accessLevel.ordinal() >= minimumAccessLevelInteger) {
				return true;
			} else {
				return false;
			}
		} else {
			throw new UnsupportedOperationException("hasPermission not supported for object <" + target + ">");
		}
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
			Object permission) {
		throw new UnsupportedOperationException();
	}

}
