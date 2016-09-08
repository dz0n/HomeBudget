package homebudget.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import homebudget.domain.Budget;
import homebudget.persist.ActiveBudgetRepository;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, 
		proxyMode=ScopedProxyMode.TARGET_CLASS)
public class ActiveBudgetService {
	private ActiveBudgetRepository repository;	
	
	@Autowired
	public ActiveBudgetService(ActiveBudgetRepository activeBudgetRepository) {
		this.repository = activeBudgetRepository;
	}
	
	/**
	 * 
	 * @return Return true if active budget was added or changed, return false if not
	 */
	public boolean set(Budget budget) {
		String name = getUsername();
		if(name == null) {
			return false;
		}
		try {
			repository.setActiveBudget(name, budget);
			return true;
		} catch (DataAccessException e) {
			return false;
		}
	}
	
	/**
	 * 
	 * @return Return true if active budget was removed or false if not
	 */
	public boolean remove() {
		String name = getUsername();
		if(name == null) {
			return false;
		}
		try {
			repository.removeActiveBudget(name);
			return true;
		} catch (DataAccessException e) {
			return false;
		}
	}
	
	public Budget get() {
		String name = getUsername();
		if(name == null) {
			return null;
		}
		
		return repository.getActiveBudget(name);
	}

	private String getUsername() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
			return auth.getName();
		} else { 
			return null;
		}
	}
}
