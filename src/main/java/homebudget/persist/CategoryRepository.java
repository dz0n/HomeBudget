package homebudget.persist;

import java.util.List;

import homebudget.domain.Budget;
import homebudget.domain.Category;

public interface CategoryRepository {
	
	public Category save(Category category);
	
	public void update(Category category);
	
	public void delete(Category category);
	
	public int deleteAll(Budget budget);
	
	public List<Category> getCategories(Budget budget);
	
	public Category getCategoryById(long id);
	
	public int count(Budget budget);
	
}
