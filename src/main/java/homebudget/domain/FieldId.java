package homebudget.domain;

public class FieldId {
	private final String field;
	private final String id;
	
	public FieldId(String field, String id) {
		this.field = field;
		this.id = id;
	}

	public final String getField() {
		return field;
	}

	public final String getId() {
		return id;
	}
}
