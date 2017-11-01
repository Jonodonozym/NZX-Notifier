
package jdz.NZXN.config;

public class ConfigChangeEvent {
	private final String property;
	private final String oldValue;
	private final String newValue;
	public ConfigChangeEvent(String property, String oldValue, String newValue){
		this.property = property;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	public String getProperty(){
		return property;
	}
	public String getOldValue(){
		return oldValue;
	}
	public String getNewValue(){
		return newValue;
	}
}
