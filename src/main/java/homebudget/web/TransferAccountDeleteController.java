package homebudget.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import homebudget.domain.AccountTransfer;
import homebudget.service.AccountTransferService;

@Controller
@RequestMapping("/transfer")
public class TransferAccountDeleteController {
	private final AccountTransferService accountTransferService;
	
	@Autowired
	public TransferAccountDeleteController(AccountTransferService accountTransferService) {
		this.accountTransferService = accountTransferService;
	}
	
	@ModelAttribute("transfer")
	public AccountTransfer addTransfer(@PathVariable String transferId) {
		return accountTransferService.getByHiddenId(transferId);
	}
	
	@RequestMapping(value = "/account/delete/{transferId}", method = GET)
	public String showDeleteAccountTransferPage(@PathVariable String transferId) {
		return "transfer/account/delete";
	}
	
	@RequestMapping(value="/account/delete/{transferId}", method=POST)
	public String deleteAccountTransferConfirmed(@PathVariable String transferId, 
			@ModelAttribute("transfer") AccountTransfer transfer,
			final RedirectAttributes redirectAttributes) {
		String budgetId = transfer.getFromAccount().getBudget().getHiddenId();
		transfer.setHiddenId(transferId);
		accountTransferService.delete(transfer);
		redirectAttributes.addFlashAttribute("msgSuccess", "Usunięto przelew.");
		return "redirect:/transfer/account/history/" + budgetId + "/0/30";
	}
}
