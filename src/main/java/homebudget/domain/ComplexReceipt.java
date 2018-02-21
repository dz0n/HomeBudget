package homebudget.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ComplexReceipt {
	private Long id;
	private String name;
	private String description = "";
	private long value = 0;
	private Date date = new Date();
	private final Map<Account, BigDecimal> accounts;
	private final Map<Subcategory, BigDecimal> subcategories;
	private User user;
	private Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	private String hiddenId = null;
	
	public ComplexReceipt() {
		this.accounts = new HashMap<Account, BigDecimal>();
		this.subcategories = new HashMap<Subcategory, BigDecimal>();
	}
	
	public ComplexReceipt(ComplexReceipt r) {
		this.id = r.getId();
		this.name = r.getName();
		this.description = r.getDescription();
		this.setLongValue(r.getLongValue());
		this.date = new Date(r.getDate().getTime());
		this.accounts = new HashMap<Account, BigDecimal>();
		for(Entry<Account, BigDecimal> entry : r.getAccounts().entrySet()) {
			this.accounts.put(new Account(entry.getKey()), entry.getValue());
		}
		this.subcategories = new HashMap<Subcategory, BigDecimal>();
		for(Entry<Subcategory, BigDecimal> entry : r.getSubcategories().entrySet()) {
			this.subcategories.put(new Subcategory(entry.getKey()), entry.getValue());
		}
		this.user = new User(r.getUser());
		this.timestamp = new Timestamp(r.getTimestamp().getTime());
		this.hiddenId = r.getHiddenId();
	}
	
	public ComplexReceipt(String name, BigDecimal value, Map<Account, BigDecimal> account, Map<Subcategory, BigDecimal> subcategory, User user) {
		this.name = name;
		this.setValue(value);
		this.accounts = account;
		this.subcategories = subcategory;
		this.user = user;
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

	public final Map<Account, BigDecimal> getAccounts() {
		return accounts;
	}

	public final Map<Subcategory, BigDecimal> getSubcategories() {
		return subcategories;
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

	public final void setTimestamp(Timestamp timestamp) {
		if(timestamp==null)
			throw new NullPointerException();
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "ComplexReceipt [id=" + id + ", name=" + name + ", description=" + description + ", value=" + value
				+ ", date=" + date + ", account=" + accounts + ", subcategory=" + subcategories + ", user=" + user
				+ ", timestamp=" + timestamp + ", hiddenId=" + hiddenId + "]";
	}
}
