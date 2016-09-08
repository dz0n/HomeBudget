package homebudget.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import homebudget.domain.Budget;
import homebudget.persist.ActiveBudgetRepository;

public class ActiveBudgetServiceTest {
	private static ActiveBudgetService service;
	private static ActiveBudgetRepository repository;
	
	@BeforeClass
	public static void setUpFields() {
		repository = Mockito.mock(ActiveBudgetRepository.class);
		
		service = new ActiveBudgetService(repository);
	}
	
	@Before
	public void setUp() {
		Budget budget = new Budget(123L, "budget name", "none", null);
		
		Mockito.when(repository.getActiveBudget("user1")).thenReturn(budget);
	}
	
	@Test
	public void testGet() {
		setUnauthorizedUser();
		Budget budget = service.get();
		assertNull(budget);
		
		setAuthorizedUser();
		budget = service.get();
		assertNotNull(budget);
		assertEquals(Long.valueOf(123L), budget.getId());
		
		Mockito.when(repository.getActiveBudget("user3")).thenReturn(null);
		setAuthorizedUser("user3");
		budget = service.get();
		assertNull(budget);
	}
	
	@SuppressWarnings("serial")
	@Test
	public void testSet() {
		Budget budget = new Budget(1234L, "budget name", "none", null);
		setUnauthorizedUser();
		boolean result = service.set(budget);
		Mockito.verify(repository, Mockito.times(0)).setActiveBudget(Mockito.anyString(), (Budget) Mockito.any());
		assertFalse(result);
		
		setAuthorizedUser();
		result = service.set(budget);
		Mockito.verify(repository, Mockito.times(1)).setActiveBudget(Mockito.anyString(), (Budget) Mockito.any());
		assertTrue(result);
		
		Mockito.doThrow(new DataAccessException("") { } ).when(repository).setActiveBudget("user2", budget);
		setAuthorizedUser("user2");
		result = service.set(budget);
		Mockito.verify(repository, Mockito.times(2)).setActiveBudget(Mockito.anyString(), (Budget) Mockito.any());
		assertFalse(result);
	}
	
	@SuppressWarnings("serial")
	@Test
	public void testRemove() {
		setUnauthorizedUser();
		boolean result = service.remove();
		Mockito.verify(repository, Mockito.times(0)).removeActiveBudget("user1");
		assertFalse(result);
		
		setAuthorizedUser();
		result = service.remove();
		Mockito.verify(repository, Mockito.times(1)).removeActiveBudget("user1");
		assertTrue(result);
		
		Mockito.doThrow(new DataAccessException("") { } ).when(repository).removeActiveBudget("user2");
		setAuthorizedUser("user2");
		result = service.remove();
		Mockito.verify(repository, Mockito.times(1)).removeActiveBudget("user2");
		assertFalse(result);
	}
	
	private void setUnauthorizedUser(){
		User user = new User("anonymousUser", "abc", new ArrayList<SimpleGrantedAuthority>());
		Authentication auth = new UsernamePasswordAuthenticationToken(user, null);
		
		SecurityContextHolder.getContext().setAuthentication(auth);
	}
	
	private void setAuthorizedUser(){
		setAuthorizedUser("user1");
	}
	
	private void setAuthorizedUser(String username){
		Collection<GrantedAuthority> grandtedAuthorities = new ArrayList<GrantedAuthority>();
		grandtedAuthorities.add(new SimpleGrantedAuthority("ROLE_FULL"));
		User user = new User(username, "abc", true, false, false, false, grandtedAuthorities);
		Authentication auth = new UsernamePasswordAuthenticationToken(user, null, grandtedAuthorities);
		SecurityContextHolder.getContext().setAuthentication(auth);
	}
}
