package homebudget.persist;

import homebudget.domain.User;
import homebudget.domain.forms.RegisterUserForm;

public interface UserRepository {
	
	public User save(RegisterUserForm user);
	
	public User getUserByUsername(String username);
	
	public User getUserByEmail(String username);
	
	public User getUserById(long id);
	
	public int count();
	
}
