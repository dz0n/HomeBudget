package homebudget.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import homebudget.domain.AccessLevel;
import homebudget.domain.Budget;
import homebudget.service.AccessToBudgetService;
import homebudget.service.ActiveBudgetService;
import homebudget.service.BudgetService;

@Controller
@RequestMapping(value = "/budgets")
public class BudgetsController {
	private final BudgetService budgetService;
	private final ActiveBudgetService activeBudgetService;
	private final AccessToBudgetService accessToBudgetService;

	@Autowired
	public BudgetsController(BudgetService budgetService,
			ActiveBudgetService activeBudgetService,
			AccessToBudgetService accessToBudgetService) {
		this.budgetService = budgetService;
		this.activeBudgetService = activeBudgetService;
		this.accessToBudgetService = accessToBudgetService;
	}

	@RequestMapping(method = GET)
	public String showBudgets(Model model, Principal principal) {
		List<Budget> budgets = budgetService.getBudgets(principal.getName());
		model.addAttribute("budgets", budgets);
		
		Map<String, AccessLevel> accessLevels = accessToBudgetService.getHiddenIdsOfAccessesToBudgets(principal.getName());
		model.addAttribute("accessLevels", accessLevels);
		return "budgets";
	}
	
	@RequestMapping(value = "/{budgetId}", method = GET)
	public String chooseBudget(@PathVariable String budgetId, Model model) {
		Budget budget = budgetService.getBudgetByHiddenId(budgetId);
		activeBudgetService.set(budget);
		return "redirect:/budget";
	}
	
}
