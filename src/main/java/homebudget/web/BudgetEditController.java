package homebudget.web;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import homebudget.domain.AccessLevel;
import homebudget.domain.Budget;
import homebudget.domain.forms.AccessForm;
import homebudget.service.AccessToBudgetService;
import homebudget.service.BudgetService;

@Controller
@RequestMapping(value = "/budget/edit")
public class BudgetEditController {
	private final BudgetService budgetService;
	private final AccessToBudgetService accessToBudgetService;
	
	@Autowired
	public BudgetEditController(BudgetService budgetService, 
			AccessToBudgetService accessToBudgetService) {
		this.budgetService = budgetService;
		this.accessToBudgetService = accessToBudgetService;
	}
	
	
	@RequestMapping(value = "/{budgetId}", method = RequestMethod.GET)
	public String showEditBudgetForm(@PathVariable String budgetId, Model model, Principal principal) {
		Budget budget = budgetService.getBudgetByHiddenId(budgetId);
		model.addAttribute("budget", budget);
		
		Map<String, AccessLevel> accessLevels = new HashMap<String, AccessLevel>(); 
		if(isUserOwnsBudget(principal, budget))
			accessLevels = accessToBudgetService.getAccessLevelsToBudget(budget);
		model.addAttribute("accessLevels", accessLevels);
		
		AccessForm accessForm = new AccessForm();
		model.addAttribute("accessForm", accessForm);
		return "budget/edit";
	}

	private boolean isUserOwnsBudget(Principal principal, Budget budget) {
		return budget.getUser().getUsername().equals(principal.getName());
	}
	
	@RequestMapping(value = "/{budgetId}", method = RequestMethod.POST)
	public String editBudget(@PathVariable String budgetId, 
			@Validated(Budget.Validation.class) Budget budget, Errors errors, 
			Principal principal) {
		if(errors.hasErrors()) {
			return "budget/edit";
		}
		
		budget.setHiddenId(budgetId);
		budgetService.update(budget, principal.getName());
		return "redirect:/budgets/" + budgetId;
	}
}
