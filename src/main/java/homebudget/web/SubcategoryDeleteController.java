package homebudget.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import homebudget.domain.Subcategory;
import homebudget.service.SubcategoryService;

@Controller
@RequestMapping(value = "/subcategory")
public class SubcategoryDeleteController {
	private final SubcategoryService subcategoryService;
	
	@Autowired
	public SubcategoryDeleteController(SubcategoryService subcategoryService) {
		this.subcategoryService = subcategoryService;
	}
	
	@ModelAttribute("subcategory")
	public Subcategory addSubcategory(@PathVariable String subcategoryId) {
		return subcategoryService.getSubcategoryByHiddenId(subcategoryId);
	}
	
	@RequestMapping(value = "/delete/{subcategoryId}", method = GET)
	public String showDeleteSubcategoryPage(@PathVariable String subcategoryId, Model model) {
		return "subcategory/delete";
	}
	
	@RequestMapping(value = "/delete/{subcategoryId}/confirm", method = GET)
	public String deleteSubcategory(@PathVariable String subcategoryId, @ModelAttribute Subcategory subcategory) {
		String budgetHiddenId = subcategory.getCategory().getBudget().getHiddenId();
		subcategory.setHiddenId(subcategoryId);
		subcategoryService.delete(subcategory);
		return "redirect:/budget/" + budgetHiddenId;
	}
	
}
