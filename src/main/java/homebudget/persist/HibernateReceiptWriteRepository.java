package homebudget.persist;

import java.io.Serializable;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import homebudget.domain.Account;
import homebudget.domain.Budget;
import homebudget.domain.Category;
import homebudget.domain.Receipt;
import homebudget.service.AccountService;
import homebudget.service.SubcategoryService;

@Repository
@Transactional
@PreAuthorize("denyAll")
public class HibernateReceiptWriteRepository implements ReceiptWriteRepository {
	private static final String UPDATE_ACCOUNT = "UPDATE account SET value = value + :value "
										+ "WHERE id = :accountId";
	private static final String UPDATE_CATEGORY = "UPDATE category SET value = value + :value "
										+ "WHERE id = :categoryId";
	private static final String DELETE_ALL = "DELETE FROM receipt "
										+ "WHERE receipt.account IN "
										+ "(SELECT account.id "
										+ "FROM account "
										+ "WHERE account.budget = :budgetId )";
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private SubcategoryService subcategoryService;
	
	@Autowired
	private ReceiptReadRepository readRepository;
	
	@Override
	@Transactional
	@PreAuthorize("hasPermission(#receipt.account.budget, 1)")
	public Receipt save(Receipt receipt) {
		long value = receipt.getLongValue();
		setCategoryValue(receipt.getSubcategory().getCategory(), value);
		setAccountValue(receipt.getAccount(), value);
		Serializable id = getCurrentSession().save(receipt);
		receipt.setId((Long) id);
		receipt.setAccount(accountService.getAccountById(receipt.getAccount().getId()));
		receipt.setSubcategory(subcategoryService.getSubcategoryById(receipt.getSubcategory().getId()));
		return receipt;
	}

	@Override
	@Transactional
	@PreAuthorize("hasPermission(#receipt.account.budget, 1)")
	public void delete(Receipt receipt) {
		long value = - receipt.getLongValue();
		setCategoryValue(receipt.getSubcategory().getCategory(), value);
		setAccountValue(receipt.getAccount(), value);
		getCurrentSession().delete(receipt);
	}
	
	@Override
	@PreAuthorize("hasPermission(#budget, 3)")
	public int deleteAll(Budget budget) {
		Query query = getCurrentSession().createSQLQuery(DELETE_ALL);
		query.setParameter("budgetId", budget.getId());
		return query.executeUpdate();
	}

	@Transactional
	private void setAccountValue(Account account, long value) {
		Query query = getCurrentSession().createSQLQuery(UPDATE_ACCOUNT);
		query.setLong("value", value);
		query.setParameter("accountId", account.getId());
		int result = query.executeUpdate();
		if(result > 1)
			throw new DataRetrievalFailureException("updated more than one Account (in query: \"" + query.getQueryString() + "\"; where id = \"" + account.getId() + "\")");
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
	
	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@PreAuthorize("hasPermission(#receipt.account.budget, 1)")
	public Receipt update(Receipt receipt) {
		Receipt newReceipt = new Receipt(receipt);
		Receipt oldReceipt = readRepository.getById(receipt.getId());
		delete(oldReceipt);
		return save(newReceipt);
	}
}
