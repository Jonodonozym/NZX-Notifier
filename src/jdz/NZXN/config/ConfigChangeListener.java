package jdz.NZXN.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.NONE)
public class ConfigChangeListener {
	private static final Map<ConfigProperty<?>, Object> listeners = new HashMap<ConfigProperty<?>, Object>();

	public static <T> void register(ConfigProperty<T> property, Listener<T> l) {
		getListeners(property).add(l);
	}

	public static <T> void register(ConfigProperty<T> property, NewValueListener<T> l) {
		getListeners(property).add((oldValue, newValue) -> {
			l.onConfigChange(newValue);
		});
	}

	@SuppressWarnings("unchecked")
	static <T> List<Listener<T>> getListeners(ConfigProperty<T> property) {
		if (!listeners.containsKey(property))
			listeners.put(property, new ArrayList<Listener<T>>());
		return (List<Listener<T>>) listeners.get(property);
	}

	public static interface Listener<E> {
		public void onConfigChange(E oldValue, E newValue);
	}

	public static interface NewValueListener<E> {
		public void onConfigChange(E newValue);
	}
}
