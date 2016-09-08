package homebudget.domain;

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

import org.springframework.transaction.annotation.Transactional;

@Entity
@Table(name = "subcategory")
@Transactional
public class Subcategory {
	
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
	@JoinColumn(name = "category")
	private Category category;
	
	@Column(name="created")
	private Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	
	@Transient
	private String hiddenId = null;
	
	public Subcategory() { }
	
	public Subcategory(Subcategory subcategory) {
		this.id = subcategory.getId();
		this.name = subcategory.getName();
		this.description = subcategory.getDescription();
		this.category = new Category(subcategory.getCategory());
		this.timestamp = new Timestamp(subcategory.getTimestamp().getTime());
		this.hiddenId = subcategory.getHiddenId();
	}
	
	public Subcategory(Long id, String name, String description, Category category) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.category = category;
	}

	public final void setHiddenId(String hiddenId) {
		this.hiddenId = hiddenId;
	}
	
	public final String getHiddenId() {
		return hiddenId;
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
	
	public final void setId(Long id) {
		this.id = id;
	}
	
	public final void setName(String name) {
		this.name = name;
	}
	
	public final void setDescription(String description) {
		this.description = description;
	}

	public final Category getCategory() {
		return category;
	}

	public final Timestamp getTimestamp() {
		return timestamp;
	}

	public final void setCategory(Category category) {
		this.category = category;
	}

	public final void setTimestamp(Timestamp timestamp) {
		if(timestamp==null)
			throw new NullPointerException();
		this.timestamp = timestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
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
		Subcategory other = (Subcategory) obj;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
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
		return true;
	}
	
}
