package find.duplicates;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.table.DefaultTableModel;

public class MainForm extends JFrame {
	Thread t;
	Thread t1;
	boolean sourceStatus = true;
	boolean destStatus = true;
	private JButton jButton1;
	private JButton jButton2;
	private JButton jButton3;
	private JButton jButton4;
	private JCheckBox jCheckBox1;
	private JCheckBox jCheckBox2;
	private JFileChooser jFileChooser1;
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JLabel jLabel3;
	private JMenu jMenu1;
	private JMenuBar jMenuBar1;
	private JMenuItem jMenuItem1;
	private JPanel jPanel1;
	private JPanel jPanel2;
	private JScrollPane jScrollPane1;
	private JScrollPane jScrollPane2;
	private JScrollPane jScrollPane3;
	private JTable jTable1;
	private JTable jTable2;
	private JTable jTable3;
	private JTextField jTextField1;
	private JTextField jTextField2;
	private JTextField jTextField3;
	private JProgressBar progressBar;

	public MainForm() {
	      this.initComponents();
	   }

	private void initComponents() {
		this.jFileChooser1 = new JFileChooser();
		this.jPanel1 = new JPanel();
		this.jTextField1 = new JTextField();
		this.jButton1 = new JButton();
		this.jButton2 = new JButton();
		this.jTextField2 = new JTextField();
		this.jButton3 = new JButton();
		this.jButton4 = new JButton();
		this.jScrollPane3 = new JScrollPane();
		this.jTable3 = new JTable();
		this.jLabel2 = new JLabel();
		this.jLabel1 = new JLabel();
		this.jTextField3 = new JTextField();
		this.jLabel3 = new JLabel();
		this.jCheckBox1 = new JCheckBox();
		this.jCheckBox2 = new JCheckBox();
		this.jPanel2 = new JPanel();
		this.jScrollPane1 = new JScrollPane();
		this.jTable1 = new JTable();
		this.progressBar = new JProgressBar();
		this.jScrollPane2 = new JScrollPane();
		this.jTable2 = new JTable();
		this.jMenuBar1 = new JMenuBar();
		this.jMenu1 = new JMenu();
		this.jMenuItem1 = new JMenuItem();
		this.jFileChooser1.setFileHidingEnabled(false);
		this.jFileChooser1.setFileSelectionMode(1);
		this.jFileChooser1.setName("jFileChooser1");
		this.setDefaultCloseOperation(3);
		this.setTitle("Find Duplicates - By Avinash");
		this.setName("Form");
		this.setResizable(false);
		this.jPanel1.setName("jPanel1");
		this.jTextField1.setName("jTextField1");
		this.jButton1.setText("Browse Source folder");
		this.jButton1.setName("jButton1");
		this.jButton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MainForm.this.jButton1ActionPerformed(evt);
			}
		});
		this.jButton2.setIcon(new ImageIcon(getClass().getResource("/find/duplicates/resources/search-25.png")));
