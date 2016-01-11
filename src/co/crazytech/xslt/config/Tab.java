package co.crazytech.xslt.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Tab")
public class Tab {
	
	public Tab() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Tab(String xmlPath, String xslPath) {
		super();
		this.xmlPath = xmlPath;
		this.xslPath = xslPath;
	}
	
	private String xmlPath,xslPath;
	@XmlElement(name="XmlPath")
	public String getXmlPath() {
		return xmlPath;
	}

	public void setXmlPath(String xmlPath) {
		this.xmlPath = xmlPath;
	}
	@XmlElement(name="XslPath")
	public String getXslPath() {
		return xslPath;
	}

	public void setXslPath(String xslPath) {
		this.xslPath = xslPath;
	}
	
	
}
