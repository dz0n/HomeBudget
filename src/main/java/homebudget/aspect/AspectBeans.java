package homebudget.aspect;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AspectBeans {
	/*redirects to /budgets when there's no active budget
	*/
	@Bean
	public ActiveBudgetCheck activeBudgetCheck() {
		return new ActiveBudgetCheck();
	}
}
