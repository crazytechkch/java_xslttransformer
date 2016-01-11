package co.crazytech.xslt.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.TransformerException;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import res.locale.LangMan;
import res.locale.MyLangMan;

import com.crazytech.io.IOUtil;
import com.crazytech.swing.browser.SimpleSwingBrowser;
import com.crazytech.swing.texteditor.DragDropTextEditor;
import com.crazytech.swing.texteditor.SyntaxEditor;
import com.crazytech.swing.texteditor.TextEditor;
import com.crazytech.xslt.XSLT;

import co.crazytech.swing.bettertabbedpane.BetterTabbedPane;
import co.crazytech.xslt.browser.BrowserPanel;
import co.crazytech.xslt.config.AppConfig;
import co.crazytech.xslt.config.Tab;

import javax.swing.JSplitPane;

import java.awt.FlowLayout;

public class Main {
	private JFrame mainframe;
	private JMenu mnFile,mnWindow;
	private JMenu mntmLang,mntmTheme;
	private JMenuItem mntmLangEn, mntmLangZhS;
	private LangMan lang;
	private MyLangMan myLang;
	private JTabbedPane tabPane;
	private BetterTabbedPane btabPane;
	private AppConfig config;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.mainframe.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}
	

	/**
	 * Create the application.
	 */
	public Main() {
		loadConfig();
		initialize();
	}
	
	private void loadConfig(){
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(AppConfig.class);
			if(new File("config.dat").exists()){
					Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
					String xmlStr = IOUtil.readFile("config.dat");
					StringReader reader = new StringReader(xmlStr);
					config = (AppConfig) unmarshaller.unmarshal(reader);
			} else {
				Marshaller marshaller = jaxbContext.createMarshaller();
				config = new AppConfig();
				config.setLocale("en");;
				config.setRstaTheme("Default");
				marshaller.marshal(config, new File("config.dat"));
			}
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		Locale locale = Locale.getDefault();
		lang = new LangMan(locale);
		myLang = new MyLangMan(locale);
		
		mainframe = new JFrame();
		mainframe.getContentPane().setLayout(new BorderLayout(0, 0));
		mainframe.setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/res/logo.png")));
		mainframe.setSize(1024, 768);
		mainframe.setTitle(myLang.getString("appname"));
		mainframe.setLocationRelativeTo(null);
		mainframe.addWindowListener(new WindowListener() {
			
		@Override
		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosing(WindowEvent e) {
			List<Tab> tabs = new ArrayList<Tab>();
			BetterTabbedPane tabPane = (BetterTabbedPane)mainframe.getContentPane().getComponent(1);
			tabPane.revalidate();
			for (int i = 0; i < tabPane.getTabbedPane().getTabCount()-1; i++) {
				BrowserPanel browser = (BrowserPanel)tabPane.getTabbedPane().getComponentAt(i);
				tabs.add(new Tab(browser.getXmlText().getCurrFilePath(), browser.getXslText().getCurrFilePath()));
			}
			config.setTabs(tabs);
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(AppConfig.class);
				Marshaller marshaller = jaxbContext.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				marshaller.marshal(config, new File("config.dat"));
			} catch (JAXBException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.exit(0);
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowIconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowOpened(WindowEvent e) {
		}

		@Override
		public void windowClosed(WindowEvent e) {
			// TODO Auto-generated method stub
		}
		});
		JMenuBar menuBar = new JMenuBar();
		mainframe.getContentPane().add(menuBar, BorderLayout.NORTH);
		
		mnFile = new JMenu(lang.getString("file"));
		menuBar.add(mnFile);
		
		mnWindow = new JMenu(lang.getString("window"));
		mntmLang = new JMenu(lang.getString("language"));
		mntmLang.setMnemonic('l');
		mnWindow.add(mntmLang);
		
		mntmTheme = new JMenu(lang.getString("theme"));
		Map<String,String> themeMap = SyntaxEditor.getThemeMap();
		for (String key : themeMap.keySet()) {
			JMenuItem themeMnItem = new JMenuItem(key);
			themeMnItem.addActionListener(mntmThemeItemListener(key));
			mntmTheme.add(themeMnItem);
		}
		mnWindow.add(mntmTheme);
		menuBar.add(mnWindow);
		
		
		mntmLangEn = new JMenuItem(lang.getString("lang_en"));
		mntmLangEn.addActionListener(mntmChangeLocaleListener("en"));
		mntmLang.add(mntmLangEn);
		
		mntmLangZhS = new JMenuItem(lang.getString("lang_zh_s"));
		mntmLangZhS.addActionListener(mntmChangeLocaleListener("zh"));
		mntmLang.add(mntmLangZhS);
		
		btabPane = new BetterTabbedPane(config);
		/* deprecated
		tabPane = new JTabbedPane(JTabbedPane.TOP);
		tabPane.addTab("", new BrowserPanel());
		tabPane.addTab("", new BrowserPanel());
		tabPane.addTab("", new BrowserPanel());
		*/
		mainframe.getContentPane().add(btabPane, BorderLayout.CENTER);
		setTheme(config.getRstaTheme());
		changeLocale(config.getLocale());
	}
	
	private ActionListener mntmChangeLocaleListener(final String locale) {
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				changeLocale(locale);
			}
		};
		
	}
	
	private ActionListener mntmThemeItemListener(String themeName) {
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setTheme(themeName);
			}
		};
	}
	
	private void setTheme(String themeName) {
		BetterTabbedPane tabPane = (BetterTabbedPane)mainframe.getContentPane().getComponent(1);
		for (int i = 0; i < tabPane.getTabbedPane().getTabCount()-1; i++) {
			BrowserPanel browser = (BrowserPanel)tabPane.getTabbedPane().getComponentAt(i);
			browser.setTheme(themeName);
		}
		tabPane.revalidate();
		try {
			JAXBContext jaxbCtx = JAXBContext.newInstance(AppConfig.class);
			Marshaller marshaller = jaxbCtx.createMarshaller();
			config.setRstaTheme(themeName);
			marshaller.marshal(config, new File("config.dat"));
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void changeLocale(String locale) {
		Locale loc = new Locale(locale);
		Locale.setDefault(loc);
		lang = new LangMan(loc);
		
		myLang = new MyLangMan(loc);
		mainframe.setTitle(myLang.getString("appname"));
		
		mnFile.setText(lang.getString("file"));
		mnWindow.setText(lang.getString("window"));
		mntmTheme.setText(lang.getString("theme"));
		mntmLang.setText(lang.getString("language"));
		mntmLangEn.setText(lang.getString("lang_en"));
		mntmLangZhS.setText(lang.getString("lang_zh_s"));
		
		BetterTabbedPane tabPane = (BetterTabbedPane)mainframe.getContentPane().getComponent(1);
		for (int i = 0; i < tabPane.getTabbedPane().getTabCount()-1; i++) {
			BrowserPanel browser = (BrowserPanel)tabPane.getTabbedPane().getComponentAt(i);
			browser.changeLocale(locale);
		}
		tabPane.revalidate();
		setUILang(loc);
		try {
			JAXBContext jaxbCtx = JAXBContext.newInstance(AppConfig.class);
			Marshaller marshaller = jaxbCtx.createMarshaller();
			config.setLocale(locale);
			marshaller.marshal(config, new File("config.dat"));
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void setUILang(Locale locale) {
		LangMan lang = new LangMan(locale);
		UIManager.put("FileChooser.openDialogTitleText",lang.getString("open"));
	}

	public LangMan getLang() {
		return lang;
	}
	
}
