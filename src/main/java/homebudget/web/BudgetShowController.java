package homebudget.web;

import java.math.BigDecimal;
import java.text.Collator;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import homebudget.domain.Account;
import homebudget.domain.Budget;
import homebudget.domain.Category;
import homebudget.domain.Receipt;
import homebudget.service.AccountService;
import homebudget.service.ActiveBudgetService;
import homebudget.service.BudgetCalculationService;
import homebudget.service.BudgetService;
import homebudget.service.CategoryService;
import homebudget.service.ReceiptService;

@Controller
@RequestMapping(value = "/budget")
public class BudgetShowController {
	private static final int MAX_LAST_RECEIPTS = 10;
	
	private final BudgetService budgetService;
	private final CategoryService categoryService;
	private final AccountService accountService;
	private final ActiveBudgetService activeBudgetService;
	private final ReceiptService receiptService;
	private final BudgetCalculationService budgetCalculationService;
	
	@Autowired
	public BudgetShowController(BudgetService budgetService, 
			CategoryService categoryService,
			AccountService accountService,
			ActiveBudgetService activeBudgetService,
			ReceiptService receiptService,
			BudgetCalculationService budgetCalculationService) {
		this.budgetService = budgetService;
		this.categoryService = categoryService;
		this.accountService = accountService;
		this.activeBudgetService = activeBudgetService;
		this.receiptService = receiptService;
		this.budgetCalculationService = budgetCalculationService;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String showBudget(Model model) {
		Budget budget = activeBudgetService.get();
		return "redirect:/budget/" + budget.getHiddenId();
	}
	
	@RequestMapping(value = "/{budgetId}", method = RequestMethod.GET)
	public String showBudgetWithId(@PathVariable String budgetId, Model model) {
		Budget budget = budgetService.getBudgetByHiddenId(budgetId);
		model.addAttribute("budget", budget);
		
		List<Category> categories = categoryService.getCategories(budget);
		categories = sortCategoriesByName(categories);
		model.addAttribute("categories", categories);
		model.addAttribute("sumOfCategories", getSumOfCategories(categories));
		
		List<Account> accounts = accountService.getAccounts(budget);
		accounts = sortAccountsByName(accounts);
		model.addAttribute("accounts", accounts);
		model.addAttribute("sumOfAccounts", getSumOfAccounts(accounts));
		
		model.addAttribute("monthBalance", budgetCalculationService.getMonthBalance(budget));
		
		if(!budgetService.isBudgetBalanced(budget)) {
			BigDecimal categoriesSum = categoryService.getCategoriesSum(budget);
			BigDecimal accountsSum = accountService.getAccountsSum(budget);
			NumberFormat numberFormat = getNumberFormat();
			model.addAttribute("msgWarning", "Budżet nie jest zrównoważony:"
							+ " suma wartości kategorii wynosi " + numberFormat.format(categoriesSum)
							+ ", podczas gdy suma wartości kont wynosi " + numberFormat.format(accountsSum) + ".");
		}
		
		List<Receipt> receipts = receiptService.getReceipts(budget, 0, MAX_LAST_RECEIPTS);
		model.addAttribute("receipts", receipts);
		
		return "budget/budget";
	}

	private Comparator<Category> getCategoryComparator() {
		return new Comparator<Category>() {
			@Override
			public int compare(Category first, Category second) {
				if(first.getName()==null && second.getName()==null)
					return 0;
				Collator collator = Collator.getInstance(LocaleContextHolder.getLocale());
				return collator.compare(first.getName(), second.getName());
			}
		};
	}

	private List<Category> sortCategoriesByName(List<Category> categories) {
		Collections.sort(categories, getCategoryComparator());
		return categories;
	}
	
	private BigDecimal getSumOfCategories(List<Category> categories) {
		BigDecimal sum = BigDecimal.valueOf(0);
		for(Category category : categories) {
			sum = sum.add(category.getValue());
		}
		return sum;
	}
	
	private Comparator<Account> getAccountComparator() {
		return new Comparator<Account>() {
			@Override
			public int compare(Account first, Account second) {
				if(first.getName()==null && second.getName()==null)
					return 0;
				Collator collator = Collator.getInstance(LocaleContextHolder.getLocale());
				return collator.compare(first.getName(), second.getName());
			}
		};
	}

	private List<Account> sortAccountsByName(List<Account> accounts) {
		Collections.sort(accounts, getAccountComparator());
		return accounts;
	}
	
	private BigDecimal getSumOfAccounts(List<Account> accounts) {
		BigDecimal sum = BigDecimal.valueOf(0);
		for(Account account : accounts) {
			sum = sum.add(account.getValue());
		}
		return sum;
	}
	
	private NumberFormat getNumberFormat() {
		Locale locale = LocaleContextHolder.getLocale();
		NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
		numberFormat.setMinimumFractionDigits(2);
		numberFormat.setMaximumFractionDigits(2);
		numberFormat.setMinimumIntegerDigits(1);
		return numberFormat;
	}
	
}
