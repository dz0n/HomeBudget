package homebudget.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListIdHider implements IdHider {
	private Map<Class<?>, ArrayList<Long>> classMap = new HashMap<Class<?>, ArrayList<Long>>();

	@Override
	public <T> String getHiddenId(Class<T> type, Long id) {
		if (type == null)
			throw new NullPointerException("Type is null");
		
		ArrayList<Long> ids = classMap.get(type);
		if(ids == null) {
			ids = new ArrayList<Long>();
			classMap.put(type, ids);
		}
		
		int hiddenId = ids.indexOf(id);
		if(hiddenId >= 0) {
			return Long.toString(hiddenId + 1);
		} else {
			ids.add(id);
			return Long.toString(ids.size());
		}
	}

	@Override
	public <T> Long getId(Class<T> type, String hiddenId) {
		if (type == null)
			throw new NullPointerException("Type is null");
		
		ArrayList<Long> ids = classMap.get(type);
		if(ids == null)
			return null;
		
		try {
			return ids.get(Integer.parseInt(hiddenId) - 1);
		} catch(IndexOutOfBoundsException e) {
			return null;
		}
	}
}
