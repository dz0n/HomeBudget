package homebudget.persist;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import homebudget.domain.AccessLevel;
import homebudget.service.UserService;

@Repository
public class JdbcAccessToBudgetForPermissionEvaluatorRepository {
	private final String SELECT_BUDGET_AND_LEVEL = "SELECT budget, level FROM budget_access " +
				"WHERE user = ?";
	
	private JdbcOperations jdbcOperations;
	private UserService userService;
	
	@Autowired
	public JdbcAccessToBudgetForPermissionEvaluatorRepository(JdbcOperations jdbcOperations, UserService userService) {
		super();
		this.jdbcOperations = jdbcOperations;
		this.userService = userService;
	}
	
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
	
	private Long getUserId(String username) {
		return userService.getUserByUsername(username).getId();
	}
}