//		this.jButton2.setIcon(new ImageIcon(this.getClass().getResource("https://i.sstatic.net/IzNJg.png")));
		this.jButton2.setText("Search");
		this.jButton2.setName("jButton2");
		this.jButton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MainForm.this.jButton2ActionPerformed(evt);
			}
		});
		this.jTextField2.setName("jTextField2");
		this.jButton3.setText("Browse Destination folder");
		this.jButton3.setName("jButton3");
		this.jButton3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MainForm.this.jButton3ActionPerformed(evt);
			}
		});
		this.jButton4.setText("Delete Selected Files");
		this.jButton4.setName("jButton4");
		this.jButton4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MainForm.this.jButton4ActionPerformed(evt);
			}
		});
		this.jScrollPane3.setName("jScrollPane3");
		this.jTable3.setModel(new DefaultTableModel(new Object[0][], new String[] { "File Path", "File Name" }));
		this.jTable3.setMaximumSize(new Dimension(0, 0));
		this.jTable3.setMinimumSize(new Dimension(0, 0));
		this.jTable3.setName("jTable3");
		this.jTable3.setPreferredSize(new Dimension(0, 0));
		this.jScrollPane3.setViewportView(this.jTable3);
		this.jLabel2.setName("jLabel2");
		this.jLabel1.setText("File type:");
		this.jLabel1.setName("jLabel1");
		this.jTextField3.setText(".java");
		this.jTextField3.setName("jTextField3");
		this.jLabel3.setText("(ex: .java)");
		this.jLabel3.setName("jLabel3");
		this.jCheckBox1.setText("Select all Source");
		this.jCheckBox1.setName("jCheckBox1");
		this.jCheckBox1.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				MainForm.this.jCheckBox1ItemStateChanged(evt);
			}
		});
		this.jCheckBox1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MainForm.this.jCheckBox1ActionPerformed(evt);
			}
		});
		this.jCheckBox2.setText("Select all Destination");
		this.jCheckBox2.setName("jCheckBox2");
		this.jCheckBox2.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				MainForm.this.jCheckBox2ItemStateChanged(evt);
			}
		});
		GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
		this.jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout
						.createParallelGroup(Alignment.LEADING).addComponent(this.jTextField1, -2, 429, -2)
						.addGroup(jPanel1Layout.createSequentialGroup()
								.addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING, false)
										.addGroup(jPanel1Layout.createSequentialGroup().addComponent(this.jLabel1)
												.addGap(4, 4, 4).addComponent(this.jTextField3, -2, 74, -2)
												.addPreferredGap(ComponentPlacement.RELATED, -1, 32767)
												.addComponent(this.jLabel3).addGap(18, 18, 18)
												.addComponent(this.jButton2).addGap(187, 187, 187))
										.addComponent(this.jTextField2, -2, 429, -2))
								.addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
										.addGroup(jPanel1Layout.createSequentialGroup().addGap(12, 12, 12)
												.addGroup(jPanel1Layout.createParallelGroup(Alignment.TRAILING, false)
														.addComponent(this.jButton1, Alignment.LEADING, -1, -1, 32767)
														.addComponent(this.jButton3, Alignment.LEADING)))
										.addGroup(jPanel1Layout.createSequentialGroup()
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(this.jLabel2, -2, 184, -2))))
						.addGroup(jPanel1Layout.createSequentialGroup().addComponent(this.jCheckBox1).addGap(18, 18, 18)
								.addComponent(this.jCheckBox2).addGap(18, 18, 18).addComponent(this.jButton4)))
						.addContainerGap(580, 32767))
				.addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
						.addGroup(jPanel1Layout.createSequentialGroup().addGap(0, 627, 32767)
								.addComponent(this.jScrollPane3, -2, 10, -2).addGap(0, 627, 32767))));
		jPanel1Layout
				.setVerticalGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
						.addGroup(jPanel1Layout.createSequentialGroup().addGap(28, 28, 28)
								.addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
										.addComponent(this.jTextField1, -2, -1, -2).addComponent(this.jButton1))
								.addGap(6, 6, 6)
								.addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
										.addComponent(this.jTextField2, -2, -1, -2).addComponent(this.jButton3))
								.addGap(5, 5, 5)
								.addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel1Layout
										.createSequentialGroup()
										.addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
												.addComponent(this.jLabel1).addComponent(this.jTextField3, -2, -1, -2)
												.addComponent(this.jButton2).addComponent(this.jLabel3))
										.addPreferredGap(ComponentPlacement.RELATED, -1, 32767)
										.addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
												.addComponent(this.jCheckBox1).addComponent(this.jCheckBox2)
												.addComponent(this.jButton4))
										.addGap(4, 4, 4)).addComponent(this.jLabel2, -2, 27, -2))
								.addContainerGap())
						.addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
								.addGroup(jPanel1Layout.createSequentialGroup().addGap(0, 77, 32767)
										.addComponent(this.jScrollPane3, -2, 0, -2).addGap(0, 78, 32767))));
		this.jPanel2.setBackground(new Color(224, 233, 242));
		this.jPanel2.setName("jPanel2");
		this.jScrollPane1.setAutoscrolls(true);
		this.jScrollPane1.setName("jScrollPane1");
		this.jScrollPane1.setPreferredSize(new Dimension(450, 404));
		this.jTable1.setModel(new DefaultTableModel(new Object[][] { { null, null, null, null, null, null } },
				new String[] { "Source File Name", "Source File Path", "Delete Source", "Destn File Name",
						"Destn File Path", "Delete Destn" }) {
			Class[] types = new Class[] { Object.class, Object.class, Boolean.class, Object.class, Object.class,
					Boolean.class };

			public Class getColumnClass(int columnIndex) {
				return this.types[columnIndex];
			}
		});
		this.jTable1.setAutoResizeMode(0);
		this.jTable1.setName("jTable1");
		this.jTable1.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				MainForm.this.jTable1MouseClicked(evt);
			}
		});
		this.jScrollPane1.setViewportView(this.jTable1);
		this.progressBar.setFocusable(false);
		this.progressBar.setName("progressBar");
		this.progressBar.setString("Searching Duplicates");
		this.progressBar.setStringPainted(true);
		GroupLayout jPanel2Layout = new GroupLayout(this.jPanel2);
		this.jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING,
						jPanel2Layout.createSequentialGroup().addContainerGap(1064, 32767)
								.addComponent(this.progressBar, -2, 200, -2))
				.addGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING)
						.addGroup(jPanel2Layout.createSequentialGroup().addGap(0, 0, 32767)
								.addComponent(this.jScrollPane1, -2, 1264, -2).addGap(0, 0, 32767))));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING,
						jPanel2Layout.createSequentialGroup().addContainerGap(499, 32767).addComponent(this.progressBar,
								-2, -1, -2))
				.addGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING)
						.addGroup(jPanel2Layout.createSequentialGroup().addGap(0, 0, 32767)
								.addComponent(this.jScrollPane1, -2, 524, -2).addGap(0, 0, 32767))));
		this.progressBar.setVisible(false);
		this.jScrollPane2.setName("jScrollPane2");
		this.jTable2.setModel(new DefaultTableModel(new Object[0][], new String[] { "File Path", "File Name" }));
		this.jTable2.setMaximumSize(new Dimension(0, 0));
		this.jTable2.setMinimumSize(new Dimension(0, 0));
		this.jTable2.setName("jTable2");
		this.jScrollPane2.setViewportView(this.jTable2);
		this.jTable2.setVisible(false);
		this.jMenuBar1.setName("jMenuBar1");
		this.jMenu1.setText("File");
		this.jMenu1.setName("jMenu1");
		this.jMenuItem1.setText("Exit");
		this.jMenuItem1.setName("jMenuItem1");
		this.jMenuItem1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MainForm.this.jMenuItem1ActionPerformed(evt);
			}
		});
		this.jMenu1.add(this.jMenuItem1);
		this.jMenuBar1.add(this.jMenu1);
		this.setJMenuBar(this.jMenuBar1);
		GroupLayout layout = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING)
				.addComponent(this.jPanel2, -1, -1, 32767).addComponent(this.jPanel1, -1, -1, 32767)
				.addGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup()
						.addGap(0, 632, 32767).addComponent(this.jScrollPane2, -2, 0, -2).addGap(0, 632, 32767))));
		layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addComponent(this.jPanel1, -2, -1, -2)
						.addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jPanel2, -1, -1, 32767))
				.addGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup()
						.addGap(0, 335, 32767).addComponent(this.jScrollPane2, -2, 0, -2).addGap(0, 336, 32767))));
		this.pack();
	}

	private void jButton1ActionPerformed(ActionEvent evt) {
		this.jFileChooser1.showOpenDialog((Component) null);

		try {
			this.jTextField1.setText(this.jFileChooser1.getSelectedFile().toString());
		} catch (Exception var3) {
		}

	}

	private void jButton2ActionPerformed(ActionEvent evt) {
		this.clearTable();
		this.LoadSourceFiles();
	}

	private void jButton3ActionPerformed(ActionEvent evt) {
		this.jFileChooser1.showOpenDialog((Component) null);

		try {
			this.jTextField2.setText(this.jFileChooser1.getSelectedFile().toString());
		} catch (Exception var3) {
		}

	}

	private void jTable1MouseClicked(MouseEvent evt) {
		this.calculateFiletoDelete();
	}

	private void jMenuItem1ActionPerformed(ActionEvent evt) {
		System.exit(0);
	}

	private void jButton4ActionPerformed(ActionEvent evt) {
		if (this.calculateFiletoDelete() == 0) {
			JOptionPane.showMessageDialog((Component) null, "No file selected");
		} else {
			int res = JOptionPane.showConfirmDialog((Component) null, "Do you want to delete selected files");
			if (res == 0) {
				this.deleteSelectedFiles();
				res = JOptionPane.showConfirmDialog((Component) null, "Do you want to reload files");
				if (res == 0) {
					this.clearTable();
					this.LoadSourceFiles();
				}
			}
		}

		this.jCheckBox1.setSelected(false);
		this.jCheckBox2.setSelected(false);
		this.changeItemOne();
		this.changeItemTwo();
	}

	private void jCheckBox1ActionPerformed(ActionEvent evt) {
	}

	public void changeItemOne() {
		for (int i = 0; i < this.jTable1.getRowCount(); ++i) {
			try {
				if (this.jCheckBox1.isSelected()) {
					this.jTable1.setValueAt(true, i, 2);
				} else {
					this.jTable1.setValueAt(false, i, 2);
				}
			} catch (Exception var3) {
			}
		}

	}

	public void changeItemTwo() {
		for (int i = 0; i < this.jTable1.getRowCount(); ++i) {
			try {
				if (this.jCheckBox2.isSelected()) {
					this.jTable1.setValueAt(true, i, 5);
				} else {
					this.jTable1.setValueAt(false, i, 5);
				}
			} catch (Exception var3) {
			}
		}

	}

	private void jCheckBox1ItemStateChanged(ItemEvent evt) {
		this.changeItemOne();
		this.calculateFiletoDelete();
	}

	private void jCheckBox2ItemStateChanged(ItemEvent evt) {
		this.changeItemTwo();
		this.calculateFiletoDelete();
	}

	private void LoadSourceFiles() {
		if (this.jTextField1.getText() != null) {
			if (this.jTextField1.getText().trim().equals("")) {
				JOptionPane.showMessageDialog((Component) null, "Please Choose the Source Location to search");
			} else if (this.jTextField2.getText().trim().equals("")) {
				JOptionPane.showMessageDialog((Component) null, "Please Choose the destination Location to search");
			} else {
				this.t = new Thread(new MainForm.SourceMessageLoop());
				this.t.start();
			}
		} else {
			JOptionPane.showMessageDialog((Component) null, "Please Choose the Location to search");
		}

	}

	private void deleteSelectedFiles() {
		boolean a = true;
		this.progressBar.setVisible(true);
		this.progressBar.setIndeterminate(true);
		this.progressBar.setString("Deleting Files");

		for (int i = 0; i < this.jTable1.getRowCount(); ++i) {
			File f;
			if (this.jTable1.getValueAt(i, 2).toString().equalsIgnoreCase("true")) {
				try {
					f = new File(this.jTable1.getValueAt(i, 1).toString());
					f.delete();
				} catch (Exception var5) {
					a = false;
				}
			}

			if (this.jTable1.getValueAt(i, 5).toString().equalsIgnoreCase("true")) {
				try {
					f = new File(this.jTable1.getValueAt(i, 4).toString());
					f.delete();
				} catch (Exception var6) {
					a = false;
				}
			}
		}

		this.progressBar.setIndeterminate(false);
		this.progressBar.setVisible(false);
		if (a) {
			JOptionPane.showMessageDialog((Component) null, "Files Deleted");
		} else if (!a) {
			JOptionPane.showMessageDialog((Component) null, "Cannot able to delete some files");
		}

	}

	private void FindDuplicates() {
		for (int i = 0; i < this.jTable2.getRowCount(); ++i) {
			for (int j = 0; j < this.jTable3.getRowCount(); ++j) {
				if (this.jTable2.getValueAt(i, 1).equals(this.jTable3.getValueAt(j, 1))) {
					this.addToMainTable(this.jTable2.getValueAt(i, 1).toString(),
							this.jTable2.getValueAt(i, 0).toString(), this.jTable3.getValueAt(j, 1).toString(),
							this.jTable3.getValueAt(j, 0).toString());
				}
			}
		}

	}

	public void addToMainTable(String fileFolder1, String path1, String fileFolder2, String path2) {
		int dest = this.jTable1.getRowCount();
		Vector v = new Vector(3, 2);
		v.addElement(fileFolder1);
		v.addElement(path1);
		v.addElement(false);
		v.addElement(fileFolder2);
		v.addElement(path2);
		v.addElement(false);
		if (dest + 1 < this.jTable1.getRowCount() - 1) {
			((DefaultTableModel) this.jTable1.getModel()).insertRow(dest + 1, v);
		} else {
			((DefaultTableModel) this.jTable1.getModel()).addRow(v);
		}

	}

	public void Destwalkin(File dir, int row) {
		String pattern = "file pattern";
		File[] listFile = dir.listFiles();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; ++i) {
				String filepath;
				String lstindx;
				if (listFile[i].isDirectory()) {
					filepath = listFile[i].toString();
					lstindx = filepath.substring(filepath.lastIndexOf(File.separator) + 1, filepath.length());
					++row;
					this.Destwalkin(listFile[i], row);
				} else {
					if (listFile[i].getName().endsWith(pattern)) {
					}

					filepath = listFile[i].getPath().toString();
					lstindx = filepath.substring(filepath.lastIndexOf(File.separator) + 1, filepath.length());
					if (listFile[i].getPath().toString().endsWith(this.jTextField3.getText())) {
						this.addToDestTable(listFile[i].getPath().toString(), listFile[i].getName().toString(), "", "");
						++row;
					}
				}
			}
		}

	}

	public void addToDestTable(String fileFolder, String path, String createdDate, String modifiedDate) {
		int dest = this.jTable3.getRowCount();
		Vector v = new Vector(3, 2);
		v.addElement(fileFolder);
		v.addElement(path);
		v.addElement(createdDate);
		v.addElement(modifiedDate);
		if (dest + 1 < this.jTable3.getRowCount() - 1) {
			((DefaultTableModel) this.jTable3.getModel()).insertRow(dest + 1, v);
		} else {
			((DefaultTableModel) this.jTable3.getModel()).addRow(v);
		}

	}

	public void Sourcewalkin(File dir, int row) {
		String pattern = "file pattern";
		File[] listFile = dir.listFiles();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; ++i) {
				String filepath;
				String lstindx;
				if (listFile[i].isDirectory()) {
					filepath = listFile[i].toString();
					lstindx = filepath.substring(filepath.lastIndexOf(File.separator) + 1, filepath.length());
					++row;
					this.Sourcewalkin(listFile[i], row);
				} else {
					if (listFile[i].getName().endsWith(pattern)) {
					}

					filepath = listFile[i].getPath().toString();
					lstindx = filepath.substring(filepath.lastIndexOf(File.separator) + 1, filepath.length());
					if (listFile[i].getPath().toString().endsWith(this.jTextField3.getText())) {
						this.addToSrcTable(listFile[i].getPath().toString(), listFile[i].getName().toString(), "", "");
						++row;
					}
				}
			}
		}

	}

	public void addToSrcTable(String fileFolder, String path, String createdDate, String modifiedDate) {
		int dest = this.jTable2.getRowCount();
		Vector v = new Vector(3, 2);
		v.addElement(fileFolder);
		v.addElement(path);
		v.addElement(createdDate);
		v.addElement(modifiedDate);
		if (dest + 1 < this.jTable2.getRowCount() - 1) {
			((DefaultTableModel) this.jTable2.getModel()).insertRow(dest + 1, v);
		} else {
			((DefaultTableModel) this.jTable2.getModel()).addRow(v);
		}

	}

	private int calculateFiletoDelete() {
		int count = 0;

		try {
			for (int i = 0; i < this.jTable1.getRowCount(); ++i) {
				if (this.jTable1.getValueAt(i, 2).toString().equalsIgnoreCase("true")) {
					++count;
				}

				if (this.jTable1.getValueAt(i, 5).toString().equalsIgnoreCase("true")) {
					++count;
				}
			}
		} catch (Exception var3) {
		}

		this.jLabel2.setText(count + " file(s) selected");
		return count;
	}

	public void clearTable() {
		this.jLabel2.setText("");
		this.jTable1.setModel(new DefaultTableModel(new Object[0][], new String[] { "Source File Name",
				"Source File Path", "Delete Source", "Destn File Name", "Destn File Path", "Delete Destn" }) {
			Class[] types = new Class[] { Object.class, Object.class, Boolean.class, Object.class, Object.class,
					Boolean.class };

			public Class getColumnClass(int columnIndex) {
				return this.types[columnIndex];
			}
		});
		this.jTable2.setModel(new DefaultTableModel(new Object[0][], new String[] { "File/Folder", "Location" }));
		this.jTable3.setModel(new DefaultTableModel(new Object[0][], new String[] { "File/Folder", "Location" }));
	}

	public static void main(String[] args) {
		try {
			LookAndFeelInfo[] arr$ = UIManager.getInstalledLookAndFeels();
			int len$ = arr$.length;

			for (int i$ = 0; i$ < len$; ++i$) {
				LookAndFeelInfo info = arr$[i$];
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException var5) {
			Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, (String) null, var5);
		} catch (InstantiationException var6) {
			Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, (String) null, var6);
		} catch (IllegalAccessException var7) {
			Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, (String) null, var7);
		} catch (UnsupportedLookAndFeelException var8) {
			Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, (String) null, var8);
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				(new MainForm()).setVisible(true);
			}
		});
	}

	public class SourceMessageLoop implements Runnable {
		public void run() {
			MainForm.this.progressBar.setVisible(true);
			MainForm.this.progressBar.setIndeterminate(true);

			try {
				MainForm.this.progressBar.setString("Loading Source Files");
				File dir = new File(MainForm.this.jTextField1.getText());
				MainForm.this.Sourcewalkin(dir, 0);
				MainForm.this.progressBar.setString("Loading Destination Files");
				File dir1 = new File(MainForm.this.jTextField2.getText());
				MainForm.this.Destwalkin(dir1, 0);
				MainForm.this.progressBar.setString("Searching Duplicates");
				MainForm.this.FindDuplicates();
			} catch (Exception var3) {
			}

			MainForm.this.progressBar.setIndeterminate(false);
			MainForm.this.progressBar.setVisible(false);
			MainForm.this.jLabel2.setText(MainForm.this.jTable1.getRowCount() + " duplicate(s) found");
		}
	}
}