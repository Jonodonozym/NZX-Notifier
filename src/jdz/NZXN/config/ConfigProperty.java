
package jdz.NZXN.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFileChooser;

import jdz.NZXN.utils.StringUtils;
import lombok.Getter;
import lombok.NonNull;

public abstract class ConfigProperty<E> {
	private static final Set<ConfigProperty<?>> all = new HashSet<ConfigProperty<?>>();

	public static Set<ConfigProperty<?>> getAll() {
		return Collections.unmodifiableSet(all);
	}

	public static final ConfigProperty<Long> LAST_CHECK = new LongProperty("Last Check", 0L) {
		@Override
		public Long getDefaultValue() {
			return System.currentTimeMillis();
		}
	};

	public static final ConfigProperty<Integer> CHECK_INTERVAL_MINUTES = new IntegerProperty("Check Interval Minutes",
			10);
	public static final ConfigProperty<Boolean> ANNOUNCEMENT_ALERTS_ENABLED = new BooleanProperty(
			"Announcement Alerts Enabled", true);
	public static final ConfigProperty<Boolean> ANNOUNCEMENT_SAVING_ENABLED = new BooleanProperty(
			"Announcement Saving Enabled", false);
	public static final ConfigProperty<Boolean> GROUP_BY_YEAR = new BooleanProperty(
			"Group By Year", false);
	public static final ConfigProperty<Boolean> GROUP_BY_MONTH = new BooleanProperty(
			"Group By Month", true);
	public static final ConfigProperty<File> ANNOUNCEMENT_SAVING_FOLDER = new FileProperty("Announcement Saving Folder",
			new File(new JFileChooser().getFileSystemView().getDefaultDirectory(), "NZX Announcements"));
	public static final ConfigProperty<List<String>> SECURITY_WHITELIST = new StringListProperty("Security Whitelist",
			new ArrayList<>());
	public static final ConfigProperty<List<String>> SECURITY_BLACKLIST = new StringListProperty("Security Blacklist",
			new ArrayList<>());
	public static final ConfigProperty<List<String>> TYPE_WHITELIST = new StringListProperty("Type Whitelist",
			new ArrayList<>());
	public static final ConfigProperty<List<String>> TYPE_BLACKLIST = new StringListProperty("Type Blacklist",
			new ArrayList<>());
	public static final ConfigProperty<List<String>> DESCRIPTION_WHITELIST = new StringListProperty(
			"Description Whitelist", new ArrayList<>());
	public static final ConfigProperty<List<String>> DESCRIPTION_BLACKLIST = new StringListProperty(
			"Description Blacklist", new ArrayList<>());
	public static final ConfigProperty<Boolean> PRICE_ALERTS_ENABLED = new BooleanProperty("Price Alerts Enabled",
			true);
	public static final ConfigProperty<List<String>> PRICE_ALERTS = new StringListProperty("Price Alerts",
			new ArrayList<>());
	public static final ConfigProperty<Boolean> IS_MUTED = new BooleanProperty("Is Muted", false);

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

	public static class IntegerProperty extends ConfigProperty<Integer> {
		public IntegerProperty(String name, Integer defaultValue) {
			super(name, defaultValue);
		}

		@Override
		public Integer parse(String s) {
			return Integer.parseInt(s);
		}
	}

	public static class DoubleProperty extends ConfigProperty<Double> {
		public DoubleProperty(String name, Double defaultValue) {
			super(name, defaultValue);
		}

		@Override
		public Double parse(String s) {
			return Double.parseDouble(s);
		}
	}

	public static class LongProperty extends ConfigProperty<Long> {
		public LongProperty(String name, Long defaultValue) {
			super(name, defaultValue);
		}

		@Override
		public Long parse(String s) {
			return Long.parseLong(s);
		}
	}

	public static class BooleanProperty extends ConfigProperty<Boolean> {
		public BooleanProperty(String name, Boolean defaultValue) {
			super(name, defaultValue);
		}

		@Override
		public Boolean parse(String s) {
			return Boolean.parseBoolean(s);
		}
	}

	public static class StringListProperty extends ConfigProperty<List<String>> {
		public StringListProperty(String name, List<String> defaultValue) {
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

	public static class FileProperty extends ConfigProperty<File> {
		public FileProperty(String name, File defaultValue) {
			super(name, defaultValue);
		}

		@Override
		public File parse(String s) {
			return new File(s);
		}

		@Override
		public String toString(File file) {
			return file.getAbsolutePath();
		}
	}
}
