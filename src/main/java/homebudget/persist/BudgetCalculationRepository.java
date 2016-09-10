package homebudget.persist;

import java.math.BigDecimal;
import java.util.Date;

import homebudget.domain.Account;
import homebudget.domain.Budget;
import homebudget.domain.Category;
import homebudget.domain.Subcategory;

public interface BudgetCalculationRepository {

	public BigDecimal getMonthBalance(Budget budget, Date date);
	
	public BigDecimal getMonthBalance(Account account, Date date);
	
	public BigDecimal getMonthBalance(Category category, Date date);
	
	public BigDecimal getMonthBalance(Subcategory subcategory, Date date);
	
	public BigDecimal getExpensesSum(Budget budget, Date date);
	
	public BigDecimal getIncomeSum(Budget budget, Date date);
}