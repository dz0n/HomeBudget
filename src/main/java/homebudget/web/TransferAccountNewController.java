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

import homebudget.domain.Account;
import homebudget.domain.AccountTransfer;
import homebudget.domain.Budget;
import homebudget.domain.forms.AccountTransferForm;
import homebudget.service.AccountService;
import homebudget.service.AccountTransferService;
import homebudget.service.BudgetService;
import homebudget.service.UserService;

@Controller
@RequestMapping("/transfer")
public class TransferAccountNewController {
	private static final int MAX_LAST_TRANSFERS = 5;
	
	private final AccountTransferService accountTransferService;
	private final AccountService accountService;
	private final BudgetService budgetService;
	private final UserService userService;
	
	@Autowired
	public TransferAccountNewController(AccountTransferService accountTransferService,
			AccountService accountService, BudgetService budgetService,
			UserService userService) {
		this.accountTransferService = accountTransferService;
		this.accountService = accountService;
		this.budgetService = budgetService;
		this.userService = userService;
	}
	
	@ModelAttribute("budget")
	public Budget addBudget(@PathVariable String budgetId) {
		return budgetService.getBudgetByHiddenId(budgetId);
	}
	
	@ModelAttribute("accounts")
	public List<Account> addAccounts(@ModelAttribute Budget budget) {
		return accountService.getAccounts(budget);
	}
	
	@ModelAttribute("transfers")
	public List<AccountTransfer> addLastTransfer(@ModelAttribute Budget budget) {
		return accountTransferService.getTransfers(budget, 0, MAX_LAST_TRANSFERS);
	}
	
	@RequestMapping(value = "/account/{budgetId}", method = GET)
	public String showNewAccountTransferForm(@PathVariable String budgetId, Model model) {
		model.addAttribute("accountTransferForm", new AccountTransferForm());
		return "transfer/account/new";
	}
	
	@RequestMapping(value = "/account/{budgetId}", method = POST)
	public String createNewAccountTransfer(@Valid AccountTransferForm accountTransferForm, Errors errors, 
			@PathVariable String budgetId, Model model,
			@ModelAttribute Budget budget,
			Principal principal, final RedirectAttributes redirectAttributes) {
		if(errors.hasErrors()){
			return "transfer/account/new";
		}
		
		AccountTransfer transfer = getTransfer(accountTransferForm);
		transfer.setUser(userService.getUserByUsername(principal.getName()));
		transfer = accountTransferService.save(transfer);
		
		redirectAttributes.addFlashAttribute("msgSuccess", "Przelew dodany.");
		return "redirect:/transfer/account/" + budget.getHiddenId();
	}
	
	private AccountTransfer getTransfer(AccountTransferForm form) {
		AccountTransfer transfer = new AccountTransfer();
		Account fromAccount = accountService.getAccountByHiddenId(form.getFromAccountId());
		Account toAccount = accountService.getAccountByHiddenId(form.getToAccountId());
		transfer.setFromAccount(fromAccount);
		transfer.setToAccount(toAccount);
		
		transfer.setName(form.getName());
		transfer.setDescription(form.getDescription());
		transfer.setValue(form.getValue());
		transfer.setDate(new Date(form.getDate().getTime()));
		return transfer;
	}
}
