package homebudget.persist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import homebudget.config.RepositoryTestConfig;
import homebudget.domain.Budget;
import homebudget.domain.Category;
import homebudget.domain.Subcategory;
import homebudget.service.BudgetService;
import homebudget.service.CategoryService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RepositoryTestConfig.class)
@WebAppConfiguration
@ActiveProfiles("test")
@WithMockUser("user2")
public class HibernateSubcategoryRepositoryDeleteAllTest {
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private SubcategoryRepository subcategoryRepository;
	@Autowired
	private BudgetService budgetService;
	
	@Test
	public void testDeleteAll() {
		Budget budget = budgetService.getBudgetById(3);
		Category category = getCategory(budget);
		prepareSubcategories(category);
		
		assertTrue(subcategoryRepository.getSubcategories(category).size() >= 3);
		
		subcategoryRepository.deleteAll(budget);
		
		assertEquals(0, subcategoryRepository.getSubcategories(category).size());
	}

	private Category getCategory(Budget budget) {
		Category category = new Category(null, "for delete in subcategory", "", budget, 1234);
		return categoryService.save(category);
	}

	private void prepareSubcategories(Category category) {
		Subcategory subcategory1 = new Subcategory(null, "for delete 1", "", category);
		subcategoryRepository.save(subcategory1);
		Subcategory subcategory2 = new Subcategory(null, "for delete 2", "", category);
		subcategoryRepository.save(subcategory2);
		Subcategory subcategory3 = new Subcategory(null, "for delete 3", "", category);
		subcategoryRepository.save(subcategory3);
	}
}
