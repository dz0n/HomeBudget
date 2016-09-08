package homebudget.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION,
		proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MapIdHider implements IdHider {
	private Map<Class<?>, Map<Long, String>> classMap = new HashMap<Class<?>, Map<Long, String>>();

	@Override
	public <T> String getHiddenId(Class<T> type, Long id) {
		if (type == null)
			throw new NullPointerException("Type is null");
		
		Map<Long, String> ids = classMap.get(type);
		if(ids == null) {
			ids = new HashMap<Long, String>();
			classMap.put(type, ids);
		}
		
		if(ids.containsKey(id)) {
			return ids.get(id);
		} else {		
			ids.put(id, Integer.toString(ids.size() + 1));
			return Integer.toString(ids.size());
		}
	}

	@Override
	public <T> Long getId(Class<T> type, String hiddenId) {
		if (type == null)
			throw new NullPointerException("Type is null");
		
		Map<Long, String> ids = classMap.get(type);
		if(ids == null)
			return null;
		
		for(Entry<Long, String> entry : ids.entrySet()) {
			if(entry.getValue().equals(hiddenId)) {
				return entry.getKey();
			}
		}
		
		return null;
	}
}
