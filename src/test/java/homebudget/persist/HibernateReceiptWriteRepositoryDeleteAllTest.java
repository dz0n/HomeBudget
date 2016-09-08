package homebudget.persist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

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
import homebudget.domain.Account;
import homebudget.domain.Budget;
import homebudget.domain.Category;
import homebudget.domain.Receipt;
import homebudget.domain.Subcategory;
import homebudget.service.AccountService;
import homebudget.service.BudgetService;
import homebudget.service.CategoryService;
import homebudget.service.SubcategoryService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RepositoryTestConfig.class)
@WebAppConfiguration
@ActiveProfiles("test")
@WithMockUser("user2")
public class HibernateReceiptWriteRepositoryDeleteAllTest {
	@Autowired
	private ReceiptWriteRepository repository;
	@Autowired
	private AccountService accountService;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private SubcategoryService subcategoryService;
	@Autowired
	private ReceiptReadRepository readRepository;
	@Autowired
	private BudgetService budgetService;
	
	private Budget budget;
	
	
	@Before
	public void prepareReceipts() {
		budget = budgetService.getBudgetById(3);
		Account account = new Account(null, "for delete in receipt", "", budget, 1234);
		accountService.save(account);
		Category category = new Category(null, "for delete in receipt", "", budget, 1234);
		categoryService.save(category);
		Subcategory subcategory = new Subcategory(null, "for delete in receipt", "", category);
		subcategoryService.save(subcategory);
		
		repository.save(new Receipt("rec 1", BigDecimal.valueOf(11), account, subcategory));
		repository.save(new Receipt("rec 2", BigDecimal.valueOf(12), account, subcategory));
		repository.save(new Receipt("rec 3", BigDecimal.valueOf(13), account, subcategory));
		repository.save(new Receipt("rec 4", BigDecimal.valueOf(14), account, subcategory));
		repository.save(new Receipt("rec 5", BigDecimal.valueOf(15), account, subcategory));
	}

	@Test
	public void testDeleteAll() {
		assertTrue(readRepository.count(budget)>=5);
		
		repository.deleteAll(budget);
		
		assertEquals(0, readRepository.count(budget));
	}
}
