package homebudget.domain.forms;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

public class CategoryTransferForm {
	
	@Size(min=0, max=45, message="{budget.name.size}")
	private String name = "";
	
	@Size(min=0, max=100, message="{budget.description.size}")
	private String description = "";
	
	@NotNull(message="{category.value.isnull}")
	@NumberFormat(style=Style.NUMBER, pattern="#,###.##")
	private BigDecimal value = BigDecimal.valueOf(0);
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date date = new Date();
	
	@NotNull(message="{default.categoryid.isnull}")
	@NumberFormat(style=Style.NUMBER)
	private String fromCategoryId;
	
	@NumberFormat(style=Style.NUMBER)
	private String toCategoryId;

	public final String getName() {
		return name;
	}

	public final String getDescription() {
		return description;
	}

	public final BigDecimal getValue() {
		return value;
	}

	public final Date getDate() {
		return date;
	}

	public final String getFromCategoryId() {
		return fromCategoryId;
	}

	public final String getToCategoryId() {
		return toCategoryId;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final void setDescription(String description) {
		this.description = description;
	}

	public final void setValue(BigDecimal value) {
		this.value = value;
	}

	public final void setDate(Date date) {
		this.date = date;
	}

	public final void setFromCategoryId(String fromCategoryId) {
		this.fromCategoryId = fromCategoryId;
	}

	public final void setToCategoryId(String toCategoryId) {
		this.toCategoryId = toCategoryId;
	}
	
}
