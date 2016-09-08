package homebudget.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import homebudget.domain.AccessLevel;
import homebudget.domain.Budget;
import homebudget.domain.User;
import homebudget.persist.BudgetRepository;
import homebudget.web.ObjectNotFoundException;

@Service
public class BudgetService {
	private final BudgetRepository budgetRepository;
	private final AccountService accountService;
	private final CategoryService categoryService;
	private final AccessToBudgetService accessToBudgetService;
	private final UserService userService;
	private final ReceiptService receiptService;
	private final SubcategoryService subcategoryService;
	private final AccountTransferService accountTransferService;
	private final CategoryTransferService categoryTransferService;
	private final IdHider idHider;
	
	@Autowired
	public BudgetService(BudgetRepository budgetRepository, AccountService accountService,
			CategoryService categoryService, AccessToBudgetService accessToBudgetService, UserService userService,
			ReceiptService receiptService, SubcategoryService subcategoryService,
			AccountTransferService accountTransferService, CategoryTransferService categoryTransferService,
			IdHider idHider) {
		this.budgetRepository = budgetRepository;
		this.accountService = accountService;
		this.categoryService = categoryService;
		this.accessToBudgetService = accessToBudgetService;
		this.userService = userService;
		this.receiptService = receiptService;
		this.subcategoryService = subcategoryService;
		this.accountTransferService = accountTransferService;
		this.categoryTransferService = categoryTransferService;
		this.idHider = idHider;
	}

	public class BudgetNotFoundException extends ObjectNotFoundException {
		private static final long serialVersionUID = 6736491682944074494L;
	}
	
	public boolean isBudgetBalanced(Budget budget) {
		return accountService.getAccountsSum(budget).compareTo(categoryService.getCategoriesSum(budget)) == 0;
	}
	
	public Budget save(Budget budget, String username) {
		User user = userService.getUserByUsername(username);
		budget.setUser(user);
		Budget savedBudget = budgetRepository.save(budget);
		savedBudget = setHiddenId(savedBudget);
		return savedBudget;
	}
	
	public Budget update(Budget budget, String username) {
		User user = userService.getUserByUsername(username);
		budget.setUser(user);
		budget = setIdIfNull(budget);
		
		Budget updatedBudget = budgetRepository.update(budget);
		updatedBudget = setHiddenId(updatedBudget);
		return updatedBudget;
		
	}

	private Budget setIdIfNull(Budget budget) {
		if(budget.getId()==null) {
			budget.setId(idHider.getId(Budget.class, budget.getHiddenId()));
		}
		return budget;
	}
	
	public void delete(Budget budget, String username) {
		User user = userService.getUserByUsername(username);
		budget.setUser(user);
		budget = setIdIfNull(budget);
		
		receiptService.deleteAll(budget);
		accountTransferService.deleteAll(budget);
		categoryTransferService.deleteAll(budget);
		accountService.deleteAll(budget);
		subcategoryService.deleteAll(budget);
		categoryService.deleteAll(budget);
		
		budgetRepository.delete(budget);
	}
	
	public List<Budget> getBudgets(String username) {
		List<Budget> budgets = budgetRepository.getBudgets(username);
		
		Map<Long, AccessLevel> sharedBudgetsId = accessToBudgetService.getIdsOfAccessesToBudgets(username);
		for(Entry<Long, AccessLevel> foreignBudgetId : sharedBudgetsId.entrySet()) {
			budgets.add(getBudgetById(foreignBudgetId.getKey()));
		}
		
		for(Budget budget : budgets) {
			budget = setHiddenId(budget);
		}
		
		return budgets;
	}
	
	public List<Budget> getOwnBudgets(String username) {
		List<Budget> budgets = budgetRepository.getBudgets(username);
		for(Budget budget : budgets) {
			budget = setHiddenId(budget);
		}
		return budgets;
	}
	
	public List<Budget> getSharedBudgets(String username) {
		List<Budget> sharedBudgets = new ArrayList<Budget>();
		Map<Long, AccessLevel> sharedBudgetsId = accessToBudgetService.getIdsOfAccessesToBudgets(username);
		for(Entry<Long, AccessLevel> foreignBudgetId : sharedBudgetsId.entrySet()) {
			sharedBudgets.add(getBudgetById(foreignBudgetId.getKey()));
		}
		
		for(Budget budget : sharedBudgets) {
			budget = setHiddenId(budget);
		}
		
		return sharedBudgets;
	}
	
	public Budget getBudgetById(long id) {
		Budget budget = budgetRepository.getBudgetById(id);
		if(budget==null)
			throw new BudgetNotFoundException();
		budget = setHiddenId(budget);
		return budget;
	}
	
	public Budget getBudgetByHiddenId(String hiddenId) {
		Long id = idHider.getId(Budget.class, hiddenId);
		return getBudgetById(id);
	}

	private Budget setHiddenId(Budget budget) {
		budget.setHiddenId(idHider.getHiddenId(Budget.class, budget.getId()));
		return budget;
	}
	
}
