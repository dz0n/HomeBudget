package homebudget.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import homebudget.domain.Budget;
import homebudget.domain.Category;
import homebudget.domain.Subcategory;
import homebudget.persist.SubcategoryRepository;
import homebudget.web.ObjectNotFoundException;

@Service
public class SubcategoryService {
	private final SubcategoryRepository subcategoryRepository;
	private final IdHider idHider;

	@Autowired
	public SubcategoryService(SubcategoryRepository subcategoryRepository,
			IdHider idHider) {
		this.subcategoryRepository = subcategoryRepository;
		this.idHider = idHider;
	}
	
	public class SubcategoryNotFoundException extends ObjectNotFoundException {
		private static final long serialVersionUID = 6663897211743457483L;
		
	}

	public Subcategory save(Subcategory subcategory) {
		Subcategory savedSubcategory = subcategoryRepository.save(subcategory);
		savedSubcategory = setHiddenId(savedSubcategory);
		return savedSubcategory;
	}

	public void update(Subcategory subcategory) {
		subcategory = setIdIfNull(subcategory);
		subcategoryRepository.update(subcategory);
	}

	private Subcategory setIdIfNull(Subcategory subcategory) {
		if(subcategory.getId()==null) {
			subcategory.setId(idHider.getId(Subcategory.class, subcategory.getHiddenId()));
		}
		return subcategory;
	}
	
	public void delete(Subcategory subcategory) {
		subcategory = setIdIfNull(subcategory);
		subcategoryRepository.delete(subcategory);
	}
	
	public int deleteAll(Budget budget) {
		return subcategoryRepository.deleteAll(budget);
	}

	public List<Subcategory> getSubcategories(Category category) {
		if(subcategoryRepository.count(category)==0) {
			return Collections.emptyList();
		} else {
			return getSubcategoriesWithHiddenIds(category);
		}
	}
	
	private List<Subcategory> getSubcategoriesWithHiddenIds(Category category) {
		List<Subcategory> subcategories = subcategoryRepository.getSubcategories(category);
		for(Subcategory subcategory : subcategories) {
			subcategory = setHiddenId(subcategory);
		}
		return subcategories;
	}
	
	public Subcategory getSubcategoryById(long id) {
		Subcategory subcategory = subcategoryRepository.getSubcategoryById(id);
		if(subcategory==null)
			throw new SubcategoryNotFoundException();
		subcategory = setHiddenId(subcategory);
		return subcategory;
	}
	
	public Subcategory getSubcategoryByHiddenId(String hiddenId) {
		Long id = idHider.getId(Subcategory.class, hiddenId);
		return getSubcategoryById(id);
	}
	
	private Subcategory setHiddenId(Subcategory subcategory) {
		subcategory.setHiddenId(idHider.getHiddenId(Subcategory.class, subcategory.getId()));
		if(subcategory.getCategory()!=null) {
			subcategory.setCategory(setHiddenId(subcategory.getCategory()));
		}
		return subcategory;
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
