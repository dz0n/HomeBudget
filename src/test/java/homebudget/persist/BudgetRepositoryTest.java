package homebudget.persist;

import static org.junit.Assert.assertEquals;

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
import org.springframework.transaction.annotation.Transactional;

import homebudget.config.RepositoryTestConfig;
import homebudget.domain.Budget;
import homebudget.domain.User;
import homebudget.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RepositoryTestConfig.class)
@WebAppConfiguration
@ActiveProfiles("test")
@WithMockUser("user2")
public class BudgetRepositoryTest {
	@Autowired
	private BudgetRepository budgetRepository;
	@Autowired
	private UserService userService;
	private User user;
	
	@Before
	public void setup() {
		user = userService.getUserByUsername("user2");
	}
	
	@Test
	//TODO for refactoring
	@Ignore("for refactoring")
	public void count() {
		assertEquals(3, budgetRepository.count());
	}
	
	@Test
	@WithMockUser("proba")
	//TODO for refactoring
	@Ignore("for refactoring")
	public void getBudgets() {
		List<Budget> budgets = budgetRepository.getBudgets("proba");
		assertEquals(2, budgets.size());
	}
	
	@Test
	public void save() {
		int count = budgetRepository.count();
		Budget budget = new Budget(null, "test", "brak", user);
		Budget savedBudget = budgetRepository.save(budget);
		
		assertEquals(count + 1, budgetRepository.count());
		long id = savedBudget.getId();
		assertEquals(Long.valueOf(id), budgetRepository.getBudgetById(id).getId());
		budgetRepository.delete(savedBudget);
	}
	
	@Test
	@Transactional
	public void update() {
		Budget budget = new Budget(1L, "test 2", "bla", user);
		budgetRepository.update(budget);
		
		assertEquals("test 2", budgetRepository.getBudgetById(1).getName());
	}
	
	@Test
	public void delete() {
		int count = budgetRepository.count();
		Budget budget = new Budget(null, "test 5", "", user);
		budgetRepository.save(budget);
		
		assertEquals(count + 1, budgetRepository.count());
		
		budgetRepository.delete(budget);
		assertEquals(count, budgetRepository.count());
	}
	
}
