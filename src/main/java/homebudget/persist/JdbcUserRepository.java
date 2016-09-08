package homebudget.persist;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import homebudget.domain.User;
import homebudget.domain.forms.RegisterUserForm;

@Repository
@Profile("not_used")
public class JdbcUserRepository implements UserRepository {
	private final String INSERT_USER = "insert into users(username, password, email) " +
										"values  (?, ?, ?)";
	private final String SELECT_BY_USERNAME = "select id, username from users " +
												"where username=?";
	
	private JdbcOperations jdbcOperations;
	
	@Autowired
	public JdbcUserRepository(JdbcOperations jdbcOperations) {
		this.jdbcOperations = jdbcOperations;
	}
	
	@Override
	public User save(RegisterUserForm user) {
		jdbcOperations.update(INSERT_USER,
				user.getUsername(),
				user.getPassword(),
				user.getEmail());
		
		User insertedUser = this.getUserByUsername(user.getUsername());
		
		return insertedUser;
	}
	
	public User getUserByUsername(String username) {
		return jdbcOperations.queryForObject(SELECT_BY_USERNAME, new UserRowMapper(), username);
	}
	
	private static class UserRowMapper implements RowMapper<User> {

		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new User(
					rs.getLong("id"),
					rs.getString("username"));
		}
		
	}

	@Override
	public int count() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public User getUserByEmail(String username) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public User getUserById(long id) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
}
