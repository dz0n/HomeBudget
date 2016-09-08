package homebudget.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import homebudget.domain.Account;
import homebudget.domain.Budget;
import homebudget.domain.Category;
import homebudget.domain.MonthValue;
import homebudget.domain.Subcategory;
import homebudget.persist.BudgetCalculationRepository;

@Service
public class BudgetCalculationService {
	private final BudgetCalculationRepository budgetCalculationRepository;
	private final AccountService accountService;
	private final ReceiptService receiptService;
	
	@Autowired
	public BudgetCalculationService(BudgetCalculationRepository budgetCalculationRepository,
			AccountService accountService,
			ReceiptService receiptService) {
		this.budgetCalculationRepository = budgetCalculationRepository;
		this.accountService = accountService;
		this.receiptService = receiptService;
	}
	
	public BigDecimal getMonthBalance(Budget budget) {
		return getMonthBalance(budget, new Date());
	}
	
	public BigDecimal getMonthBalance(Budget budget, Date date) {
		BigDecimal value = budgetCalculationRepository.getMonthBalance(budget, date);
		if(value==null) {
			return BigDecimal.valueOf(0);
		}
		return value;
	}
	
	public BigDecimal getMonthBalance(Account account) {
		return getMonthBalance(account, new Date());
	}
	
	public BigDecimal getMonthBalance(Account account, Date date) {
		return budgetCalculationRepository.getMonthBalance(account, date);
	}
	
	public BigDecimal getMonthBalance(Category category) {
		return getMonthBalance(category, new Date());
	}
	
	public BigDecimal getMonthBalance(Category category, Date date) {
		return budgetCalculationRepository.getMonthBalance(category, date);
	}
	
	public BigDecimal getMonthBalance(Subcategory subcategory) {
		return getMonthBalance(subcategory, new Date());
	}
	
	public BigDecimal getMonthBalance(Subcategory subcategory, Date date) {
		return budgetCalculationRepository.getMonthBalance(subcategory, date);
	}
	
	public List<MonthValue> getLastMonthsBalance(Budget budget, Date endMonthDate, int months) {
		List<MonthValue> monthsValues = new ArrayList<MonthValue>();
		for(DateTime month : getLastMonths(new DateTime(endMonthDate), months)) {
			MonthValue monthValue = new MonthValue(
					month.getYear() + "-" + String.format("%02d", month.getMonthOfYear()), 
					this.getMonthBalance(budget, month.toDate())
					);
			monthsValues.add(monthValue);
		}
		return monthsValues;
	}
	
	public List<MonthValue> getLastMonthsBalance(Budget budget, Date endMonthDate) {
		Date firstReceiptDate = receiptService.getFirstReceiptDate(budget);
		LocalDate startDate = new LocalDate(firstReceiptDate);
		LocalDate endDate = new LocalDate(endMonthDate);
		int monthsBetween = Months.monthsBetween(startDate.withDayOfMonth(1), endDate.withDayOfMonth(1)).getMonths() + 1;
		
		return this.getLastMonthsBalance(budget, endMonthDate, monthsBetween);
	}
	
	private List<DateTime> getLastMonths(DateTime endMonthDate, int months) {
		List<DateTime> dates = new ArrayList<DateTime>();
		DateTime startMonth = new DateTime(endMonthDate).minusMonths(months-1);
		for(int month = 0; month < months; month++) {
			dates.add(startMonth.plusMonths(month));
		}
		return dates;
	}
	
	public List<MonthValue> getLastMonthsBudgetValues(Budget budget, Date endMonthDate, int months) {
		List<MonthValue> monthsBalances = getLastMonthsBalance(budget, endMonthDate, months);
		List<MonthValue> monthsValues = new ArrayList<MonthValue>();
		BigDecimal accountsValue = getSumOfAccounts(budget);
		
		for(int month = monthsBalances.size()-1; month >= 0; month--) {
			MonthValue monthValue = new MonthValue(monthsBalances.get(month).getMonth(), accountsValue);
			monthsValues.add(monthValue);
			accountsValue = accountsValue.subtract(monthsBalances.get(month).getValue());
		}
		Collections.reverse(monthsValues);
		return monthsValues;
	}
	
	public List<MonthValue> getLastMonthsBudgetValues(Budget budget, Date endMonthDate) {
		Date firstReceiptDate = receiptService.getFirstReceiptDate(budget);
		LocalDate startDate = new LocalDate(firstReceiptDate);
		LocalDate endDate = new LocalDate(endMonthDate);
		int monthsBetween = Months.monthsBetween(startDate.withDayOfMonth(1), endDate.withDayOfMonth(1)).getMonths() + 1;
		
		return this.getLastMonthsBalance(budget, endMonthDate, monthsBetween);
	}
	
	private BigDecimal getSumOfAccounts(Budget budget) {
		BigDecimal accountsValue = BigDecimal.valueOf(0);
		List<Account> accounts = accountService.getAccounts(budget);
		for(Account account : accounts) {
			accountsValue = accountsValue.add(account.getValue());
		}
		return accountsValue;
	}
	
}
