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

import homebudget.domain.Budget;
import homebudget.domain.Category;
import homebudget.domain.CategoryTransfer;
import homebudget.domain.forms.CategoryTransferForm;
import homebudget.service.CategoryService;
import homebudget.service.CategoryTransferService;
import homebudget.service.UserService;

@Controller
@RequestMapping("/transfer")
public class TransferCategoryEditController {
	private static final int MAX_LAST_TRANSFERS = 5;
	
	private final CategoryTransferService categoryTransferService;
	private final CategoryService categoryService;
	private final UserService userService;
	
	@Autowired
	public TransferCategoryEditController(CategoryTransferService categoryTransferService,
			CategoryService categoryService, UserService userService) {
		this.categoryTransferService = categoryTransferService;
		this.categoryService = categoryService;
		this.userService = userService;
	}
	
	@ModelAttribute("transfer")
	public CategoryTransfer addTransfer(@PathVariable String transferId) {
		return categoryTransferService.getByHiddenId(transferId);
	}
	
	@ModelAttribute("budget")
	public Budget addBudget(@ModelAttribute("transfer") CategoryTransfer transfer) {
		return transfer.getFromCategory().getBudget();
	}
	
	@ModelAttribute("categories")
	public List<Category> addCategories(@ModelAttribute Budget budget) {
		return categoryService.getCategories(budget);
	}
	
	@ModelAttribute("transfers")
	public List<CategoryTransfer> addLastTransfer(@ModelAttribute Budget budget) {
		return categoryTransferService.getTransfers(budget, 0, MAX_LAST_TRANSFERS);
	}
	
	@RequestMapping(value = "/category/edit/{transferId}", method = GET)
	public String showEditCategoryTransferForm(@PathVariable String transferId, Model model,
			@ModelAttribute("transfer") CategoryTransfer transfer) {
		CategoryTransferForm form = getCategoryTransferForm(transfer);
		model.addAttribute("categoryTransferForm", form);
		return "transfer/category/edit";
	}
	
	private CategoryTransferForm getCategoryTransferForm(CategoryTransfer transfer) {
		CategoryTransferForm form = new CategoryTransferForm();
		form.setName(transfer.getName());
		form.setDescription(transfer.getDescription());
		form.setValue(transfer.getValue());
		form.setFromCategoryId(transfer.getFromCategory().getHiddenId());
		form.setToCategoryId(transfer.getToCategory().getHiddenId());
		form.setDate(new Date(transfer.getDate().getTime()));
		return form;
	}
	
	@RequestMapping(value = "/category/edit/{transferId}", method = POST)
	public String editCategoryTransfer(@Valid CategoryTransferForm categoryTransferForm, Errors errors, 
			@PathVariable String transferId, Model model,
			@ModelAttribute("transfer") CategoryTransfer transfer,
			Principal principal, final RedirectAttributes redirectAttributes) {
		if(errors.hasErrors()){
			return "redirect:/transfer/category/edit/" + transfer.getHiddenId();
		}
		
		transfer = updateTransferFieldsWithFormValues(transfer, categoryTransferForm);
		if(transfer.getUser()==null)
			transfer.setUser(userService.getUserByUsername(principal.getName()));
		transfer.setHiddenId(transferId);
		transfer = categoryTransferService.update(transfer);
		
		redirectAttributes.addFlashAttribute("msgSuccess", "Przelew zmieniony.");
		return "redirect:/transfer/category/edit/" + transfer.getHiddenId();
	}
	
	private CategoryTransfer updateTransferFieldsWithFormValues(CategoryTransfer transfer, CategoryTransferForm form) {
		transfer.setName(form.getName());
		transfer.setDescription(form.getDescription());
		transfer.setValue(form.getValue());
		transfer.setDate(new Date(form.getDate().getTime()));
		transfer.setFromCategory(categoryService.getCategoryByHiddenId(form.getFromCategoryId()));
		transfer.setToCategory(categoryService.getCategoryByHiddenId(form.getToCategoryId()));
		return transfer;
	}
}
