package homebudget.persist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.After;
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
import homebudget.domain.AccessLevel;
import homebudget.domain.Budget;
import homebudget.service.BudgetService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RepositoryTestConfig.class)
@WebAppConfiguration
@ActiveProfiles("test")
@WithMockUser("proba")
public class AccessToBudgetRepositoryTest {
	private final String username1 = "proba";
	private final String username2 = "user3";
	
	@Autowired
	private AccessToBudgetRepository repository;
	@Autowired
	private BudgetService budgetService;
	private Budget budget;
	private Budget anotherBudget;
	
	@Before
	public void setUp() throws Exception {
		budget = budgetService.getBudgetById(1);
		anotherBudget = budgetService.getBudgetById(2);
	}
	
	@After
	public void cleanUp() throws Exception {
		repository.removeAccessToBudget(budget, username1);
		repository.removeAccessToBudget(anotherBudget, username1);
		repository.removeAccessToBudget(budget, username2);
		repository.removeAccessToBudget(anotherBudget, username2);
	}

	@Test
	public void addAccessToBudgetNoneLevelTest() {
		repository.addAccessToBudget(budget, username1, AccessLevel.NONE);
		Map<Long, AccessLevel> accesses = repository.getIdsOfAccessesToBudgets(username1);
		assertFalse(accesses.containsKey(budget.getId()));
	}
	
	@Test
	public void addAccessToBudgetModifyLevelTest() {
		repository.addAccessToBudget(budget, username1, AccessLevel.MODIFY);
		Map<Long, AccessLevel> accesses = repository.getIdsOfAccessesToBudgets(username1);
		assertTrue(accesses.containsKey(budget.getId()));
	}
	
	@Test
	public void addAccessToBudgetReadLevelTest() {
		repository.addAccessToBudget(budget, username1, AccessLevel.USE);
		Map<Long, AccessLevel> accesses = repository.getIdsOfAccessesToBudgets(username1);
		assertTrue(accesses.containsKey(budget.getId()));
	}
	
	@Test
	public void addAccessToBudgetFullLevelTest() {
		repository.addAccessToBudget(budget, username1, AccessLevel.FULL);
		Map<Long, AccessLevel> accesses = repository.getIdsOfAccessesToBudgets(username1);
		assertTrue(accesses.containsKey(budget.getId()));
	}
	
	@Test
	public void mapSizeOneTest() {
		Map<Long, AccessLevel> accesses = repository.getIdsOfAccessesToBudgets(username1);
		int size = accesses.size();
		repository.addAccessToBudget(budget, username1, AccessLevel.MODIFY);
		accesses = repository.getIdsOfAccessesToBudgets(username1);
		assertEquals(size + 1, accesses.size());
	}
	
	@Test
	public void mapSizeTwoTest() {
		Map<Long, AccessLevel> accesses = repository.getIdsOfAccessesToBudgets(username1);
		int size = accesses.size();
		repository.addAccessToBudget(budget, username1, AccessLevel.MODIFY);
		repository.addAccessToBudget(anotherBudget, username1, AccessLevel.USE);
		accesses = repository.getIdsOfAccessesToBudgets(username1);
		assertEquals(size + 2, accesses.size());
	}
	
	@Test
	public void mapSizeTwoWhenAnotherUserTest() {
		Map<Long, AccessLevel> accesses = repository.getIdsOfAccessesToBudgets(username1);
		int size = accesses.size();
		repository.addAccessToBudget(budget, username1, AccessLevel.MODIFY);
		repository.addAccessToBudget(anotherBudget, username1, AccessLevel.USE);
		repository.addAccessToBudget(anotherBudget, username2, AccessLevel.FULL);
		accesses = repository.getIdsOfAccessesToBudgets(username1);
		assertEquals(size + 2, accesses.size());
	}
	
	@Test
	public void mapSizeTwoWhenTwoBudgetsTest() {
		Map<Long, AccessLevel> accesses = repository.getIdsOfAccessesToBudgets(username1);
		int size = accesses.size();
		repository.addAccessToBudget(budget, username1, AccessLevel.MODIFY);
		repository.addAccessToBudget(anotherBudget, username1, AccessLevel.FULL);
		accesses = repository.getIdsOfAccessesToBudgets(username1);
		assertEquals(size + 2, accesses.size());
	}
	
	@Test
	public void mapSizeTwoWhenThoughAnotherUserTest() {
		Map<Long, AccessLevel> accesses = repository.getIdsOfAccessesToBudgets(username1);
		int size = accesses.size();
		repository.addAccessToBudget(budget, username1, AccessLevel.MODIFY);
		repository.addAccessToBudget(anotherBudget, username1, AccessLevel.FULL);
		repository.addAccessToBudget(budget, username2, AccessLevel.USE);
		repository.addAccessToBudget(anotherBudget, username2, AccessLevel.FULL);
		accesses = repository.getIdsOfAccessesToBudgets(username1);
		assertEquals(size + 2, accesses.size());
	}
	
