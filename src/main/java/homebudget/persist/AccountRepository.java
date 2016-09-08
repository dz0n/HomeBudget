package homebudget.persist;

import java.util.List;

import homebudget.domain.Account;
import homebudget.domain.Budget;

public interface AccountRepository {
	
	public Account save(Account account);
	
	public void update(Account account);
	
	public void delete(Account account);
	
	public int deleteAll(Budget budget);
	
	public List<Account> getAccounts(Budget budget);
	
	public Account getAccountById(long id);
	
	public int count(Budget budget);
	
}
