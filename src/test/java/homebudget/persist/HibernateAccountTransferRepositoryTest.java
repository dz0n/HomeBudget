package homebudget.persist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
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
import homebudget.domain.AccountTransfer;
import homebudget.domain.Budget;
import homebudget.service.AccountService;
import homebudget.service.BudgetService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RepositoryTestConfig.class)
@WebAppConfiguration
@ActiveProfiles("test")
@WithMockUser("proba")
public class HibernateAccountTransferRepositoryTest {
	
	@Autowired
	private AccountTransferRepository repository;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private BudgetService budgetService;
	
	private Account account1, account2;
	
	@Before
	public void setUp() throws Exception {
		account1 = accountService.getAccountById(1);
		account2 = accountService.getAccountById(2);
	}

	@Test
	public void testGetById() {
		AccountTransfer transfer = repository.getById(1);
		
		assertEquals("account transfer #1", transfer.getName());
		assertEquals(Long.valueOf(1), transfer.getFromAccount().getId());
		assertEquals(Long.valueOf(2), transfer.getToAccount().getId());
		assertEquals(1100, transfer.getLongValue());
	}
	
	@Test
	public void testSave() {
		int count = repository.count();
		AccountTransfer transfer = new AccountTransfer("account test 1", account1, account2, BigDecimal.valueOf(3000));
		transfer = repository.save(transfer);
		
		assertEquals(count + 1, repository.count());
	}
	
	@Test
	public void testValuesWhenSave() {
		long fromAccountValue = account1.getLongValue();
		long toAccountValue = account2.getLongValue();
		long value = 2000;
		AccountTransfer transfer = new AccountTransfer("account test 2", account1, account2, BigDecimal.valueOf(0));
		transfer.setLongValue(value);
		transfer = repository.save(transfer);
		
		assertEquals(fromAccountValue - value, transfer.getFromAccount().getLongValue());
		assertEquals(toAccountValue + value, transfer.getToAccount().getLongValue());
		
		assertEquals(fromAccountValue - value, accountService.getAccountById(account1.getId()).getLongValue());
		assertEquals(toAccountValue + value, accountService.getAccountById(account2.getId()).getLongValue());
	}
	
	@Test
	public void testDelete() {
		int count = repository.count();
		AccountTransfer transfer = new AccountTransfer("account test 3", account1, account2, BigDecimal.valueOf(4000));
		transfer = repository.save(transfer);
		long id = transfer.getId();
		
		assertEquals(count + 1, repository.count());
		
		AccountTransfer transferForDelete = repository.getById(id);
		repository.delete(transferForDelete);
		
		assertEquals(count, repository.count());
	}
	
	@Test
	public void testNullAfterDelete() {
		int count = repository.count();
		AccountTransfer transfer = new AccountTransfer("account test 3", account1, account2, BigDecimal.valueOf(4000));
		transfer = repository.save(transfer);
		long id = transfer.getId();
		
		assertEquals(count + 1, repository.count());
		
		AccountTransfer transferForDelete = repository.getById(id);
		repository.delete(transferForDelete);
		AccountTransfer nullTransfer = repository.getById(id);
		
		assertNull(nullTransfer);
	}
	
	@Test
	public void testValuesWhenDelete() {
		long value = 2000;
		AccountTransfer transfer = new AccountTransfer("account test 2", account1, account2, BigDecimal.valueOf(0));
		transfer.setLongValue(value);
		transfer = repository.save(transfer);
		
		long fromAccountValue = transfer.getFromAccount().getLongValue();
		long toAccountValue = transfer.getToAccount().getLongValue();
		
		repository.delete(transfer);
		
		assertEquals(fromAccountValue + value, accountService.getAccountById(account1.getId()).getLongValue());
		assertEquals(toAccountValue - value, accountService.getAccountById(account2.getId()).getLongValue());
	}
	
	@Test
	public void testCountBudget() {
		Budget budget = budgetService.getBudgetById(2);
		assertEquals(4, repository.count(budget));
	}
	
	@Test
	@Ignore("H2 database can't create query with order by date without date in select")
	public void testGetList() {
		Budget budget = budgetService.getBudgetById(2);
		List<AccountTransfer> transfers = repository.getTransfers(budget, 0, 10);
		assertEquals(4, transfers.size());
		
		AccountTransfer transfer = repository.getById(2);
		assertTrue(transfers.contains(transfer));
		transfer = repository.getById(3);
		assertTrue(transfers.contains(transfer));
		transfer = repository.getById(4);
		assertTrue(transfers.contains(transfer));
		transfer = repository.getById(5);
		assertTrue(transfers.contains(transfer));
		
		transfers = repository.getTransfers(budget, 3, 3);
		assertEquals(1, transfers.size());
		
		transfers = repository.getTransfers(budget, 0, 1);
		assertEquals(1, transfers.size());
		
		transfers = repository.getTransfers(budget, 1, 2);
		assertEquals(2, transfers.size());
		
		transfers = repository.getTransfers(budget, 2, 5);
		assertEquals(2, transfers.size());
	}
	
}
