package co.crazytech.swing.bettertabbedpane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.crazytech.io.IOUtil;
import com.crazytech.swing.browser.SimpleSwingBrowser;

import co.crazytech.xslt.browser.BrowserPanel;
import co.crazytech.xslt.config.AppConfig;
import co.crazytech.xslt.config.Tab;

public class BetterTabbedPane extends JPanel {
	JTabbedPane tabbedPane;
    Integer numTabs = 0;
    JPanel component;
    JFrame frame;
    private AppConfig config;
 
    public BetterTabbedPane(JFrame frame,AppConfig config) {
        //this.component = firstComponent;
        this.config = config;
        this.frame = frame;
        setLayout(new BorderLayout(0, 0));
    	createGUI();
        setDisplay();
    }
 
    /** set diplay for JFrame */
    private void setDisplay() {
        //setSize(450, 300);
		setVisible(true);
    }
 
    /** set title and add JTabbedPane into JFrame */
    private void createGUI() {
        createJTabbedPane();
        add(tabbedPane);
    }
 
    /** create JTabbedPane contain 2 tab */
    private void createJTabbedPane() {
        /* create JTabbedPane */
        tabbedPane = new JTabbedPane(JTabbedPane.TOP,
                JTabbedPane.SCROLL_TAB_LAYOUT);
        int count = 0;
        if(config.getTabs()!=null&&config.getTabs().size()>0){
	        for (Tab tab: config.getTabs()) {
	        	/* add first tab */
	        	tabbedPane.add(tab.getXmlPath()!=null&&tab.getXslPath()!=null?
	        			new BrowserPanel(frame,config,tab.getXmlPath(),tab.getXslPath()):new BrowserPanel(frame,config), 
	        			"Tab " + String.valueOf(count),
	        			numTabs++);
	        	
	        	tabbedPane.setTabComponentAt(count, new BetterTab(this));
	        	count++;
	        }
        } else {
        	tabbedPane.add(new BrowserPanel(frame,config), 
        			"Tab " + String.valueOf(count),
        			numTabs++);
        	tabbedPane.setTabComponentAt(count, new BetterTab(this));
        	count++;
        }
        
 
        /* add tab to add new tab when click */
        tabbedPane.add(new JPanel(), "+", numTabs++);
        tabbedPane.addChangeListener(changeListener);
        initRunMenuItem(tabbedPane);
    }
 
    /** create JPanel contain a JLabel */
    private static JPanel createJPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 1));
        panel.add(new JScrollPane(createTextArea(10, 40)));
        return panel;
    }
    
    public ChangeListener changeListener = new ChangeListener() {
		
		@Override
		public void stateChanged(ChangeEvent e) {
			JAXBContext ctx;
			try {
				ctx = JAXBContext.newInstance(AppConfig.class);
				Unmarshaller unmarshaller = ctx.createUnmarshaller();
				StringReader reader = new StringReader(IOUtil.readFile("config.dat"));
				AppConfig config = (AppConfig)unmarshaller.unmarshal(reader);
				addNewTab(new BrowserPanel(frame,config));
				initRunMenuItem((JTabbedPane)e.getSource());
			} catch (JAXBException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	};
	
	private void initRunMenuItem(JTabbedPane tabpane){
		BrowserPanel browserPanel = (BrowserPanel)tabpane.getComponentAt(tabpane.getSelectedIndex());
		JMenuItem mntm = frame.getJMenuBar().getMenu(3).getItem(0);
		for (ActionListener action : mntm.getActionListeners()) {
			mntm.removeActionListener(action);
		}
		mntm.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				browserPanel.getBtnTransform().doClick();
			}
		});
	}
 
    private static JTextArea createTextArea(int row, int col) {
        JTextArea ta = new JTextArea(row, col);
        ta.setWrapStyleWord(true);
        ta.setLineWrap(true);
        ta.setForeground(Color.BLUE);
        return ta;
    }
 
    public void addNewTab(Component panel) {
        int index = numTabs - 1;
        if (tabbedPane.getSelectedIndex() == index) { /* if click new tab */
            /* add new tab */
            tabbedPane.add(panel, "Tab " + String.valueOf(index),
                    index);
            /* set tab is custom tab */
            tabbedPane.setTabComponentAt(index, new BetterTab(this));
            tabbedPane.removeChangeListener(changeListener);
            tabbedPane.setSelectedIndex(index);
            tabbedPane.addChangeListener(changeListener);
            numTabs++;
        }
    }
 
    public void removeTab(int index) {
        tabbedPane.remove(index);
        numTabs--;
 
        if (index == numTabs - 1 && index > 0) {
            tabbedPane.setSelectedIndex(numTabs - 2);
        } else {
            tabbedPane.setSelectedIndex(index);
        }
 
        if (numTabs == 1) {
            addNewTab(component);
        }
    }
 
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setTitle("title");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 300);
        frame.setLocationRelativeTo(null);
        JAXBContext ctx;
		try {
			ctx = JAXBContext.newInstance(AppConfig.class);
			Unmarshaller unmarshaller = ctx.createUnmarshaller();
			String xmlStr = IOUtil.readFile("config.dat");
			StringReader reader = new StringReader(xmlStr);
			AppConfig config = (AppConfig)unmarshaller.unmarshal(reader);
			frame.add(new BetterTabbedPane(frame,config));
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        frame.setVisible(true);
    }

	public JPanel getComponent() {
		return component;
	}

	public void setComponent(JPanel component) {
		this.component = component;
	}

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}
}
