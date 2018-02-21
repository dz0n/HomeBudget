package homebudget.web;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import homebudget.domain.FieldId;
import homebudget.service.DefaultFieldsService;

@RestController
@RequestMapping("/default")
public class DefaultFieldsRestController {
	private final DefaultFieldsService defaultFieldsService;

	@Autowired
	public DefaultFieldsRestController(DefaultFieldsService defaultFieldsService) {
		this.defaultFieldsService = defaultFieldsService;
	}

	@RequestMapping("/account/{budgetId}")
	public List<FieldId> defaultAccount(@PathVariable String budgetId, Principal principal) {
		return defaultFieldsService.getDefaultAccountHiddenId(budgetId, principal.getName());
	}
	
	@RequestMapping("/subcategory/{budgetId}")
	public List<FieldId> defaultSubcategory(@PathVariable String budgetId, Principal principal) {
		return defaultFieldsService.getDefaultSubcategoryHiddenId(budgetId, principal.getName());
	}
}
