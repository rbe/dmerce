import javax.swing.JSeparator;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import java.awt.BorderLayout;

/**
* This code was generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a
* for-profit company or business) then you should purchase
* a license - please visit www.cloudgarden.com for details.
*/
public class NewSwingApp extends javax.swing.JFrame {
	private JMenuItem helpMenuItem;
	private JMenu jMenu5;
	private JMenuItem deleteMenuItem;
	private JSeparator jSeparator1;
	private JMenuItem pasteMenuItem;
	private JMenuItem copyMenuItem;
	private JMenuItem cutMenuItem;
	private JMenu jMenu4;
	private JMenuItem exitMenuItem;
	private JSeparator jSeparator2;
	private JMenuItem closeFileMenuItem;
	private JMenuItem saveAsMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem openFileMenuItem;
	private JMenuItem newFileMenuItem;
	private JMenu jMenu3;
	private JMenuBar jMenuBar1;

	public NewSwingApp() {
		initGUI();
	}

	/**
	* Initializes the GUI.
	* Auto-generated code - any changes you make will disappear.
	*/
	public void initGUI(){
		try {
			preInitGUI();
	
	
			BorderLayout thisLayout = new BorderLayout();
			this.getContentPane().setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setSize(new java.awt.Dimension(407,297));
			jMenuBar1 = new JMenuBar();
			jMenu3 = new JMenu();
			newFileMenuItem = new JMenuItem();
			openFileMenuItem = new JMenuItem();
			saveMenuItem = new JMenuItem();
			saveAsMenuItem = new JMenuItem();
			closeFileMenuItem = new JMenuItem();
			jSeparator2 = new JSeparator();
			exitMenuItem = new JMenuItem();
			jMenu4 = new JMenu();
			cutMenuItem = new JMenuItem();
			copyMenuItem = new JMenuItem();
			pasteMenuItem = new JMenuItem();
			jSeparator1 = new JSeparator();
			deleteMenuItem = new JMenuItem();
			jMenu5 = new JMenu();
			helpMenuItem = new JMenuItem();
	
			setJMenuBar(jMenuBar1);
	
			jMenu3.setText("File");
			jMenu3.setVisible(true);
			jMenuBar1.add(jMenu3);
	
			newFileMenuItem.setText("New");
			newFileMenuItem.setVisible(true);
			newFileMenuItem.setPreferredSize(new java.awt.Dimension(28,16));
			newFileMenuItem.setBounds(new java.awt.Rectangle(5,5,28,16));
			jMenu3.add(newFileMenuItem);
	
			openFileMenuItem.setText("Open");
			openFileMenuItem.setVisible(true);
			openFileMenuItem.setBounds(new java.awt.Rectangle(5,5,60,30));
			jMenu3.add(openFileMenuItem);
	
			saveMenuItem.setText("Save");
			saveMenuItem.setVisible(true);
			saveMenuItem.setBounds(new java.awt.Rectangle(5,5,60,30));
			jMenu3.add(saveMenuItem);
	
			saveAsMenuItem.setText("Save As ...");
			saveAsMenuItem.setVisible(true);
			saveAsMenuItem.setBounds(new java.awt.Rectangle(5,5,60,30));
			jMenu3.add(saveAsMenuItem);
	
			closeFileMenuItem.setText("Close");
			closeFileMenuItem.setVisible(true);
			closeFileMenuItem.setBounds(new java.awt.Rectangle(5,5,60,30));
			jMenu3.add(closeFileMenuItem);
	
			jSeparator2.setVisible(true);
			jSeparator2.setBounds(new java.awt.Rectangle(5,5,60,30));
			jMenu3.add(jSeparator2);
	
			exitMenuItem.setText("Exit");
			exitMenuItem.setVisible(true);
			exitMenuItem.setBounds(new java.awt.Rectangle(5,5,60,30));
			jMenu3.add(exitMenuItem);
	
			jMenu4.setText("Edit");
			jMenu4.setVisible(true);
			jMenuBar1.add(jMenu4);
	
			cutMenuItem.setText("Cut");
			cutMenuItem.setVisible(true);
			cutMenuItem.setPreferredSize(new java.awt.Dimension(27,16));
			cutMenuItem.setBounds(new java.awt.Rectangle(5,5,27,16));
			jMenu4.add(cutMenuItem);
	
			copyMenuItem.setText("Copy");
			copyMenuItem.setVisible(true);
			copyMenuItem.setBounds(new java.awt.Rectangle(5,5,60,30));
			jMenu4.add(copyMenuItem);
	
			pasteMenuItem.setText("Paste");
			pasteMenuItem.setVisible(true);
			pasteMenuItem.setBounds(new java.awt.Rectangle(5,5,60,30));
			jMenu4.add(pasteMenuItem);
	
			jSeparator1.setVisible(true);
			jSeparator1.setBounds(new java.awt.Rectangle(5,5,60,30));
			jMenu4.add(jSeparator1);
	
			deleteMenuItem.setText("Delete");
			deleteMenuItem.setVisible(true);
			deleteMenuItem.setBounds(new java.awt.Rectangle(5,5,60,30));
			jMenu4.add(deleteMenuItem);
	
			jMenu5.setText("Help");
			jMenu5.setVisible(true);
			jMenuBar1.add(jMenu5);
	
			helpMenuItem.setText("Help");
			helpMenuItem.setVisible(true);
			helpMenuItem.setPreferredSize(new java.awt.Dimension(31,16));
			helpMenuItem.setBounds(new java.awt.Rectangle(5,5,31,16));
			jMenu5.add(helpMenuItem);
	
			postInitGUI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/** Add your pre-init code in here 	*/
	public void preInitGUI(){
	}

	/** Add your post-init code in here 	*/
	public void postInitGUI(){
	}

	/** Auto-generated main method */
	public static void main(String[] args){
		showGUI();
	}

	/**
	* This static method creates a new instance of this class and shows
	* it inside a new JFrame, (unless it is already a JFrame).
	*
	* It is a convenience method for showing the GUI, but it can be
	* copied and used as a basis for your own code.	*
	* It is auto-generated code - the body of this method will be
	* re-generated after any changes are made to the GUI.
	* However, if you delete this method it will not be re-created.	*/
	public static void showGUI(){
		try {
			NewSwingApp inst = new NewSwingApp();
			inst.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
