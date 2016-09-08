package homebudget.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import homebudget.domain.CategoryTransfer;
import homebudget.domain.Budget;
import homebudget.service.CategoryTransferService;
import homebudget.service.BudgetService;

@Controller
@RequestMapping("/transfer")
public class TransferCategoryHistoryController {
	private static final int MAX_LAST_TRANSFERS = 30;
	
	private final CategoryTransferService categoryTransferService;
	private final BudgetService budgetService;
	
	@Autowired
	public TransferCategoryHistoryController(CategoryTransferService categoryTransferService,
			BudgetService budgetService) {
		this.categoryTransferService = categoryTransferService;
		this.budgetService = budgetService;
	}
	
	@ModelAttribute("budget")
	public Budget addBudget(@PathVariable String budgetId) {
		return budgetService.getBudgetByHiddenId(budgetId);
	}
	
	@RequestMapping(value = "/category/history/{budgetId}/{start}/{max}", method = RequestMethod.GET)
	public String showTransfersHistory(@PathVariable String budgetId, 
			@PathVariable int start, @PathVariable int max, 
			@ModelAttribute Budget budget, Model model) {
		if(start<0)
			start = start * -1;
		model.addAttribute("start", start);
		
		if(max == 0)
			max = 10;
		if(max < 0)
			max = max * -1;
		model.addAttribute("max", max);
		
		int transfersCount = categoryTransferService.count(budget);
		int pagesCount = 1;
		if(transfersCount>0)
			pagesCount = setPagesCounter(max, transfersCount);
		model.addAttribute("pagesCount", pagesCount);
		
		List<CategoryTransfer> transfers = categoryTransferService.getTransfers(budget, start, max);
		model.addAttribute("transfers", transfers);
		return "transfer/category/history";
	}
	
	private int setPagesCounter(int max, int count) {
		return (int) Math.ceil((double) count / (double) max);
	}
	
	@RequestMapping(value = "/category/history/{budgetId}", method = RequestMethod.GET)
	public String showDefaultTransfersHistory(@PathVariable String budgetId,
			@ModelAttribute Budget budget) {
		return "redirect:/transfer/category/history/" + budget.getHiddenId() + "/0/" + MAX_LAST_TRANSFERS;
	}
}
