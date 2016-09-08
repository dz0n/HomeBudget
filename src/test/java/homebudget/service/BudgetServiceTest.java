package homebudget.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import homebudget.domain.AccessLevel;
import homebudget.domain.Budget;
import homebudget.domain.User;
import homebudget.persist.BudgetRepository;

public class BudgetServiceTest {
	private BudgetService budgetService;
	private BudgetRepository budgetRepository;
	private AccountService accountService;
	private CategoryService categoryService;
	private AccessToBudgetService accessToBudgetService;
	private UserService userService;
	private ReceiptService receiptService;
	private SubcategoryService subcategoryService;
	private AccountTransferService accountTransferService;
	private CategoryTransferService categoryTransferService;
	private IdHider idHider;
	
	private User userA = new User("user A");
	private User userB = new User("user B");
	private Budget ownBudget1 = new Budget(1L, "own 1", "", userA);
	private Budget ownBudget2 = new Budget(2L, "own 2", "", userA);
	private Budget ownBudget3 = new Budget(3L, "own 3", "", userA);
	private Budget sharedBudget1 = new Budget(4L, "shared 1", "", userB);
	private Budget sharedBudget2 = new Budget(5L, "shared 2", "", userB);
	
	@Before
	public void setUpFields() {
		budgetRepository = Mockito.mock(BudgetRepository.class);
		accessToBudgetService = Mockito.mock(AccessToBudgetService.class);
		accountService = Mockito.mock(AccountService.class);
		categoryService = Mockito.mock(CategoryService.class);
		receiptService = Mockito.mock(ReceiptService.class);
		subcategoryService = Mockito.mock(SubcategoryService.class);
		accountTransferService = Mockito.mock(AccountTransferService.class);
		categoryTransferService = Mockito.mock(CategoryTransferService.class);
		idHider = new MapIdHider();
		budgetService = new BudgetService(budgetRepository, accountService, categoryService, accessToBudgetService, userService, receiptService, subcategoryService, accountTransferService, categoryTransferService, idHider);
		
		ArrayList<Budget> ownBudgets = new ArrayList<Budget>();
		ownBudgets.add(ownBudget1);
		ownBudgets.add(ownBudget2);
		ownBudgets.add(ownBudget3);
		Mockito.when(budgetRepository.getBudgets(userA.getUsername())).thenReturn(ownBudgets);
		
		Map<Long, AccessLevel> sharedBudgetsIds = new HashMap<Long, AccessLevel>();
		sharedBudgetsIds.put(sharedBudget1.getId(), AccessLevel.FULL);
		sharedBudgetsIds.put(sharedBudget2.getId(), AccessLevel.USE);
		Mockito.when(accessToBudgetService.getIdsOfAccessesToBudgets(userA.getUsername())).thenReturn(sharedBudgetsIds);
		
		Mockito.when(budgetRepository.getBudgetById(sharedBudget1.getId())).thenReturn(sharedBudget1);
		Mockito.when(budgetRepository.getBudgetById(sharedBudget2.getId())).thenReturn(sharedBudget2);
	}
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetBudgets() {
		List<Budget> budgets = budgetService.getBudgets(userA.getUsername());
		assertEquals(5, budgets.size());
	}

	@Test
	public void testGetOwnBudgets() {
		List<Budget> budgets = budgetService.getOwnBudgets(userA.getUsername());
		assertEquals(3, budgets.size());
	}

	@Test
	public void testGetSharedBudgets() {
		List<Budget> budgets = budgetService.getSharedBudgets(userA.getUsername());
		assertEquals(2, budgets.size());
	}
	
	@Test
	public void testGetBudgetsContain() {
		List<Budget> budgets = budgetService.getBudgets(userA.getUsername());
		assertTrue(budgets.contains(ownBudget1));
		assertTrue(budgets.contains(ownBudget2));
		assertTrue(budgets.contains(ownBudget3));
		assertTrue(budgets.contains(sharedBudget1));
		assertTrue(budgets.contains(sharedBudget2));
	}

	@Test
	public void testGetOwnBudgetsContain() {
		List<Budget> budgets = budgetService.getOwnBudgets(userA.getUsername());
		assertTrue(budgets.contains(ownBudget1));
		assertTrue(budgets.contains(ownBudget2));
		assertTrue(budgets.contains(ownBudget3));
		assertFalse(budgets.contains(sharedBudget1));
		assertFalse(budgets.contains(sharedBudget2));
	}

	@Test
	public void testGetSharedBudgetsContain() {
		List<Budget> budgets = budgetService.getSharedBudgets(userA.getUsername());
		assertFalse(budgets.contains(ownBudget1));
		assertFalse(budgets.contains(ownBudget2));
		assertFalse(budgets.contains(ownBudget3));
		assertTrue(budgets.contains(sharedBudget1));
		assertTrue(budgets.contains(sharedBudget2));
	}
}
