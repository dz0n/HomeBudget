package homebudget.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import homebudget.domain.Budget;
import homebudget.domain.Category;
import homebudget.domain.CategoryTransfer;
import homebudget.persist.CategoryTransferRepository;
import homebudget.web.ObjectNotFoundException;

@Service
public class CategoryTransferService {
	private final CategoryTransferRepository repository;
	private final IdHider idHider;
	
	@Autowired
	public CategoryTransferService(CategoryTransferRepository repository, IdHider idHider) {
		this.repository = repository;
		this.idHider = idHider;
	}
	
	public class CategoryTransferNotFoundException extends ObjectNotFoundException {
		private static final long serialVersionUID = -1128539949937258672L;
		
	}
	
	public CategoryTransfer save(CategoryTransfer categoryTransfer) {
		CategoryTransfer savedTransfer = repository.save(categoryTransfer);
		savedTransfer = setHiddenId(savedTransfer);
		return savedTransfer;
	}
	
	public void delete(CategoryTransfer categoryTransfer) {
		categoryTransfer = setIdIfNull(categoryTransfer);
		repository.delete(categoryTransfer);
	}

	private CategoryTransfer setIdIfNull(CategoryTransfer categoryTransfer) {
		if(categoryTransfer.getId()==null) {
			categoryTransfer.setId(idHider.getId(CategoryTransfer.class, categoryTransfer.getHiddenId()));
		}
		return categoryTransfer;
	}
	
	public int deleteAll(Budget budget) {
		return repository.deleteAll(budget);
	}
	
	public CategoryTransfer update(CategoryTransfer categoryTransfer) {
		categoryTransfer = setIdIfNull(categoryTransfer);
		CategoryTransfer updatedTransfer = repository.update(categoryTransfer);
		updatedTransfer = setHiddenId(updatedTransfer);
		return updatedTransfer;
	}
	
	public CategoryTransfer getById(long id) {
		CategoryTransfer transfer = repository.getById(id);
		if(transfer==null)
			throw new CategoryTransferNotFoundException();
		transfer = setHiddenId(transfer);
		return transfer;
	}
	
	public CategoryTransfer getByHiddenId(String hiddenId) {
		Long id = idHider.getId(CategoryTransfer.class, hiddenId);
		return getById(id);
	}
	
	public List<CategoryTransfer> getTransfers(Budget budget, int start, int max) {
		if(repository.count(budget)==0) {
			return Collections.emptyList();
		} else {
			return getTransfersWithHiddenIds(budget, start, max);
		}
	}
	
	private List<CategoryTransfer> getTransfersWithHiddenIds(Budget budget, int start, int max) {
		List<CategoryTransfer> transfers = repository.getTransfers(budget, start, max);
		for(CategoryTransfer transfer : transfers) {
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

	private CategoryTransfer setHiddenId(CategoryTransfer transfer) {
		transfer.setHiddenId(idHider.getHiddenId(CategoryTransfer.class, transfer.getId()));
		if(transfer.getFromCategory()!=null) {
			transfer.setFromCategory(setHiddenId(transfer.getFromCategory()));
		}
		if(transfer.getToCategory()!=null) {
			transfer.setToCategory(setHiddenId(transfer.getToCategory()));
		}
		return transfer;
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
