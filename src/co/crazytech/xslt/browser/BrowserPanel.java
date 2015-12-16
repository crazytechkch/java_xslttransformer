package co.crazytech.xslt.browser;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.xml.transform.TransformerException;

import com.crazytech.io.IOUtil;
import com.crazytech.swing.browser.SimpleSwingBrowser;
import com.crazytech.swing.texteditor.DragDropTextEditor;
import com.crazytech.swing.texteditor.TextEditor;
import com.crazytech.xslt.XSLT;

import res.locale.LangMan;
import res.locale.MyLangMan;

public class BrowserPanel extends JPanel {
	private SimpleSwingBrowser browser;
	private TextEditor outputText;
	private JTabbedPane tabPane;
	private JButton btnTransform;
	private JSplitPane splitPane;
	private JPanel rightHost, _panel_1;
	private DragDropTextEditor xmlText,xslText;
	private Locale locale;
	private LangMan lang;
	private MyLangMan myLang;
	
	
	public BrowserPanel() {
		super();
		locale = Locale.getDefault();
		lang = new LangMan(locale);
		init();
	}
	
	private void init() {
		browser = new SimpleSwingBrowser(locale);
		
		outputText = new TextEditor(lang.getString("sourcecode"),locale);
		
		xmlText = new DragDropTextEditor("Drop XML Here",locale);
		GridBagConstraints gbc_xmlScrollPane = new GridBagConstraints();
		gbc_xmlScrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_xmlScrollPane.fill = GridBagConstraints.BOTH;
		gbc_xmlScrollPane.gridx = 0;
		gbc_xmlScrollPane.gridy = 0;
		
		xslText = new DragDropTextEditor("Drop XSL Here",locale);
		GridBagConstraints gbc_xslScrollPane = new GridBagConstraints();
		gbc_xslScrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_xslScrollPane.fill = GridBagConstraints.BOTH;
		gbc_xslScrollPane.gridx = 0;
		gbc_xslScrollPane.gridy = 1;
		
		
		splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.8);
		
		rightHost = new JPanel();
		splitPane.setRightComponent(rightHost);
		rightHost.setLayout(new BorderLayout(0, 0));
		
		JPanel rightPane = new JPanel();
		rightHost.add(rightPane);
//	rightPane.setPreferredSize(new Dimension(300, 700));
		GridBagLayout gbl_rightPane = new GridBagLayout();
		gbl_rightPane.columnWidths = new int[]{0, 0};
		gbl_rightPane.rowHeights = new int[]{0, 0, 0, 0};
		gbl_rightPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_rightPane.rowWeights = new double[]{1.0, 1.0, 0.0, Double.MIN_VALUE};
		rightPane.setLayout(gbl_rightPane);
		rightPane.add(xmlText, gbc_xmlScrollPane);
		rightPane.add(xslText, gbc_xslScrollPane);
		
		btnTransform = new JButton("TRANSFORM");
		btnTransform.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent ev) {
				Runnable runnable = new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String strXml = xmlText.getText();
						String strXsl = xslText.getText();
						try {
							String strOutput = XSLT.transform(strXsl, strXml, null, IOUtil.DEFAULT_CHARSET);
							browser.loadContent(strOutput);
							outputText.setText(strOutput);
						} catch (TransformerException | IOException e) {
							// TODO Auto-generated catch block
							String errorMsg = myLang.getString("error")+"\n"+e.getLocalizedMessage();
							outputText.setText(errorMsg);
							browser.loadContent(errorMsg);
						} finally {
							tabPane.setSelectedIndex(0);
							
						}
						
					}
				};
				runnable.run();
			}
		});
		GridBagConstraints gbc_btnTransform = new GridBagConstraints();
		gbc_btnTransform.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnTransform.gridx = 0;
		gbc_btnTransform.gridy = 2;
		rightPane.add(btnTransform, gbc_btnTransform);
		
		_panel_1 = new JPanel();
		splitPane.setLeftComponent(_panel_1);
		_panel_1.setLayout(new BorderLayout(0, 0));
		
		tabPane = new JTabbedPane(JTabbedPane.LEFT);
		_panel_1.add(tabPane);
		tabPane.addTab("HTML", null, browser.getContentPane(), null);
		
		
		JScrollPane outputPane = new JScrollPane();
		tabPane.addTab(lang.getString("sourcecode"), null, outputPane, null);
		outputPane.setViewportView(outputText);
		add(splitPane);
	}

	public void changeLocale(String locale){
		Locale loc = new Locale(locale);
		Locale.setDefault(loc);
		lang = new LangMan(loc);
		myLang = new MyLangMan(loc);
		
		xmlText.setHint(myLang.getString("dropxml"));
		xslText.setHint(myLang.getString("dropxsl"));
		btnTransform.setText(myLang.getString("transform"));
		outputText.setHint(lang.getString("sourcecode"));
		outputText.onLocaleChange(loc);
		xmlText.onLocaleChange(loc);
		xslText.onLocaleChange(loc);
		tabPane.setTitleAt(1, lang.getString("sourcecode"));
	}
}