package homebudget.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.math.BigDecimal;
import java.security.Principal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import homebudget.domain.Account;
import homebudget.domain.Budget;
import homebudget.domain.Category;
import homebudget.domain.Receipt;
import homebudget.domain.Subcategory;
import homebudget.domain.forms.ReceiptForm;
import homebudget.service.AccountService;
import homebudget.service.ActiveBudgetService;
import homebudget.service.BudgetCalculationService;
import homebudget.service.CategoryService;
import homebudget.service.ReceiptService;
import homebudget.service.SubcategoryService;
import homebudget.service.UserService;

@Controller
@RequestMapping("/receipt")
public class ReceiptNewController {
	private static final int MAX_LAST_RECEIPTS = 5;
	
	private final ActiveBudgetService activeBudgetService;
	private final ReceiptService receiptService;
	private final SubcategoryService subcategoryService;
	private final CategoryService categoryService;
	private final AccountService accountService;
	private final UserService userService;
	private final BudgetCalculationService budgetCalculationService;
	
	@Autowired
	public ReceiptNewController(ActiveBudgetService activeBudgetService, ReceiptService receiptService,
			SubcategoryService subcategoryService, CategoryService categoryService, AccountService accountService,
			UserService userService, BudgetCalculationService budgetCalculationService) {
		this.activeBudgetService = activeBudgetService;
		this.receiptService = receiptService;
		this.subcategoryService = subcategoryService;
		this.categoryService = categoryService;
		this.accountService = accountService;
		this.userService = userService;
		this.budgetCalculationService = budgetCalculationService;
	}
	
	@ModelAttribute("budget")
	public Budget addBudget() {
		return activeBudgetService.get();
	}
	
	@ModelAttribute("accounts")
	public List<Account> addAccounts(@ModelAttribute Budget budget) {
		return accountService.getAccounts(budget);
	}
	
	@ModelAttribute("categories")
	public List<Category> addCategories(@ModelAttribute Budget budget) {
		return categoryService.getCategories(budget);
	}
	
	@ModelAttribute
	public void addSubcategoriesToModel(@ModelAttribute Budget budget, Model model) {
		//Map<id of category, List<name of subcategory>>
		Map<String, List<String>> subcategoriesNames = new HashMap<String, List<String>>();
		//Map<id of category, List<id of subcategory>>
		Map<String, List<String>> subcategoriesHiddenIds = new HashMap<String, List<String>>();
		
		List<Category> categories = categoryService.getCategories(budget);
		for(Category category : categories) {
			List<Subcategory> subcategories = subcategoryService.getSubcategories(category);
	
			ArrayList<String> subs = new ArrayList<String>();
			ArrayList<String> ids = new ArrayList<String>();
			for(Subcategory subcategory : subcategories) {
				subs.add(subcategory.getName());
				ids.add(subcategory.getHiddenId());
			}
			
			subcategoriesNames.put(category.getHiddenId(), subs);
			subcategoriesHiddenIds.put(category.getHiddenId(), ids);
		}
		model.addAttribute("subNames", subcategoriesNames);
		model.addAttribute("subIds", subcategoriesHiddenIds);
	}
	
	@ModelAttribute("receipts")
	public List<Receipt> addLastReceipts(@ModelAttribute Budget budget) {
		return receiptService.getReceipts(budget, 0, MAX_LAST_RECEIPTS);
	}

	@RequestMapping(method=GET)
	public String showNewReceiptForm(Model model, @ModelAttribute Budget budget) {
		model.addAttribute("receiptForm", new ReceiptForm());
		return "receipt/new";
	}
	
	@RequestMapping(method=POST)
	public String createNewReceipt(@Valid ReceiptForm receiptForm, Errors errors, Model model, 
			Principal principal, @ModelAttribute Budget budget,
			final RedirectAttributes redirectAttributes) {
		if(errors.hasErrors()) {
			return "receipt/new";
		}
		
		Subcategory subcategory = subcategoryService.getSubcategoryByHiddenId(receiptForm.getSubcategoryId());
		Account account = accountService.getAccountByHiddenId(receiptForm.getAccountId());
		if(!subcategory.getCategory().getBudget().equals(account.getBudget())) {
			model.addAttribute("msgError", "Podkategoria i konto muszą należeć do tego samego budżetu.");
			return "receipt/new";
		}
		
		Receipt receipt = new Receipt();
		receipt.setUser(userService.getUserByUsername(principal.getName()));
		receipt.setAccount(account);
		receipt.setSubcategory(subcategory);
		receipt = fillReceiptWithFormValues(receipt, receiptForm);
		receiptService.save(receipt);
		
		BigDecimal sumInCurrentMonth = budgetCalculationService.getMonthBalance(budget);
		NumberFormat numberFormat = getNumberFormat();
		redirectAttributes.addFlashAttribute("msgSuccess", "Wydatek/wpływ dodany. "
				+ "Saldo wpływów i wydatków w bieżącym miesiącu wynosi " + numberFormat.format(sumInCurrentMonth) + " zł.");
		return "redirect:/receipt";
	}
	
	private Receipt fillReceiptWithFormValues(Receipt receipt, ReceiptForm form) {
		receipt.setName(form.getName());
		receipt.setDescription(form.getDescription());
		if(form.getType() == ReceiptForm.Type.EXPENSE) {
			receipt.setValue(form.getValue().negate());
		} else {
			receipt.setValue(form.getValue());
		}
		receipt.setDate(new Date(form.getDate().getTime()));
		return receipt;
	}
	
	private NumberFormat getNumberFormat() {
		Locale locale = LocaleContextHolder.getLocale();
		NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
		numberFormat.setMinimumFractionDigits(2);
		numberFormat.setMaximumFractionDigits(2);
		numberFormat.setMinimumIntegerDigits(1);
		return numberFormat;
	}
}
