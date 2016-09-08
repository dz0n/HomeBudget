package homebudget.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/analysis")
public class AnalysisControler {
	@RequestMapping(value="/monthlybalance", method=GET)
	public String monthlyBalance(Model model) {
		return "analysis/monthly/balance";
	}
	
	@RequestMapping(value="/monthlybudget", method=GET)
	public String monthlyBudgets(Model model) {
		return "analysis/monthly/budget";
	}
}
