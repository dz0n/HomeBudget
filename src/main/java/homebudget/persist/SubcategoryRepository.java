package homebudget.persist;

import java.util.List;

import homebudget.domain.Budget;
import homebudget.domain.Category;
import homebudget.domain.Subcategory;

public interface SubcategoryRepository {
	
	public Subcategory save(Subcategory subcategory);
	
	public void update(Subcategory subcategory);
	
	public void delete(Subcategory subcategory);
	
	public int deleteAll(Budget budget);
	
	public List<Subcategory> getSubcategories(Category category);
	
	public Subcategory getSubcategoryById(long id);
	
	public int count(Category category);
	
}
