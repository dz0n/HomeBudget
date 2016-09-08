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

import homebudget.domain.Category;
import homebudget.domain.Category.Validation;
import homebudget.domain.Budget;
import homebudget.service.CategoryService;
import homebudget.service.BudgetService;

@Controller
@RequestMapping(value = "/category")
public class CategoryNewController {
	private final BudgetService budgetService;
	private final CategoryService categoryService;
	
	@Autowired
	public CategoryNewController(BudgetService budgetService, CategoryService categoryService) {
		this.budgetService = budgetService;
		this.categoryService = categoryService;
	}
	
	@ModelAttribute("budget")
	public Budget addBudget(@PathVariable String budgetId) {
		return budgetService.getBudgetByHiddenId(budgetId);
	}
	
	@RequestMapping(value = "/new/{budgetId}", method = GET)
	public String showNewCategoryForm(@PathVariable String budgetId, Model model) {
		model.addAttribute("category", new Category());
		return "category/new";
	}
	
	@RequestMapping(value = "/new/{budgetId}", method = POST)
	public String createCategory(@PathVariable String budgetId, @Validated(Validation.class) Category category, 
			@ModelAttribute Budget budget,
			Errors errors, Model model) {
		if(errors.hasErrors()) {
			return "category/new";
		}
		
		category.setBudget(budget);
		category = categoryService.save(category);
		
		return "redirect:/category/" + category.getHiddenId();
	}
}
