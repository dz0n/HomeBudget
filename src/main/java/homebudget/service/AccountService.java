package homebudget.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import homebudget.domain.Account;
import homebudget.domain.Budget;
import homebudget.persist.AccountRepository;
import homebudget.web.ObjectNotFoundException;

@Service
public class AccountService {
	private final AccountRepository accountRepository;
	private final IdHider idHider;
	
	@Autowired
	public AccountService(AccountRepository accountRepository, IdHider idHider) {
		this.accountRepository = accountRepository;
		this.idHider = idHider;
	}

	public class AccountNotFoundException extends ObjectNotFoundException {
		private static final long serialVersionUID = 9073103575505225813L;
		
	}
	
	public BigDecimal getAccountsSum(Budget budget) {
		List<Account> accounts = this.getAccounts(budget);
		long accountsSum = 0;
		for(Account account : accounts) {
			accountsSum += account.getLongValue();
		}
		return BigDecimal.valueOf(accountsSum).divide(BigDecimal.valueOf(100));
	}
	
	public int count(Budget budget) {
		return accountRepository.count(budget);
	}
	
	public Account save(Account account) {
		Account savedAccount = accountRepository.save(account);
		savedAccount = setHiddenId(savedAccount);
		return savedAccount;
	}
	
	public void update(Account account) {
		account = setIdIfNull(account);
		accountRepository.update(account);
	}

	private Account setIdIfNull(Account account) {
		if(account.getId()==null) {
			account.setId(idHider.getId(Account.class, account.getHiddenId()));
		}
		return account;
	}
	
	public void delete(Account account) {
		account = setIdIfNull(account);
		accountRepository.delete(account);
	}
	
	public int deleteAll(Budget budget) {
		return accountRepository.deleteAll(budget);
	}
	
	public List<Account> getAccounts(Budget budget) {
		if(accountRepository.count(budget)==0) {
			return Collections.emptyList();
		} else {
			return getAccountsWithHiddenIds(budget);
		}
	}
	
	private List<Account> getAccountsWithHiddenIds(Budget budget) {
		List<Account> accounts = accountRepository.getAccounts(budget);
		for(Account account : accounts) {
			account = setHiddenId(account);
		}
		return accounts;
	}
	
	public Account getAccountById(long id) {
		Account account = accountRepository.getAccountById(id);
		if(account==null)
			throw new AccountNotFoundException();
		account = setHiddenId(account);
		return account;
	}
	
	public Account getAccountByHiddenId(String hiddenId) {
		Long id = idHider.getId(Account.class, hiddenId);
		return getAccountById(id);
	}
	
	private Account setHiddenId(Account account) {
		account.setHiddenId(idHider.getHiddenId(Account.class, account.getId()));
		if(account.getBudget()!=null) {
			account.setBudget(setHiddenId(account.getBudget()));
		}
		return account;
	}
	
	private Budget setHiddenId(Budget budget) {
		budget.setHiddenId(idHider.getHiddenId(Budget.class, budget.getId()));
		return budget;
	}
}
