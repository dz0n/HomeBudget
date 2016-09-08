package homebudget.persist;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import homebudget.domain.AccessLevel;
import homebudget.domain.Budget;
import homebudget.service.IdHider;
import homebudget.service.UserService;

@Repository
@PreAuthorize("denyAll")
public class JdbcAccessToBudgetRepository implements AccessToBudgetRepository {
	private final String SELECT_BUDGET_AND_LEVEL = "SELECT budget, level FROM budget_access " +
									"WHERE user = ?";
	private final String SELECT_LEVEL = "SELECT level FROM budget_access " +
									"WHERE user = ? AND budget = ?";
	private final String SELECT_BUDGET_LEVELS = "SELECT level, user FROM budget_access " +
									"WHERE budget = ?";
	private final String INSERT = "INSERT INTO budget_access(user, budget, level) " +
									"VALUES (?, ?, ?)";
	private final String UPDATE = "UPDATE budget_access " +
									"SET level = ? " +
									"WHERE user = ? AND budget = ?";
	private final String DELETE = "DELETE FROM budget_access " +
									"WHERE user = ? AND budget = ?";
	private final String COUNT = "SELECT (count(level)) FROM budget_access " +
									"WHERE user = ? AND budget = ?";
	
	private JdbcOperations jdbcOperations;
	private UserService userService;
	private IdHider idHider;

	@Autowired
	public JdbcAccessToBudgetRepository(JdbcOperations jdbcOperations, UserService userService, IdHider idHider) {
		super();
		this.jdbcOperations = jdbcOperations;
		this.userService = userService;
		this.idHider = idHider;
	}

	@Override
	@PreAuthorize("#budget.user.username == principal.username")
	public void addAccessToBudget(Budget budget, String username, AccessLevel level) {
		Long userId = getUserId(username);
		if(isAnyAccessToBudget(budget, userId)) {
			jdbcOperations.update(UPDATE, level.ordinal(), userId, budget.getId());
		} else {
			jdbcOperations.update(INSERT, userId, budget.getId(), level.ordinal());
		}
	}

	private boolean isAnyAccessToBudget(Budget budget, Long userId) {
		int count = jdbcOperations.queryForObject(COUNT, 
				Integer.class, 
				userId, 
				budget.getId()
			);
		return count > 0;
	}

	private Long getUserId(String username) {
		return userService.getUserByUsername(username).getId();
	}

	@Override
	@PreAuthorize("#budget.user.username == principal.username")
	public void removeAccessToBudget(Budget budget, String username) {
		jdbcOperations.update(DELETE, getUserId(username), budget.getId());
	}

	@Override
	@PreAuthorize("#username == principal.username")
	public Map<Long, AccessLevel> getIdsOfAccessesToBudgets(String username) {
		return jdbcOperations.query(SELECT_BUDGET_AND_LEVEL, new AccessToBudgetExtractor(), getUserId(username));
	}
	
	private final class AccessToBudgetExtractor implements ResultSetExtractor<Map<Long, AccessLevel>> {
		@Override
		public Map<Long, AccessLevel> extractData(ResultSet rs) throws SQLException, DataAccessException {
			HashMap<Long, AccessLevel> mapOfAccesses = new HashMap<Long, AccessLevel>();
			while(rs.next()) {
				if(rs.getInt("level") > 0)
					mapOfAccesses.put(rs.getLong("budget"), AccessLevel.values[rs.getInt("level")]);
			}
			return mapOfAccesses;
		}
	}
	
	@Override
	@PreAuthorize("#username == principal.username")
	public Map<String, AccessLevel> getHiddenIdsOfAccessesToBudgets(String username) {
		return jdbcOperations.query(SELECT_BUDGET_AND_LEVEL, new AccessToBudgetExtractorWithHiddenIds(), getUserId(username));
	}

	private final class AccessToBudgetExtractorWithHiddenIds implements ResultSetExtractor<Map<String, AccessLevel>> {
		@Override
		public Map<String, AccessLevel> extractData(ResultSet rs) throws SQLException, DataAccessException {
			HashMap<String, AccessLevel> mapOfAccesses = new HashMap<String, AccessLevel>();
			while(rs.next()) {
				if(rs.getInt("level") > 0)
					mapOfAccesses.put(idHider.getHiddenId(Budget.class, rs.getLong("budget")), AccessLevel.values[rs.getInt("level")]);
			}
			return mapOfAccesses;
		}
	}
	
	@Override
	@PreAuthorize("hasPermission(#budget, 1)")
	public AccessLevel getAccessLevel(Budget budget, String username) {
		try {
			Integer level = jdbcOperations.queryForObject(SELECT_LEVEL, Integer.class, getUserId(username), budget.getId());
			if(level==null)
				level = 0;
			return AccessLevel.values[level];
		} catch(EmptyResultDataAccessException e) {
			return AccessLevel.NONE;
		}
	}

	@Override
	@PreAuthorize("#budget.user.username == principal.username")
	public Map<String, AccessLevel> getAccessLevelsToBudget(Budget budget) {
		return jdbcOperations.query(SELECT_BUDGET_LEVELS, new BudgetUsersExtractor(), budget.getId());
	}
	
	private final class BudgetUsersExtractor implements ResultSetExtractor<Map<String, AccessLevel>> {
		@Override
		public Map<String, AccessLevel> extractData(ResultSet rs) throws SQLException, DataAccessException {
			HashMap<String, AccessLevel> mapOfAccesses = new HashMap<String, AccessLevel>();
			while(rs.next()) {
				if(rs.getInt("level") > 0 && rs.getObject("user") != null)					
					mapOfAccesses.put(userService.getUserById(rs.getLong("user")).getUsername(), AccessLevel.values[rs.getInt("level")]);
			}
			return mapOfAccesses;
		}
	}
}
