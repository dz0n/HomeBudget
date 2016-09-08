package homebudget.persist;

import java.util.List;

import homebudget.domain.AccountTransfer;
import homebudget.domain.Budget;

public interface AccountTransferRepository {
	
	public AccountTransfer save(AccountTransfer accountTransfer);
	
	public void delete(AccountTransfer accountTransfer);
	
	public int deleteAll(Budget budget);
	
	public AccountTransfer update(AccountTransfer accountTransfer);
	
	public AccountTransfer getById(long id);
	
	public List<AccountTransfer> getTransfers(Budget budget, int start, int max);
	
	public int count(Budget budget);
	
	public int count();
	
}
