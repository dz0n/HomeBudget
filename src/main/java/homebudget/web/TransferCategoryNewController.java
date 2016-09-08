package homebudget.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.security.Principal;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import homebudget.domain.Category;
import homebudget.domain.CategoryTransfer;
import homebudget.domain.Budget;
import homebudget.domain.forms.CategoryTransferForm;
import homebudget.service.CategoryService;
import homebudget.service.CategoryTransferService;
import homebudget.service.BudgetService;
import homebudget.service.UserService;

@Controller
@RequestMapping("/transfer")
public class TransferCategoryNewController {
	private static final int MAX_LAST_TRANSFERS = 5;
	
	private final CategoryTransferService categoryTransferService;
	private final CategoryService categoryService;
	private final BudgetService budgetService;
	private final UserService userService;
	
	@Autowired
	public TransferCategoryNewController(CategoryTransferService categoryTransferService,
			CategoryService categoryService, BudgetService budgetService,
			UserService userService) {
		this.categoryTransferService = categoryTransferService;
		this.categoryService = categoryService;
		this.budgetService = budgetService;
		this.userService = userService;
	}
	
	@ModelAttribute("budget")
	public Budget addBudget(@PathVariable String budgetId) {
		return budgetService.getBudgetByHiddenId(budgetId);
	}
	
	@ModelAttribute("categories")
	public List<Category> addCategories(@ModelAttribute Budget budget) {
		return categoryService.getCategories(budget);
	}
	
	@ModelAttribute("transfers")
	public List<CategoryTransfer> addLastTransfer(@ModelAttribute Budget budget) {
		return categoryTransferService.getTransfers(budget, 0, MAX_LAST_TRANSFERS);
	}
	
	@RequestMapping(value = "/category/{budgetId}", method = GET)
	public String showNewCategoryTransferForm(@PathVariable String budgetId, Model model) {
		model.addAttribute("categoryTransferForm", new CategoryTransferForm());
		return "transfer/category/new";
	}
	
	@RequestMapping(value = "/category/{budgetId}", method = POST)
	public String createNewCategoryTransfer(@Valid CategoryTransferForm categoryTransferForm, Errors errors, 
			@PathVariable String budgetId, Model model,
			@ModelAttribute Budget budget,
			Principal principal, final RedirectAttributes redirectAttributes) {
		if(errors.hasErrors()){
			return "transfer/category/new";
		}
		
		CategoryTransfer transfer = getTransfer(categoryTransferForm);
		transfer.setUser(userService.getUserByUsername(principal.getName()));
		transfer = categoryTransferService.save(transfer);
		
		redirectAttributes.addFlashAttribute("msgSuccess", "Przelew dodany.");
		return "redirect:/transfer/category/" + budget.getHiddenId();
	}
	
	private CategoryTransfer getTransfer(CategoryTransferForm form) {
		CategoryTransfer transfer = new CategoryTransfer();
		Category fromCategory = categoryService.getCategoryByHiddenId(form.getFromCategoryId());
		Category toCategory = categoryService.getCategoryByHiddenId(form.getToCategoryId());
		transfer.setFromCategory(fromCategory);
		transfer.setToCategory(toCategory);
		
		transfer.setName(form.getName());
		transfer.setDescription(form.getDescription());
		transfer.setValue(form.getValue());
		transfer.setDate(new Date(form.getDate().getTime()));
		return transfer;
	}
}
