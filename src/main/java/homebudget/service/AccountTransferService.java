package homebudget.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import homebudget.domain.Account;
import homebudget.domain.AccountTransfer;
import homebudget.domain.Budget;
import homebudget.persist.AccountTransferRepository;
import homebudget.web.ObjectNotFoundException;

@Service
public class AccountTransferService {
	private final AccountTransferRepository repository;
	private final IdHider idHider;
	
	@Autowired
	public AccountTransferService(AccountTransferRepository repository, IdHider idHider) {
		this.repository = repository;
		this.idHider = idHider;
	}
	
	public class AccountTransferNotFoundException extends ObjectNotFoundException {
		private static final long serialVersionUID = 1525226211278794059L;
		
	}
	
	public AccountTransfer save(AccountTransfer accountTransfer) {
		AccountTransfer savedTransfer = repository.save(accountTransfer);
		savedTransfer = setHiddenId(savedTransfer);
		return savedTransfer;
	}
	
	public void delete(AccountTransfer accountTransfer) {
		accountTransfer = setIdIfNull(accountTransfer);
		repository.delete(accountTransfer);
	}

	private AccountTransfer setIdIfNull(AccountTransfer accountTransfer) {
		if(accountTransfer.getId()==null) {
			accountTransfer.setId(idHider.getId(AccountTransfer.class, accountTransfer.getHiddenId()));
		}
		return accountTransfer;
	}
	
	public int deleteAll(Budget budget) {
		return repository.deleteAll(budget);
	}
	
	public AccountTransfer update(AccountTransfer accountTransfer) {
		accountTransfer = setIdIfNull(accountTransfer);
		AccountTransfer updatedTransfer = repository.update(accountTransfer);
		updatedTransfer = setHiddenId(updatedTransfer);
		return updatedTransfer;
	}
	
	public AccountTransfer getById(long id) {
		AccountTransfer transfer = repository.getById(id);
		if(transfer==null)
			throw new AccountTransferNotFoundException();
		transfer = setHiddenId(transfer);
		return transfer;
	}
	
	public AccountTransfer getByHiddenId(String hiddenId) {
		Long id = idHider.getId(AccountTransfer.class, hiddenId);
		return getById(id);
	}
	
	public List<AccountTransfer> getTransfers(Budget budget, int start, int max) {
		if(repository.count(budget)==0) {
			return Collections.emptyList();
		} else {
			return getTransfersWithHiddenIds(budget, start, max);
		}
	}
	
	private List<AccountTransfer> getTransfersWithHiddenIds(Budget budget, int start, int max) {
		List<AccountTransfer> transfers = repository.getTransfers(budget, start, max);
		for(AccountTransfer transfer : transfers) {
			transfer = setHiddenId(transfer);
		}
		return transfers;
	}
	
	public int count(Budget budget) {
		return repository.count(budget);
	}
	
	public int count() {
		return repository.count();
	}
	
	private AccountTransfer setHiddenId(AccountTransfer transfer) {
		transfer.setHiddenId(idHider.getHiddenId(AccountTransfer.class, transfer.getId()));
		if(transfer.getFromAccount()!=null) {
			transfer.setFromAccount(setHiddenId(transfer.getFromAccount()));
		}
		if(transfer.getToAccount()!=null) {
			transfer.setToAccount(setHiddenId(transfer.getToAccount()));
		}
		return transfer;
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
