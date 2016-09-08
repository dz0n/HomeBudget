package homebudget.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import homebudget.domain.Account;
import homebudget.domain.Account.Validation;
import homebudget.domain.Budget;
import homebudget.service.AccountService;
import homebudget.service.BudgetService;

@Controller
@RequestMapping(value = "/account")
public class AccountNewController {
	private final BudgetService budgetService;
	private final AccountService accountService;
	
	@Autowired
	public AccountNewController(BudgetService budgetService, AccountService accountService) {
		this.budgetService = budgetService;
		this.accountService = accountService;
	}
	
	@ModelAttribute("budget")
	public Budget addBudget(@PathVariable String budgetId) {
		return budgetService.getBudgetByHiddenId(budgetId);
	}
	
	@RequestMapping(value = "/new/{budgetId}", method = GET)
	public String showNewAccountForm(@PathVariable String budgetId, Model model) {
		model.addAttribute("account", new Account());
		return "account/new";
	}
	
	@RequestMapping(value = "/new/{budgetId}", method = POST)
	public String createAccount(@PathVariable String budgetId, @Validated(Validation.class) Account account, 
			@ModelAttribute Budget budget,
			Errors errors, Model model) {
		if(errors.hasErrors()) {
			return "account/new";
		}
		
		account.setBudget(budget);
		account = accountService.save(account);
		
		return "redirect:/account/" + account.getHiddenId();
	}
}
