package homebudget.persist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
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
import homebudget.domain.User;
import homebudget.persist.CategoryRepository;
import homebudget.service.BudgetService;
import homebudget.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RepositoryTestConfig.class)
@WebAppConfiguration
@ActiveProfiles("test")
@WithMockUser("proba")
public class HibernateCategoryRepositoryTest {
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private BudgetService budgetService;
	
	@Autowired
	private UserService userService;
	
	private User user1;
	private Budget budget1;
	private Category cat1;
	
	private Budget budget2;
	private Category cat2;
	private Category cat3;
	private Category cat4;
	
	@Before
	public void setUp() throws Exception {
		user1 = userService.getUserByUsername("proba"); 
		budget1 = budgetService.getBudgets(user1.getUsername()).get(0);
		cat1 = new Category(null, "cat1,", "none", budget1, 10000); 
		
		budget2 = budgetService.getBudgets(user1.getUsername()).get(1);
		cat2 = new Category(null, "cat2,", "none", budget2, 20000);
		cat3 = new Category(null, "cat3,", "none", budget2, 30000);
		cat4 = new Category(null, "cat4,", "none", budget2, 40000);
	}

	@Test
	public void testSave() {
		Category category = categoryRepository.save(cat1);
		
		assertTrue(category.equals(cat1));
		
		categoryRepository.delete(category);
	}

	@Test
	public void testUpdate() {
		Category category = categoryRepository.save(cat1);
		
		category = categoryRepository.getCategoryById(category.getId());
		category.setName("cat1 changed");
		category.setValue(category.getValue().multiply(BigDecimal.valueOf(5)));
		
		categoryRepository.update(category);
		
		Category categoryUpdated = categoryRepository.getCategoryById(category.getId());
		assertTrue(category.equals(categoryUpdated));
		
		categoryRepository.delete(categoryUpdated);
	}
	
	@Test
	public void testDelete() {
		Category category = categoryRepository.save(cat1);
		
		assertTrue(category.equals(cat1));
		
		categoryRepository.delete(category);
		
		category = categoryRepository.getCategoryById(cat1.getId());
		assertNull(category);
	}

	@Test
	public void testGetCategories() {
		List<Category> categories = categoryRepository.getCategories(budget2);
		int size = categories.size();
		
		Category category2 = categoryRepository.save(cat2);
		Category category3 = categoryRepository.save(cat3);
		Category category4 = categoryRepository.save(cat4);
		categories = categoryRepository.getCategories(budget2);
		assertEquals(size + 3, categories.size());
		
		categoryRepository.delete(category2);
		categoryRepository.delete(category3);
		categories = categoryRepository.getCategories(budget2);
		assertEquals(size + 1, categories.size());
		categoryRepository.delete(category4);
		categories = categoryRepository.getCategories(budget2);
		assertEquals(size, categories.size());
	}

	@Test
	public void testCount() {
		int count = categoryRepository.count(budget1);
		Category category1 = categoryRepository.save(cat1);
		Category category2 = categoryRepository.save(cat2);
		assertEquals(count + 1, categoryRepository.count(budget1));
		
		categoryRepository.delete(category1);
		categoryRepository.delete(category2);
		assertEquals(count, categoryRepository.count(budget1));
	}

}
