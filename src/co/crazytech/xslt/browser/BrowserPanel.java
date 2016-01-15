package co.crazytech.xslt.browser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
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
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import org.xml.sax.SAXException;

import com.crazytech.io.IOUtil;
import com.crazytech.swing.browser.SimpleSwingBrowser;
import com.crazytech.swing.texteditor.DragDropSyntaxEditor;
import com.crazytech.swing.texteditor.DragDropTextEditor;
import com.crazytech.swing.texteditor.SyntaxEditor;
import com.crazytech.swing.texteditor.TextEditor;
import com.crazytech.xslt.XSLT;

import co.crazytech.commons.util.RandomCharacters;
import co.crazytech.xslt.config.AppConfig;
import res.locale.LangMan;
import res.locale.MyLangMan;

public class BrowserPanel extends JPanel {
	private JFrame frame;
	private SimpleSwingBrowser browser;
	private SyntaxEditor outputText;
	private JTabbedPane tabPane;
	private JButton btnTransform;
	private JSplitPane splitPane;
	private JPanel pdfViewerContainer;
	private JPanel rightHost, _panel_1;
	private DragDropSyntaxEditor xmlText,xslText;
	private Locale locale;
	private LangMan lang;
	private MyLangMan myLang;
	private AppConfig config;
	private JMenuItem mntmNew,mntmRefresh,mntmOpen,mntmSave,mntmSaveAs,
		mntmUndo,mntmRedo,mntmSelectAll,mntmCut,mntmCopy,mntmPaste,
		mntmRun,
		mntmFind,mntmReplace,mntmGoToLine;
	private SwingController pdfCtrl;
	private List<String> tempFileNames;
	
	public BrowserPanel(JFrame frame, AppConfig config) {
		super();
		this.frame = frame;
		locale = Locale.getDefault();
		lang = new LangMan(locale);
		init(config);
	}
	
	public BrowserPanel(JFrame frame, AppConfig config, String xmlPath, String xslPath) {
		super();
		this.frame = frame;
		locale = Locale.getDefault();
		lang = new LangMan(locale);
		init(config);
		setRstaContent(xmlPath, xslPath);
		btnTransform.doClick();
	}
	
	private void init(AppConfig config) {
		setLayout(new BorderLayout(0, 0));
		tempFileNames = new ArrayList<String>();
		browser = new SimpleSwingBrowser(locale);
		
		outputText = new SyntaxEditor(frame,this,lang.getString("sourcecode"),locale);
		outputText.getRtextArea().setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_HTML);
		outputText.setAutocompletion(SyntaxEditor.RstaAC.AC_HTML);
		outputText.getRtextArea().addFocusListener(syntaxFocusListener(outputText));
		xmlText = new DragDropSyntaxEditor(frame,this,"Drop XML Here",locale);
		xmlText.getRtextArea().setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
		xmlText.setAutocompletion(SyntaxEditor.RstaAC.AC_XML);
		xmlText.getRtextArea().addFocusListener(syntaxFocusListener(xmlText));
		GridBagConstraints gbc_xmlScrollPane = new GridBagConstraints();
		gbc_xmlScrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_xmlScrollPane.fill = GridBagConstraints.BOTH;
		gbc_xmlScrollPane.gridx = 0;
		gbc_xmlScrollPane.gridy = 0;
		
		xslText = new DragDropSyntaxEditor(frame,this,"Drop XSL Here",locale);
		xslText.getRtextArea().setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
		xslText.setAutocompletion(SyntaxEditor.RstaAC.AC_XML);
		xslText.setAutocompletion(SyntaxEditor.RstaAC.AC_HTML);
		xslText.getRtextArea().addFocusListener(syntaxFocusListener(xslText));
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
		btnTransform.addActionListener(transformActionListener());
		GridBagConstraints gbc_btnTransform = new GridBagConstraints();
		gbc_btnTransform.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnTransform.gridx = 0;
		gbc_btnTransform.gridy = 1;
		rightPane.add(btnTransform, gbc_btnTransform);
		
		_panel_1 = new JPanel();
		_panel_1.setLayout(new BorderLayout(0, 0));
		
		tabPane = new JTabbedPane(JTabbedPane.TOP);
		tabPane.addTab("HTML", null, browser.getContentPane(), null);
		
		pdfCtrl = new SwingController();
		SwingViewBuilder iceBuilder = new SwingViewBuilder(pdfCtrl);
		pdfViewerContainer = new JPanel();
		pdfViewerContainer.setLayout(new BorderLayout());
		JPanel pdfViewer = iceBuilder.buildViewerPanel();
		pdfViewer.setMinimumSize(new Dimension(300, 300));
		pdfViewerContainer.add(pdfViewer,BorderLayout.CENTER);
		tabPane.addTab("FO-PDF", null, pdfViewerContainer, null);
		
