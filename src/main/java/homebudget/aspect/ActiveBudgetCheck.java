package homebudget.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;

import homebudget.service.ActiveBudgetService;

@Aspect
public class ActiveBudgetCheck {	
	@Autowired
	private ActiveBudgetService activeBudgetService;

	@Around("execution(* homebudget.web.BudgetShowController.showBudget(..))" 
			+ " or execution(* homebudget.web.ReceiptNewController.showNewReceiptForm(..))"
			+ " or execution(* homebudget.web.BudgetHistoryController.showDefaultBudgetHistory(..))" 
			+ " or execution(* homebudget.web.TransferAccountHistoryController.showDefaultTransfersHistory(..))"
			+ " or execution(* homebudget.web.TransferCategoryHistoryController.showDefaultTransfersHistory(..))"
			+ " or execution(* homebudget.web.TransferAccountNewDefaultController.showNewAccountTransferFormWithActiveBudget(..))"
			+ " or execution(* homebudget.web.TransferCategoryNewDefaultController.showNewCategoryTransferFormWithActiveBudget(..))"
			+ " or execution(* homebudget.web.AnalysisControler.monthlyBalance(..))"
			+ " or execution(* homebudget.web.AnalysisControler.monthlyBudgets(..))")
	public Object check(ProceedingJoinPoint proceedingJoinPoint) {
		Object value = "redirect:/budgets";
		try {
			if (activeBudgetService.get() != null) {
				value = proceedingJoinPoint.proceed();
			}
		} catch (Throwable e) {
			return value;
		}
		return value;
	}
	
	public final ActiveBudgetService getActiveBudgetService() {
		return activeBudgetService;
	}
	
	public final void setActiveBudgetService(ActiveBudgetService activeBudgetService) {
		this.activeBudgetService = activeBudgetService;
	}
}
