package homebudget.persist;

import homebudget.domain.Budget;

public interface DefaultFieldsRepository {
	
	public Long getDefaultSubcategoryId(Budget budget, String username);
	
	public Long getDefaultAccountId(Budget budget, String username);
	
}
