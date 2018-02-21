package homebudget.persist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import homebudget.domain.Budget;
import homebudget.service.UserService;

@Repository
@PreAuthorize("denyAll")
public class JdbcDefaultFieldsRepository implements DefaultFieldsRepository {
	private static final String DEFAULT_ACCOUNT = "SELECT n.account FROM "
												+ "("
													+ "SELECT r.account "
													+ "FROM receipt AS r "
													+ "JOIN account AS a ON r.account = a.id "
													+ "WHERE r.user = ?	AND a.budget=? "
													+ "ORDER BY r.created DESC " 
													+ "LIMIT 0, 25"
												+ ") AS n "
												+ "GROUP BY n.account "
												+ "ORDER BY count(n.account) DESC "
												+ "LIMIT 0, 1;";
	private static final String DEFAULT_SUBCATEGORY = "SELECT n.subcategory FROM "
												+ "("
													+ "SELECT r.subcategory "
													+ "FROM receipt AS r "
													+ "JOIN account AS a ON r.account = a.id "
													+ "WHERE r.user = ?	AND a.budget=? "
													+ "ORDER BY r.created DESC " 
													+ "LIMIT 0, 25"
												+ ") AS n "
												+ "GROUP BY n.subcategory "
												+ "ORDER BY count(n.subcategory) DESC "
												+ "LIMIT 0, 1;";
	
	@Autowired
	private JdbcOperations jdbcOperations;
	@Autowired
	private UserService userService;
	
	@Override
	@PreAuthorize("hasPermission(#budget, 1) && #username == principal.username")
	public Long getDefaultSubcategoryId(Budget budget, String username) {
		try {
			return jdbcOperations.queryForObject(DEFAULT_SUBCATEGORY, Long.class, userService.getUserByUsername(username).getId(), budget.getId());
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	@Override
	@PreAuthorize("hasPermission(#budget, 1) && #username == principal.username")
	public Long getDefaultAccountId(Budget budget, String username) {
		try {
			return jdbcOperations.queryForObject(DEFAULT_ACCOUNT, Long.class, userService.getUserByUsername(username).getId(), budget.getId());
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
	}
}
