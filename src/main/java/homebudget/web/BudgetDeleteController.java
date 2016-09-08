package homebudget.web;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import homebudget.domain.Budget;
import homebudget.persist.ReceiptReadRepository;
import homebudget.service.AccountService;
import homebudget.service.BudgetService;
import homebudget.service.CategoryService;

@Controller
@RequestMapping(value = "/budget/delete")
public class BudgetDeleteController {
	private final BudgetService budgetService;
	private final AccountService accountService;
	private final CategoryService categoryService;
	private final ReceiptReadRepository receiptReadRepository;
	
	@Autowired
	public BudgetDeleteController(BudgetService budgetService, AccountService accountService,
			CategoryService categoryService, ReceiptReadRepository receiptReadRepository) {
		this.budgetService = budgetService;
		this.accountService = accountService;
		this.categoryService = categoryService;
		this.receiptReadRepository = receiptReadRepository;
	}

	@ModelAttribute("budget")
	public Budget addBudget(@PathVariable String budgetId) {
		return budgetService.getBudgetByHiddenId(budgetId);
	}
	
	@ModelAttribute("accountsCounter")
	public Integer addAccountsCounter(@ModelAttribute Budget budget) {
		return accountService.count(budget);
	}
	
	@ModelAttribute("categoriesCounter") 
	public Integer addCategoriesCounter(@ModelAttribute Budget budget){
		return categoryService.count(budget);
	}
	
	@ModelAttribute("receiptsCounter") 
	public Integer addReceiptsCounter(@ModelAttribute Budget budget){
		return receiptReadRepository.count(budget);
	}
	
	@RequestMapping(value = "/{budgetId}", method = RequestMethod.GET)
	public String deleteBudget(@PathVariable String budgetId) {
		return "budget/delete";
	}
	
	@RequestMapping(value = "/{budgetId}/confirm", method = RequestMethod.GET)
	public String deleteBudgetConfirmed(@PathVariable String budgetId,
			@ModelAttribute Budget budget, Principal principal) {
		budget.setHiddenId(budgetId);
		budgetService.delete(budget, principal.getName());
		return "redirect:/budgets/";
	}
}
