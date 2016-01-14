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
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
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
	private JMenuItem mntmExit,mntmLangEn, mntmLangZhS;
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
		mainframe.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		mainframe.addWindowListener(new WindowListener() {
			
		@Override
		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosing(WindowEvent e) {
			confirmExit();
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
		mainframe.setJMenuBar(menuBar());
		
		
		btabPane = new BetterTabbedPane(mainframe,config);
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
	
	private JMenuBar menuBar() {
		JMenuBar menuBar = new JMenuBar();
		mnFile = new JMenu(lang.getString("file"));
		mnFile.setMnemonic('f');
		JMenuItem mntm = new JMenuItem(lang.getString("new"), 'n');
		mntm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		mnFile.add(mntm);
		mntm = new JMenuItem(lang.getString("refresh"), 'r');
		mntm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		mnFile.add(mntm);
		mntm = new JMenuItem(lang.getString("open"), 'o');
		mntm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		mnFile.add(mntm);
		mntm = new JMenuItem(lang.getString("save"), 's');
		mntm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		mnFile.add(mntm);
		mntm = new JMenuItem(lang.getString("saveas"), 's');
		mntm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, InputEvent.CTRL_DOWN_MASK));
		mnFile.add(mntm);
		mntmExit = new JMenuItem(lang.getString("exit"));
		mntmExit.setMnemonic('E');
		mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
		mntmExit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				confirmExit();
			}
		});
		mnFile.add(mntmExit);
		menuBar.add(mnFile);
		
		JMenu mnEdit = new JMenu(lang.getString("edit"));
		mnEdit.setMnemonic('e');
		mntm = new JMenuItem(lang.getString("undo"), 'u');
		mntm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
		mnEdit.add(mntm);
		mntm = new JMenuItem(lang.getString("redo"), 'r');
		mntm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
		mnEdit.add(mntm);
		mntm = new JMenuItem(lang.getString("selectall"), 's');
		mntm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
		mnEdit.add(mntm);
		mntm = new JMenuItem(lang.getString("cut"), 'c');
		mntm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
		mnEdit.add(mntm);
		mntm = new JMenuItem(lang.getString("copy"), 'c');
		mntm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
		mnEdit.add(mntm);
		mntm = new JMenuItem(lang.getString("paste"), 'p');
		mntm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
		mnEdit.add(mntm);
		menuBar.add(mnEdit);
		
		JMenu mnSearch = new JMenu(lang.getString("search"));
		mnSearch.setMnemonic('s');
		mntm = new JMenuItem(lang.getString("find")+"...", 'f');
		mntm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
		mnSearch.add(mntm);
		mntm = new JMenuItem(lang.getString("replace")+"...", 'r');
		mntm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK));
		mnSearch.add(mntm);
		mntm = new JMenuItem(lang.getString("goto")+"...", 'g');
		mntm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
		mnSearch.add(mntm);
		menuBar.add(mnSearch);
		
		mnWindow = new JMenu(lang.getString("window"));
		mnWindow.setMnemonic('w');
		mntmLang = new JMenu(lang.getString("language"));
		mntmLang.setMnemonic('l');
		mnWindow.add(mntmLang);
		
		mntmTheme = new JMenu(lang.getString("theme"));
		mntmTheme.setMnemonic('t');
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
		return menuBar;
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
		BetterTabbedPane tabPane = (BetterTabbedPane)mainframe.getContentPane().getComponent(0);
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
		mntmExit.setText(lang.getString("exit"));
		mntmTheme.setText(lang.getString("theme"));
		mntmLang.setText(lang.getString("language"));
		mntmLangEn.setText(lang.getString("lang_en"));
		mntmLangZhS.setText(lang.getString("lang_zh_s"));
		
		BetterTabbedPane tabPane = (BetterTabbedPane)mainframe.getContentPane().getComponent(0);
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
	
	private void confirmExit(){
		switch (optionDialog(mainframe, lang.getString("confirm_exit"), lang.getString("exit"))) {
		case 0:
			List<Tab> tabs = new ArrayList<Tab>();
			BetterTabbedPane tabPane = (BetterTabbedPane)mainframe.getContentPane().getComponent(1);
			tabPane.revalidate();
			for (int i = 0; i < tabPane.getTabbedPane().getTabCount()-1; i++) {
				BrowserPanel browser = (BrowserPanel)tabPane.getTabbedPane().getComponentAt(i);
				tabs.add(new Tab(browser.getXmlText().getCurrFilePath(), browser.getXslText().getCurrFilePath()));
				browser.onExit();
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
			break;

		default:
			break;
		}
	}
	
	public static int optionDialog(Component component, String msg, String title) {
		return JOptionPane.showConfirmDialog(component, msg, title, JOptionPane.OK_CANCEL_OPTION);
	}
	
	private void setUILang(Locale locale) {
		LangMan lang = new LangMan(locale);
		UIManager.put("FileChooser.openDialogTitleText",lang.getString("open"));
	}

	public LangMan getLang() {
		return lang;
	}
	
}
