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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Table(name = "account_transfer")
@Transactional
public class AccountTransfer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Size(min=0, max=45)
	@Column(name = "name")
	private String name = "";
	
	@Size(min=0, max=100)
	@Column(name = "description")
	private String description = "";
	
	@ManyToOne(fetch = FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@JoinColumn(name = "from_account")
	private Account fromAccount;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@JoinColumn(name = "to_account")
	private Account toAccount;
	
	@NotNull
	@NumberFormat(style=Style.NUMBER)
	@Column(name = "value")
	private long value = 0;
	
	@DateTimeFormat(pattern="YYYY-MM-dd")
	private Date date = new Date();
	
	@ManyToOne(fetch = FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@JoinColumn(name="user")
	private User user;
	
	@Column(name="created")
	private Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	
	@Transient
	private String hiddenId = null;

	public AccountTransfer() { }
	
	public AccountTransfer(AccountTransfer accountTransfer) {
		this.id = accountTransfer.getId();
		this.name = accountTransfer.getName();
		this.description = accountTransfer.getDescription();
		this.setLongValue(accountTransfer.getLongValue());
		this.date = new Date(accountTransfer.getDate().getTime());
		this.fromAccount = new Account(accountTransfer.getFromAccount());
		this.toAccount = new Account(accountTransfer.getToAccount());
		this.user = new User(accountTransfer.getUser());
		this.timestamp = new Timestamp(accountTransfer.getTimestamp().getTime());
		this.hiddenId = accountTransfer.getHiddenId();
	}

	public AccountTransfer(String name, Account fromAccount, Account toAccount, BigDecimal value) {
		this.name = name;
		this.fromAccount = fromAccount;
		this.toAccount = toAccount;
		this.setValue(value);
	}
	
	public final Date getDate() {
		return date;
	}

	public final String getDescription() {
		return description;
	}

	public final Account getFromAccount() {
		return fromAccount;
	}

	public final String getHiddenId() {
		return hiddenId;
	}

	public final Long getId() {
		return id;
	}

	public final long getLongValue() {
		return this.value;
	}

	public final String getName() {
		return name;
	}

	public final Timestamp getTimestamp() {
		return timestamp;
	}

	public final Account getToAccount() {
		return toAccount;
	}

	public final User getUser() {
		return user;
	}

	public final BigDecimal getValue() {
		return BigDecimal.valueOf(value).divide(BigDecimal.valueOf(100));
	}

	public final void setDate(Date date) {
		if(date==null)
			throw new NullPointerException();
		this.date = date;
	}

	public final void setDescription(String description) {
		this.description = description;
	}

	public final void setFromAccount(Account fromAccount) {
		this.fromAccount = fromAccount;
	}

	public final void setHiddenId(String hiddenId) {
		this.hiddenId = hiddenId;
	}

	public final void setId(Long id) {
		this.id = id;
	}

	public final void setLongValue(long value) {
		this.value = value;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final void setTimestamp(Timestamp timestamp) {
		if(timestamp==null)
			throw new NullPointerException();
		this.timestamp = timestamp;
	}

	public final void setToAccount(Account toAccount) {
		this.toAccount = toAccount;
	}

	public final void setUser(User user) {
		this.user = user;
	}
	
	public final void setValue(BigDecimal value) {
		this.value = value.multiply(BigDecimal.valueOf(100)).longValue();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((fromAccount == null) ? 0 : fromAccount.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		result = prime * result + ((toAccount == null) ? 0 : toAccount.hashCode());
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
		AccountTransfer other = (AccountTransfer) obj;
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
		if (fromAccount == null) {
			if (other.fromAccount != null)
				return false;
		} else if (!fromAccount.equals(other.fromAccount))
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
		if (toAccount == null) {
			if (other.toAccount != null)
				return false;
		} else if (!toAccount.equals(other.toAccount))
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
