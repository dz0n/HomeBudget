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
import homebudget.service.CategoryService;
import homebudget.service.ReceiptService;

@Controller
@RequestMapping(value = "/category")
public class CategoryEditController {
	private final CategoryService categoryService;
	
	@Autowired
	public CategoryEditController(CategoryService categoryService,
			ReceiptService receiptService) {
		this.categoryService = categoryService;
	}
	
	@ModelAttribute("category")
	public Category addCategory(@PathVariable String categoryId) {
		return categoryService.getCategoryByHiddenId(categoryId);
	}
	
	@RequestMapping(value = "/edit/{categoryId}", method = GET)
	public String showEditCategoryForm(@PathVariable String categoryId) {
		return "category/edit";
	}
	
	@RequestMapping(value = "/edit/{categoryId}", method = POST)
	public String editCategory(@Validated(Category.Validation.class) Category category, Errors errors,
			@PathVariable String categoryId, Model model) {
		if(errors.hasErrors()) {
			return "category/edit";
		}
		
		Category originalCategory = categoryService.getCategoryByHiddenId(categoryId);
		category.setId(originalCategory.getId());
		category.setHiddenId(originalCategory.getHiddenId());
		categoryService.update(category);
		
		return "redirect:/category/" + category.getHiddenId();
	}
}
