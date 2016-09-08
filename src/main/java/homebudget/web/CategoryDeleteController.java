package homebudget.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import homebudget.domain.Category;
import homebudget.service.CategoryService;

@Controller
@RequestMapping(value = "/category")
public class CategoryDeleteController {
	private final CategoryService categoryService;
	
	@Autowired
	public CategoryDeleteController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	
	@ModelAttribute("category")
	public Category addCategory(@PathVariable String categoryId) {
		return categoryService.getCategoryByHiddenId(categoryId);
	}
	
	@RequestMapping(value = "/delete/{categoryId}", method = GET)
	public String showDeleteCategoryPage(@PathVariable String categoryId, Model model) {
		return "category/delete";
	}
	
	@RequestMapping(value = "/delete/{categoryId}/confirm", method = GET)
	public String deleteCategory(@PathVariable String categoryId, @ModelAttribute Category category) {
		String budgetHiddenId = category.getBudget().getHiddenId();
		category.setHiddenId(categoryId);
		categoryService.delete(category);
		return "redirect:/budget/" + budgetHiddenId;
	}
	
}
