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

import homebudget.domain.Account;
import homebudget.domain.AccountTransfer;
import homebudget.domain.Budget;
import homebudget.service.AccountService;

@Repository
@Transactional
@PreAuthorize("denyAll")
public class HibernateAccountTransferRepository implements AccountTransferRepository {
	private static final String UPDATE_ACCOUNT = "UPDATE account SET value = value + :value "
										+ "WHERE id = :accountId";
	private static final String DELETE_ALL = "DELETE FROM account_transfer "
										+ "WHERE account_transfer.from_account IN "
										+ "(SELECT account.id "
										+ "FROM account "
										+ "WHERE account.budget = :budgetId )";
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private AccountService accountService;
	
	@Override
	@Transactional
	@PreAuthorize("hasPermission(#accountTransfer.toAccount.budget, 1)" +
			" && hasPermission(#accountTransfer.fromAccount.budget, 1)")
	public AccountTransfer save(AccountTransfer accountTransfer) {
		long value = accountTransfer.getLongValue();

		if(!accountTransfer.getFromAccount().equals(accountTransfer.getToAccount())) {
			setAccountValue(accountTransfer.getFromAccount(), -value);
			setAccountValue(accountTransfer.getToAccount(), value);
		}
		
		Serializable id = getCurrentSession().save(accountTransfer);
		accountTransfer.setId((Long) id);
		accountTransfer.setFromAccount(accountService.getAccountById(accountTransfer.getFromAccount().getId()));
		accountTransfer.setToAccount(accountService.getAccountById(accountTransfer.getToAccount().getId()));
		return accountTransfer;
	}	
	
	@Override
	@Transactional
	@PreAuthorize("hasPermission(#accountTransfer.toAccount.budget, 1)" +
			" && hasPermission(#accountTransfer.fromAccount.budget, 1)")
	public void delete(AccountTransfer accountTransfer) {
		long value = accountTransfer.getLongValue();
		
		if(!accountTransfer.getFromAccount().equals(accountTransfer.getToAccount())) {
			setAccountValue(accountTransfer.getFromAccount(), value);
			setAccountValue(accountTransfer.getToAccount(), -value);
		}
		
		getCurrentSession().delete(accountTransfer);
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
	
	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@PreAuthorize("hasPermission(#accountTransfer.toAccount.budget, 1)" +
			" && hasPermission(#accountTransfer.fromAccount.budget, 1)")
	public AccountTransfer update(AccountTransfer accountTransfer) {
		AccountTransfer newTransfer = new AccountTransfer(accountTransfer);
		AccountTransfer oldTransfer = getById(accountTransfer.getId());
		delete(oldTransfer);
		return save(newTransfer);
	}

	@Override
	@PreAuthorize("permitAll")
	@PostAuthorize("returnObject == null || hasPermission(returnObject.toAccount.budget, 1)" +
			" && hasPermission(returnObject.fromAccount.budget, 1)")
	public AccountTransfer getById(long id) {
		return (AccountTransfer) getCurrentSession().get(AccountTransfer.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	@PreAuthorize("hasPermission(#budget, 1)")
	@PostFilter("hasPermission(filterObject.toAccount.budget, 1)" +
			" && hasPermission(filterObject.fromAccount.budget, 1)")
	public List<AccountTransfer> getTransfers(Budget budget, int start, int max) {		
		Criteria criteriaForUniqueIds = createCriteria();
		criteriaForUniqueIds.createAlias("accountTransfer.toAccount", "toAccount");
		criteriaForUniqueIds.add(Restrictions.eq("toAccount.budget", budget));
		criteriaForUniqueIds.setFetchMode("toAccount.budget", FetchMode.JOIN);
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
		criteria.createAlias("accountTransfer.toAccount", "toAccount");
		criteria.add(Restrictions.eq("toAccount.budget", budget));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return criteria.list().size();
	}

	@Override
	@PreAuthorize("permitAll")
	public int count() {
		return getCurrentSession()
				.createCriteria(AccountTransfer.class).list().size();
	}
	
	private Criteria createCriteria() {
		return getCurrentSession().createCriteria(AccountTransfer.class, "accountTransfer")
				.addOrder(Order.desc("date"))
				.addOrder(Order.desc("timestamp"));
	}
	
	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
	
}
