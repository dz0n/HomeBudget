package homebudget.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

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

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Łukasz Gorgolewski
 *
 */
@Entity
@Table(name = "receipt")
@Transactional
public class Receipt {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@Size(min=1, max=45, message="{budget.name.size}")
	private String name;
	
	@Size(min=0, max=100, message="{budget.description.size}")
	private String description = "";
	
	@NotNull(message="{category.value.isnull}")
	@NumberFormat(style=Style.NUMBER)
	private long value = 0;
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date date = new Date();
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "account")
	private Account account;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "subcategory")
	private Subcategory subcategory;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="user")
	private User user;
	
	@Column(name="created")
	private Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	
	@Transient
	private String hiddenId = null;
	
	public Receipt() { }
	
	public Receipt(Receipt r) {
		this.id = r.getId();
		this.name = r.getName();
		this.description = r.getDescription();
		this.setLongValue(r.getLongValue());
		this.date = new Date(r.getDate().getTime());
		this.account = new Account(r.getAccount());
		this.subcategory = new Subcategory(r.getSubcategory());
		this.user = new User(r.getUser());
		this.timestamp = new Timestamp(r.getTimestamp().getTime());
		this.hiddenId = r.getHiddenId();
	}
	
	public Receipt(String name, BigDecimal value, Account account, Subcategory subcategory) {
		this.name = name;
		this.setValue(value);
		this.account = account;
		this.subcategory = subcategory;
		
		this.user = account.getBudget().getUser();
	}

	public final User getUser() {
		return user;
	}

	public final void setUser(User user) {
		this.user = user;
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

	public final BigDecimal getValue() {
		return BigDecimal.valueOf(value).divide(BigDecimal.valueOf(100));
	}
	
	public final void setValue(BigDecimal value) {
		this.value = value.multiply(BigDecimal.valueOf(100)).longValue();
	}
	
	public final long getLongValue() {
		return value;
	}
	
	public final void setLongValue(long value) {
		this.value = value;
	}
	
	public final Date getDate() {
		return date;
	}

	public final Account getAccount() {
		return account;
	}

	public final Subcategory getSubcategory() {
		return subcategory;
	}

	public final Timestamp getTimestamp() {
		return timestamp;
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
	
	public final void setDate(Date date) {
		if(date==null)
			throw new NullPointerException();
		this.date = date;
	}

	public final void setAccount(Account account) {
		this.account = account;
	}

	public final void setSubcategory(Subcategory subcategory) {
		this.subcategory = subcategory;
	}

	public final void setTimestamp(Timestamp timestamp) {
		if(timestamp==null)
			throw new NullPointerException();
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "Receipt [id=" + id + ", name=" + name + ", description=" + description + ", value=" + value + ", date="
				+ date + ", account=" + account + ", subcategory=" + subcategory + ", user=" + user + ", timestamp="
				+ timestamp + ", hiddenId=" + hiddenId + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((account == null) ? 0 : account.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((hiddenId == null) ? 0 : hiddenId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((subcategory == null) ? 0 : subcategory.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		Receipt other = (Receipt) obj;
		if (account == null) {
			if (other.account != null)
				return false;
		} else if (!account.equals(other.account))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (hiddenId == null) {
			if (other.hiddenId != null)
				return false;
		} else if (!hiddenId.equals(other.hiddenId))
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
		if (subcategory == null) {
			if (other.subcategory != null)
				return false;
		} else if (!subcategory.equals(other.subcategory))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (value != other.value)
			return false;
		return true;
	}
	
}
