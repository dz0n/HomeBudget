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

import homebudget.domain.Subcategory;
import homebudget.service.SubcategoryService;
import homebudget.service.ReceiptService;

@Controller
@RequestMapping(value = "/subcategory")
public class SubcategoryEditController {
	private final SubcategoryService subcategoryService;
	
	@Autowired
	public SubcategoryEditController(SubcategoryService subcategoryService,
			ReceiptService receiptService) {
		this.subcategoryService = subcategoryService;
	}
	
	@ModelAttribute("subcategory")
	public Subcategory addSubcategory(@PathVariable String subcategoryId) {
		return subcategoryService.getSubcategoryByHiddenId(subcategoryId);
	}
	
	@RequestMapping(value = "/edit/{subcategoryId}", method = GET)
	public String showEditSubcategoryForm(@PathVariable String subcategoryId) {
		return "subcategory/edit";
	}
	
	@RequestMapping(value = "/edit/{subcategoryId}", method = POST)
	public String editSubcategory(@Validated(Subcategory.Validation.class) Subcategory subcategory, Errors errors,
			@PathVariable String subcategoryId, Model model) {
		if(errors.hasErrors()) {
			return "subcategory/edit";
		}
		
		Subcategory originalSubcategory = subcategoryService.getSubcategoryByHiddenId(subcategoryId);
		subcategory.setId(originalSubcategory.getId());
		subcategory.setHiddenId(originalSubcategory.getHiddenId());
		subcategory.setHiddenId(subcategoryId);
		subcategoryService.update(subcategory);
		
		return "redirect:/subcategory/" + subcategory.getHiddenId();
	}
}
