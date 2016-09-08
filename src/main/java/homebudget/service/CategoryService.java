package homebudget.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import homebudget.domain.Budget;
import homebudget.domain.Category;
import homebudget.domain.Subcategory;
import homebudget.persist.CategoryRepository;
import homebudget.web.ObjectNotFoundException;

@Service
public class CategoryService {
	private final CategoryRepository categoryRepository;
	private final SubcategoryService subcategoryService;
	private final IdHider idHider;
	
	@Autowired
	public CategoryService(CategoryRepository categoryRepository, 
			SubcategoryService subcategoryService,
			IdHider idHider) {
		this.categoryRepository = categoryRepository;
		this.subcategoryService = subcategoryService;
		this.idHider = idHider;
	}
	
	public class CategoryNotFoundException extends ObjectNotFoundException {
		private static final long serialVersionUID = 3639140708852498940L;
		
	}
	
	public BigDecimal getCategoriesSum(Budget budget) {
		List<Category> categories = this.getCategories(budget);
		long categoriesSum = 0;
		for(Category category : categories) {
			categoriesSum += category.getLongValue();
		}
		return BigDecimal.valueOf(categoriesSum).divide(BigDecimal.valueOf(100));
	}
	
	public int count(Budget budget) {
		return categoryRepository.count(budget);
	}
	
	public Category save(Category category) {
		Category savedCategory = categoryRepository.save(category);
		savedCategory = setHiddenId(savedCategory);
		
		createDefaultSubcategory(savedCategory);
		
		return savedCategory;
	}

	private void createDefaultSubcategory(Category category) {
		Subcategory defaultSubcategory = new Subcategory();
		defaultSubcategory.setName(category.getName());
		defaultSubcategory.setDescription("Domyślna podkategoria");
		defaultSubcategory.setCategory(category);
		defaultSubcategory = subcategoryService.save(defaultSubcategory);
	}

	public void update(Category category) {
		category = setIdIfNull(category);
		categoryRepository.update(category);
	}

	private Category setIdIfNull(Category category) {
		if(category.getId()==null) {
			category.setId(idHider.getId(Category.class, category.getHiddenId()));
		}
		return category;
	}
	
	public void delete(Category category) {
		category = setIdIfNull(category);
		categoryRepository.delete(category);
	}
	
	public int deleteAll(Budget budget) {
		return categoryRepository.deleteAll(budget);
	}
	
	public List<Category> getCategories(Budget budget) {
		if(categoryRepository.count(budget)==0) {
			return Collections.emptyList();
		} else {
			return getCategoriesWithHiddenIds(budget);
		}
	}

	private List<Category> getCategoriesWithHiddenIds(Budget budget) {
		List<Category> categories = categoryRepository.getCategories(budget);
		for(Category category : categories) {
			category = setHiddenId(category);
		}
		return categories;
	}

	public Category getCategoryById(long id) {
		Category category = categoryRepository.getCategoryById(id);
		if(category==null)
			throw new CategoryNotFoundException();
		category = setHiddenId(category);
		return category;
	}
	
	public Category getCategoryByHiddenId(String hiddenId) {
		Long id = idHider.getId(Category.class, hiddenId);
		return getCategoryById(id);
	}
	
	private Category setHiddenId(Category category) {
		category.setHiddenId(idHider.getHiddenId(Category.class, category.getId()));
		if(category.getBudget()!=null) {
			category.setBudget(setHiddenId(category.getBudget()));
		}
		return category;
	}
	
	private Budget setHiddenId(Budget budget) {
		budget.setHiddenId(idHider.getHiddenId(Budget.class, budget.getId()));
		return budget;
	}
}
