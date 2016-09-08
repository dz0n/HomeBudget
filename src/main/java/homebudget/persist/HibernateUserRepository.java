package homebudget.persist;

import java.io.Serializable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import homebudget.domain.User;
import homebudget.domain.forms.RegisterUserForm;

@Repository
@Transactional
public class HibernateUserRepository implements UserRepository {
	private SessionFactory sessionFactory;
	
	@Autowired
	public HibernateUserRepository(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public User save(RegisterUserForm user) {
		Serializable id =  getCurrentSession().save(user);
		User savedUser = getUserById((Long) id);
		return savedUser;
	}

	@Override
	public User getUserByUsername(String username) {
		try {
			return (User) getCurrentSession()
				.createCriteria(User.class)
				.add(Restrictions.eq("username", username))
				.list().get(0);
		} catch (IndexOutOfBoundsException e) {
			throw new EmptyResultDataAccessException("at least one user expected", 1, e);
		}
	}
	
	@Override
	public User getUserByEmail(String username) {
		try {
			return (User) getCurrentSession()
				.createCriteria(User.class)
				.add(Restrictions.eq("email", username))
				.list().get(0);
		} catch (IndexOutOfBoundsException e) {
			throw new EmptyResultDataAccessException("at least one user expected", 1, e);
		}
	}
	
	@Override
	public int count(){
		return getCurrentSession()
				.createCriteria(User.class).list().size();
	}

	@Override
	public User getUserById(long id) {
		return (User) getCurrentSession().get(User.class, id);
	}
	
}
