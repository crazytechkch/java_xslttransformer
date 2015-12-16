package res.locale;

import java.util.Locale;
import java.util.ResourceBundle;

public class MyLangMan {
	private Locale locale;
	private ResourceBundle resBundle;
	
	
	public MyLangMan(Locale locale) {
		super();
		this.locale = locale;
		resBundle = ResourceBundle.getBundle("res/locale/myvalue", locale);
	}

	public String getString(String key) {
		return resBundle.getString(key);
	}
}
