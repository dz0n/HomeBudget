package homebudget.domain;

/**
 * @author Łukasz Gorgolewski
 *
 *Access levels:
 *NONE (0) - user don't have access to budget
 *USE (1) - user can read budget, add receipts and transfer, but can't add, modify, delete categories, accounts etc
 *MODIFY (2) - user can do everything in budget, except delete whole budget
 *FULL (3) - user can do everything, even delete budget
 *
 */
public enum AccessLevel {
	NONE("NONE"),
	USE("USE"),
	MODIFY("MODIFY"), 
	FULL("FULL");
	
	private String text;
	
	private AccessLevel(String text) {
		this.text = text;
	}
	
	public static final AccessLevel values[] = values();
	
	@Override
	public String toString() {
		return this.text;
	}
}
