package homebudget.persist;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import homebudget.domain.Budget;
import homebudget.domain.Category;
import homebudget.domain.CategoryTransfer;
import homebudget.service.CategoryService;

@Repository
@Transactional
@PreAuthorize("denyAll")
public class HibernateCategoryTransferRepository implements CategoryTransferRepository {
	private final String UPDATE_CATEGORY = "UPDATE category SET value = value + :value "
										+ "WHERE id = :categoryId";
	private static final String DELETE_ALL = "DELETE FROM category_transfer "
										+ "WHERE category_transfer.from_category IN "
										+ "(SELECT account.id "
										+ "FROM account "
										+ "WHERE account.budget = :budgetId )";
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private CategoryService categoryService;
	
	@Override
	@Transactional
	@PreAuthorize("hasPermission(#categoryTransfer.toCategory.budget, 1)" +
			" && hasPermission(#categoryTransfer.fromCategory.budget, 1)")
	public CategoryTransfer save(CategoryTransfer categoryTransfer) {
		long value = categoryTransfer.getLongValue();

		if(!categoryTransfer.getFromCategory().equals(categoryTransfer.getToCategory())) {
			setCategoryValue(categoryTransfer.getFromCategory(), -value);
			setCategoryValue(categoryTransfer.getToCategory(), value);
		}
		
		Serializable id = getCurrentSession().save(categoryTransfer);
		categoryTransfer.setId((Long) id);
		categoryTransfer.setFromCategory(categoryService.getCategoryById(categoryTransfer.getFromCategory().getId()));
		categoryTransfer.setToCategory(categoryService.getCategoryById(categoryTransfer.getToCategory().getId()));
		return categoryTransfer;
	}	
	
	@Override
	@Transactional
	@PreAuthorize("hasPermission(#categoryTransfer.toCategory.budget, 1)" +
			" && hasPermission(#categoryTransfer.fromCategory.budget, 1)")
	public void delete(CategoryTransfer categoryTransfer) {
		long value = categoryTransfer.getLongValue();
		
		if(!categoryTransfer.getFromCategory().equals(categoryTransfer.getToCategory())) {
			setCategoryValue(categoryTransfer.getFromCategory(), value);
			setCategoryValue(categoryTransfer.getToCategory(), -value);
		}
		
		getCurrentSession().delete(categoryTransfer);
	}
	
	@Override
	@PreAuthorize("hasPermission(#budget, 3)")
	public int deleteAll(Budget budget) {
		Query query = getCurrentSession().createSQLQuery(DELETE_ALL);
		query.setParameter("budgetId", budget.getId());
		return query.executeUpdate();
	}

	@Transactional
	private void setCategoryValue(Category category, long value) {
		Query query = getCurrentSession().createSQLQuery(UPDATE_CATEGORY);
		query.setLong("value", value);
		query.setParameter("categoryId", category.getId());
		int result = query.executeUpdate();
		if(result > 1)
			throw new DataRetrievalFailureException("updated more than one Category (in query: \"" + query.getQueryString() + "\"; where id = \"" + category.getId() + "\")");
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@PreAuthorize("hasPermission(#categoryTransfer.toCategory.budget, 1)" +
			" && hasPermission(#categoryTransfer.fromCategory.budget, 1)")
	public CategoryTransfer update(CategoryTransfer categoryTransfer) {
		CategoryTransfer newTransfer = new CategoryTransfer(categoryTransfer);
		CategoryTransfer oldTransfer = getById(categoryTransfer.getId());
		delete(oldTransfer);
		return save(newTransfer);
	}

	@Override
	@PreAuthorize("permitAll")
	@PostAuthorize("returnObject == null || hasPermission(returnObject.toCategory.budget, 1)" +
			" && hasPermission(returnObject.fromCategory.budget, 1)")
	public CategoryTransfer getById(long id) {
		return (CategoryTransfer) getCurrentSession().get(CategoryTransfer.class, id);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@PreAuthorize("hasPermission(#budget, 1)")
	@PostFilter("hasPermission(filterObject.toCategory.budget, 1)" +
			" && hasPermission(filterObject.fromCategory.budget, 1)")
	public List<CategoryTransfer> getTransfers(Budget budget, int start, int max) {
		Criteria criteriaForUniqueIds = createCriteria();
		criteriaForUniqueIds.createAlias("categoryTransfer.toCategory", "toCategory");
		criteriaForUniqueIds.add(Restrictions.eq("toCategory.budget", budget));
		criteriaForUniqueIds.setFetchMode("toCategory.budget", FetchMode.JOIN);
		criteriaForUniqueIds.setFirstResult(start).setMaxResults(max);
		criteriaForUniqueIds.setProjection(Projections.distinct(Projections.id()));
		List<Long> ids = criteriaForUniqueIds.list();
		
		Criteria finalCriteria = createCriteria();
		finalCriteria.add(Restrictions.in("id", ids));
		return finalCriteria.list();
	}

	@Override
	@PreAuthorize("hasPermission(#budget, 1)")
	public int count(Budget budget) {
		Criteria criteria = createCriteria();
		criteria.createAlias("categoryTransfer.toCategory", "toCategory");
		criteria.add(Restrictions.eq("toCategory.budget", budget));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return criteria.list().size();
	}

	@Override
	@PreAuthorize("permitAll")
	public int count() {
		return getCurrentSession()
				.createCriteria(CategoryTransfer.class).list().size();
	}
	
	private Criteria createCriteria() {
		return getCurrentSession().createCriteria(CategoryTransfer.class, "categoryTransfer")
				.addOrder(Order.desc("date"))
				.addOrder(Order.desc("timestamp"));
	}
	
	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
	
}
