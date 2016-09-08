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

import homebudget.domain.Account;
import homebudget.domain.Receipt;
import homebudget.service.AccountService;
import homebudget.service.BudgetCalculationService;
import homebudget.service.ReceiptService;

@Controller
@RequestMapping(value = "/account")
public class AccountShowController {
	private static final int MAX_LAST_RECEIPTS = 10;
	
	private final AccountService accountService;
	private final ReceiptService receiptService;
	private final BudgetCalculationService budgetCalculationService;
	
	@Autowired
	public AccountShowController(AccountService accountService,
			ReceiptService receiptService,
			BudgetCalculationService budgetCalculationService) {
		this.accountService = accountService;
		this.receiptService = receiptService;
		this.budgetCalculationService = budgetCalculationService;
	}
	
	@ModelAttribute("account")
	public Account addAccount(@PathVariable String accountId) {
		return accountService.getAccountByHiddenId(accountId);
	}
	
	@ModelAttribute("receipts")
	public List<Receipt> addLastReceipts(@ModelAttribute Account account) {
		return receiptService.getReceipts(account, 0, MAX_LAST_RECEIPTS);
	}
	
	@ModelAttribute("monthBalance")
	public BigDecimal addMonthBalance(@ModelAttribute Account account) {
		return budgetCalculationService.getMonthBalance(account);
	}
	
	@RequestMapping(value = "/{accountId}", method = GET)
	public String showAccount(@PathVariable String accountId) {
		return "account/account";
	}
	
	@RequestMapping(value = "/history/{accountId}/{start}/{max}", method = GET)
	public String showAccountHistory(@PathVariable String accountId,
			@PathVariable int start, @PathVariable int max, 
			@ModelAttribute Account account, Model model) {
		if(start<0)
			start = start * -1;
		model.addAttribute("start", start);
		
		if(max == 0)
			max = 10;
		if(max < 0)
			max = max * -1;
		model.addAttribute("max", max);
		
		int receiptsCount = receiptService.count(account);
		int pagesCount = 1;
		if(receiptsCount>0)
			pagesCount = setPagesCounter(max, receiptsCount);
		model.addAttribute("pagesCount", pagesCount);
		
		List<Receipt> receipts = receiptService.getReceipts(account, start, max);
		model.addAttribute("receipts", receipts);
		return "account/history";
	}
	
	private int setPagesCounter(int max, int count) {
		return (int) Math.ceil((double) count / (double) max);
	}
	
	@RequestMapping(value = "/history/{accountId}", method = RequestMethod.GET)
	public String showDefaultBudgetHistory(@PathVariable String accountId) {
		return "redirect:/account/history/" + accountId + "/0/" + MAX_LAST_RECEIPTS;
	}
}
