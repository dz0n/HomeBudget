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
import homebudget.service.UserService;

@Controller
@RequestMapping("/transfer")
public class TransferAccountEditController {
	private static final int MAX_LAST_TRANSFERS = 5;
	
	private final AccountTransferService accountTransferService;
	private final AccountService accountService;
	private final UserService userService;
	
	@Autowired
	public TransferAccountEditController(AccountTransferService accountTransferService,
			AccountService accountService, UserService userService) {
		this.accountTransferService = accountTransferService;
		this.accountService = accountService;
		this.userService = userService;
	}
	
	@ModelAttribute("transfer")
	public AccountTransfer addAccountTransfer(@PathVariable String transferId) {
		return accountTransferService.getByHiddenId(transferId);
	}
	
	@ModelAttribute("budget")
	public Budget addBudget(@ModelAttribute("transfer") AccountTransfer transfer) {
		return transfer.getFromAccount().getBudget();
	}
	
	@ModelAttribute("accounts")
	public List<Account> addAccounts(@ModelAttribute Budget budget) {
		return accountService.getAccounts(budget);
	}
	
	@ModelAttribute("transfers")
	public List<AccountTransfer> addLastTransfer(@ModelAttribute Budget budget) {
		return accountTransferService.getTransfers(budget, 0, MAX_LAST_TRANSFERS);
	}
	
	@RequestMapping(value = "/account/edit/{transferId}", method = GET)
	public String showEditAccountTransferForm(@PathVariable String transferId, Model model,
			@ModelAttribute("transfer") AccountTransfer transfer) {
		AccountTransferForm form = getAccountTransferForm(transfer);
		model.addAttribute("accountTransferForm", form);
		return "transfer/account/edit";
	}
	
	private AccountTransferForm getAccountTransferForm(AccountTransfer transfer) {
		AccountTransferForm form = new AccountTransferForm();
		form.setName(transfer.getName());
		form.setDescription(transfer.getDescription());
		form.setValue(transfer.getValue());
		form.setFromAccountId(transfer.getFromAccount().getHiddenId());
		form.setToAccountId(transfer.getToAccount().getHiddenId());
		form.setDate(new Date(transfer.getDate().getTime()));
		return form;
	}
	
	@RequestMapping(value = "/account/edit/{transferId}", method = POST)
	public String editAccountTransfer(@Valid AccountTransferForm accountTransferForm, Errors errors, 
			@PathVariable String transferId, Model model,
			@ModelAttribute("transfer") AccountTransfer transfer,
			Principal principal, final RedirectAttributes redirectAttributes) {
		if(errors.hasErrors()){
			return "redirect:/transfer/account/edit/" + transfer.getHiddenId();
		}
		
		transfer = updateTransferFieldsWithFormValues(transfer, accountTransferForm);
		if(transfer.getUser()==null)
			transfer.setUser(userService.getUserByUsername(principal.getName()));
		transfer.setHiddenId(transferId);
		transfer = accountTransferService.update(transfer);
		
		redirectAttributes.addFlashAttribute("msgSuccess", "Przelew zmieniony.");
		return "redirect:/transfer/account/edit/" + transfer.getHiddenId();
	}
	
	private AccountTransfer updateTransferFieldsWithFormValues(AccountTransfer transfer, AccountTransferForm form) {
		transfer.setName(form.getName());
		transfer.setDescription(form.getDescription());
		transfer.setValue(form.getValue());
		transfer.setDate(new Date(form.getDate().getTime()));
		transfer.setFromAccount(accountService.getAccountByHiddenId(form.getFromAccountId()));
		transfer.setToAccount(accountService.getAccountByHiddenId(form.getToAccountId()));
		return transfer;
	}
}
