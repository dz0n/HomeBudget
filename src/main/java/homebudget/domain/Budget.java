package homebudget.domain;

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

import org.springframework.transaction.annotation.Transactional;

@Entity
@Table(name = "budget")
@Transactional
public class Budget {
	
	public interface Validation {	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id = null;
	
	@NotNull(groups = { Validation.class })
	@Size(min=1, max=45, message="{budget.name.size}", groups = { Validation.class })
	@Column
	private String name;
	
	@Size(min=0, max=100, message="{budget.description.size}", groups = { Validation.class })
	@Column
	private String description = "";
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="user")
	private User user;
	
	@Transient
	private String hiddenId = null;
	
	public Budget() { }
	
	public Budget(Budget budget) {
		this.id = budget.getId();
		this.name = budget.getName();
		this.description = budget.getDescription();
		this.user = new User(budget.getUser());
		this.hiddenId = budget.getHiddenId();
	}
	
	public Budget(Long id, String name, String description, User user) {
		this.id = id;
		this.name = name;
		this.description = description;
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

	public final void setId(Long id) {
		this.id = id;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final String getDescription() {
		return description;
	}

	public final void setDescription(String description) {
		this.description = description;
	}

	public final User getUser() {
		return user;
	}

	public final void setUser(User user) {
		this.user = user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		Budget other = (Budget) obj;
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
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Budget [id=" + id + ", name=" + name + ", description=" + description + ", user=" + user + ", hiddenId="
				+ hiddenId + "]";
	}
	
}
