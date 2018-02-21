package homebudget.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import homebudget.domain.Account;
import homebudget.domain.Budget;
import homebudget.domain.FieldId;
import homebudget.domain.Subcategory;
import homebudget.persist.DefaultFieldsRepository;

@Service
public class DefaultFieldsService {
	private final DefaultFieldsRepository defaultFieldsRepository;
	private final IdHider idHider;
	private final BudgetService budgetService;
	private final SubcategoryService subcategoryService;
	
	@Autowired
	public DefaultFieldsService(DefaultFieldsRepository defaultFieldsRepository, IdHider idHider,
			BudgetService budgetService, SubcategoryService subcategoryService) {
		this.defaultFieldsRepository = defaultFieldsRepository;
		this.idHider = idHider;
		this.budgetService = budgetService;
		this.subcategoryService = subcategoryService;
	}
	
	public List<FieldId> getDefaultSubcategoryHiddenId(String budgetHiddenId, String username) {
		Budget budget = budgetService.getBudgetByHiddenId(budgetHiddenId);
		Long subcategoryId = defaultFieldsRepository.getDefaultSubcategoryId(budget, username);
		
		if(subcategoryId == null) {
			return Collections.emptyList();
		} else {
			Subcategory subcategory = subcategoryService.getSubcategoryById(subcategoryId);
			List<FieldId> fields = new ArrayList<FieldId>();
			FieldId categoryFieldId = new FieldId("category", subcategory.getCategory().getHiddenId());
			fields.add(categoryFieldId);
			FieldId subcategoryFieldId = new FieldId("subcategory", subcategory.getHiddenId());
			fields.add(subcategoryFieldId);
			
			return fields;
		}
	}
	
	public List<FieldId> getDefaultAccountHiddenId(String budgetHiddenId, String username) {
		Budget budget = budgetService.getBudgetByHiddenId(budgetHiddenId);
		Long id = defaultFieldsRepository.getDefaultAccountId(budget, username);
		if(id == null) {
			return Collections.emptyList();
		} else {
			List<FieldId> fields = new ArrayList<FieldId>();
			FieldId fieldId = new FieldId("account", idHider.getHiddenId(Account.class, id));
			fields.add(fieldId);
			return fields;
		}
	}
}
