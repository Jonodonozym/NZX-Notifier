
package jdz.NZXN.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jdz.NZXN.utils.StringUtils;
import lombok.Getter;
import lombok.NonNull;

public abstract class ConfigProperty<E> {
	private static final Set<ConfigProperty<?>> all = new HashSet<ConfigProperty<?>>();

	public static Set<ConfigProperty<?>> getAll() {
		return Collections.unmodifiableSet(all);
	}

	public static final ConfigProperty<java.lang.Long> LAST_CHECK = new Long("Last Check", 0L) {
		@Override
		public java.lang.Long getDefaultValue() {
			return System.currentTimeMillis();
		}
	};

	public static final ConfigProperty<java.lang.Integer> CHECK_INTERVAL_MINUTES = new Integer("Check Interval Minutes", 10);
	public static final ConfigProperty<java.lang.Boolean> ANNOUNCEMENT_ALERTS_ENABLED = new Boolean("Announcement Alerts Enabled", true);
	public static final ConfigProperty<java.lang.Boolean> ANNOUNCEMENT_SAVING_ENABLED = new Boolean("Announcement Saving Enabled", true);
	public static final ConfigProperty<List<String>> SECURITY_WHITELIST = new StringList("Security Whitelist", new ArrayList<>());
	public static final ConfigProperty<List<String>> SECURITY_BLACKLIST = new StringList("Security Blacklist", new ArrayList<>());
	public static final ConfigProperty<List<String>> TYPE_WHITELIST = new StringList("Type Whitelist", new ArrayList<>());
	public static final ConfigProperty<List<String>> TYPE_BLACKLIST = new StringList("Type Blacklist", new ArrayList<>());
	public static final ConfigProperty<List<String>> DESCRIPTION_WHITELIST = new StringList("Description Whitelist", new ArrayList<>());
	public static final ConfigProperty<List<String>> DESCRIPTION_BLACKLIST = new StringList("Description Blacklist", new ArrayList<>());
	public static final ConfigProperty<java.lang.Boolean> PRICE_ALERTS_ENABLED = new Boolean("Price Alerts Enabled", true);
	public static final ConfigProperty<List<String>> PRICE_ALERTS = new StringList("Price Alerts", new ArrayList<>());
	public static final ConfigProperty<java.lang.Boolean> IS_MUTED = new Boolean("Is Muted", false);

	@Getter @NonNull private final String name;
	@Getter @NonNull private final E defaultValue;

	public ConfigProperty(String name, E defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
		all.add(this);
	}

	public String toString(E value) {
		return value.toString();
	}
	
	public E get() {
		return FileConfiguration.get(this);
	}
	
	public void set(E value) {
		FileConfiguration.set(this, value);
	}
	
	public abstract E parse(String s);
	
	public static class Integer extends ConfigProperty<java.lang.Integer> {
		public Integer(String name, java.lang.Integer defaultValue) {
			super(name, defaultValue);
		}

		@Override
		public java.lang.Integer parse(String s) {
			return java.lang.Integer.parseInt(s);
		}
	}
	
	public static class Double extends ConfigProperty<java.lang.Double> {
		public Double(String name, java.lang.Double defaultValue) {
			super(name, defaultValue);
		}

		@Override
		public java.lang.Double parse(String s) {
			return java.lang.Double.parseDouble(s);
		}
	}
	
	public static class Long extends ConfigProperty<java.lang.Long> {
		public Long(String name, java.lang.Long defaultValue) {
			super(name, defaultValue);
		}

		@Override
		public java.lang.Long parse(String s) {
			return java.lang.Long.parseLong(s);
		}
	}
	
	public static class Boolean extends ConfigProperty<java.lang.Boolean> {
		public Boolean(String name, java.lang.Boolean defaultValue) {
			super(name, defaultValue);
		}

		@Override
		public java.lang.Boolean parse(String s) {
			return java.lang.Boolean.parseBoolean(s);
		}
	}
	
	public static class StringList extends ConfigProperty<List<String>>{
		public StringList(String name, List<String> defaultValue) {
			super(name, defaultValue);
		}
		
		@Override
		public String toString(List<String> list) {
			return StringUtils.mergeList(list, ",");
		}

		@Override
		public List<String> parse(String s) {
			return StringUtils.parseList(s, ",");
		}	
	}
}
