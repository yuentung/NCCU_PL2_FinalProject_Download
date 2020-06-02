import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RegisterPanel extends JPanel{
	
	private static final int FIELD_WIDTH = 10;
	private static final String[] enrollYearArray = {"105", "106", "107", "108"};
	private static final String[] majorInChArray = {"-請選擇科系-", "企管系", "資管系"};
	private static final String[] majorInNumArray = {"", "305", "306"};
	
	private JLabel studentIDLabel;
	private JTextField studentIDTextField;
	private JLabel majorLabel;
	private JComboBox majorComboBox;
	private JButton loginButton;
	private HashMap<String, String> majorMap;
	
	public RegisterPanel() {
		this.majorMap = new HashMap<>();
		for(int i = 0; i < this.majorInNumArray.length; i++) {
			this.majorMap.put(this.majorInNumArray[i], this.majorInChArray[i]);
		}
		createStudentIDComp();
		arrange();
	}
	
	// 1.建選系下拉式選單
	private void createStudentIDComp() {
		this.studentIDLabel = new JLabel("Student ID :", SwingConstants.RIGHT);
		this.studentIDTextField = new JTextField(FIELD_WIDTH);
		this.studentIDTextField.setHorizontalAlignment(JTextField.CENTER);
		
		class StudentIDTextFieldListener implements DocumentListener {
			
			private void doSomething() {
				String studentID = studentIDTextField.getText();
				String majorInNum;
				String majorInCh;
				if(studentID.length() == 9) {
					majorInNum = studentID.substring(3, 6);
					majorInCh = majorMap.get(majorInNum);
					if(majorInCh != null) {
						majorComboBox.setSelectedItem(majorInCh);
					}else {
						majorComboBox.setSelectedIndex(0);
					}
				}
			}
			
			public void changedUpdate(DocumentEvent event) {
				doSomething();
			}
			
			public void removeUpdate(DocumentEvent event) {
				doSomething();
			}
			
			public void insertUpdate(DocumentEvent event) {
				doSomething();
			}
			
		}
		
		DocumentListener studentIDTextFieldListener = new StudentIDTextFieldListener();
		this.studentIDTextField.getDocument().addDocumentListener(studentIDTextFieldListener);
		this.majorLabel = new JLabel("Major :", SwingConstants.RIGHT);
		this.majorComboBox = new JComboBox();
		for(String major:this.majorInChArray) {
			this.majorComboBox.addItem(major);
		}
		DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();
	    listRenderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER);
	    this.majorComboBox.setRenderer(listRenderer);
		this.loginButton = new JButton("Login");
		
		class LoginButtonListener implements ActionListener {
			
			public void actionPerformed(ActionEvent event) {
				String studentID = studentIDTextField.getText();
				String enrollYear = studentID.substring(0, 3);
				String majorInNum = studentID.substring(3, 6);
				String majorInCh1 = "";
				String errorOrNot = "";
				if(studentID.length() != 9) {
					errorOrNot += "很抱歉，您的學號長度有誤\n";
				}else {
					List<String> enrollYearList = Arrays.asList(enrollYearArray);
					if(!enrollYearList.contains(enrollYear)) {
						errorOrNot += "很抱歉，您的入學年度不在服務範圍\n";
					}
					List<String> majorInNumList = Arrays.asList(majorInNumArray);
					if(!majorInNumList.contains(majorInNum)) {
						errorOrNot += "很抱歉，您的主修科系不在服務範圍\n";
					}else {
						majorInCh1 = majorMap.get(majorInNum);
					}
				}
				String majorInCh2 = (String)majorComboBox.getSelectedItem();
				if(majorInCh2.equals("-請選擇科系-")) {
					errorOrNot += "請選擇您的科系";
				}
				if(errorOrNot.equals("")) {
					String[] majorArray = {majorInCh1, majorInCh2};
					setVisible(false);
					HomeFrame homeFrame = getFrame();
					homeFrame.add(new FinishedCoursePanel(studentID, majorArray));
					homeFrame.setSize(820, 820);
					homeFrame.setTitle("Course Registration Guide - FinishedCourse");
					homeFrame.setLocationRelativeTo(null);
				}else {
					JOptionPane.showMessageDialog(null, errorOrNot);
				}
			}
			
		}
		
		ActionListener loginButtonListener = new LoginButtonListener();
		this.loginButton.addActionListener(loginButtonListener);
		
	}
	
	private void arrange() {
		this.setLayout(new BorderLayout());
		JPanel leftPanel = new JPanel();
		leftPanel.setPreferredSize(new Dimension(170, 100));
		this.studentIDLabel.setPreferredSize(new Dimension(80, 20));
		this.majorLabel.setPreferredSize(new Dimension(80, 20));
		this.majorComboBox.setPreferredSize(new Dimension(133, 20));
		leftPanel.add(this.studentIDLabel);
		leftPanel.add(this.studentIDTextField);
		leftPanel.add(this.majorLabel);
		leftPanel.add(this.majorComboBox);
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(null);
		rightPanel.setPreferredSize(new Dimension(70, 100));
		this.loginButton.setBounds(0, 5, 54, 55);
		rightPanel.add(this.loginButton);
		this.add(leftPanel, BorderLayout.CENTER);
		this.add(rightPanel, BorderLayout.EAST);
	}
	
	private HomeFrame getFrame() {
		for(Frame frame:JFrame.getFrames()) {
			if(frame.getTitle().equals("Course Registration Guide - Login")) {
				HomeFrame homeFrame = (HomeFrame) frame;
				return homeFrame;
			}
		}
		return null;
	}
	
}