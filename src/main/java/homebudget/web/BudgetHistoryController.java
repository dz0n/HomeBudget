package homebudget.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import homebudget.domain.Budget;
import homebudget.domain.Receipt;
import homebudget.service.ActiveBudgetService;
import homebudget.service.BudgetService;
import homebudget.service.ReceiptService;

@Controller
@RequestMapping(value = "/budget")
public class BudgetHistoryController {
	private static final int MAX_LAST_RECEIPTS = 30;
	
	private final BudgetService budgetService;
	private final ActiveBudgetService activeBudgetService;
	private final ReceiptService receiptService;
	
	@Autowired
	public BudgetHistoryController(BudgetService budgetService, 
			ActiveBudgetService activeBudgetService, 
			ReceiptService receiptService) {
		this.budgetService = budgetService;
		this.activeBudgetService = activeBudgetService;
		this.receiptService = receiptService;
	}
	
	@RequestMapping(value = "/history/{budgetId}/{start}/{max}", method = RequestMethod.GET)
	public String showBudgetHistory(@PathVariable String budgetId, @PathVariable int start, @PathVariable int max, Model model) {
		Budget budget = budgetService.getBudgetByHiddenId(budgetId);
		model.addAttribute("budget", budget);
		
		if(start<0)
			start = start * -1;
		model.addAttribute("start", start);
		
		if(max == 0)
			max = 10;
		if(max < 0)
			max = max * -1;
		model.addAttribute("max", max);
		
		int receiptsCount = receiptService.count(budget);
		int pagesCount = 1;
		if(receiptsCount>0)
			pagesCount = setPagesCounter(max, receiptsCount);
		model.addAttribute("pagesCount", pagesCount);
		
		List<Receipt> receipts = receiptService.getReceipts(budget, start, max);
		model.addAttribute("receipts", receipts);
		return "budget/history";
	}

	private int setPagesCounter(int max, int count) {
		return (int) Math.ceil((double) count / (double) max);
	}
	
	@RequestMapping(value = "/history", method = RequestMethod.GET)
	public String showDefaultBudgetHistory() {
		Budget budget = activeBudgetService.get();
		return "redirect:/budget/history/" + budget.getHiddenId() + "/0/" + MAX_LAST_RECEIPTS;
	}
	
}
