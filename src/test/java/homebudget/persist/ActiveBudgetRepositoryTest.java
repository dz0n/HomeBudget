package homebudget.persist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
import homebudget.domain.User;
import homebudget.persist.ActiveBudgetRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RepositoryTestConfig.class)
@WebAppConfiguration
@ActiveProfiles("test")
@WithMockUser("proba")
public class ActiveBudgetRepositoryTest {
	
	@Autowired
	private ActiveBudgetRepository repository;
	
	@Test
	public void testGetActiveBudgetIsNull() {
		Budget budget = repository.getActiveBudget("proba");
		assertNull(budget);
	}
	
	@Test
	public void testGetActiveBudget() {
		Budget budget = new Budget(1L, "test", "", new User(11L, "proba"));
		repository.setActiveBudget("proba", budget);
		
		Budget activeBudget = repository.getActiveBudget("proba");
		assertNotNull(budget);
		assertEquals("test", activeBudget.getName());
		assertEquals(Long.valueOf(1L), activeBudget.getId());
		
		repository.removeActiveBudget("proba");
	}

	@Test
	public void testSetActiveBudget() {
		Budget budget = new Budget(1L, "test", "", new User(11L, "proba"));
		repository.setActiveBudget("proba", budget);
		
		Budget activeBudget = repository.getActiveBudget("proba");
		assertNotNull(activeBudget);
		
		repository.removeActiveBudget("proba");
		activeBudget = repository.getActiveBudget("proba");
		assertNull(activeBudget);
		
		repository.removeActiveBudget("proba");
	}

}
