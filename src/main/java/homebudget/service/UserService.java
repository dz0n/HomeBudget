package homebudget.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import homebudget.domain.User;
import homebudget.domain.forms.RegisterUserForm;
import homebudget.persist.UserRepository;

@Service("userService")
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public User save(RegisterUserForm user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}
	
	public User getUserByUsername(String username) {
		return userRepository.getUserByUsername(username);
	}
	
	public boolean isNameUnique(String username) {
		try {
			User user = userRepository.getUserByUsername(username);
			if(user.getUsername().equals(username)) {
				return false;
			} else {
				return true;
			}
		} catch(EmptyResultDataAccessException e) {
			return true;
		}
	}
	
	public boolean isEmailUnique(String email) {
		try {
			User user = userRepository.getUserByEmail(email);
			if(user==null) {
				return true;
			} else {
				return false;
			}
		} catch(EmptyResultDataAccessException e) {
			return true;
		}
	}
	
	public User getUserById(long id) {
		return userRepository.getUserById(id);
	}
}
