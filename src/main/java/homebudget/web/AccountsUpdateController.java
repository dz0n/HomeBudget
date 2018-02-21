package homebudget.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import homebudget.domain.Account;
import homebudget.domain.Budget;
import homebudget.domain.Category;
import homebudget.domain.Subcategory;
import homebudget.service.AccountService;
import homebudget.service.AccountsUpdateService;
import homebudget.service.ActiveBudgetService;
import homebudget.service.CategoryService;
import homebudget.service.SubcategoryService;

@Controller
@RequestMapping("/accountsUpdate")
public class AccountsUpdateController {
	private final AccountsUpdateService accountsUpdateService;
	private final AccountService accountService;
	private final SubcategoryService subcategoryService;
	private final CategoryService categoryService;
	private final ActiveBudgetService activeBudgetService;
	
	private final String subcategoryParamName = "subcategoryId";
	private final String accountParamName = "accountId";
	private final String valueParamName = "value";
	
	@Autowired
	public AccountsUpdateController(AccountsUpdateService accountsUpdateService, AccountService accountService,
			SubcategoryService subcategoryService, CategoryService categoryService,
			ActiveBudgetService activeBudgetService) {
		this.accountsUpdateService = accountsUpdateService;
		this.accountService = accountService;
		this.subcategoryService = subcategoryService;
		this.categoryService = categoryService;
		this.activeBudgetService = activeBudgetService;
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
		Map<String, List<String>> subcategoriesNames = new HashMap<String, List<String>>();
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
	
	@RequestMapping(method=GET)
	public String showForm() {
		return "account/updateValue";
	}
	
	@RequestMapping(method=POST)
	public String updateAccounts(@RequestParam Map<String, String> params, final RedirectAttributes redirectAttributes) {
		Subcategory subcategory = subcategoryService.getSubcategoryByHiddenId(params.get(subcategoryParamName));
		
		for(Map.Entry<String, String> param : params.entrySet()) {
			if(param.getKey().toLowerCase().contains(accountParamName.toLowerCase())) {
				String paramId = getAccountId(param.getKey());
				String value = params.get(valueParamName + paramId);
				Account account = accountService.getAccountByHiddenId(param.getValue());
				accountsUpdateService.updateAccount(account, BigDecimal.valueOf(Double.parseDouble(value)), subcategory);
			}
		}
		
		redirectAttributes.addFlashAttribute("msgSuccess", "Zaktualizowano wartość kont.");
		return "redirect:/budget";
	}
	
	private String getAccountId(String parameter) {
		return parameter.substring(accountParamName.length(), parameter.length());
	}
}
