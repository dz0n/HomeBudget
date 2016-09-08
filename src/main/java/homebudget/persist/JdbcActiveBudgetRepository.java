package homebudget.persist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import homebudget.domain.Budget;
import homebudget.service.BudgetService;
import homebudget.service.UserService;

@Repository
@PreAuthorize("denyAll")
public class JdbcActiveBudgetRepository implements ActiveBudgetRepository {
	private final String SELECT = "SELECT budget FROM budget_active " +
									"WHERE user = ?";
	private final String INSERT = "INSERT INTO budget_active(user, budget) " +
									"VALUES (?, ?)";
	private final String UPDATE = "UPDATE budget_active " +
									"SET budget = ? " +
									"WHERE user = ?";
	private final String DELETE = "DELETE FROM budget_active " +
									"WHERE user = ?";
	private final String COUNT = "SELECT (count(user)) FROM budget_active " +
									"WHERE user = ?";
	
	private BudgetService budgetService;
	private UserService userService;
	private JdbcOperations jdbcOperations;
	
	@Autowired
	public JdbcActiveBudgetRepository(JdbcOperations jdbcOperations, BudgetService budgetService, UserService userService) {
		this.jdbcOperations = jdbcOperations;
		this.budgetService = budgetService;
		this.userService = userService;
	}

	@Override
	@PreAuthorize("#username == principal.username")
	@PostAuthorize("returnObject == null || hasPermission(returnObject, 1)")
	public Budget getActiveBudget(String username) {
		try {
			Long budgetId = jdbcOperations.queryForObject(SELECT, Long.class, getUserId(username));
			return budgetService.getBudgetById(budgetId);
		} catch (DataAccessException e) {
			return null;
		} catch(NullPointerException e) {
			return null;
		}
	}

	@Override
	@PreAuthorize("#username == principal.username && hasPermission(#budget, 1)")
	public void setActiveBudget(String username, Budget budget) {
		if (count(username) == 0) {
			jdbcOperations.update(INSERT, getUserId(username), budget.getId());
		} else {
			jdbcOperations.update(UPDATE, budget.getId(), getUserId(username));
		}
	}

	@Override
	@PreAuthorize("#username == principal.username")
	public void removeActiveBudget(String username) {
		jdbcOperations.update(DELETE, getUserId(username));
	}
	
	private int count(String username) {
		return jdbcOperations.queryForObject(COUNT, Integer.class, userService.getUserByUsername(username).getId());
	}
	
	private Long getUserId(String username) {
		return userService.getUserByUsername(username).getId();
	}
}
