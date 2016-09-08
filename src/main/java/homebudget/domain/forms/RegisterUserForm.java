package homebudget.domain.forms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Table(name = "users")
@Transactional
public class RegisterUserForm {
	
	public interface Validation { };
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;
	
	@NotNull
	@Size(min=5, max=30, message="{user.username.size}", groups = { Validation.class })
	private String username;
	
	@NotNull
	@Size(min=8, max=30, message="{user.password.size}", groups = { Validation.class })
	@Pattern(regexp="((?=.*[a-z])(?=.*[0-9])(?=.*[A-Z]).{8,30})", message="{user.password.pattern}", groups = { Validation.class })
	private String password;
	
	@Transient
	@Size(min=8, max=30, message="{user.password.size}", groups = { Validation.class })
	@Pattern(regexp="((?=.*[a-z])(?=.*[0-9])(?=.*[A-Z]).{8,30})", message="{user.password.pattern}", groups = { Validation.class })
	private String repeatedPassword;
	
	@NotNull
	@Email(message="{user.email.valid}", groups = { Validation.class })
	private String email;
	
	public RegisterUserForm() { }
	
	public RegisterUserForm(String username, String password, String email) {
		this(username, password, null, email);
	}
	
	public RegisterUserForm(String username, String password, String repeatedPassword, String email) {
		this.username = username;
		this.password = password;
		this.repeatedPassword = repeatedPassword;
		this.email = email;
	}

	public final String getUsername() {
		return username;
	}

	public final String getPassword() {
		return password;
	}

	public final String getRepeatedPassword() {
		return repeatedPassword;
	}

	public final String getEmail() {
		return email;
	}

	public final void setUsername(String username) {
		this.username = username;
	}

	public final void setPassword(String password) {
		this.password = password;
	}

	public final void setRepeatedPassword(String repeatedPassword) {
		this.repeatedPassword = repeatedPassword;
	}

	public final void setEmail(String email) {
		this.email = email;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((repeatedPassword == null) ? 0 : repeatedPassword.hashCode());
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
		RegisterUserForm other = (RegisterUserForm) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (repeatedPassword == null) {
			if (other.repeatedPassword != null)
				return false;
		} else if (!repeatedPassword.equals(other.repeatedPassword))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	
}
