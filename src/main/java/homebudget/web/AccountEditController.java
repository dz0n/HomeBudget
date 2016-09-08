package homebudget.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import homebudget.domain.Account;
import homebudget.service.AccountService;
import homebudget.service.ReceiptService;

@Controller
@RequestMapping(value = "/account")
public class AccountEditController {
	private final AccountService accountService;
	
	@Autowired
	public AccountEditController(AccountService accountService,
			ReceiptService receiptService) {
		this.accountService = accountService;
	}
	
	@ModelAttribute("account")
	public Account addAccount(@PathVariable String accountId) {
		return accountService.getAccountByHiddenId(accountId);
	}
	
	@RequestMapping(value = "/edit/{accountId}", method = GET)
	public String showEditAccountForm(@PathVariable String accountId) {
		return "account/edit";
	}
	
	@RequestMapping(value = "/edit/{accountId}", method = POST)
	public String editAccount(@Validated(Account.Validation.class) Account account, Errors errors,
			@PathVariable String accountId, Model model) {
		if(errors.hasErrors()) {
			return "account/edit";
		}
		
		Account originalAccount = accountService.getAccountByHiddenId(accountId);
		account.setId(originalAccount.getId());
		account.setHiddenId(originalAccount.getHiddenId());
		accountService.update(account);
		
		return "redirect:/account/" + account.getHiddenId();
	}
}
