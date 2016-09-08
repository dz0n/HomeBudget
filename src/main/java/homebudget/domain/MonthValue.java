package homebudget.domain;

import java.math.BigDecimal;

public class MonthValue {
	private final String month;
	private final BigDecimal value;
	
	public MonthValue(String month, BigDecimal value) {
		this.month = month;
		this.value = value;
	}

	public final String getMonth() {
		return month;
	}

	public final BigDecimal getValue() {
		return value;
	}
	
}
