package co.crazytech.xslt.browser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.batik.util.MimeTypeConstants;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryConfig;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.events.FOPEventListenerProxy;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.xml.sax.SAXException;

import com.crazytech.io.IOUtil;
import com.crazytech.swing.browser.SimpleSwingBrowser;
import com.crazytech.swing.texteditor.DragDropSyntaxEditor;
import com.crazytech.swing.texteditor.DragDropTextEditor;
import com.crazytech.swing.texteditor.SyntaxEditor;
import com.crazytech.swing.texteditor.TextEditor;
import com.crazytech.xslt.XSLT;

import co.crazytech.xslt.config.AppConfig;
import res.locale.LangMan;
import res.locale.MyLangMan;

public class BrowserPanel extends JPanel {
	private SimpleSwingBrowser browser;
	private SyntaxEditor outputText;
	private JTabbedPane tabPane;
	private JButton btnTransform;
	private JSplitPane splitPane;
	private JPanel rightHost, _panel_1;
	private DragDropSyntaxEditor xmlText,xslText;
	private Locale locale;
	private LangMan lang;
	private MyLangMan myLang;
	private AppConfig config;
	
	public BrowserPanel(AppConfig config) {
		super();
		locale = Locale.getDefault();
		lang = new LangMan(locale);
		init(config);
	}
	
	public BrowserPanel(AppConfig config, String xmlPath, String xslPath) {
		super();
		locale = Locale.getDefault();
		lang = new LangMan(locale);
		init(config);
		setRstaContent(xmlPath, xslPath);
		btnTransform.doClick();
	}
	
	private void init(AppConfig config) {
		setLayout(new BorderLayout(0, 0));
		
		browser = new SimpleSwingBrowser(locale);
		
		outputText = new SyntaxEditor(this,lang.getString("sourcecode"),locale);
		outputText.getRtextArea().setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_HTML);
		outputText.setAutocompletion(SyntaxEditor.RstaAC.AC_HTML);
		xmlText = new DragDropSyntaxEditor(this,"Drop XML Here",locale);
		xmlText.getRtextArea().setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
		xmlText.setAutocompletion(SyntaxEditor.RstaAC.AC_XML);
		
		GridBagConstraints gbc_xmlScrollPane = new GridBagConstraints();
		gbc_xmlScrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_xmlScrollPane.fill = GridBagConstraints.BOTH;
		gbc_xmlScrollPane.gridx = 0;
		gbc_xmlScrollPane.gridy = 0;
		
		xslText = new DragDropSyntaxEditor(this,"Drop XSL Here",locale);
		xslText.getRtextArea().setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
		xslText.setAutocompletion(SyntaxEditor.RstaAC.AC_XML);
		xslText.setAutocompletion(SyntaxEditor.RstaAC.AC_HTML);
		GridBagConstraints gbc_xslScrollPane = new GridBagConstraints();
		gbc_xslScrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_xslScrollPane.fill = GridBagConstraints.BOTH;
		gbc_xslScrollPane.gridx = 0;
		gbc_xslScrollPane.gridy = 1;
		
		
		splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.7);
		
		rightHost = new JPanel();
		splitPane.setRightComponent(rightHost);
		rightHost.setLayout(new BorderLayout(0, 0));
		
		
		JSplitPane textSplitPane = new JSplitPane();
		textSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		textSplitPane.setResizeWeight(0.5);
		textSplitPane.setTopComponent(xmlText);
		textSplitPane.setBottomComponent(xslText);
		
		GridBagConstraints gbc_textSplitPane = new GridBagConstraints();
		gbc_textSplitPane.insets = new Insets(0, 0, 5, 0);
		gbc_textSplitPane.fill = GridBagConstraints.BOTH;
		gbc_textSplitPane.gridx = 0;
		gbc_textSplitPane.gridy = 0;
		
		JPanel rightPane = new JPanel();
		rightHost.add(rightPane);
		//	rightPane.setPreferredSize(new Dimension(300, 700));
		
		GridBagLayout gbl_rightPane = new GridBagLayout();
		gbl_rightPane.columnWidths = new int[]{0, 0};
		gbl_rightPane.rowHeights = new int[]{0, 0};
		gbl_rightPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_rightPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		rightPane.setLayout(gbl_rightPane);
		//rightPane.add(xmlText, gbc_xmlScrollPane);
		rightPane.add(textSplitPane, gbc_textSplitPane);
		
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
							if(strOutput.indexOf("fo:root")!=-1){
								IOUtil.writeFile(strOutput, "temp.fo");
								FopFactory fopfac = FopFactory.newInstance(new File("fop.conf"));
								OutputStream out = new BufferedOutputStream(new FileOutputStream(new File("temp.pdf")));
								Fop fop = fopfac.newFop(MimeConstants.MIME_PDF,out);
								TransformerFactory factory = TransformerFactory.newInstance();
								Transformer transformer = factory.newTransformer();
								Source src = new StreamSource(new File("temp.fo"));
								Result res = new SAXResult(fop.getDefaultHandler());
								transformer.transform(src, res);
							
							}
						} catch (TransformerException | IOException e) {
							// TODO Auto-generated catch block
							String errorMsg = myLang.getString("error")+"\n"+e.getLocalizedMessage();
							outputText.setText(errorMsg);
							browser.loadContent(errorMsg);
						} catch (SAXException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
		gbc_btnTransform.gridy = 1;
		rightPane.add(btnTransform, gbc_btnTransform);
		
		_panel_1 = new JPanel();
		_panel_1.setLayout(new BorderLayout(0, 0));
		
		tabPane = new JTabbedPane(JTabbedPane.TOP);
		tabPane.addTab("HTML", null, browser.getContentPane(), null);
		
		
		JScrollPane outputPane = new JScrollPane();
		tabPane.addTab(lang.getString("sourcecode"), null, outputPane, null);
		outputPane.setViewportView(outputText);
		_panel_1.add(tabPane);
		splitPane.setLeftComponent(_panel_1);
		add(splitPane,BorderLayout.CENTER);
		setTheme(config.getRstaTheme());
		changeLocale(config.getLocale());
		
	}
	
	private void setRstaContent(String xmlPath, String xslPath){
		try {
			xmlText.setText(IOUtil.readFile(xmlPath));
			xmlText.setCurrFilePath(xmlPath);
			xslText.setText(IOUtil.readFile(xslPath));
			xslText.setCurrFilePath(xslPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setTheme(String themeName) {
		outputText.setTheme(outputText.getRtextArea(), themeName);
		xmlText.setTheme(xmlText.getRtextArea(), themeName);
		xslText.setTheme(xslText.getRtextArea(), themeName);
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

	public DragDropSyntaxEditor getXmlText() {
		return xmlText;
	}

	public DragDropSyntaxEditor getXslText() {
		return xslText;
	}
}