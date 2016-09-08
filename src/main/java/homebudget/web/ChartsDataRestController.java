package homebudget.web;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import homebudget.domain.Budget;
import homebudget.domain.MonthValue;
import homebudget.service.ActiveBudgetService;
import homebudget.service.BudgetCalculationService;

@RestController
@RequestMapping("/data/charts/monthly")
public class ChartsDataRestController {
	private final BudgetCalculationService budgetCalculationService;
	private final ActiveBudgetService activeBudgetService;
	
	@Autowired
	public ChartsDataRestController(BudgetCalculationService budgetCalculationService,
			ActiveBudgetService activeBudgetService) {
		this.budgetCalculationService = budgetCalculationService;
		this.activeBudgetService = activeBudgetService;
	}
	
	@RequestMapping("/balance")
	public List<MonthValue> monthlyBalance() {
		Budget budget = activeBudgetService.get();
		return budgetCalculationService.getLastMonthsBalance(budget, new Date());
	}
	
	@RequestMapping("/balance/{months}")
	public List<MonthValue> customizedMonthlyBalance(@PathVariable int months) {
		Budget budget = activeBudgetService.get();
		return budgetCalculationService.getLastMonthsBalance(budget, new Date(), months);
	}
	
	@RequestMapping("/budget")
	public List<MonthValue> monthlyBudget() {
		Budget budget = activeBudgetService.get();
		return budgetCalculationService.getLastMonthsBudgetValues(budget, new Date());
	}
	
	@RequestMapping("/budget/{months}")
	public List<MonthValue> customizedMonthlyBudget(@PathVariable int months) {
		Budget budget = activeBudgetService.get();
		return budgetCalculationService.getLastMonthsBudgetValues(budget, new Date(), months);
	}
}
