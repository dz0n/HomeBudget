package homebudget.domain.forms;

import javax.validation.constraints.Size;

import homebudget.domain.AccessLevel;

public class AccessForm {
	@Size(min=5, max=30, message="{user.username.size}")
	private String username;
	
	private AccessLevel accessLevel = AccessLevel.USE;
	
	public final String getUsername() {
		return username;
	}
	public final AccessLevel getAccessLevel() {
		return accessLevel;
	}
	public final void setUsername(String username) {
		this.username = username;
	}
	public final void setAccessLevel(AccessLevel accessLevel) {
		this.accessLevel = accessLevel;
	}
}
