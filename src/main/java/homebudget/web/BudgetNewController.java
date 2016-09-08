package homebudget.web;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import homebudget.domain.Budget;
import homebudget.domain.User;
import homebudget.persist.UserRepository;
import homebudget.service.BudgetService;

@Controller
@RequestMapping(value = "/budget")
public class BudgetNewController {
	private final BudgetService budgetService;
	private final UserRepository userRepository;
	
	@Autowired
	public BudgetNewController(BudgetService budgetService,
			UserRepository userRepository) {
		this.budgetService = budgetService;
		this.userRepository = userRepository;
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String showNewBudgetForm(Model model) {
		model.addAttribute("budget", new Budget());
		return "budget/new";
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String createBudget(@Validated(Budget.Validation.class) Budget budget, Errors errors, Principal principal) {
		if(errors.hasErrors()) {
			return "budget/new";
		}
		User user = userRepository.getUserByUsername(principal.getName());
		budget.setUser(user);
		budget = budgetService.save(budget, principal.getName());
		return "redirect:/budgets/" + budget.getHiddenId();
	}
}
