package co.crazytech.xslt.ui;

import java.awt.BorderLayout;
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
import java.io.IOException;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.xml.transform.TransformerException;

import res.locale.LangMan;
import res.locale.MyLangMan;

import com.crazytech.io.IOUtil;
import com.crazytech.swing.browser.SimpleSwingBrowser;
import com.crazytech.swing.texteditor.DragDropTextEditor;
import com.crazytech.swing.texteditor.TextEditor;
import com.crazytech.xslt.XSLT;

import co.crazytech.xslt.browser.BrowserPanel;

import javax.swing.JSplitPane;

import java.awt.FlowLayout;

public class Main {
	private JFrame mainframe;
	private JMenu mnLang;
	private JMenuItem mntmLangEn, mntmLangZhS;
	private LangMan lang;
	private MyLangMan myLang;
	private JTabbedPane tabPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.mainframe.setVisible(true);
					window.mainframe.setTitle("XSLT Transformer");
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
		initialize();
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
		mainframe.setLocationRelativeTo(null);
		
		JMenuBar menuBar = new JMenuBar();
		mainframe.getContentPane().add(menuBar, BorderLayout.NORTH);
		
		mnLang = new JMenu(lang.getString("language"));
		mnLang.setMnemonic('l');
		menuBar.add(mnLang);
		
		mntmLangEn = new JMenuItem(lang.getString("lang_en"));
		mntmLangEn.addActionListener(mntmChangeLocaleListener("en"));
		mnLang.add(mntmLangEn);
		
		mntmLangZhS = new JMenuItem(lang.getString("lang_zh_s"));
		mntmLangZhS.addActionListener(mntmChangeLocaleListener("zh"));
		mnLang.add(mntmLangZhS);
		
		tabPane = new JTabbedPane(JTabbedPane.TOP);
		tabPane.addTab("", new BrowserPanel());
		tabPane.addTab("", new BrowserPanel());
		tabPane.addTab("", new BrowserPanel());
		
		mainframe.getContentPane().add(tabPane, BorderLayout.CENTER);
	}
	
	private ActionListener mntmChangeLocaleListener(final String locale) {
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				changeLocale(locale);
			}
		};
		
	}
	
	private void changeLocale(String locale) {
		Locale loc = new Locale(locale);
		Locale.setDefault(loc);
		lang = new LangMan(loc);
		
		myLang = new MyLangMan(loc);
		mainframe.setTitle(myLang.getString("appname"));
		
		mnLang.setText(lang.getString("language"));
		mntmLangEn.setText(lang.getString("lang_en"));
		mntmLangZhS.setText(lang.getString("lang_zh_s"));
		
		
		setUILang(loc);
	}
	
	private void setUILang(Locale locale) {
		LangMan lang = new LangMan(locale);
		UIManager.put("FileChooser.openDialogTitleText",lang.getString("open"));
	}
	
}
