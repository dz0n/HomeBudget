package homebudget.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import homebudget.domain.Account;
import homebudget.service.AccountService;

@Controller
@RequestMapping(value = "/account")
public class AccountDeleteController {
	private final AccountService accountService;
	
	@Autowired
	public AccountDeleteController(AccountService accountService) {
		this.accountService = accountService;
	}
	
	@ModelAttribute("account")
	public Account addAccount(@PathVariable String accountId) {
		return accountService.getAccountByHiddenId(accountId);
	}
	
	@RequestMapping(value = "/delete/{accountId}", method = GET)
	public String showDeleteAccountPage(@PathVariable String accountId, Model model) {
		return "account/delete";
	}
	
	@RequestMapping(value = "/delete/{accountId}/confirm", method = GET)
	public String deleteAccount(@PathVariable String accountId, @ModelAttribute Account account) {
		String budgetHiddenId = account.getBudget().getHiddenId();
		account.setHiddenId(accountId);
		accountService.delete(account);
		return "redirect:/budget/" + budgetHiddenId;
	}
	
}
