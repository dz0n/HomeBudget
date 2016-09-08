package homebudget.persist;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import homebudget.domain.Account;
import homebudget.domain.Budget;
import homebudget.domain.Category;
import homebudget.domain.Subcategory;

@Repository
@Transactional
@PreAuthorize("denyAll")
public class HibernateBudgetCalculationRepository implements BudgetCalculationRepository {
	private final static String SELECT_BUDGET_SUM_IN_MONTH = "SELECT sum(r.value) "
															+ "FROM receipt AS r "
															+ "JOIN account AS a "
															+ "ON r.account = a.id "
															+ "WHERE a.budget=:budget "
															+ "AND month(date)=:month "
															+ "AND year(date)=:year";
	private final static String SELECT_ACCOUNT_SUM_IN_MONTH = "SELECT sum(r.value) "
															+ "FROM receipt AS r "
															+ "WHERE r.account=:account "
															+ "AND month(date)=:month "
															+ "AND year(date)=:year";
	private final static String SELECT_SUBCATEGORY_SUM_IN_MONTH = "SELECT sum(r.value) "
															+ "FROM receipt AS r "
															+ "WHERE r.subcategory=:subcategory "
															+ "AND month(date)=:month "
															+ "AND year(date)=:year";
	private final static String SELECT_CATEGORY_SUM_IN_MONTH = "SELECT sum(r.value) "
															+ "FROM receipt AS r "
															+ "JOIN subcategory AS s "
															+ "ON r.subcategory = s.id "
															+ "WHERE s.category=:category "
															+ "AND month(date)=:month "
															+ "AND year(date)=:year";
	private final SessionFactory sessionFactory;
	
	@Autowired
	public HibernateBudgetCalculationRepository(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	@PreAuthorize("hasPermission(#budget, 1)")
	public BigDecimal getMonthBalance(Budget budget, Date date) {
		Query query = getPreparedSumSQLQuery(SELECT_BUDGET_SUM_IN_MONTH, date);
		query.setParameter("budget", budget.getId());
		return getSum(query);
	}

	@Override
	@PreAuthorize("hasPermission(#account.budget, 1)")
	public BigDecimal getMonthBalance(Account account, Date date) {
		Query query = getPreparedSumSQLQuery(SELECT_ACCOUNT_SUM_IN_MONTH, date);
		query.setParameter("account", account.getId());
		return getSum(query);
	}

	@Override
	@PreAuthorize("hasPermission(#category.budget, 1)")
	public BigDecimal getMonthBalance(Category category, Date date) {
		Query query = getPreparedSumSQLQuery(SELECT_CATEGORY_SUM_IN_MONTH, date);
		query.setParameter("category", category.getId());
		return getSum(query);
	}

	@Override
	@PreAuthorize("hasPermission(#subcategory.category.budget, 1)")
	public BigDecimal getMonthBalance(Subcategory subcategory, Date date) {
		Query query = getPreparedSumSQLQuery(SELECT_SUBCATEGORY_SUM_IN_MONTH, date);
		query.setParameter("subcategory", subcategory.getId());
		return getSum(query);
	}

	private SQLQuery getPreparedSumSQLQuery(String textQuery, Date date) {
		SQLQuery query = getCurrentSession().createSQLQuery(textQuery);
		LocalDate localDate = new LocalDate(date);
		query.setParameter("month", localDate.getMonthOfYear());
		query.setParameter("year", localDate.getYear());
		return query;
	}

	private BigDecimal getSum(Query query) {
		@SuppressWarnings("rawtypes")
		List sumList = query.list();
		if(sumList.size()==0) {
			return BigDecimal.valueOf(0); 
		}
		BigDecimal sum = (BigDecimal) sumList.get(0);
		if(sum==null) {
			return BigDecimal.valueOf(0);
		}
		return sum.divide(BigDecimal.valueOf(100));
	}

	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
	
}
