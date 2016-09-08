//package homebudget.web;
//
//import static org.springframework.web.bind.annotation.RequestMethod.GET;
//import static org.springframework.web.bind.annotation.RequestMethod.POST;
//
//import java.math.BigDecimal;
//import java.security.Principal;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.validation.Valid;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.Errors;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import homebudget.domain.Budget;
//import homebudget.domain.Category;
//import homebudget.domain.CategoryTransfer;
//import homebudget.domain.Subcategory;
//import homebudget.domain.forms.CategoryTransferForm;
//import homebudget.service.ActiveBudgetService;
//import homebudget.service.BudgetService;
//import homebudget.service.CategoryService;
//import homebudget.service.CategoryTransferService;
//import homebudget.service.IdHider;
//import homebudget.service.UserService;
//
//@Controller
//@RequestMapping("/transfer")
//public class TransferCategoryController {
//	private static final int MAX_LAST_TRANSFERS = 5;
//	
//	private CategoryTransferService categoryTransferService;
//	private ActiveBudgetService activeBudgetService;
//	private CategoryService categoryService;
//	private BudgetService budgetService;
//	private UserService userService;
//	private IdHider idHider;
//	
//	@Autowired
//	public TransferCategoryController(CategoryTransferService categoryTransferService,
//			ActiveBudgetService activeBudgetService, CategoryService categoryService, BudgetService budgetService,
//			UserService userService, IdHider idHider) {
//		this.categoryTransferService = categoryTransferService;
//		this.activeBudgetService = activeBudgetService;
//		this.categoryService = categoryService;
//		this.budgetService = budgetService;
//		this.userService = userService;
//		this.idHider = idHider;
//	}
//	
//	@RequestMapping(value = "/category", method = GET)
//	public String showNewCategoryTransferFormWithActiveBudget() {
//		Budget budget = activeBudgetService.get();
//		String id = idHider.getHiddenId(Budget.class, budget.getId());
//		return "redirect:/transfer/category/" + id;
//	}
//	
//	@RequestMapping(value = "/category/{budgetId}", method = GET)
//	public String showNewCategoryTransferForm(@PathVariable String budgetId, Model model) {
//		model.addAttribute("categoryTransferForm", new CategoryTransferForm());
//		Budget budget = activeBudgetService.get();
//		addBudgetToModel(budget, model);
//		addLastTransfersListToModel(categoryTransferService.getTransfers(budget, 0, MAX_LAST_TRANSFERS), model);
//		return "transfer/category/new";
//	}
//	
//	@RequestMapping(value = "/category/{budgetId}", method = POST)
//	public String createNewCategoryTransfer(@Valid CategoryTransferForm categoryTransferForm, Errors errors, 
//			@PathVariable String budgetId, Model model,
//			Principal principal, final RedirectAttributes redirectAttributes) {
//		Budget budget = activeBudgetService.get();
//		CategoryTransfer transfer = transformFormToTransfer(categoryTransferForm);
//		transfer.setUser(userService.getUserByUsername(principal.getName()));
//		if(errors.hasErrors() || transfer==null){
//			addBudgetToModel(budget, model);
//			addLastTransfersListToModel(categoryTransferService.getTransfers(budget, 0, MAX_LAST_TRANSFERS), model);
//			model.addAttribute("msgError", "Nie udało się stworzyć transferu.");
//			return "transfer/category/new";
//		}
//		
//		categoryTransferService.save(transfer);
//		redirectAttributes.addFlashAttribute("msgSuccess", "Przelew dodany.");
//		return "redirect:/transfer/category/new";
//	}
//	
//	@RequestMapping(value = "/category/delete/{transferId}", method = GET)
//	public String showDeleteCategoryTransferPage(@PathVariable String transferId, Model model) {
//		Long id = idHider.getId(CategoryTransfer.class, transferId);
//		CategoryTransfer transfer = categoryTransferService.getById(id);
//		transfer = fillTransferAndItsFieldsWithHashIds(transfer);
//		
//		model.addAttribute("transfer", transfer);
//		return "transfer/category/delete";
//	}
//	
//	@RequestMapping(value="/category/delete/{transferId}/confirm", method=GET)
//	public String deleteCategoryTransferConfirmed(@PathVariable String transferId, final RedirectAttributes redirectAttributes) {
//		Long id = idHider.getId(CategoryTransfer.class, transferId);
//		if(id == null) {
//			redirectAttributes.addFlashAttribute("msgError", "Nie udało się usunąć wydatku, nieprawidłowe id.");
//			return "redirect:/budget";
//		}
//		CategoryTransfer transfer = categoryTransferService.getById(id);
//		transfer = fillTransferAndItsFieldsWithHashIds(transfer);
//		
//		categoryTransferService.delete(transfer);
//		return "redirect:/budget";
//	}
//	
//	@RequestMapping(value = "/category/edit/{transferId}", method = GET)
//	public String showEditCategoryTransferForm(@PathVariable String transferId, Model model, final RedirectAttributes redirectAttributes) {
//		Long id = idHider.getId(CategoryTransfer.class, transferId);
//		if(id == null) {
//			redirectAttributes.addFlashAttribute("msgError", "Nieprawidłowe id.");
//			return "redirect:/budget";
//		}
//		CategoryTransfer transfer = categoryTransferService.getById(id);
//		transfer = fillTransferAndItsFieldsWithHashIds(transfer);
//		model.addAttribute("transfer", transfer);
//		
//		CategoryTransferForm form = createCategoryTransferForm(transfer);
//		model.addAttribute("categoryTransferForm", form);
//		
//		Budget budget = transfer.getToCategory().getBudget();
//		addBudgetToModel(budget, model);
//		addLastTransfersListToModel(categoryTransferService.getTransfers(budget, 0, MAX_LAST_TRANSFERS), model);
//		return "transfer/category/edit";
//	}
//	
//	@RequestMapping(value = "/category/edit/{transferId}", method = POST)
//	public String editCategoryTransfer(@Valid CategoryTransferForm categoryTransferForm, Errors errors, 
//			@PathVariable String transferId, Model model,
//			Principal principal, final RedirectAttributes redirectAttributes) {
//		Long id = idHider.getId(CategoryTransfer.class, transferId);
//		if(id == null) {
//			redirectAttributes.addFlashAttribute("msgError", "Nieprawidłowe id.");
//			return "redirect:/budget";
//		}
//		
//		CategoryTransfer transfer = categoryTransferService.getById(id);
//		transfer = fillTransferAndItsFieldsWithHashIds(transfer);
//		if(transfer == null) {
//			redirectAttributes.addFlashAttribute("msgError", "Brak przelewu - sprawdź czy nie został usunięty.");
//			return "redirect:/budget";
//		}
//		
//		Budget budget = transfer.getToCategory().getBudget();
//		if(errors.hasErrors()){
//			addBudgetToModel(budget, model);
//			addLastTransfersListToModel(categoryTransferService.getTransfers(budget, 0, MAX_LAST_TRANSFERS), model);
//			return "redirect:/transfer/category/edit/" + transfer.getHiddenId();
//		}
//		
//		transfer = updateTransferFieldsWithFormValues(transfer, categoryTransferForm);
//		try {
//			transfer.setFromCategory(getCategory(categoryTransferForm.getFromCategoryId()));
//			transfer.setToCategory(getCategory(categoryTransferForm.getToCategoryId()));
//		} catch(NullPointerException e) {
//			addBudgetToModel(budget, model);
//			addLastTransfersListToModel(categoryTransferService.getTransfers(budget, 0, MAX_LAST_TRANSFERS), model);
//			redirectAttributes.addFlashAttribute("msgError", "Niepoprawne konto.");
//			return "redirect:/transfer/category/edit/" + transfer.getHiddenId();
//		}
//		if(transfer.getFromCategory()==null || transfer.getToCategory()==null) {
//			addBudgetToModel(budget, model);
//			addLastTransfersListToModel(categoryTransferService.getTransfers(budget, 0, MAX_LAST_TRANSFERS), model);
//			redirectAttributes.addFlashAttribute("msgError", "Niepoprawne konto.");
//			return "redirect:/transfer/category/edit/" + transfer.getHiddenId();
//		}
//		
//		if(transfer.getUser()==null)
//			transfer.setUser(userService.getUserByUsername(principal.getName()));
//		
//		transfer = categoryTransferService.update(transfer);
//		transfer = setHashid(transfer);
//		redirectAttributes.addFlashAttribute("msgSuccess", "Przelew zmieniony.");
//		return "redirect:/transfer/category/edit/" + transfer.getHiddenId();
//	}
//	
//	@RequestMapping(value = "/category/history/{budgetId}/{start}/{max}", method = RequestMethod.GET)
//	public String showTransfersHistory(@PathVariable String budgetId, @PathVariable int start, @PathVariable int max, Model model) {
//		Long id = idHider.getId(Budget.class, budgetId);
//		if(id == null)
//			return "redirect:/budget";
//		Budget budget = budgetService.getBudgetById(id);
//		budget = fillBudgetAndItsFieldsWithHashIds(budget);	
//		model.addAttribute("budget", budget);
//		
//		if(start<0)
//			start = start * -1;
//		model.addAttribute("start", start);
//		
//		if(max == 0)
//			max = 10;
//		if(max < 0)
//			max = max * -1;
//		model.addAttribute("max", max);
//		
//		int count = categoryTransferService.count(budget);
//		int pagesCount = 1;
//		if(count>0)
//			pagesCount = (int) Math.ceil((double) count / (double) max);
//		model.addAttribute("pagesCount", pagesCount);
//		
//		List<CategoryTransfer> transfers = categoryTransferService.getTransfers(budget, start, max);
//		for(CategoryTransfer transfer : transfers) {
//			transfer.setHiddenId(idHider.getHiddenId(CategoryTransfer.class, transfer.getId()));
//		}
//		model.addAttribute("transfers", transfers);
//		return "transfer/category/history";
//	}
//	
//	@RequestMapping(value = "/category/history", method = RequestMethod.GET)
//	public String showDefaultTransfersHistory() {
//		Budget budget = activeBudgetService.get();
//		String id = idHider.getHiddenId(Budget.class, budget.getId());
//		return "redirect:/transfer/category/history/" + id + "/0/" + MAX_LAST_TRANSFERS;
//	}
//	
//	private CategoryTransfer updateTransferFieldsWithFormValues(CategoryTransfer transfer, CategoryTransferForm form) {
//		transfer.setName(form.getName());
//		transfer.setDescription(form.getDescription());
//		transfer.setValue(form.getValue().doubleValue());
//		transfer.setDate(new Date(form.getDate().getTime()));
//		return transfer;
//	}
//	
//	private Category getCategory(long id) {
//		Long fromCategoryId = idHider.getId(Category.class, Long.toString(id));
//		if(fromCategoryId == null) {
//			return null;
//		}
//		return categoryService.getCategoryById(fromCategoryId);
//	}
//	
//	private CategoryTransferForm createCategoryTransferForm(CategoryTransfer transfer) {
//		CategoryTransferForm form = new CategoryTransferForm();
//		form.setName(transfer.getName());
//		form.setDescription(transfer.getDescription());
//		form.setValue(BigDecimal.valueOf(transfer.getValue()));
//		form.setFromCategoryId(transfer.getFromCategory().getId());
//		form.setToCategoryId(transfer.getToCategory().getId());
//		form.setDate(new Date(transfer.getDate().getTime()));
//		return form;
//	}
//	
//	private CategoryTransfer fillTransferAndItsFieldsWithHashIds(CategoryTransfer transfer) {
//		transfer = setHashid(transfer);
//		
//		if(transfer.getToCategory() != null) {
//			transfer.getToCategory().setHiddenId(idHider.getHiddenId(Category.class, transfer.getToCategory().getId()));
//			if(transfer.getToCategory().getBudget() != null) {
//				transfer.getToCategory().getBudget().setHiddenId(idHider.getHiddenId(Budget.class, transfer.getToCategory().getBudget().getId()));
//			}
//		}
//		
//		if(transfer.getFromCategory() != null) {
//			transfer.getFromCategory().setHiddenId(idHider.getHiddenId(Category.class, transfer.getFromCategory().getId()));
//			if(transfer.getFromCategory().getBudget() != null) {
//				transfer.getFromCategory().getBudget().setHiddenId(idHider.getHiddenId(Budget.class, transfer.getFromCategory().getBudget().getId()));
//			}
//		}
//		return transfer;
//	}
//	
//	private void addBudgetToModel(Budget budget, Model model) {
//		budget = fillBudgetAndItsFieldsWithHashIds(budget);
//		model.addAttribute("budget", budget);
//	}
//
//	private Budget fillBudgetAndItsFieldsWithHashIds(Budget budget) {
//		budget.setHiddenId(idHider.getHiddenId(Budget.class, budget.getId()));
//		return budget;
//	}
//
//	private CategoryTransfer transformFormToTransfer(CategoryTransferForm form) {
//		try{
//			return transformFormToTransferWithReferences(form);
//		} catch(NullPointerException e) {
//			return null;
//		}
//	}
//
//	private CategoryTransfer transformFormToTransferWithReferences(CategoryTransferForm form) {
//		CategoryTransfer transfer = getTransferWithCategories(form);
//		copyFormFieldsToTransfer(form, transfer);
//		return transfer;
//	}
//
//	private CategoryTransfer copyFormFieldsToTransfer(CategoryTransferForm form, CategoryTransfer transfer) {
//		transfer.setName(form.getName());
//		transfer.setDescription(form.getDescription());
//		transfer.setValue(form.getValue().doubleValue());
//		transfer.setDate(new Date(form.getDate().getTime()));
//		return transfer;
//	}
//	
//	private CategoryTransfer getTransferWithCategories(CategoryTransferForm form) {
//		Category fromCategory = categoryService.getCategoryById(idHider.getId(Category.class, Long.toString(form.getFromCategoryId())));
//		Category toCategory = categoryService.getCategoryById(idHider.getId(Category.class, Long.toString(form.getToCategoryId())));
//		if(!fromCategory.getBudget().equals(toCategory.getBudget())){
//			return null;
//		}
//		CategoryTransfer transfer = new CategoryTransfer();
//		transfer.setFromCategory(fromCategory);
//		transfer.setToCategory(toCategory);
//		return transfer;
//	}
//	
//	private void addLastTransfersListToModel(List<CategoryTransfer> lastTransfers, Model model) {
//		for(CategoryTransfer transfer : lastTransfers) {
//			transfer = setHashid(transfer);
//		}
//		model.addAttribute("transfers", lastTransfers);
//	}
//	
//	private CategoryTransfer setHashid(CategoryTransfer transfer) {
//		transfer.setHiddenId(idHider.getHiddenId(CategoryTransfer.class, transfer.getId()));
//		return transfer;
//	}
//}
