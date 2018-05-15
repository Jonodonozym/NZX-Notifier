
package jdz.NZXN.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class ConfigChangeEvent<E> {
	private final ConfigProperty<E> property;
	private final E oldValue;
	private final E newValue;
}
