package homebudget.service;

public interface IdHider {

	public <T> String getHiddenId(Class<T> type, Long id);
	
	public <T> Long getId(Class<T> type, String hiddenId);
	
}
