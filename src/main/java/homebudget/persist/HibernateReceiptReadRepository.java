package homebudget.persist;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import homebudget.domain.Account;
import homebudget.domain.Budget;
import homebudget.domain.Category;
import homebudget.domain.Receipt;
import homebudget.domain.Subcategory;

@Repository
@Transactional
@PreAuthorize("denyAll")
public class HibernateReceiptReadRepository implements ReceiptReadRepository {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	@PreAuthorize("permitAll")
	public int count() {
		return getCurrentSession()
				.createCriteria(Receipt.class).list().size();
	}
	
	@Override
	@PreAuthorize("hasPermission(#budget, 1)")
	public int count(Budget budget) {
		Criteria criteria = createCriteria();
		criteria.createAlias("receipt.account", "account");
		criteria.add(Restrictions.eq("account.budget", budget));
		return criteria.list().size();
	}

	@Override
	@PreAuthorize("hasPermission(#category.budget, 1)")
	public int count(Category category) {
		Criteria criteria = createCriteria();
		criteria.createAlias("receipt.subcategory", "subcategory");
		criteria.add(Restrictions.eq("subcategory.category", category));
		return criteria.list().size();
	}

	@Override
	@PreAuthorize("hasPermission(#subcategory.category.budget, 1)")
	public int count(Subcategory subcategory) {
		return createCriteria()
				.add(Restrictions.eq("subcategory", subcategory))
				.list().size();
	}

	@Override
	@PreAuthorize("hasPermission(#account.budget, 1)")
	public int count(Account account) {
		return createCriteria()
				.add(Restrictions.eq("account", account))
				.list().size();
	}

	@Override
	@PreAuthorize("permitAll")
	@PostAuthorize("returnObject == null || hasPermission(returnObject.account.budget, 1)")
	public Receipt getById(long id) {
		return (Receipt) getCurrentSession().get(Receipt.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	@PreAuthorize("hasPermission(#budget, 1)")
	@PostFilter("hasPermission(filterObject.account.budget, 1)")
	public List<Receipt> getReceipts(Budget budget, int start, int max) {
		Criteria criteria = createCriteria();
		criteria.createAlias("receipt.account", "account");
		criteria.add(Restrictions.eq("account.budget", budget));
		return criteria.setFirstResult(start)
				.setMaxResults(max)
				.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	@PreAuthorize("hasPermission(#category.budget, 1)")
	@PostFilter("hasPermission(filterObject.account.budget, 1)")
	public List<Receipt> getReceipts(Category category, int start, int max) {
		Criteria criteria = createCriteria();
		criteria.createAlias("receipt.subcategory", "subcategory");
		criteria.add(Restrictions.eq("subcategory.category", category));
		return criteria.setFirstResult(start)
				.setMaxResults(max)
				.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	@PreAuthorize("hasPermission(#subcategory.category.budget, 1)")
	@PostFilter("hasPermission(filterObject.account.budget, 1)")
	public List<Receipt> getReceipts(Subcategory subcategory, int start, int max) {
		return createCriteria()
				.add(Restrictions.eq("subcategory", subcategory))
				.setFirstResult(start)
				.setMaxResults(max)
				.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	@PreAuthorize("hasPermission(#account.budget, 1)")
	@PostFilter("hasPermission(filterObject.account.budget, 1)")
	public List<Receipt> getReceipts(Account account, int start, int max) {
		return createCriteria()
				.add(Restrictions.eq("account", account))
				.setFirstResult(start)
				.setMaxResults(max)
				.list();
	}
	
	@Override
	@PreAuthorize("hasPermission(#budget, 1)")
	public Date getFirstReceiptDate(Budget budget) {
		Criteria criteria = createCriteria();
		criteria.createAlias("receipt.account", "account");
		criteria.add(Restrictions.eq("account.budget", budget));
		criteria.setProjection(Projections.min("date"));
		return (Date) criteria.uniqueResult();
	}

	private Criteria createCriteria() {
		return getCurrentSession().createCriteria(Receipt.class, "receipt")
				.addOrder(Order.desc("date"))
				.addOrder(Order.desc("timestamp"));
	}
	
	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
	
}