		JScrollPane outputPane = new JScrollPane();
		tabPane.addTab(lang.getString("sourcecode"), null, outputPane, null);
		outputPane.setViewportView(outputText);
		_panel_1.add(tabPane);
		splitPane.setLeftComponent(_panel_1);
		add(splitPane,BorderLayout.CENTER);
		initMenuItems();
		setTheme(config.getRstaTheme());
		changeLocale(config.getLocale());
		
	}
	
	private void initMenuItems(){
		JMenuBar menubar = frame.getJMenuBar();
		mntmNew = menubar.getMenu(0).getItem(0);
		mntmRefresh = menubar.getMenu(0).getItem(1);
		mntmOpen = menubar.getMenu(0).getItem(2);
		mntmSave = menubar.getMenu(0).getItem(3);
		mntmSaveAs = menubar.getMenu(0).getItem(4);
		mntmUndo = menubar.getMenu(1).getItem(0);
		mntmRedo = menubar.getMenu(1).getItem(1);
		mntmSelectAll = menubar.getMenu(1).getItem(2);
		mntmCut = menubar.getMenu(1).getItem(3);
		mntmCopy = menubar.getMenu(1).getItem(4);
		mntmPaste = menubar.getMenu(1).getItem(5);
		mntmFind = menubar.getMenu(2).getItem(0);
		mntmReplace = menubar.getMenu(2).getItem(1);
		mntmGoToLine = menubar.getMenu(2).getItem(2);
		mntmRun = menubar.getMenu(3).getItem(0);
	}
	
	
	private FocusListener syntaxFocusListener(SyntaxEditor editor){
		return new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				resetMenuItemListeners(editor);
			}
		};
	}
	
	private void resetMenuItemListeners(SyntaxEditor editor){
		resetMenuItemListener(mntmNew, editor.mnActNew());
		resetMenuItemListener(mntmRefresh, editor.mnActRefresh());
		resetMenuItemListener(mntmOpen, editor.mnActOpen());
		resetMenuItemListener(mntmSave, editor.mnActSave());
		resetMenuItemListener(mntmSaveAs, editor.mnActSaveAs());
		resetMenuItemListener(mntmUndo, editor.mnActUndo());
		resetMenuItemListener(mntmRedo, editor.mnActRedo());
		resetMenuItemListener(mntmSelectAll, editor.mnActSelectAll());
		mntmCut.setAction(editor.mnActCut());
		mntmCopy.setAction(editor.mnActCopy());
		mntmPaste.setAction(editor.mnActPaste());
		mntmFind.setAction(editor.showFindDialogAction());
		mntmReplace.setAction(editor.showReplaceDialogAction());
		mntmGoToLine.setAction(editor.goToLineAction());
	}
	
	private void resetMenuItemListener(JMenuItem menuItem,ActionListener listener){
		for (ActionListener actListener : menuItem.getActionListeners()) {
			menuItem.removeActionListener(actListener);
		}
		menuItem.addActionListener(listener);
	}
	
	private ActionListener transformActionListener(){
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Runnable runnable = new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String strXml = xmlText.getText();
						String strXsl = xslText.getText();
						try {
							String strOutput = XSLT.transform(strXsl, strXml, null, IOUtil.DEFAULT_CHARSET);
							if(strOutput.indexOf("fo:root")!=-1){
								clearTempFiles();
								transformFoPdf(strOutput);
								tabPane.setSelectedIndex(1);
							} else {
								browser.loadContent(strOutput);
								tabPane.setSelectedIndex(0);
							}
							outputText.setText(strOutput);
						} catch (TransformerException | IOException e) {
							// TODO Auto-generated catch block
							String errorMsg = myLang.getString("error")+"\n"+e.getLocalizedMessage();
							outputText.setText(errorMsg);
							showErrorDialog("Transformer Exception", errorMsg);
						} catch (SAXException e) {
							// TODO Auto-generated catch block
							showErrorDialog("SAXException", e.getMessage());
							e.printStackTrace();
						}
						
					}
				};
				runnable.run();
			}
		};
	}
	
	private void showErrorDialog(String title, String message){
		SyntaxEditor errorText = new SyntaxEditor(frame, getParent(), "", locale);
		errorText.setText(message);
		errorText.getRtextArea().setColumns(60);
		errorText.getRtextArea().setLineWrap(true);
		errorText.getRtextArea().setWrapStyleWord(true);
		JOptionPane.showMessageDialog(frame, errorText,title,JOptionPane.PLAIN_MESSAGE);
		
	}
	
	private void transformFoPdf(String trOutput) throws IOException, SAXException, TransformerException{
		browser.loadContent("Not Available");
		String filename = RandomCharacters.randomString(8);
		tempFileNames.add(filename);
		new File("temp/").mkdirs();
		File foFile = new File("temp/"+filename+".fo");
		IOUtil.overwriteFile(trOutput, "temp/"+filename+".fo");
		FopFactory fopfac = FopFactory.newInstance(new File("src/res/fop.xconf"));
		File pdfFile = new File("temp/"+filename+".pdf");
		OutputStream out = new BufferedOutputStream(new FileOutputStream(pdfFile));
		Fop fop = fopfac.newFop(MimeConstants.MIME_PDF,out);
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		Source src = new StreamSource(foFile);
		Result res = new SAXResult(fop.getDefaultHandler());
		transformer.transform(src, res);
		out.close();
		InputStream in = new FileInputStream(pdfFile);
		pdfCtrl.openDocument(in,"","");
		in.close();
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
		tabPane.setTitleAt(2, lang.getString("sourcecode"));
	}
	
	private void clearTempFiles(){
		pdfCtrl.closeDocument();
		for (String filename : tempFileNames) {
			new File("temp/"+filename+".fo").delete();
			new File("temp/"+filename+".pdf").delete();
		}
	}
	
	
	
	public void onExit(){
		clearTempFiles();
	}

	public DragDropSyntaxEditor getXmlText() {
		return xmlText;
	}

	public DragDropSyntaxEditor getXslText() {
		return xslText;
	}

	public JButton getBtnTransform() {
		return btnTransform;
	}
}