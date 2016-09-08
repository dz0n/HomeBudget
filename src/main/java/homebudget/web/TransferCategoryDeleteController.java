package homebudget.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import homebudget.domain.CategoryTransfer;
import homebudget.service.CategoryTransferService;

@Controller
@RequestMapping("/transfer")
public class TransferCategoryDeleteController {
	private final CategoryTransferService categoryTransferService;
	
	@Autowired
	public TransferCategoryDeleteController(CategoryTransferService categoryTransferService) {
		this.categoryTransferService = categoryTransferService;
	}
	
	@ModelAttribute("transfer")
	public CategoryTransfer addTransfer(@PathVariable String transferId) {
		return categoryTransferService.getByHiddenId(transferId);
	}
	
	@RequestMapping(value = "/category/delete/{transferId}", method = GET)
	public String showDeleteCategoryTransferPage(@PathVariable String transferId) {
		return "transfer/category/delete";
	}
	
	@RequestMapping(value="/category/delete/{transferId}", method=POST)
	public String deleteCategoryTransferConfirmed(@PathVariable String transferId, 
			@ModelAttribute("transfer") CategoryTransfer transfer,
			final RedirectAttributes redirectAttributes) {
		String budgetId = transfer.getFromCategory().getBudget().getHiddenId();
		transfer.setHiddenId(transferId);
		categoryTransferService.delete(transfer);
		redirectAttributes.addFlashAttribute("msgSuccess", "Usunięto przelew.");
		return "redirect:/transfer/category/history/" + budgetId + "/0/30";
	}
}
