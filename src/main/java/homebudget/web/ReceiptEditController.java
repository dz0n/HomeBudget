package homebudget.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import homebudget.domain.Account;
import homebudget.domain.Budget;
import homebudget.domain.Category;
import homebudget.domain.Receipt;
import homebudget.domain.Subcategory;
import homebudget.domain.forms.ReceiptForm;
import homebudget.domain.forms.ReceiptForm.Type;
import homebudget.service.AccountService;
import homebudget.service.CategoryService;
import homebudget.service.ReceiptService;
import homebudget.service.SubcategoryService;

@Controller
@RequestMapping("/receipt/edit")
public class ReceiptEditController {
	private static final int MAX_LAST_RECEIPTS = 5;
	
	private final ReceiptService receiptService;
	private final SubcategoryService subcategoryService;
	private final CategoryService categoryService;
	private final AccountService accountService;
	
	@Autowired
	public ReceiptEditController(ReceiptService receiptService, SubcategoryService subcategoryService, 
			CategoryService categoryService, AccountService accountService) {
		this.receiptService = receiptService;
		this.subcategoryService = subcategoryService;
		this.categoryService = categoryService;
		this.accountService = accountService;
	}
	
	@ModelAttribute("receipt")
	public Receipt addReceipt(@PathVariable String receiptId) {
		return receiptService.getByHiddenId(receiptId);
	}
	
	@ModelAttribute("budget")
	public Budget addBudget(@ModelAttribute Receipt receipt) {
		return receipt.getAccount().getBudget();
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
	
	@RequestMapping(value="/{receiptId}", method=GET)
	public String showEditReceiptForm(@PathVariable String receiptId, Model model, 
			@ModelAttribute Receipt receipt, 
			final RedirectAttributes redirectAttributes) {
		ReceiptForm form = createReceiptForm(receipt);
		model.addAttribute("receiptForm", form);
		return "receipt/edit";
	}
	
	private ReceiptForm createReceiptForm(Receipt receipt) {
		ReceiptForm form = new ReceiptForm();
		if(receipt.getLongValue()<0) {
			form.setType(Type.EXPENSE);
			form.setValue(receipt.getValue().negate());
		} else {
			form.setType(Type.INCOME);
			form.setValue(receipt.getValue());
		}
		form.setName(receipt.getName());
		form.setDescription(receipt.getDescription());
		form.setDate(new Date(receipt.getDate().getTime()));
		if(receipt.getAccount()!=null && receipt.getAccount().getHiddenId()==null) {
			form.setAccountId(receipt.getAccount().getHiddenId());
		}
		if(receipt.getSubcategory()!=null && receipt.getSubcategory().getCategory()!=null 
				&& receipt.getSubcategory().getCategory().getHiddenId()!=null) {
			form.setCategoryId(receipt.getSubcategory().getCategory().getHiddenId());
		}
		if(receipt.getSubcategory()!=null && receipt.getSubcategory().getHiddenId()!=null) {
			form.setSubcategoryId(receipt.getSubcategory().getHiddenId());
		}
		return form;
	}
	
	@RequestMapping(value="/{receiptId}", method=POST)
	public String editReceipt(@Valid ReceiptForm receiptForm, Errors errors, Model model, 
			@ModelAttribute Receipt receipt,
			@PathVariable String receiptId, Principal principal, 
			final RedirectAttributes redirectAttributes) {
		if(errors.hasErrors()) {
			return "receipt/edit";
		}
		
		Subcategory subcategory = subcategoryService.getSubcategoryByHiddenId(receiptForm.getSubcategoryId());
		Account account = accountService.getAccountByHiddenId(receiptForm.getAccountId());
		if(!subcategory.getCategory().getBudget().equals(account.getBudget())) {
			model.addAttribute("msgError", "Podkategoria i konto muszą należeć do tego samego budżetu.");
			return "receipt/new";
		}
		
		receipt.setSubcategory(subcategory);
		receipt.setAccount(account);
		receipt = fillReceiptWithFormValues(receipt, receiptForm);
		receipt = receiptService.update(receipt);
		
		redirectAttributes.addFlashAttribute("msgSuccess", "Zmodyfikowano wydatek/wpływ.");
		return "redirect:/receipt/edit/" + receipt.getHiddenId();
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
}
