package homebudget.persist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Ignore;
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
@WithMockUser("proba")
public class HibernateReceiptReadRepositoryTest {
	@Autowired
	private ReceiptReadRepository repository;
	
	@Autowired
	private ReceiptWriteRepository writeRepository;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private SubcategoryService subcategoryService;
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private BudgetService budgetService;

	@Test
	public void testCountAccount() {
		Account account = accountService.getAccountById(1);
		assertEquals(4, repository.count(account));
		
		account = accountService.getAccountById(3);
		assertEquals(2, repository.count(account));
	}
	
	@Test
	public void testCountSubcategory() {
		Subcategory subcategory = subcategoryService.getSubcategoryById(1);
		assertEquals(5, repository.count(subcategory));
		
		subcategory = subcategoryService.getSubcategoryById(3);
		assertEquals(3, repository.count(subcategory));
	}
	
	@Test
	public void testCountCategory() {
		Category category = categoryService.getCategoryById(1);
		assertEquals(8, repository.count(category));
		
		category = categoryService.getCategoryById(3);
		assertEquals(1, repository.count(category));
	}
	
	@Test
	public void testCountBudget() {
		Budget budget = budgetService.getBudgetById(1);
		assertEquals(8, repository.count(budget));
		
		budget = budgetService.getBudgetById(2);
		assertEquals(4, repository.count(budget));
	}
	
	@Test
	public void testGetById() {
		Receipt receipt = repository.getById(1);
		assertEquals(Long.valueOf(1), receipt.getId());
		assertEquals("receipt #1", receipt.getName());
		
		receipt = repository.getById(12);
		assertEquals(Long.valueOf(12), receipt.getId());
		assertEquals("receipt #12", receipt.getName());
	}
	
	@Test
	public void testGetLastAccount() {
		Account account = accountService.getAccountById(1);
		List<Receipt> receipts = repository.getReceipts(account, 0, 100);
		assertEquals(4, receipts.size());
		Receipt receipt = repository.getById(1);
		assertTrue(receipts.contains(receipt));
		receipt = repository.getById(4);
		assertFalse(receipts.contains(receipt));
		
		receipts = repository.getReceipts(account, 0, 2);
		assertEquals(2, receipts.size());
		
		receipts = repository.getReceipts(account, 1, 2);
		assertEquals(2, receipts.size());
		
		receipts = repository.getReceipts(account, 0, 1);
		assertEquals(1, receipts.size());
		
		receipts = repository.getReceipts(account, 10, 50);
		assertEquals(0, receipts.size());
	}
	
	@Test
	public void testGetLastSubcategory() {
		Subcategory subcategory = subcategoryService.getSubcategoryById(3);
		List<Receipt> receipts = repository.getReceipts(subcategory, 0, 100);
		assertEquals(3, receipts.size());
		Receipt receipt = repository.getById(11);
		assertTrue(receipts.contains(receipt));
		receipt = repository.getById(12);
		assertFalse(receipts.contains(receipt));
		
		receipts = repository.getReceipts(subcategory, 0, 2);
		assertEquals(2, receipts.size());
		
		receipts = repository.getReceipts(subcategory, 1, 2);
		assertEquals(2, receipts.size());
		
		receipts = repository.getReceipts(subcategory, 0, 1);
		assertEquals(1, receipts.size());
		
		receipts = repository.getReceipts(subcategory, 10, 50);
		assertEquals(0, receipts.size());
	}
	
	@Test
	public void testGetLastCategory() {
		Category category = categoryService.getCategoryById(1);
		List<Receipt> receipts = repository.getReceipts(category, 0, 100);
		assertEquals(8, receipts.size());
		Receipt receipt = repository.getById(3);
		assertTrue(receipts.contains(receipt));
		receipt = repository.getById(9);
		assertFalse(receipts.contains(receipt));
		
		receipts = repository.getReceipts(category, 0, 8);
		assertEquals(8, receipts.size());
		
		receipts = repository.getReceipts(category, 3, 8);
		assertEquals(5, receipts.size());
		
		receipts = repository.getReceipts(category, 0, 1);
		assertEquals(1, receipts.size());
		
		receipts = repository.getReceipts(category, 10, 50);
		assertEquals(0, receipts.size());
	}
	
	@Test
	public void testGetLastBudget() {
		Budget budget = budgetService.getBudgetById(2);
		List<Receipt> receipts = repository.getReceipts(budget, 0, 100);
		assertEquals(4, receipts.size());
		Receipt receipt = repository.getById(10);
		assertTrue(receipts.contains(receipt));
		receipt = repository.getById(7);
		assertFalse(receipts.contains(receipt));
		
		receipts = repository.getReceipts(budget, 0, 3);
		assertEquals(3, receipts.size());
		
		receipts = repository.getReceipts(budget, 2, 8);
		assertEquals(2, receipts.size());
		
		receipts = repository.getReceipts(budget, 1, 1);
		assertEquals(1, receipts.size());
		
		receipts = repository.getReceipts(budget, 10, 50);
		assertEquals(0, receipts.size());
	}
	
	@Test
	@Ignore("doesn't work in H2")
	public void testGetFirstReceiptDate() {
		Account account = accountService.getAccountById(3);
		Subcategory subcategory = subcategoryService.getSubcategoryById(3);
		
		Receipt receipt = new Receipt("test", BigDecimal.valueOf(12), account, subcategory);
		Date date = (new LocalDate(2003, 5, 8)).toDate();
		receipt.setDate(date);
		writeRepository.save(receipt);
		
		Receipt newestReceipt = new Receipt("test2", BigDecimal.valueOf(12), account, subcategory);
		Date newestDate = (new LocalDate(2005, 2, 14)).toDate();
		receipt.setDate(newestDate);
		writeRepository.save(newestReceipt);
		
		assertEquals(date, repository.getFirstReceiptDate(account.getBudget()));
	}
}
