package homebudget.persist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
import homebudget.domain.Receipt;
import homebudget.domain.Subcategory;
import homebudget.service.AccountService;
import homebudget.service.ReceiptService;
import homebudget.service.ReceiptService.ReceiptNotFoundException;
import homebudget.service.SubcategoryService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RepositoryTestConfig.class)
@WebAppConfiguration
@ActiveProfiles("test")
@WithMockUser("proba")
public class HibernateReceiptWriteRepositoryTest {
	@Autowired
	private ReceiptWriteRepository repository;
	@Autowired
	private ReceiptService receiptService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private SubcategoryService subcategoryService;
	@Autowired
	private ReceiptReadRepository readRepository;
	
	private Account account;
	private Subcategory subcategory;
	private Receipt receipt;

	@Before
	public void setUp() throws Exception {
		account = accountService.getAccountById(1);
		subcategory = subcategoryService.getSubcategoryById(1);
		receipt = new Receipt("test receipt 1", BigDecimal.valueOf(123), account, subcategory);
	}

	@Test
	public void testSave() {
		int count = receiptService.count(account);
		Receipt savedReceipt = repository.save(receipt);
		receipt.setId(savedReceipt.getId());
		
		assertEquals(count + 1, receiptService.count(account));
		assertEquals(receipt, savedReceipt);
		
		repository.delete(savedReceipt);
	}
	
	@Test
	public void testDelete() {
		receipt = repository.save(receipt);
		int count = receiptService.count(account);
		repository.delete(receipt);
		
		assertEquals(count - 1, receiptService.count(account));
	}
	
	@Test(expected=ReceiptNotFoundException.class)
	public void testThrowExceptionAfterDelete() {
		receipt = repository.save(receipt);
		long id = receipt.getId();
		repository.delete(receipt);
		
		assertNull(receiptService.getById(id));
	}
	
	@Test
	public void testUpdate() {
		receipt = repository.save(receipt);
		receipt = readRepository.getById(receipt.getId());
		Subcategory newSubcategory = subcategoryService.getSubcategoryById(2);
		Account newAccount = accountService.getAccountById(2);
		receipt.setAccount(newAccount);
		receipt.setSubcategory(newSubcategory);
		receipt.setLongValue(2212);
		receipt.setName("updated receipt 1");
		receipt.setDescription("updated desc");
		receipt = repository.update(receipt);
		
		Receipt updatedReceipt = readRepository.getById(receipt.getId());
		receipt.getAccount().setLongValue(updatedReceipt.getAccount().getLongValue());
		receipt.getSubcategory().getCategory().setLongValue(updatedReceipt.getSubcategory().getCategory().getLongValue());
		assertEquals(receipt, updatedReceipt);
	}
	
	@Test
	public void testValueWhenSave() {
		long value = -2550;
		receipt.setLongValue(value);
		long categoryValue = receipt.getSubcategory().getCategory().getLongValue();
		long accountValue = receipt.getAccount().getLongValue();
		receipt = repository.save(receipt);
		
		assertEquals(categoryValue + value, receipt.getSubcategory().getCategory().getLongValue());
		assertEquals(accountValue + value, receipt.getAccount().getLongValue());
		
		assertEquals(receipt.getAccount().getBudget(), receipt.getSubcategory().getCategory().getBudget());
	}
	
	@Test
	public void testValueWhenDelete() {
		long value = -1000;
		receipt.setLongValue(value);
		receipt = repository.save(receipt);
		long categoryValue = receipt.getSubcategory().getCategory().getLongValue();
		long accountValue = receipt.getAccount().getLongValue();
		repository.delete(receipt);
		
		assertEquals(categoryValue - value, subcategoryService.getSubcategoryById(1).getCategory().getLongValue());
		assertEquals(accountValue - value, accountService.getAccountById(1).getLongValue());
		
		assertEquals(receipt.getAccount().getBudget(), receipt.getSubcategory().getCategory().getBudget());
	}
	
	
}
