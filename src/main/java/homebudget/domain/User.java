package homebudget.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Table(name = "users")
@Transactional
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;
	
	@NotNull
	@Size(min=5, max=30, message="{user.username.size}")
	private String username;
	
	@NotNull
	@Email(message="{user.email.valid}")
	private String email;
	
	public User() { }
	
	public User(User user) {
		this(user.getId(), user.getUsername());
	}
	
	public User(String username) {
		this(null, username);
	}
	
	public User(Long id, String username) {
		this.id = id;
		this.username = username;
	}

	public final Long getId() {
		return id;
	}

	public final String getUsername() {
		return username;
	}

	public final String getEmail() {
		return email;
	}

	public final void setId(Long id) {
		this.id = id;
	}

	public final void setUsername(String username) {
		this.username = username;
	}

	public final void setEmail(String email) {
		this.email = email;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	
}
