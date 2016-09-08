package homebudget.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Table(name = "account")
@Transactional
public class Account {
	
	public interface Validation { };
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@NotNull(groups = { Validation.class })
	@Size(min=1, max=45, message="{budget.name.size}", groups = { Validation.class })
	@Column(name = "name")
	private String name;
	
	@Size(min=0, max=100, message="{budget.description.size}", groups = { Validation.class })
	@Column(name = "description")
	private String description = "";
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "budget")
	@Fetch(FetchMode.SELECT)
	private Budget budget;
	
	@NotNull(message="{category.value.isnull}", groups = { Validation.class })
	@NumberFormat(style=Style.NUMBER)
	@Column(name = "value")
	private long value = 0;
	
	@Column(name="created")
	private Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	
	@Transient
	private String hiddenId = null;
	
	public Account() { }
	
	public Account(Account account) {
		this.setId(account.getId());
		this.setName(account.getName());
		this.setDescription(account.getDescription());
		this.setBudget(new Budget(account.getBudget()));
		this.setLongValue(account.getLongValue());
		this.setTimestamp(new Timestamp(account.getTimestamp().getTime()));
		this.setHiddenId(account.getHiddenId());
	}
	
	public Account(Long id, String name, String description, Budget budget, long value) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.budget = budget;
		this.value = value;
	}
	
	public final String getHiddenId() {
		return hiddenId;
	}
	
	public final void setHiddenId(String hiddenId) {
		this.hiddenId = hiddenId;
	}
	
	public final Long getId() {
		return id;
	}
	
	public final String getName() {
		return name;
	}
	
	public final String getDescription() {
		return description;
	}
	
	public final Budget getBudget() {
		return budget;
	}
	
	public final BigDecimal getValue() {
		return BigDecimal.valueOf(value).divide(BigDecimal.valueOf(100));
	}
	
	public final void setId(Long id) {
		this.id = id;
	}
	
	public final void setName(String name) {
		this.name = name;
	}
	
	public final void setDescription(String description) {
		this.description = description;
	}
	
	public final void setBudget(Budget budget) {
		this.budget = budget;
	}
	
	public final void setValue(BigDecimal value) {
		this.value = value.multiply(BigDecimal.valueOf(100)).longValue();
	}

	public final Timestamp getTimestamp() {
		return timestamp;
	}

	public final void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
	public final long getLongValue() {
		return value;
	}
	
	public final void setLongValue(long value) {
		this.value = value;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((budget == null) ? 0 : budget.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		result = prime * result + (int) (value ^ (value >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		if (budget == null) {
			if (other.budget != null)
				return false;
		} else if (!budget.equals(other.budget))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		if (value != other.value)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Account [id=" + id + ", name=" + name + ", description=" + description + ", budget=" + budget
				+ ", value=" + value + ", timestamp=" + timestamp + ", hiddenId=" + hiddenId + "]";
	}
	
}
