package homebudget.persist;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
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

import homebudget.domain.Budget;
import homebudget.domain.User;
import homebudget.service.UserService;

@Repository
@Transactional
@PreAuthorize("denyAll")
public class HibernateBudgetRepository implements BudgetRepository {
	private final SessionFactory sessionFactory;
	private final UserService userService;
	
	@Autowired
	public HibernateBudgetRepository(SessionFactory sessionFactory, UserService userService) {
		this.sessionFactory = sessionFactory;
		this.userService = userService;
	}

	@Override
	@PreAuthorize("hasPermission(#budget, 2)")
	public Budget save(Budget budget) {
		Serializable id = getCurrentSession().save(budget);
		return new Budget(
				(Long) id, 
				budget.getName(),
				budget.getDescription(), 
				budget.getUser());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@PreAuthorize("#username == principal.username")
	@PostFilter("hasPermission(filterObject, 1)")
	public List<Budget> getBudgets(String username) {
		User user = userService.getUserByUsername(username);
		return budgetCriteria()
				.add(Restrictions.eq("user", user))
				.list();
	}
	
	@Override
	@PreAuthorize("permitAll")
	@PostAuthorize("returnObject == null || hasPermission(returnObject, 1)")
	public Budget getBudgetById(long id) {
		return (Budget) getCurrentSession().get(Budget.class, id);
	}

	@Override
	@PreAuthorize("permitAll")
	public int count() {
		return getCurrentSession()
				.createCriteria(Budget.class).list().size();
	}

	@Override
	@PreAuthorize("hasPermission(#budget, 2)")
	public Budget update(Budget budget) {
		getCurrentSession().update(budget);
		return budget;
	}

	@Override
	@PreAuthorize("hasPermission(#budget, 3)")
	public void delete(Budget budget) {
		getCurrentSession().delete(budget);
	}
	
	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
	
	private Criteria budgetCriteria() {
		return getCurrentSession().createCriteria(Budget.class)
				.addOrder(Order.asc("name"));
	}
}
