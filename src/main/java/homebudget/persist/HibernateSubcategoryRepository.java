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
import homebudget.domain.Subcategory;

@Repository
@Transactional
@PreAuthorize("denyAll")
public class HibernateSubcategoryRepository implements SubcategoryRepository {
	private static final String DELETE_ALL = "DELETE FROM subcategory "
											+ "WHERE subcategory.category IN "
											+ "(SELECT category.id "
											+ "FROM category "
											+ "WHERE category.budget = :budgetId )";
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	@PreAuthorize("hasPermission(#subcategory.category.budget, 2)")
	public Subcategory save(Subcategory subcategory) {
		Serializable id = getCurrentSession().save(subcategory);
		subcategory.setId((Long) id);
		return subcategory;
	}

	@Override
	@PreAuthorize("hasPermission(#subcategory.category.budget, 2)")
	public void update(Subcategory subcategory) {
		getCurrentSession().update(subcategory);
	}

	@Override
	@PreAuthorize("hasPermission(#subcategory.category.budget, 2)")
	public void delete(Subcategory subcategory) {
		getCurrentSession().delete(subcategory);
	}

	@Override
	@PreAuthorize("hasPermission(#budget, 2)")
	public int deleteAll(Budget budget) {
		Query query = getCurrentSession().createSQLQuery(DELETE_ALL);
		query.setParameter("budgetId", budget);
		return query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	@PreAuthorize("hasPermission(#category.budget, 1)")
	@PostFilter("hasPermission(filterObject.category.budget, 1)")
	public List<Subcategory> getSubcategories(Category category) {
		return subcategoryCriteria()
				.add(Restrictions.eq("category", category))
				.list();
	}

	@Override
	@PreAuthorize("permitAll")
	@PostAuthorize("returnObject == null || hasPermission(returnObject.category.budget, 1)")
	public Subcategory getSubcategoryById(long id) {
		return (Subcategory) getCurrentSession().get(Subcategory.class, id);
	}

	@Override
	@PreAuthorize("hasPermission(#category.budget, 1)")
	public int count(Category category) {
		return getCurrentSession()
				.createCriteria(Subcategory.class)
				.add(Restrictions.eq("category", category))
				.list().size();
				
	}
	
	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
	
	private Criteria subcategoryCriteria() {
		return getCurrentSession().createCriteria(Subcategory.class)
				.addOrder(Order.asc("name"));
	}
	
}
