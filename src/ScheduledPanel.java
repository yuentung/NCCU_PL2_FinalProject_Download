
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ScheduledPanel extends JPanel{
	
	// Color
	private static final Color title = Color.decode("#E5CDFD");  // #CDFDFD
	private static final Color background = Color.decode("#7C5DB3");  // #30217A
	private static final Color inner = Color.decode("#F4EAFD");  // #E3FEFE
	
	// Image
	private ImageIcon icon;

	// Plan to take
	private JPanel mainPanel;
	private JPanel plantoTakePanel;			
	private JPanel plantoTakeTitlePanel;
	private JLabel plantoTakeLabel;	
	private JPanel plantoTakeMainPanel;
	private JPanel addCoursePanel;
	private JLabel addCourseLabel;
	private JTextField addCourseField ;
	private JButton addCourseBtn;
	private JPanel coursePanel;
	private ArrayList<String> planToTakeList;	// courseID
	private ArrayList<String> planToTakeNameList;	// courseName
	private ArrayList<JButton> courseBtnList;

	// Other Plan
	private JLabel otherPlanLabel;	
	private JPanel otherPlanPanel;			
	private JPanel otherPlanTitlePanel;
	private JPanel otherPlanMainPanel;	
	private JPanel addOtherPanel;
	private JLabel addOtherLabel;
	private JTextField addOtherTextField;
	private JButton addOtherBtn;
	private JPanel checkBoxPanel;
	private JButton saveOther;
	private JButton resetOther;
	private int listMaxSize;
	private ArrayList<ScheduledTimeList> otherPlanLists;	// 本學期其他事情的 list
	private ArrayList<ScheduledTimeList> otherPlanListsChanged;	// 本學期其他事情的 list
	private ArrayList<OtherPlan> otherPlan;	// 本學期其他事情的 list
	private ArrayList<String> timeID;
	private ArrayList<JCheckBox> checkboxList;
	private ArrayList<String> checkboxNameList;
	private ArrayList<JComboBox> timeComboList;
	
	// Next Page
	private JPanel nextPagePanel;
	private JButton nextPageButton;
	private ArrayList<String> weekdayArray;


	
	/* Constructor */
	
	public ScheduledPanel() {

		this.listMaxSize = 10;
		this.planToTakeList = new ArrayList<String>();
		this.planToTakeNameList = new ArrayList<String>();
		this.courseBtnList = new ArrayList<JButton>();
		this.otherPlanLists = new ArrayList<ScheduledTimeList>();
		this.otherPlanListsChanged = new ArrayList<ScheduledTimeList>();
		this.otherPlan = new ArrayList<OtherPlan>();
		this.timeID = new ArrayList<String>();
		this.checkboxList = new ArrayList<JCheckBox>();
		this.checkboxNameList = new ArrayList<String>();
		this.timeComboList = new ArrayList<JComboBox>();
		this.plantoTakeLabel = new JLabel("已決定要修習的課程");
		this.otherPlanLabel = new JLabel("已有其他安排的時間");
		this.weekdayArray = new ArrayList<String>();
		this.weekdayArray.add("一");
		this.weekdayArray.add("二");
		this.weekdayArray.add("三");
		this.weekdayArray.add("四");
		this.weekdayArray.add("五");		
		
		setLayout(new BorderLayout());
		add(createTitlePanel(),BorderLayout.NORTH);
		createMainPanel();
		add(mainPanel,BorderLayout.CENTER);
		add(createNextPagePanel(),BorderLayout.SOUTH);
		
	}
	
	/* Title Panel */
	
	private JPanel createTitlePanel() {
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new FlowLayout());
		titlePanel.setPreferredSize(new Dimension(800,55));
		arrange(titlePanel, "Background");
		icon = new ImageIcon("img/schedule.png", "Schedule");	
		JLabel label = new JLabel(icon);		
		titlePanel.add(label);
		return titlePanel;
	}
	
	
	/* Main Panel */
	
	private void createMainPanel() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout());
		arrange(mainPanel, "Background");
		
		mainPanel.add(createPlantoTakePanel());
		mainPanel.add(createOtherPlanPanel());
		
		saveOther.setEnabled(false);
		resetOther.setEnabled(false);
		checkBoxPanel.setEnabled(false);
		addOtherPanel.setEnabled(false);
		addOtherTextField.setEnabled(false);
		addOtherBtn.setEnabled(false);
	}
	

	
	
	/* plantoTakePanel
	 * 從 Course1082 課程中選出已決定要修的課（確切哪一門課，用 courseID）*/
	
	private JPanel createPlantoTakePanel() {
		plantoTakePanel = new JPanel();
		plantoTakePanel.setPreferredSize(new Dimension(800, 220));
		plantoTakePanel.setLayout(new BorderLayout());
		plantoTakePanel.setBorder(BorderFactory.createMatteBorder(5, 10, 5, 10, background));

		// title panel
		plantoTakeTitlePanel = new JPanel();
		arrange(plantoTakeTitlePanel, "Title");
		plantoTakeTitlePanel.add(plantoTakeLabel);
		
		// course panel		
		coursePanel = new JPanel();	
		arrange(coursePanel, "Inner");
		coursePanel.setLayout(new WrapLayout(WrapLayout.LEFT));
		
		// main panel
		plantoTakeMainPanel = new JPanel();
		plantoTakeMainPanel.setLayout(new BorderLayout());
		plantoTakeMainPanel.add(AddCoursePanel(), BorderLayout.NORTH);
		plantoTakeMainPanel.add(coursePanel, BorderLayout.CENTER);
		arrange(plantoTakeMainPanel, "Main");
		
		// panel
		plantoTakePanel.add(plantoTakeTitlePanel, BorderLayout.NORTH);
		plantoTakePanel.add(plantoTakeMainPanel, BorderLayout.CENTER);	
		plantoTakePanel.add(courseSaveResetPanel(), BorderLayout.SOUTH);				
		return plantoTakePanel;
	}
	
	// Add Course Panel	
	private JPanel AddCoursePanel() {
		addCoursePanel = new JPanel();
		addCoursePanel.setLayout(new FlowLayout());
		arrange(addCoursePanel, "Inner");
		addCourseLabel = new JLabel("請輸入課程代碼：");
		addCourseField = new JTextField(20);
		addCourseField.setHorizontalAlignment(JTextField.CENTER);
		
		addCourseBtn = new JButton("Add");
		class AddListener implements ActionListener{
			public void actionPerformed(ActionEvent event) {			
				String courseID = addCourseField.getText();
				try {
					String sql_match = String.format("SELECT * FROM Course1082_user WHERE subNum = '%s'", courseID);
					System.out.println(sql_match);
					ResultSet match = doSql(sql_match, "executeQuery");
					match.next();
					String courseName = match.getString("subNam");
					System.out.println("Course Name: " + courseName);
					if(planToTakeList.size() >= 8) {
						JOptionPane.showMessageDialog(null, "已輸入超過 8 門課程");	
					}
					else if(courseName.equals("")) {
						JOptionPane.showMessageDialog(null, "無法選擇該門課程");	
					}
					else if(planToTakeList.contains(courseID)) {
						JOptionPane.showMessageDialog(null, "不得加入重複課程");	
					}
					else {
						JButton courseButton = new JButton(courseName);
						planToTakeList.add(match.getString("subNum"));
						planToTakeNameList.add(courseName);
						class courseBtnListener implements ActionListener{
							public void actionPerformed(ActionEvent event) {
								courseButton.setEnabled(false);
								courseButton.setVisible(false);
								planToTakeList.remove(courseID);
								planToTakeNameList.remove(courseName);
								// Console
								System.out.println("Plan To Take List: " + planToTakeList);
								System.out.println("Plan To Take Name List: " + planToTakeNameList);

							}
						}
						courseButton.addActionListener(new courseBtnListener());
						courseButton.setEnabled(true);
						courseButton.setVisible(true);
						coursePanel.add(courseButton);
						courseBtnList.add(courseButton);
						revalidate();
					}									
				}
				catch(SQLException e) {
					JOptionPane.showMessageDialog(null, "本學期沒有開設這門課");	
					System.out.println("Add Course Error!");
				}
				finally{
					addCourseField.setText(null);
				}				
			}
		}
		addCourseBtn.addActionListener(new AddListener());
		
		addCoursePanel.add(addCourseLabel);
		addCoursePanel.add(addCourseField);
		addCoursePanel.add(addCourseBtn);
		return addCoursePanel;
	}

	// Save and Reset Panel
	private JPanel courseSaveResetPanel() {
		JPanel saveResetPanel = new JPanel();
		arrange(saveResetPanel, "Inner");
		JButton save = new JButton("Save");
		JButton reset = new JButton("Reset");

		// save
		class SaveListener implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				save.setEnabled(false);
				coursePanel.setEnabled(false);
				addCoursePanel.setEnabled(false);
				addCourseField.setEnabled(false);
				addCourseBtn.setEnabled(false);
				for(JButton courseButton: courseBtnList) {
					courseButton.setEnabled(false);
				}
				
				saveOther.setEnabled(true);
				resetOther.setEnabled(true);
				checkBoxPanel.setEnabled(true);
				addOtherPanel.setEnabled(true);
				addOtherTextField.setEnabled(true);
				addOtherBtn.setEnabled(true);

				for(JCheckBox check: checkboxList) {
					check.setEnabled(true);
				}
				for(JComboBox combo: timeComboList) {
					combo.setEnabled(true);
				}
				
				// Console
				System.out.println("Saved Plan To Take List: " + planToTakeList);
				System.out.println("Saved Plan To Take Name List: " + planToTakeNameList);
			}
		}
		save.addActionListener(new SaveListener());
		
		// reset
		class ResetListener implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				save.setEnabled(true);
				coursePanel.setEnabled(true);
				addCoursePanel.setEnabled(true);
				addCourseField.setEnabled(true);
				addCourseBtn.setEnabled(true);
				addCourseField.setText(null);;
				planToTakeList.clear();
				courseBtnList.clear();
				coursePanel.removeAll();
				
				saveOther.setEnabled(false);
				resetOther.setEnabled(false);
				checkBoxPanel.setEnabled(false);
				addOtherPanel.setEnabled(false);
				addOtherTextField.setEnabled(false);
				addOtherBtn.setEnabled(false);
				nextPageButton.setEnabled(false);

				for(JCheckBox check: checkboxList) {
					check.setEnabled(false);
				}
				for(JComboBox combo: timeComboList) {
					combo.setEnabled(false);
				}
				
				// Console
				System.out.println("Reseted Plan To Take List: " + planToTakeList);
				System.out.println("Reseted Plan To Take Name List: " + planToTakeNameList);
			}
		}
		reset.addActionListener(new ResetListener());
		
		saveResetPanel.add(save);
		saveResetPanel.add(reset);
		return saveResetPanel;
	}
	
		


	/* otherPlanPanel */
		
	private JPanel createOtherPlanPanel() {
		otherPlanPanel = new JPanel();
		otherPlanPanel.setPreferredSize(new Dimension(800,455));
		otherPlanPanel.setLayout(new BorderLayout());
		otherPlanPanel.setBorder(BorderFactory.createMatteBorder(0, 10, 0, 10, background));
		
		// title panel
		otherPlanTitlePanel = new JPanel();	
		arrange(otherPlanTitlePanel, "Title");
		otherPlanTitlePanel.add(otherPlanLabel);

		// combine panel
		createOtherPlanMainPanel();
		otherPlanPanel.add(otherPlanTitlePanel, BorderLayout.NORTH);
		otherPlanPanel.add(otherPlanMainPanel, BorderLayout.CENTER);
		otherPlanPanel.add(otherSaveResetPanel(), BorderLayout.SOUTH);
		
		return otherPlanPanel;
	}
	
	// Main Panel
	private void createOtherPlanMainPanel() {
		otherPlanMainPanel = new JPanel();
		otherPlanMainPanel.setLayout(new BorderLayout());
		arrange(otherPlanMainPanel, "Main");		

		checkBoxPanel = new JPanel();
		checkBoxPanel.setLayout(new GridLayout(listMaxSize,1));
		arrange(checkBoxPanel, "Inner");

		// combine panel
		otherPlanMainPanel.add(AddOtherPanel(), BorderLayout.NORTH);
		otherPlanMainPanel.add(checkBoxPanel, BorderLayout.CENTER);
		
	}
	
	// Single Plan Panel
	private JPanel createSingleOtherPanel(String name) {
		JPanel singleScheduledPanel = new JPanel();
		singleScheduledPanel.setLayout(new GridLayout(1,2));
		arrange(singleScheduledPanel, "Inner");
		ScheduledTimeList otherPlanList = new ScheduledTimeList(name);
		
		// check box for 已有安排的時間
		
		JCheckBox checkBox = new JCheckBox(name);
		checkboxList.add(checkBox);
		checkboxNameList.add(checkBox.getText());
		
		class CheckBoxListener implements ActionListener{
			public void actionPerformed(ActionEvent event) {
				if(checkBox.isSelected()) {
					otherPlanLists.add(otherPlanList);
				}
				else {
					String notSelected = checkBox.getText();
					if(otherPlanLists.size() > 0) {
						for(int i = 0; i < otherPlanLists.size(); i++) {
							if(otherPlanLists.get(i).getCourseName().equals(notSelected)) {
								otherPlanLists.remove(otherPlanLists.get(i));
							}							
						}
					}					
				}
				
				// Console
				otherPlanList.scheduledTimeDetail();
			}
		}
		checkBox.addActionListener(new CheckBoxListener());
		
		// combo box
		
		JPanel timePanel = new JPanel();
		arrange(timePanel, "Inner");
		JLabel dayLabel = new JLabel("星期");
		JLabel timeLabel = new JLabel("時間：");
		JLabel fromLabel = new JLabel("從");
		JLabel endLabel = new JLabel("到");
		
		// 星期
		JComboBox<String> dayComboBox = new JComboBox<String>();
		dayComboBox.addItem("一");
		dayComboBox.addItem("二");
		dayComboBox.addItem("三");
		dayComboBox.addItem("四");
		dayComboBox.addItem("五");
		dayComboBox.setSelectedIndex(-1);
		
		class DayComboBoxListener implements ActionListener{
			public void actionPerformed(ActionEvent event) {

				if(checkBox.isSelected()) {
					otherPlanList.setDay((String)dayComboBox.getSelectedItem());
					// Console
					otherPlanList.scheduledTimeDetail();
				}
				else {
					JOptionPane.showMessageDialog(null, "請先勾選事件");
					dayComboBox.setSelectedIndex(-1);
				}				
			}
		}
		dayComboBox.addActionListener(new DayComboBoxListener());
		
		// 開始
		JComboBox<String> fromComboBox = new JComboBox<String>();
		for(int i = 6; i < 22; i++) {
			fromComboBox.addItem(Integer.toString(i));
		}
		fromComboBox.setSelectedIndex(-1);
		
		class FromComboBoxListener implements ActionListener{
			public void actionPerformed(ActionEvent event) {
				if(dayComboBox.getSelectedIndex() == -1) {
					JOptionPane.showMessageDialog(null, "請先選擇星期");
					fromComboBox.setSelectedIndex(-1);
				}
				else {
					otherPlanList.setFromTime((String)fromComboBox.getSelectedItem());
					// Console
					otherPlanList.scheduledTimeDetail();
				}
			}
		}
		fromComboBox.addActionListener(new FromComboBoxListener());
		
		// 結束
		JComboBox<String> endComboBox = new JComboBox<String>();
		for(int i = 7; i <= 22; i++) {
			endComboBox.addItem(Integer.toString(i));
		}
		endComboBox.setSelectedIndex(-1);
		
		class EndComboBoxListener implements ActionListener{
			public void actionPerformed(ActionEvent event) {
				String from = (String)(fromComboBox.getSelectedItem());
				String end = (String)(endComboBox.getSelectedItem());
				if(fromComboBox.getSelectedIndex() == -1) {
					JOptionPane.showMessageDialog(null, "請先選擇開始時間");
					fromComboBox.setSelectedIndex(-1);
					endComboBox.setSelectedIndex(-1);
				}
				else if(Integer.parseInt(from) >= Integer.parseInt(end)) {	
					JOptionPane.showMessageDialog(null, "結束時間須晚於開始時間");
					fromComboBox.setSelectedIndex(-1);
					endComboBox.setSelectedIndex(-1);
				}
				else {
					otherPlanList.setFromTime((String)fromComboBox.getSelectedItem());
					otherPlanList.setEndTime((String)endComboBox.getSelectedItem());
					// Console
					otherPlanList.scheduledTimeDetail();
				}
			}
		}
		endComboBox.addActionListener(new EndComboBoxListener());
		
		// add combo boxes to timeComboList
		timeComboList.add(dayComboBox);
		timeComboList.add(fromComboBox);
		timeComboList.add(endComboBox);
		
		// combine panels
		timePanel.add(dayLabel);
		timePanel.add(dayComboBox);
		timePanel.add(timeLabel);
		timePanel.add(fromLabel);
		timePanel.add(fromComboBox);
		timePanel.add(endLabel);
		timePanel.add(endComboBox);
				
		singleScheduledPanel.add(checkBox);
		singleScheduledPanel.add(timePanel);
		
		return singleScheduledPanel;
	}
	
	// Add Panel
	private JPanel AddOtherPanel() {
		addOtherPanel = new JPanel();
		arrange(addOtherPanel, "Inner");
		
		addOtherLabel = new JLabel("事件名稱：");
		addOtherTextField = new JTextField(20);
		addOtherTextField.setHorizontalAlignment(JTextField.CENTER);
		
		addOtherBtn = new JButton("Add");
		class AddListener implements ActionListener{
			public void actionPerformed(ActionEvent event) {
				String name = addOtherTextField.getText();
				if(checkboxList.size() > 0) {
					if(checkboxList.size() >= listMaxSize) {
						JOptionPane.showMessageDialog(null, "已輸入超過 10 個事件");
					}
					else if(checkboxNameList.contains(name)) {
						JOptionPane.showMessageDialog(null, "請勿輸入重複的事件名稱");	
					}
					else {
						checkBoxPanel.add(createSingleOtherPanel(addOtherTextField.getText()));
						revalidate();
					}									
				}
				else {
					checkBoxPanel.add(createSingleOtherPanel(addOtherTextField.getText()));
					revalidate();
				}
				addOtherTextField.setText(null);				
			}
		}
		addOtherBtn.addActionListener(new AddListener());

		// combine panel
		addOtherPanel.add(addOtherLabel);
		addOtherPanel.add(addOtherTextField);
		addOtherPanel.add(addOtherBtn);
		
		return addOtherPanel;
	}
	
	// Save and Reset Panel
	private JPanel otherSaveResetPanel() {
		JPanel saveResetPanel = new JPanel();
		arrange(saveResetPanel, "Inner");
		saveOther = new JButton("Save");
		class SaveListener implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				// save
				boolean error = false;
				if(otherPlanLists.size() > 0) {
					for(int i = 0; i < otherPlanLists.size(); i++) {
						if(otherPlanLists.get(i).getEndTime() == null) {
							error = true;
						}
					}
				}
				if(!error) {
					saveOther.setEnabled(false);
					checkBoxPanel.setEnabled(false);
					addOtherPanel.setEnabled(false);
					addOtherTextField.setEnabled(false);
					addOtherBtn.setEnabled(false);
					for(JCheckBox check: checkboxList) {
						check.setEnabled(false);
					}
					for(JComboBox combo: timeComboList) {
						combo.setEnabled(false);
					}
					nextPageButton.setEnabled(true);
					
					// Console
					String listsDetail = "";
					for(ScheduledTimeList list: otherPlanLists) {
						listsDetail = listsDetail + list.scheduledTimeDetailStr() + ", ";
					}
					System.out.println("Saved Other Plan Lists: " + listsDetail);
					System.out.println("Saved Checkbox Name List: " + checkboxNameList);
				}
				else {
					JOptionPane.showMessageDialog(null, "勾選的事件中，有時間尚未填寫完整");
				}				
			}
		}
		saveOther.addActionListener(new SaveListener());
		
		resetOther = new JButton("Reset");
		class ResetListener implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				saveOther.setEnabled(true);
				checkBoxPanel.setEnabled(true);
				addOtherPanel.setEnabled(true);
				addOtherTextField.setEnabled(true);
				addOtherBtn.setEnabled(true);
				addOtherTextField.setText(null);;
				otherPlanLists.clear();
				checkboxList.clear();
				checkboxNameList.clear();
				timeComboList.clear();
				checkBoxPanel.removeAll();
				validate();
				repaint();
				nextPageButton.setEnabled(false);
				// Console
				String listsDetail = "";
				for(ScheduledTimeList list: otherPlanLists) {
					listsDetail = listsDetail + list.scheduledTimeDetailStr() + ", ";
				}
				System.out.println("Saved Other Plan Lists: " + listsDetail);
				System.out.println("Reseted Checkbox Name List: " + checkboxNameList);

			}
		}
		resetOther.addActionListener(new ResetListener());
		
		saveResetPanel.add(saveOther);
		saveResetPanel.add(resetOther);
		
		return saveResetPanel;
	}
	
	
	
	
	/* Next Page */
	
	// otherPlanLists 從時間轉成代號
	public void changeTimetoID() {
		if(otherPlanLists.size() > 0) {
			for(int i = 0; i < otherPlanLists.size(); i++) {
				ScheduledTimeList otherPlanChanged = new ScheduledTimeList(otherPlanLists.get(i).getCourseName());
				otherPlanChanged.setDay(otherPlanLists.get(i).getDay());
				switch(otherPlanLists.get(i).getFromTime()) {
				case "6": otherPlanChanged.setFromTime("A");break;
				case "7": otherPlanChanged.setFromTime("B");break;
				case "8": otherPlanChanged.setFromTime("1");break;
				case "9": otherPlanChanged.setFromTime("2");break;
				case "10": otherPlanChanged.setFromTime("3");break;
				case "11": otherPlanChanged.setFromTime("4");break;
				case "12": otherPlanChanged.setFromTime("C");break;
				case "13": otherPlanChanged.setFromTime("D");break;
				case "14": otherPlanChanged.setFromTime("5");break;
				case "15": otherPlanChanged.setFromTime("6");break;
				case "16": otherPlanChanged.setFromTime("7");break;
				case "17": otherPlanChanged.setFromTime("8");break;
				case "18": otherPlanChanged.setFromTime("E");break;
				case "19": otherPlanChanged.setFromTime("F");break;
				case "20": otherPlanChanged.setFromTime("G");break;
				case "21": otherPlanChanged.setFromTime("H");break;				
				}
				switch(otherPlanLists.get(i).getEndTime()) {
				case "7": otherPlanChanged.setEndTime("A");break;
				case "8": otherPlanChanged.setEndTime("B");break;
				case "9": otherPlanChanged.setEndTime("1");break;
				case "10": otherPlanChanged.setEndTime("2");break;
				case "11": otherPlanChanged.setEndTime("3");break;
				case "12": otherPlanChanged.setEndTime("4");break;
				case "13": otherPlanChanged.setEndTime("C");break;
				case "14": otherPlanChanged.setEndTime("D");break;
				case "15": otherPlanChanged.setEndTime("5");break;
				case "16": otherPlanChanged.setEndTime("6");break;
				case "17": otherPlanChanged.setEndTime("7");break;
				case "18": otherPlanChanged.setEndTime("8");break;
				case "19": otherPlanChanged.setEndTime("E");break;
				case "20": otherPlanChanged.setEndTime("F");break;
				case "21": otherPlanChanged.setEndTime("G");break;
				case "22": otherPlanChanged.setEndTime("H");break;				
				}
				otherPlanListsChanged.add(otherPlanChanged);
			}
		}
		// Console
		String listsDetail = "";
		for(ScheduledTimeList list: otherPlanListsChanged) {
			listsDetail = listsDetail + list.scheduledTimeDetailStr() + ", ";
		}
		System.out.println("Other Plan Lists Changed: " + listsDetail);		
	}
	
	// 時間代號 array list
	public void createTimeID() {
		timeID.add("A");
		timeID.add("B");
		for(int i = 1; i <= 4; i++) {
			timeID.add("" + i);
		}
		timeID.add("C");
		timeID.add("D");
		for(int i = 5; i <= 8; i++) {
			timeID.add("" + i);
		}
		timeID.add("E");
		timeID.add("F");
		timeID.add("G");
		timeID.add("H");
	}
	
	// 轉換完成的 list
	public void createOtherPlan() {
		changeTimetoID();
		createTimeID();
		if(otherPlanListsChanged.size() > 0) {
			for(int i = 0; i < otherPlanListsChanged.size(); i++) {
				OtherPlan singlePlan = new OtherPlan(otherPlanListsChanged.get(i).getCourseName(), otherPlanListsChanged.get(i).getDay());
				int indexFromID = timeID.indexOf(otherPlanListsChanged.get(i).getFromTime());
				int indexEndID = timeID.indexOf(otherPlanListsChanged.get(i).getEndTime());
				String time = "";
				for(int j = indexFromID; j <= indexEndID; j++) {
					time = time + timeID.get(j);
				}
				singlePlan.setTime(time);				
				otherPlan.add(singlePlan);
			}
		}
		// Console
		String listsDetail = "";
		for(OtherPlan list: otherPlan) {
			listsDetail = listsDetail + list.otherPlanDetail() + ", ";
		}
		System.out.println("Other Plan: " + listsDetail);		
		
	}
	
	
	// 把 一C二C三C 這種切開來
	private ArrayList<String> findTime(String courseTime) {
		ArrayList<Integer> dayIndexList = new ArrayList<Integer>();
		ArrayList<String> courseTimeList = new ArrayList<String>();
		for(String weekday: weekdayArray) {
			if(courseTime.contains(weekday)) {
				int index = courseTime.indexOf(weekday);
				dayIndexList.add(index);
			}
		}
		String time = "";
		for(int i = 0; i < dayIndexList.size(); i++) {
			if(i != dayIndexList.size() - 1) {
				time = courseTime.substring(dayIndexList.get(i), dayIndexList.get(i+1));
			}else {
				time = courseTime.substring(dayIndexList.get(i));
			}
			courseTimeList.add(time);
		}
		return courseTimeList;
	}

	
	// Next Page Panel
	private JPanel createNextPagePanel() {
		nextPagePanel = new JPanel();	
		arrange(nextPagePanel, "Background");
		nextPageButton = new JButton("Next Page");
		nextPageButton.setEnabled(false);
		
		class NextListener implements ActionListener{
			public void actionPerformed(ActionEvent event) {
								
				// 1. 已決定要修的課 PlanToTake
				System.out.println("\nNext Page: ");
				doSql("DELETE FROM PlanToTake", "execute");		// clear the table
				if(planToTakeList.size() > 0) {
					String where_subNums = String.format("subNum = '%s'", planToTakeList.get(0));
					for(int i = 1; i < planToTakeList.size(); i++) {
						where_subNums = where_subNums + String.format(" OR subNum = '%s'", planToTakeList.get(i));
					}
					String sql_insert1 = String.format("INSERT INTO PlanToTake SELECT DISTINCT * FROM Course1082 WHERE %s", where_subNums);
					System.out.println("＊已決定要修的課 PlanToTake＊\n" + sql_insert1);
					doSql(sql_insert1, "execute");						
				}
				
				// 2. 已有其他安排的時間 OtherPlan	
				System.out.println("＊已有其他安排的時間 Other Plan＊");
				createOtherPlan();				
				doSql("DELETE FROM OtherPlan", "execute");		// clear the table
				if(otherPlan.size() > 0) {
					for(int i = 0; i < otherPlan.size(); i++) {
						String sql_insert2 = String.format("INSERT INTO OtherPlan (subNam, subTime) VALUES ('%s', '%s')", otherPlan.get(i).getName(), otherPlan.get(i).getDay() + otherPlan.get(i).getTime());
						System.out.println(sql_insert2);
						doSql(sql_insert2, "execute");
					}
				}
				
				// 3. 更新 Course1082_user：排除 subNam 在 planToTakeNameList 裡的				
				if(planToTakeNameList.size() > 0) {
					String where_subNams = String.format("subNam = '%s'", planToTakeNameList.get(0));
					for(int i = 1; i < planToTakeNameList.size(); i++) {
						where_subNams = where_subNams + String.format(" OR subNam = '%s'", planToTakeNameList.get(i));
					}
					String sql_subNam = String.format("DELETE FROM Course1082_user WHERE %s", where_subNams);
					System.out.println("＊更新 Course1082_user＊\n" + sql_subNam);
					doSql(sql_subNam, "execute");						
				}
				
//				String sql_subNam = String.format("DELETE FROM Course1082_user WHERE Course1082_user.subNam = PlanToTake.subNam");
//				doSql(sql_subNam, "execute");						

				
				// 4. 更新 Course1082_user：排除 subTime 在 PlanToTake (database) 裡的
				if(planToTakeList.size() > 0) {
					for(int i = 0; i < planToTakeList.size(); i++) {
						ArrayList<String> courseToDelete = new ArrayList<String>();
						try {
							String sql_course = String.format("SELECT * FROM PlanToTake WHERE subNum = '%s'", planToTakeList.get(i));
							ResultSet course = doSql(sql_course, "executeQuery");
							course.next();
							String courseTime = course.getString("subTime");
							ArrayList<String> planToTakeDayTimeList= findTime(courseTime);
							for(int p = 0; p < planToTakeDayTimeList.size(); p++) {
								String planToTakeDayTime = planToTakeDayTimeList.get(p);
								String sql_dayContained = String.format("SELECT * FROM Course1082_user WHERE subTime LIKE '%s'",  "%" + planToTakeDayTime.substring(0, 1) + "%");
								ResultSet dayContained = doSql(sql_dayContained, "executeQuery");
								dayContained.next();
								while(dayContained.next()) {
									String dayTime = dayContained.getString("subTime");
									for(int j = 0; j < findTime(dayTime).size(); j++) {
										if(findTime(dayTime).get(j).contains(planToTakeDayTime.substring(0, 1))) {
											for(int k = 1; k < planToTakeDayTime.length(); k++) {
												if(findTime(dayTime).get(j).contains(planToTakeDayTime.substring(k - 1, k))) {
													String courseID = dayContained.getString("subNum");
													courseToDelete.add(courseID);
												}
											}
										}
									}
								}								
							}
							if(courseToDelete.size() > 0) {
								String sql_subTime = String.format("DELETE FROM Course1082_user WHERE subNum = '%s'", courseToDelete.get(0));
								for(int j = 1; j < courseToDelete.size(); j++) {
									sql_subTime = sql_subTime + String.format(" OR subNum = '%s'", "%" + courseToDelete.get(i));
								}
								doSql(sql_subTime, "execute");
								System.out.println("＊更新 Course1082_user from PlanToTake＊");
							}
						}
						catch(Exception e){
							System.out.println("Error: Get subTime from PlanToTake");
							e.printStackTrace();
						}
					}				
				}	
				
				// 5. 更新 Course1082_user：排除 subTime 在 otherPlan 裡的				
				if(otherPlan.size() > 0) {
					for(int i = 0; i < otherPlan.size(); i++) {
						ArrayList<String> courseToDelete = new ArrayList<String>();
						try {
							String sql_dayContained = String.format("SELECT * FROM Course1082_user WHERE subTime LIKE '%s'",  "%" + otherPlan.get(i).getDay() + "%");
							ResultSet dayContained = doSql(sql_dayContained, "executeQuery");
							dayContained.next();
							while(dayContained.next()) {
								String dayTime = dayContained.getString("subTime");
								for(int j = 0; j < findTime(dayTime).size(); j++) {
									if(findTime(dayTime).get(j).contains(otherPlan.get(i).getDay())) {
										for(int k = 1; k < otherPlan.get(i).getTime().length(); k++) {
											if(findTime(dayTime).get(j).contains(otherPlan.get(i).getTime().substring(k - 1, k))) {
												String courseID = dayContained.getString("subNum");
												courseToDelete.add(courseID);
											}
										}										
									}
								}																
							}
						}
						catch(Exception e) {
							System.out.println("Error: Delete Course1082_user from otherPlan");
							e.printStackTrace();
						}	
						if(courseToDelete.size() > 0) {
							String sql_subTime = String.format("DELETE FROM Course1082_user WHERE subNum = '%s'", courseToDelete.get(0));
							for(int j = 1; j < courseToDelete.size(); j++) {
								sql_subTime = sql_subTime + String.format(" OR subNum = '%s'", "%" + courseToDelete.get(i));
							}
							doSql(sql_subTime, "execute");
							System.out.println("＊更新 Course1082_user from OtherPlan＊");
						}
					}				
				}
				
				// 6. Next Panel
//				setVisible(false);
//				HomeFrame homeFrame = getFrame();
//				homeFrame.add(new SelectCoursePanel());
//				homeFrame.setSize(760, 720);
//				homeFrame.setTitle("Course Registration Guide - SelectCourse");
//				homeFrame.setLocationRelativeTo(null);
			
			}
		}
		nextPageButton.addActionListener(new NextListener());
		
		nextPagePanel.add(nextPageButton);
		return nextPagePanel;		
	}
	
	
	/* Arrange Panels */	

	public void arrange(JPanel panel, String type) {
		if(type.equals("Title")) {
			panel.setBackground(title);
		}
		else if(type.equals("Main")) {
			panel.setBorder(BorderFactory.createLineBorder(Color.white, 5, true));
		}
		else if(type.equals("Inner")) {
			panel.setBackground(inner);
		}
		else if(type.equals("Background")) {
			panel.setBackground(background);
		}
	}
	
	
	/* 寫 SQL */
	private ResultSet doSql(String sql, String method) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection("jdbc:mysql://140.119.19.73:9306/TG06?characterEncoding=UTF-8", "TG06", "i8p3q6");
			Statement statement = connection.createStatement();
			if(method.equals("execute")) {
				statement.execute(sql);
			}else if(method.equals("executeQuery")) {
				ResultSet resultSet = statement.executeQuery(sql);
				return resultSet;
			}
			return null;
		}catch(Exception e) {
			System.out.println("Error occur!");
			e.printStackTrace();
			return null;
		}
	}
	
	
	/* getFrame */
	private HomeFrame getFrame() {
		for(Frame frame:JFrame.getFrames()) {
			if(frame.getTitle().equals("Course Registration Guide - ScheduleSetup")) {
				HomeFrame homeFrame = (HomeFrame) frame;
				return homeFrame;
			}
		}
		return null;
	}
	

	
}
