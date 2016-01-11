package co.crazytech.xslt.config;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="AppConfig")
public class AppConfig {
	private String rstaTheme,locale;
	private List<Tab> tabs;
	
	@XmlElement(name="RstaTheme")
	public String getRstaTheme() {
		return rstaTheme;
	}

	public void setRstaTheme(String rstaTheme) {
		this.rstaTheme = rstaTheme;
	}
	
	@XmlElement(name="Locale")
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	@XmlElementWrapper(name="Tabs")
	@XmlElement(name="Tab")
	public List<Tab> getTabs() {
		return tabs;
	}

	public void setTabs(List<Tab> tabs) {
		this.tabs = tabs;
	}

}
