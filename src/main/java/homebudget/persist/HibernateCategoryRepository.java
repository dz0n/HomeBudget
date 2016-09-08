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

import homebudget.domain.Budget;
import homebudget.domain.Category;

@Repository
@Transactional
@PreAuthorize("denyAll")
public class HibernateCategoryRepository implements CategoryRepository {
	private static final String DELETE_ALL = "delete Category where budget = :budget";
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	@PreAuthorize("hasPermission(#category.budget, 2)")
	public Category save(Category category) {
		Serializable id = getCurrentSession().save(category);
		category.setId((Long) id);
		return category;
	}

	@Override
	@PreAuthorize("hasPermission(#category.budget, 2)")
	public void update(Category category) {
		getCurrentSession().update(category);
	}

	@Override
	@PreAuthorize("hasPermission(#category.budget, 2)")
	public void delete(Category category) {
		getCurrentSession().delete(category);
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
	public List<Category> getCategories(Budget budget) {
		return categoryCriteria()
				.add(Restrictions.eq("budget", budget))
				.list();
	}

	@Override
	@PreAuthorize("permitAll")
	@PostAuthorize("returnObject == null || hasPermission(returnObject.budget, 1)")
	public Category getCategoryById(long id) {
		return (Category) getCurrentSession().get(Category.class, id);
	}

	@Override
	@PreAuthorize("hasPermission(#budget, 1)")
	public int count(Budget budget) {
		return getCurrentSession()
				.createCriteria(Category.class)
				.add(Restrictions.eq("budget", budget))
				.list().size();
				
	}
	
	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
	
	private Criteria categoryCriteria() {
		return getCurrentSession().createCriteria(Category.class)
				.addOrder(Order.asc("name"));
	}
	
}
