package homebudget.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import homebudget.domain.Budget;
import homebudget.service.ActiveBudgetService;

@Controller
@RequestMapping("/transfer")
public class TransferCategoryNewDefaultController {
	private final ActiveBudgetService activeBudgetService;
	
	@Autowired
	public TransferCategoryNewDefaultController(ActiveBudgetService activeBudgetService) {
		this.activeBudgetService = activeBudgetService;
	}
	
	@RequestMapping(value = "/category", method = GET)
	public String showNewCategoryTransferFormWithActiveBudget() {
		Budget budget = activeBudgetService.get();
		return "redirect:/transfer/category/" + budget.getHiddenId();
	}
}