	@Test
	public void mapSizeOneTheSameBudgetTest() {
		Map<Long, AccessLevel> accesses = repository.getIdsOfAccessesToBudgets(username1);
		int size = accesses.size();
		repository.addAccessToBudget(budget, username1, AccessLevel.MODIFY);
		repository.addAccessToBudget(budget, username1, AccessLevel.USE);
		repository.addAccessToBudget(budget, username1, AccessLevel.USE);
		repository.addAccessToBudget(budget, username1, AccessLevel.FULL);
		accesses = repository.getIdsOfAccessesToBudgets(username1);
		assertEquals(size + 1, accesses.size());
	}
	
	@Test
	public void lastAddAccessLevelTest() {
		repository.addAccessToBudget(budget, username1, AccessLevel.MODIFY);
		repository.addAccessToBudget(budget, username1, AccessLevel.FULL);
		repository.addAccessToBudget(budget, username1, AccessLevel.USE);
		Map<Long, AccessLevel> accesses = repository.getIdsOfAccessesToBudgets(username1);
		assertEquals(AccessLevel.USE, accesses.get(budget.getId()));
	}
	
	@Test
	public void noneAccessLevelTest() {
		repository.addAccessToBudget(budget, username1, AccessLevel.NONE);
		Map<Long, AccessLevel> accesses = repository.getIdsOfAccessesToBudgets(username1);
		assertFalse(accesses.containsKey(budget.getId()));
	}
	
	@Test
	public void noneAccessLevelAfterChangeTest() {
		repository.addAccessToBudget(budget, username1, AccessLevel.FULL);
		repository.addAccessToBudget(budget, username1, AccessLevel.NONE);
		Map<Long, AccessLevel> accesses = repository.getIdsOfAccessesToBudgets(username1);
		assertFalse(accesses.containsKey(budget.getId()));
	}
	
	@Test
	public void sizeAfterRemoveTest() {
		repository.addAccessToBudget(budget, username1, AccessLevel.FULL);
		Map<Long, AccessLevel> accesses = repository.getIdsOfAccessesToBudgets(username1);
		int size = accesses.size();
		
		repository.removeAccessToBudget(budget, username1);
		accesses = repository.getIdsOfAccessesToBudgets(username1);
		assertEquals(size - 1, accesses.size());
	}
	
	@Test
	public void removeTest() {
		repository.addAccessToBudget(budget, username1, AccessLevel.MODIFY);
		Map<Long, AccessLevel> accesses = repository.getIdsOfAccessesToBudgets(username1);
		assertTrue(accesses.containsKey(budget.getId()));
		
		repository.removeAccessToBudget(budget, username1);
		accesses = repository.getIdsOfAccessesToBudgets(username1);
		assertFalse(accesses.containsKey(budget.getId()));
	}
	
	@Test
	public void getAccessLevelReadLevelTest() {
		repository.addAccessToBudget(budget, username1, AccessLevel.USE);
		assertEquals(AccessLevel.USE, repository.getAccessLevel(budget, username1));
	}
	
	@Test
	public void getAccessLevelModifyLevelTest() {
		repository.addAccessToBudget(budget, username1, AccessLevel.MODIFY);
		assertEquals(AccessLevel.MODIFY, repository.getAccessLevel(budget, username1));
	}
	
	@Test
	public void getAccessLevelFullLevelTest() {
		repository.addAccessToBudget(budget, username1, AccessLevel.FULL);
		assertEquals(AccessLevel.FULL, repository.getAccessLevel(budget, username1));
	}
	
	@Test
	public void getAccessLevelNoneLevelTest() {
		repository.addAccessToBudget(budget, username1, AccessLevel.NONE);
		assertEquals(AccessLevel.NONE, repository.getAccessLevel(budget, username1));
	}
	
	@Test
	public void getAccessLevelAfterChangeTest() {
		repository.addAccessToBudget(budget, username1, AccessLevel.FULL);
		repository.addAccessToBudget(budget, username1, AccessLevel.USE);
		repository.addAccessToBudget(budget, username1, AccessLevel.MODIFY);
		assertEquals(AccessLevel.MODIFY, repository.getAccessLevel(budget, username1));
	}
	
	@Test
	public void getAccessLevelAnotherUserTest() {
		repository.addAccessToBudget(budget, username1, AccessLevel.FULL);
		assertEquals(AccessLevel.NONE, repository.getAccessLevel(budget, username2));
	}
	
	@Test
	public void getAccessLevelsToBudgetTest() {
		repository.addAccessToBudget(budget, username1, AccessLevel.FULL);
		repository.addAccessToBudget(budget, username2, AccessLevel.USE);
		Map<String, AccessLevel> accessLevels = repository.getAccessLevelsToBudget(budget);
		assertEquals(2, accessLevels.size());
		assertTrue(accessLevels.containsKey(username1));
		assertTrue(accessLevels.containsKey(username2));
	}
}
