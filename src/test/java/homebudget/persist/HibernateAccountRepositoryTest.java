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
import homebudget.domain.Account;
import homebudget.domain.Budget;
import homebudget.domain.User;
import homebudget.persist.AccountRepository;
import homebudget.service.BudgetService;
import homebudget.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RepositoryTestConfig.class)
@WebAppConfiguration
@ActiveProfiles("test")
@WithMockUser("proba")
public class HibernateAccountRepositoryTest {
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private BudgetService budgetService;
	
	@Autowired
	private UserService userService;
	
	private User user1;
	private Budget budget1;
	private Account acc1;
	
	private Budget budget2;
	private Account acc2;
	private Account acc3;
	private Account acc4;
	
	@Before
	public void setUp() throws Exception {
		user1 = userService.getUserByUsername("proba"); 
		budget1 = budgetService.getBudgets(user1.getUsername()).get(0);
		acc1 = new Account(null, "cat1,", "none", budget1, 10000); 
		
		budget2 = budgetService.getBudgets(user1.getUsername()).get(1);
		acc2 = new Account(null, "cat2,", "none", budget2, 20000);
		acc3 = new Account(null, "cat3,", "none", budget2, 30000);
		acc4 = new Account(null, "cat4,", "none", budget2, 40000);
	}
	
	@Test
	public void testSave() {
		Account account = accountRepository.save(acc1);
		
		assertTrue(account.equals(acc1));
		
		accountRepository.delete(account);
	}

	@Test
	public void testUpdate() {
		Account account = accountRepository.save(acc1);
		
		account = accountRepository.getAccountById(account.getId());
		account.setName("cat1 changed");
		account.setValue(account.getValue().multiply(BigDecimal.valueOf(5)));
		
		accountRepository.update(account);
		
		Account accountUpdated = accountRepository.getAccountById(account.getId());
		assertTrue(account.equals(accountUpdated));
		
		accountRepository.delete(accountUpdated);
	}
	
	@Test
	public void testDelete() {
		Account account = accountRepository.save(acc1);
		
		assertTrue(account.equals(acc1));
		
		accountRepository.delete(account);
		
		account = accountRepository.getAccountById(acc1.getId());
		assertNull(account);
	}

	@Test
	public void testGetAccounts() {
		List<Account> accounts = accountRepository.getAccounts(budget2);
		int size = accounts.size();
		
		Account account2 = accountRepository.save(acc2);
		Account account3 = accountRepository.save(acc3);
		Account account4 = accountRepository.save(acc4);
		accounts = accountRepository.getAccounts(budget2);
		assertEquals(size + 3, accounts.size());
		
		accountRepository.delete(account2);
		accountRepository.delete(account3);
		accounts = accountRepository.getAccounts(budget2);
		assertEquals(size + 1, accounts.size());
		accountRepository.delete(account4);
		accounts = accountRepository.getAccounts(budget2);
		assertEquals(size, accounts.size());
	}

	@Test
	public void testCount() {
		int count = accountRepository.count(budget1);
		Account account1 = accountRepository.save(acc1);
		Account account2 = accountRepository.save(acc2);
		assertEquals(count + 1, accountRepository.count(budget1));
		
		accountRepository.delete(account1);
		accountRepository.delete(account2);
		assertEquals(count, accountRepository.count(budget1));
	}

}
