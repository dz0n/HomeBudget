package homebudget.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import homebudget.domain.Account;
import homebudget.domain.Budget;
import homebudget.domain.Category;
import homebudget.service.IdHider;
import homebudget.service.MapIdHider;

public class MapIdHiderTest {
	private IdHider idHider;
	
	private class CategoryBis extends Category {
		
	}
	
	@Before
	public void setUp() throws Exception {
		idHider = new MapIdHider();
	}

	@Test
	public void testGetId() {
		String hashOf10 = idHider.getHiddenId(Category.class, 10L);
		assertEquals("1", hashOf10);
		String hashOf11 = idHider.getHiddenId(Category.class, 11L);
		assertEquals("2", hashOf11);
		String hashOf12 = idHider.getHiddenId(Category.class, 12L);
		assertEquals("3", hashOf12);
		String hashOf13 = idHider.getHiddenId(Category.class, 13L);
		assertEquals("4", hashOf13);
		
		String hashOf20 = idHider.getHiddenId(Budget.class, 20L);
		assertEquals("1", hashOf20);
		String hashOf21 = idHider.getHiddenId(Budget.class, 21L);
		assertEquals("2", hashOf21);

		String hashOf30 = idHider.getHiddenId(Account.class, 30L);
		assertEquals("1", hashOf30);
		String hashOf31 = idHider.getHiddenId(Account.class, 31L);
		assertEquals("2", hashOf31);
		
	}

	@Test
	public void testGetHiddenId() {
		String hashOf10 = idHider.getHiddenId(Category.class, 10L);
		assertEquals("1", hashOf10);
		String hashOf11 = idHider.getHiddenId(Category.class, 11L);
		assertEquals("2", hashOf11);
		
		String hashOf20 = idHider.getHiddenId(Budget.class, 20L);
		assertEquals("1", hashOf20);
		String hashOf21 = idHider.getHiddenId(Budget.class, 21L);
		assertEquals("2", hashOf21);

		String hashOf30 = idHider.getHiddenId(Account.class, 30L);
		assertEquals("1", hashOf30);
		String hashOf31 = idHider.getHiddenId(Account.class, 31L);
		assertEquals("2", hashOf31);
		
		assertEquals(Long.valueOf(10L), idHider.getId(Category.class, hashOf10));
		assertEquals(Long.valueOf(11L), idHider.getId(Category.class, hashOf11));
		
		assertEquals(Long.valueOf(20L), idHider.getId(Budget.class, hashOf20));
		assertEquals(Long.valueOf(21L), idHider.getId(Budget.class, hashOf21));
		
		assertEquals(Long.valueOf(30L), idHider.getId(Account.class, hashOf30));
		assertEquals(Long.valueOf(31L), idHider.getId(Account.class, hashOf31));
	}
	
	@Test
	public void testInheritance() {
		String hashOf10 = idHider.getHiddenId(Category.class, 10L);
		assertEquals("1", hashOf10);
		String hashOf11 = idHider.getHiddenId(Category.class, 11L);
		assertEquals("2", hashOf11);
		
		String hashOf20 = idHider.getHiddenId(CategoryBis.class, 20L);
		assertEquals("1", hashOf20);
	}
	
	@Test
	public void testUniqueValues() {
		String hashOf10 = idHider.getHiddenId(Category.class, 10L);
		assertEquals("1", hashOf10);
		
		String hashOf12 = idHider.getHiddenId(Category.class, 11L);
		assertEquals("2", hashOf12);
		String hashOf13 = idHider.getHiddenId(Category.class, 11L);
		assertEquals("2", hashOf13);
		
		String hashOf11 = idHider.getHiddenId(Category.class, 10L);
		assertEquals("1", hashOf11);
	}
	
	@Test
	public void testNoId() {
		Long id = idHider.getId(Category.class, "1");
		assertNull(id);
		
		idHider.getHiddenId(Category.class, 1212L);
		id = idHider.getId(Category.class, "120");
		assertNull(id);
	}
	
	@Test
	@Ignore("time tests for different versions of IdHider")
	public void timeTest() {
		long start = System.currentTimeMillis();
		for (int i = 0; i<100000; i++) {
			idHider = new MapIdHider();
			testGetHiddenId();
			for(long j = 0; j<100; j++) {
				idHider.getHiddenId(Budget.class, j);
			}
			for(int j = 80; j < 95; j++) {
				Long k = idHider.getId(Budget.class, Long.toString(j));
				k = k + 0;
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("DEBUG: Logic toke " + (end - start) + " milliseconds");
	}
}
