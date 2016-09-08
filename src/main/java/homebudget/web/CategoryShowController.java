package homebudget.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.math.BigDecimal;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import homebudget.domain.Category;
import homebudget.domain.Receipt;
import homebudget.domain.Subcategory;
import homebudget.service.BudgetCalculationService;
import homebudget.service.CategoryService;
import homebudget.service.ReceiptService;
import homebudget.service.SubcategoryService;

@Controller
@RequestMapping(value = "/category")
public class CategoryShowController {
	private static final int MAX_LAST_RECEIPTS = 10;
	
	private final CategoryService categoryService;
	private final ReceiptService receiptService;
	private final SubcategoryService subcategoryService;
	private final BudgetCalculationService budgetCalculationService;
	
	@Autowired
	public CategoryShowController(CategoryService categoryService,
			ReceiptService receiptService, 
			SubcategoryService subcategoryService,
			BudgetCalculationService budgetCalculationService) {
		this.categoryService = categoryService;
		this.receiptService = receiptService;
		this.subcategoryService = subcategoryService;
		this.budgetCalculationService = budgetCalculationService;
	}
	
	@ModelAttribute("category")
	public Category addCategory(@PathVariable String categoryId) {
		return categoryService.getCategoryByHiddenId(categoryId);
	}
	

	@ModelAttribute("receipts")
	public List<Receipt> addLastReceipts(@ModelAttribute Category category) {
		return receiptService.getReceipts(category, 0, MAX_LAST_RECEIPTS);
	}

	@ModelAttribute("monthBalance")
	public BigDecimal addMonthBalance(@ModelAttribute Category category) {
		return budgetCalculationService.getMonthBalance(category);
	}
	
	@ModelAttribute("subcategories")
	public List<Subcategory> addSubcategories(@ModelAttribute Category category) {
		List<Subcategory> subcategories = subcategoryService.getSubcategories(category);
		subcategories = sortCategoriesByName(subcategories);
		return subcategories;
	}
	
	private List<Subcategory> sortCategoriesByName(List<Subcategory> subcategories) {
		Collections.sort(subcategories, getSubcategoryComparator());
		return subcategories;
	}

	private Comparator<Subcategory> getSubcategoryComparator() {
		return new Comparator<Subcategory>() {
			@Override
			public int compare(Subcategory first, Subcategory second) {
				if(first.getName()==null && second.getName()==null)
					return 0;
				Collator collator = Collator.getInstance(LocaleContextHolder.getLocale());
				return collator.compare(first.getName(), second.getName());
			}
		};
	}
	
	@RequestMapping(value = "/{categoryId}", method = GET)
	public String showCategory(@PathVariable String categoryId) {
		return "category/category";
	}
	
	@RequestMapping(value = "/history/{categoryId}/{start}/{max}", method = GET)
	public String showCategoryHistory(@PathVariable String categoryId,
			@PathVariable int start, @PathVariable int max, 
			@ModelAttribute Category category, Model model) {
		if(start<0)
			start = start * -1;
		model.addAttribute("start", start);
		
		if(max == 0)
			max = 10;
		if(max < 0)
			max = max * -1;
		model.addAttribute("max", max);
		
		int receiptsCount = receiptService.count(category);
		int pagesCount = 1;
		if(receiptsCount>0)
			pagesCount = setPagesCounter(max, receiptsCount);
		model.addAttribute("pagesCount", pagesCount);
		
		List<Receipt> receipts = receiptService.getReceipts(category, start, max);
		model.addAttribute("receipts", receipts);
		return "category/history";
	}
	
	private int setPagesCounter(int max, int count) {
		return (int) Math.ceil((double) count / (double) max);
	}
	
	@RequestMapping(value = "/history/{categoryId}", method = RequestMethod.GET)
	public String showDefaultBudgetHistory(@PathVariable String categoryId) {
		return "redirect:/category/history/" + categoryId + "/0/" + MAX_LAST_RECEIPTS;
	}
}
