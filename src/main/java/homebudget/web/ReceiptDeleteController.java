package homebudget.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import homebudget.domain.Receipt;
import homebudget.service.ReceiptService;

@Controller
@RequestMapping("/receipt")
public class ReceiptDeleteController {
	private final ReceiptService receiptService;
	
	@Autowired
	public ReceiptDeleteController(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}
	
	@ModelAttribute("receipt")
	public Receipt addReceipt(@PathVariable String receiptId) {
		return receiptService.getByHiddenId(receiptId);
	}
	
	@RequestMapping(value="/delete/{receiptId}", method=GET)
	public String showDeletePage(@PathVariable String receiptId, Model model) {
		return "receipt/delete";
	}
	
	@RequestMapping(value="/delete/{receiptId}", method=POST)
	public String deleteConfirmed(@PathVariable String receiptId, 
			@ModelAttribute Receipt receipt,
			final RedirectAttributes redirectAttributes) {
		String budgetId = receipt.getAccount().getBudget().getHiddenId();
		receiptService.delete(receipt);
		redirectAttributes.addFlashAttribute("msgSuccess", "Usunięto wpływ/wydatek.");
		return "redirect:/budget/" + budgetId;
	}
}
