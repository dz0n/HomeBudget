package homebudget.persist;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import homebudget.domain.Account;
import homebudget.domain.Budget;

@Repository
@Transactional
@PreAuthorize("denyAll")
public class HibernateAccountRepository implements AccountRepository {
	private static final String DELETE_ALL = "delete Account where budget = :budget";
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	@PreAuthorize("hasPermission(#account.budget, 2)")
	public Account save(Account account) {
		Serializable id = getCurrentSession().save(account);
		account.setId((Long) id);
		return account;
	}

	@Override
	@PreAuthorize("hasPermission(#account.budget, 2)")
	public void update(Account account) {
		getCurrentSession().update(account);
	}

	@Override
	@PreAuthorize("hasPermission(#account.budget, 2)")
	public void delete(Account account) {
		getCurrentSession().delete(account);
	}

	@Override
	@PreAuthorize("hasPermission(#budget, 2)")
	public int deleteAll(Budget budget) {
		Query query = getCurrentSession().createQuery(DELETE_ALL);
		query.setParameter("budget", budget);
		return query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	@PreAuthorize("hasPermission(#budget, 1)")
	@PostFilter("hasPermission(filterObject.budget, 1)")
	public List<Account> getAccounts(Budget budget) {
		return accountCriteria()
				.add(Restrictions.eq("budget", budget))
				.list();
	}

	@Override
	@PreAuthorize("permitAll")
	@PostAuthorize("returnObject == null || hasPermission(returnObject.budget, 1)")
	public Account getAccountById(long id) {
		return (Account) getCurrentSession().get(Account.class, id);
	}

	@Override
	@PreAuthorize("hasPermission(#budget, 1)")
	public int count(Budget budget) {
		return getCurrentSession()
				.createCriteria(Account.class)
				.add(Restrictions.eq("budget", budget))
				.list().size();
				
	}
	
	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
	
	private Criteria accountCriteria() {
		return getCurrentSession().createCriteria(Account.class)
				.addOrder(Order.asc("name"));
	}
	
}
