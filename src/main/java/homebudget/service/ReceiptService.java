package homebudget.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import homebudget.domain.Account;
import homebudget.domain.Budget;
import homebudget.domain.Category;
import homebudget.domain.Receipt;
import homebudget.domain.Subcategory;
import homebudget.persist.ReceiptReadRepository;
import homebudget.persist.ReceiptWriteRepository;
import homebudget.web.ObjectNotFoundException;

@Service
public class ReceiptService {
	private final ReceiptReadRepository readRepository;
	private final ReceiptWriteRepository writeRepository;
	private final IdHider idHider;
	
	@Autowired
	public ReceiptService(ReceiptReadRepository readRepository, ReceiptWriteRepository writeRepository,
			IdHider idHider) {
		this.readRepository = readRepository;
		this.writeRepository = writeRepository;
		this.idHider = idHider;
	}
	
	public class ReceiptNotFoundException extends ObjectNotFoundException {
		private static final long serialVersionUID = 817335733083261259L;
	}
	
	public Receipt save(Receipt receipt) {
		Receipt savedReceipt = writeRepository.save(receipt);
		savedReceipt = setHiddenId(savedReceipt);
		return savedReceipt;
	}
	
	public Receipt update(Receipt receipt) {
		Receipt updatedReceipt =  writeRepository.update(receipt);
		updatedReceipt = setHiddenId(updatedReceipt);
		return updatedReceipt;
	}
	
	public void delete(Receipt receipt) {
		writeRepository.delete(receipt);
	}
	
	public int deleteAll(Budget budget) {
		return writeRepository.deleteAll(budget);
	}
	
	public Receipt getById(long id) {
		Receipt receipt = readRepository.getById(id);
		if(receipt==null)
			throw new ReceiptNotFoundException();
		receipt = setHiddenId(receipt);
		return receipt;
	}
	
	public Receipt getByHiddenId(String hiddenId) {
		Long id = idHider.getId(Receipt.class, hiddenId);
		return getById(id);
	}
	
	public List<Receipt> getReceipts(Budget budget, int start, int max) {
		if(readRepository.count(budget)==0) {
			return Collections.emptyList();
		} else {
			return getReceiptsWithHiddenIds(budget, start, max);
		}
	}

	private List<Receipt> getReceiptsWithHiddenIds(Budget budget, int start, int max) {
		List<Receipt> receipts = readRepository.getReceipts(budget, start, max);
		for(Receipt receipt : receipts) {
			receipt = setHiddenId(receipt);
		}
		return receipts;
	}
	
	public List<Receipt> getReceipts(Category category, int start, int max) {
		if(readRepository.count(category)==0) {
			return Collections.emptyList();
		} else {
			return getReceiptsWithHiddenIds(category, start, max);
		}
	}
	
	private List<Receipt> getReceiptsWithHiddenIds(Category category, int start, int max) {
		List<Receipt> receipts = readRepository.getReceipts(category, start, max);
		for(Receipt receipt : receipts) {
			receipt = setHiddenId(receipt);
		}
		return receipts;
	}
	
	public List<Receipt> getReceipts(Subcategory subcategory, int start, int max) {
		if(readRepository.count(subcategory)==0) {
			return Collections.emptyList();
		} else {
			return getReceiptsWithHiddenIds(subcategory, start, max);
		}
	}
	
	private List<Receipt> getReceiptsWithHiddenIds(Subcategory subcategory, int start, int max) {
		List<Receipt> receipts = readRepository.getReceipts(subcategory, start, max);
		for(Receipt receipt : receipts) {
			receipt = setHiddenId(receipt);
		}
		return receipts;
	}
	
	public List<Receipt> getReceipts(Account account, int start, int max) {
		if(readRepository.count(account)==0) {
			return Collections.emptyList();
		} else {
			return getReceiptsWithHiddenIds(account, start, max);
		}
	}
	
	private List<Receipt> getReceiptsWithHiddenIds(Account account, int start, int max) {
		List<Receipt> receipts = readRepository.getReceipts(account, start, max);
		for(Receipt receipt : receipts) {
			receipt = setHiddenId(receipt);
		}
		return receipts;
	}
	
	public int count() {
		return readRepository.count();
	}
	
	public int count(Budget budget) {
		return readRepository.count(budget);
	}
	
	public int count(Category category) {
		return readRepository.count(category);
	}
	
	public int count(Subcategory subcategory) {
		return readRepository.count(subcategory);
	}
	
	public int count(Account account) {
		return readRepository.count(account);
	}
	
	public Date getFirstReceiptDate(Budget budget) {
		return readRepository.getFirstReceiptDate(budget);
	}
	
	private Receipt setHiddenId(Receipt receipt) {
		receipt.setHiddenId(idHider.getHiddenId(Receipt.class, receipt.getId()));
		if(receipt.getAccount()!=null) {
			receipt.setAccount(setHiddenId(receipt.getAccount()));
		}
		if(receipt.getSubcategory()!=null) {
			receipt.setSubcategory(setHiddenId(receipt.getSubcategory()));
		}
		return receipt;
	}
	
	private Account setHiddenId(Account account) {
		account.setHiddenId(idHider.getHiddenId(Account.class, account.getId()));
		if(account.getBudget()!=null) {
			account.setBudget(setHiddenId(account.getBudget()));
		}
		return account;
	}

	private Subcategory setHiddenId(Subcategory subcategory) {
		subcategory.setHiddenId(idHider.getHiddenId(Subcategory.class, subcategory.getId()));
		if(subcategory.getCategory()!=null) {
			subcategory.setCategory(setHiddenId(subcategory.getCategory()));
		}
		return subcategory;
	}
	
	private Category setHiddenId(Category category) {
		category.setHiddenId(idHider.getHiddenId(Category.class, category.getId()));
		if(category.getBudget()!=null) {
			category.setBudget(setHiddenId(category.getBudget()));
		}
		return category;
	}
	
	private Budget setHiddenId(Budget budget) {
		budget.setHiddenId(idHider.getHiddenId(Budget.class, budget.getId()));
		return budget;
	}
}
