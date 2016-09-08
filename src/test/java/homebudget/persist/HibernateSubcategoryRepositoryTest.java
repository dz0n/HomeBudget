package homebudget.persist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
import homebudget.domain.Subcategory;
import homebudget.persist.SubcategoryRepository;
import homebudget.service.BudgetService;
import homebudget.service.CategoryService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RepositoryTestConfig.class)
@WebAppConfiguration
@ActiveProfiles("test")
@WithMockUser("proba")
public class HibernateSubcategoryRepositoryTest {
	
	@Autowired
	private SubcategoryRepository subcategoryRepository;
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private BudgetService budgetService;
	
	private Category category1;
	private Subcategory subcat1;
	
	private Category category2;
	private Subcategory subcat2;
	private Subcategory subcat3;
	private Subcategory subcat4;
	
	@Before
	public void setUp() throws Exception {
		Budget budget1 = budgetService.getBudgetById(1);
		category1 = categoryService.getCategories(budget1).get(0);
		subcat1 = new Subcategory(null, "cat1,", "none", category1); 
		
		Budget budget2 = budgetService.getBudgetById(2);
		category2 = categoryService.getCategories(budget2).get(0);
		subcat2 = new Subcategory(null, "cat2,", "none", category2);
		subcat3 = new Subcategory(null, "cat3,", "none", category2);
		subcat4 = new Subcategory(null, "cat4,", "none", category2);
	}

	@Test
	public void testSave() {
		Subcategory subcategory = subcategoryRepository.save(subcat1);
		
		assertTrue(subcategory.equals(subcat1));
		
		subcategoryRepository.delete(subcategory);
	}

	@Test
	public void testUpdate() {
		Subcategory subcategory = subcategoryRepository.save(subcat1);
		
		subcategory = subcategoryRepository.getSubcategoryById(subcategory.getId());
		subcategory.setName("cat1 changed");
		subcategory.setDescription("abcdef");
		
		subcategoryRepository.update(subcategory);
		
		Subcategory subcategoryUpdated = subcategoryRepository.getSubcategoryById(subcategory.getId());
		assertTrue(subcategory.equals(subcategoryUpdated));
		
		subcategoryRepository.delete(subcategoryUpdated);
	}

	public void testDelete() {
		Subcategory subcategory = subcategoryRepository.save(subcat1);
		
		assertTrue(subcategory.equals(subcat1));
		
		subcategoryRepository.delete(subcategory);
		
		subcategory = subcategoryRepository.getSubcategoryById(subcat1.getId());
		assertNull(subcategory);
	}

	@Test
	public void testGetSubcategories() {
		List<Subcategory> subcategories = subcategoryRepository.getSubcategories(category2);
		int size = subcategories.size();
		
		Subcategory subcategory2 = subcategoryRepository.save(subcat2);
		Subcategory subcategory3 = subcategoryRepository.save(subcat3);
		Subcategory subcategory4 = subcategoryRepository.save(subcat4);
		subcategories = subcategoryRepository.getSubcategories(category2);
		assertEquals(size + 3, subcategories.size());
		
		subcategoryRepository.delete(subcategory2);
		subcategoryRepository.delete(subcategory3);
		subcategories = subcategoryRepository.getSubcategories(category2);
		assertEquals(size + 1, subcategories.size());
		subcategoryRepository.delete(subcategory4);
		subcategories = subcategoryRepository.getSubcategories(category2);
		assertEquals(size, subcategories.size());
	}

	@Test
	public void testCount() {
		int count = subcategoryRepository.count(category1);
		Subcategory subcategory1 = subcategoryRepository.save(subcat1);
		Subcategory subcategory2 = subcategoryRepository.save(subcat2);
		assertEquals(count + 1, subcategoryRepository.count(category1));
		
		subcategoryRepository.delete(subcategory1);
		subcategoryRepository.delete(subcategory2);
		assertEquals(count, subcategoryRepository.count(category1));
	}

}
