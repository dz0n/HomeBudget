package homebudget.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import homebudget.domain.Receipt;
import homebudget.domain.Subcategory;
import homebudget.service.BudgetCalculationService;
import homebudget.service.ReceiptService;
import homebudget.service.SubcategoryService;

@Controller
@RequestMapping(value = "/subcategory")
public class SubcategoryShowController {
	private static final int MAX_LAST_RECEIPTS = 10;
	
	private final SubcategoryService subcategoryService;
	private final ReceiptService receiptService;
	private final BudgetCalculationService budgetCalculationService;
	
	@Autowired
	public SubcategoryShowController(SubcategoryService subcategoryService,
			ReceiptService receiptService,
			BudgetCalculationService budgetCalculationService) {
		this.subcategoryService = subcategoryService;
		this.receiptService = receiptService;
		this.budgetCalculationService = budgetCalculationService;
	}
	
	@ModelAttribute("subcategory")
	public Subcategory addSubcategory(@PathVariable String subcategoryId) {
		return subcategoryService.getSubcategoryByHiddenId(subcategoryId);
	}
	
	@ModelAttribute("receipts")
	public List<Receipt> addLastReceipts(@ModelAttribute Subcategory subcategory) {
		return receiptService.getReceipts(subcategory, 0, MAX_LAST_RECEIPTS);
	}
	
	@ModelAttribute("monthBalance")
	public BigDecimal addMonthBalance(@ModelAttribute Subcategory subcategory) {
		return budgetCalculationService.getMonthBalance(subcategory);
	}
	
	@RequestMapping(value = "/{subcategoryId}", method = GET)
	public String showSubcategory(@PathVariable String subcategoryId) {
		return "subcategory/subcategory";
	}
	
	@RequestMapping(value = "/history/{subcategoryId}/{start}/{max}", method = GET)
	public String showSubcategoryHistory(@PathVariable String subcategoryId,
			@PathVariable int start, @PathVariable int max, 
			@ModelAttribute Subcategory subcategory, Model model) {
		if(start<0)
			start = start * -1;
		model.addAttribute("start", start);
		
		if(max == 0)
			max = 10;
		if(max < 0)
			max = max * -1;
		model.addAttribute("max", max);
		
		int receiptsCount = receiptService.count(subcategory);
		int pagesCount = 1;
		if(receiptsCount>0)
			pagesCount = setPagesCounter(max, receiptsCount);
		model.addAttribute("pagesCount", pagesCount);
		
		List<Receipt> receipts = receiptService.getReceipts(subcategory, start, max);
		model.addAttribute("receipts", receipts);
		return "subcategory/history";
	}
	
	private int setPagesCounter(int max, int count) {
		return (int) Math.ceil((double) count / (double) max);
	}
	
	@RequestMapping(value = "/history/{subcategoryId}", method = RequestMethod.GET)
	public String showDefaultBudgetHistory(@PathVariable String subcategoryId) {
		return "redirect:/subcategory/history/" + subcategoryId + "/0/" + MAX_LAST_RECEIPTS;
	}
}
