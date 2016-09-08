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

import homebudget.domain.Category;
import homebudget.domain.Subcategory;
import homebudget.domain.Subcategory.Validation;
import homebudget.service.CategoryService;
import homebudget.service.SubcategoryService;

@Controller
@RequestMapping(value = "/subcategory")
public class SubcategoryNewController {
	private final CategoryService categoryService;
	private final SubcategoryService subcategoryService;
	
	@Autowired
	public SubcategoryNewController(CategoryService categoryService, SubcategoryService subcategoryService) {
		this.categoryService = categoryService;
		this.subcategoryService = subcategoryService;
	}
	
	@ModelAttribute("category")
	public Category addCategory(@PathVariable String categoryId) {
		return categoryService.getCategoryByHiddenId(categoryId);
	}
	
	@RequestMapping(value = "/new/{categoryId}", method = GET)
	public String showNewSubcategoryForm(@PathVariable String categoryId, Model model) {
		model.addAttribute("subcategory", new Subcategory());
		return "subcategory/new";
	}
	
	@RequestMapping(value = "/new/{categoryId}", method = POST)
	public String createSubcategory(@PathVariable String categoryId, @Validated(Validation.class) Subcategory subcategory, 
			@ModelAttribute Category category,
			Errors errors, Model model) {
		if(errors.hasErrors()) {
			return "subcategory/new";
		}
		
		subcategory.setCategory(category);
		subcategory = subcategoryService.save(subcategory);
		
		return "redirect:/subcategory/" + subcategory.getHiddenId();
	}
}
