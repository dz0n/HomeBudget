package homebudget.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import homebudget.domain.AccessLevel;
import homebudget.domain.Budget;
import homebudget.domain.forms.AccessForm;
import homebudget.service.AccessToBudgetService;
import homebudget.service.BudgetService;
import homebudget.service.IdHider;

@Controller
@RequestMapping("/budget/access")
public class AccessToBudgetController {
	@Autowired
	private AccessToBudgetService accessToBudgetService; 
	
	@Autowired
	private BudgetService budgetService;
	
	@Autowired
	private IdHider idHider;
	
	@RequestMapping(value="/new/{budgetId}", method=POST)
	public String addAccessLevel(@Valid AccessForm accessForm, Errors errors,
			Model model, @PathVariable String budgetId,
			final RedirectAttributes redirectAttributes) {
		Budget budget = budgetService.getBudgetById(idHider.getId(Budget.class, budgetId));
		budget = setHashid(budget);
		model.addAttribute("budget", budget);
		
		if(errors.hasErrors()) {
			return "budget/edit";
		}
		
		try {
			accessToBudgetService.addAccessToBudget(budget, accessForm.getUsername(), accessForm.getAccessLevel());
			redirectAttributes.addFlashAttribute("msgSuccess", "Dostęp nadany.");
		} catch(EmptyResultDataAccessException e) {
			redirectAttributes.addFlashAttribute("msgError", "Nie udało się dodać dostępu.");
		}
		
		return "redirect:/budget/edit/" + budgetId;
	}
	
	@RequestMapping(value="/edit/{budgetId}/{username}", method=GET)
	public String showEditForm(@PathVariable String budgetId, @PathVariable String username, Model model) {
		Budget budget = budgetService.getBudgetById(idHider.getId(Budget.class, budgetId));
		budget = setHashid(budget);
		model.addAttribute("budget", budget);
		
		AccessLevel accessLevel = accessToBudgetService.getAccessLevel(budget, username);
		model.addAttribute("accessLevel", accessLevel);
		model.addAttribute("username", username);
		return "budget/access/edit";
	}
	
	@RequestMapping(value="/edit/{budgetId}/{username}", method=POST)
	public String updateAccessLevel(@RequestParam AccessLevel accessLevel,
			@PathVariable String budgetId, @PathVariable String username,
			final RedirectAttributes redirectAttributes) {
		Budget budget = budgetService.getBudgetById(idHider.getId(Budget.class, budgetId));
		accessToBudgetService.addAccessToBudget(budget, username, accessLevel);
		
		redirectAttributes.addFlashAttribute("msgSuccess", "Zaktualizowano poziom dostępu.");
		return "redirect:/budget/edit/" + budgetId;
	}
	
	@RequestMapping(value="/delete/{budgetId}/{username}", method=GET)
	public String removeAccessLevel(@PathVariable String budgetId, @PathVariable String username,
			final RedirectAttributes redirectAttributes) {
		Budget budget = budgetService.getBudgetById(idHider.getId(Budget.class, budgetId));
		accessToBudgetService.removeAccessToBudget(budget, username);
		
		redirectAttributes.addFlashAttribute("msgSuccess", "Usunięto dostęp.");
		return "redirect:/budget/edit/" + budgetId;
	}
	
	private Budget setHashid(Budget budget) {
		budget.setHiddenId(idHider.getHiddenId(Budget.class, budget.getId()));
		return budget;
	}
}
