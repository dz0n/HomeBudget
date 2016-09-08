package homebudget.domain.forms;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

public class ReceiptForm {
	
	public enum Type { 
		EXPENSE,
		INCOME;
	}

	private Type type = Type.EXPENSE;

	@NotNull(message="{default.name.size}")
	@Size(min=1, max=45, message="{budget.name.size}")
	private String name;
	
	@Size(min=0, max=100, message="{budget.description.size}")
	private String description = "";
	
	@NotNull(message="{category.value.isnull}")
	@NumberFormat(style=Style.NUMBER, pattern="#,###.##")
	private BigDecimal value = BigDecimal.valueOf(0);
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date date = new Date();
	
	@NotNull(message="{default.accountid.isnull}")
	@NumberFormat(style=Style.NUMBER)
	private String accountId;
	
	@NumberFormat(style=Style.NUMBER)
	private String categoryId;
	
	@NotNull(message="{default.subcategoryid.isnull}")
	@NumberFormat(style=Style.NUMBER)
	private String subcategoryId;
	
	public final String getAccountId() {
		return accountId;
	}
	
	public final String getCategoryId() {
		return categoryId;
	}

	public final Date getDate() {
		return date;
	}

	public final String getDescription() {
		return description;
	}

	public final Type getType() {
		return type;
	}

	public final String getName() {
		return name;
	}

	public final String getSubcategoryId() {
		return subcategoryId;
	}
	
	public final BigDecimal getValue() {
		return value;
	}
	
	public final void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public final void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public final void setDate(Date date) {
		this.date = date;
	}

	public final void setDescription(String description) {
		this.description = description;
	}

	public final void setType(Type isExpense) {
		this.type = isExpense;
	}

	public final void setName(String name) {
		this.name = name;
	}
	
	public final void setSubcategoryId(String subcategoryId) {
		this.subcategoryId = subcategoryId;
	}
	
	public final void setValue(BigDecimal value) {
		this.value = value;
	}
	
}
