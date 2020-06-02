import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Dimension;
import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.DefaultListCellRenderer;

public class FinishedCoursePanel extends JPanel {
	
	private static final String[] courseTypeList = {"Required", "General", "Selective"};
	
	private String studentID;
	private String[] majorArray;
	private int enrollYear;
	private int enrollSemester;
	private int currentYear;
	private int currentSemester;
	private int previousYear;
	private int previousSemester;
	private JPanel leftPanel;
	private JScrollPane leftScrollPane;
	private ArrayList<JPanel> semesterPanelList;
	private ArrayList<JLabel> semesterLabelList;
	private ArrayList<JPanel> leftCoursePanelList;
	private ArrayList<JScrollPane> leftCourseScrollPaneList;
	private ArrayList<ArrayList<JButton>> leftCourseBtnList;
	private ArrayList<JButton> saveBtnList;
	private ArrayList<JButton> updateBtnList;
	private JPanel rightPanel;
	private ArrayList<JPanel> coursePanelList;
	private ArrayList<JButton> courseTypeBtnList;
	private JComboBox generalComboBox;
	private JTextField searchTextField;
	private JButton searchBtn;
	private ArrayList<JPanel> rightCoursePanelList;
	private ArrayList<JScrollPane> rightCourseScrollPaneList;
	private ArrayList<ArrayList<JButton>> rightCourseBtnList;
	private JButton nextBtn;
	private String sql1;
	private String lastCourseType;
	
	public FinishedCoursePanel(String studentID, String[] majorArray) {
		this.studentID = studentID;
		this.majorArray = majorArray;
		this.enrollYear = Integer.parseInt(this.studentID.substring(0, 3));
		this.enrollSemester = 1;
		this.currentYear = Calendar.getInstance().get(Calendar.YEAR); // 2020年
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH); // 4月
		if(currentMonth >= 8 || currentMonth <= 1) {
			this.currentYear = this.currentYear - 1911; // 109學年
			this.currentSemester = 1; // 第1學期
			this.previousYear = this.currentYear - 1;
			this.previousSemester = 2;
		}else {
			this.currentYear = this.currentYear - 1911 - 1; // 108學年
			this.currentSemester = 2; // 第2學期
			this.previousYear = this.currentYear;
			this.previousSemester = 1;
		}
		this.createLeftAndRightPanel();
		this.createSemesterPanelList();
		this.createCoursePanelList();
		this.arrange("Required");
		JScrollBar leftScrollBar = this.leftScrollPane.getVerticalScrollBar();
		leftScrollBar.setValue(leftScrollBar.getMaximum());
	}
	
	private void createLeftAndRightPanel(){
		this.leftPanel = new JPanel();
		this.leftScrollPane = new JScrollPane(this.leftPanel);
		this.rightPanel = new JPanel();
		this.nextBtn = new JButton("＞");
		
		class NextBtnListener implements ActionListener {
			
			public void actionPerformed(ActionEvent event) {
				createTable();
				setVisible(false);
				HomeFrame homeFrame = getFrame();
				homeFrame.add(new ScheduledPanel());
				homeFrame.setSize(820, 805);
				homeFrame.setTitle("Course Registration Guide");
				homeFrame.setLocationRelativeTo(null);
				
			}
			
		}
		
		ActionListener nextBtnListener = new NextBtnListener();
		this.nextBtn.addActionListener(nextBtnListener);
		this.nextBtn.setEnabled(false);
	}
	
	private void createSemesterPanelList() {
		this.semesterPanelList = new ArrayList<>();
		this.semesterLabelList = new ArrayList<>();
		this.leftCoursePanelList = new ArrayList<>();
		this.leftCourseScrollPaneList = new ArrayList<>();
		this.leftCourseBtnList = new ArrayList<>();
		this.saveBtnList = new ArrayList<>();
		this.updateBtnList = new ArrayList<>();
		try {
			String sql = String.format("SELECT semester FROM SC WHERE studentID = '%s' GROUP BY semester", studentID);
			ResultSet resultSet = this.doSql(sql, "executeQuery");
			ArrayList<String> yearAndSemesterList = new ArrayList<>();
			while(resultSet.next()) {
				yearAndSemesterList.add(resultSet.getString("semester"));
			}
			if(yearAndSemesterList.size() != 0) {
				int lastYear = Integer.parseInt(yearAndSemesterList.get(yearAndSemesterList.size() - 1).substring(0, 3));
				int lastSemester = Integer.parseInt(yearAndSemesterList.get(yearAndSemesterList.size() - 1).substring(3));
				for(int i = this.enrollYear; i <= lastYear; i++) {
					for(int j = this.enrollSemester; j <= 2; j++) {
						this.createSemesterPanel(i + "" + j);
						if(i == lastYear && j == lastSemester) {
							if(lastYear != this.previousYear || lastSemester != this.previousSemester) {
								if(j == 1) {
									this.createSemesterPanel(i + "" + 2);
								}else {
									this.createSemesterPanel((i + 1) + "" + 1);
								}
							}else {
								this.nextBtn.setEnabled(true);
							}
							break;
						}
					}
				}
			}else {
				this.createSemesterPanel(this.enrollYear + "" + this.enrollSemester);
			}
		}catch(Exception e) {
			System.out.println("Error occur!");
			e.printStackTrace();
		}
	}
	
	private void createSemesterPanel(String yearAndSemester) {
		this.lastCourseType = "Required";
		int year = Integer.parseInt(yearAndSemester.substring(0, 3));
		int semester = Integer.parseInt(yearAndSemester.substring(3));
		JPanel semesterPanel = new JPanel();
		JLabel semesterLabel = new JLabel(yearAndSemester, SwingConstants.CENTER);
		JPanel leftCoursePanel = new JPanel();
		ArrayList<JButton> leftCourseBtnListInList = new ArrayList<JButton>();
		try {			
			String sql = String.format("SELECT courseName FROM SC WHERE studentID = '%s' AND semester = '%s'", studentID, yearAndSemester);
			ResultSet resultSet = this.doSql(sql, "executeQuery");
			while(resultSet.next()) {
				String courseName = resultSet.getString("courseName");
				JButton clickedBtn = new JButton(courseName);
				if(courseName.equals("")) {
					clickedBtn.setVisible(false);
				}else {
					clickedBtn.setEnabled(false);
				}
				leftCourseBtnListInList.add(clickedBtn);
			}			
		}catch(Exception e) {
			System.out.println("Error occur!");
			e.printStackTrace();
		}
		JScrollPane leftCourseScrollPane = new JScrollPane(leftCoursePanel);
		JButton saveBtn = new JButton("Save");
		JButton updateBtn = new JButton("Update");
		if(leftCourseBtnListInList.size() != 0) {
			saveBtn.setEnabled(false);
		}else {
			updateBtn.setEnabled(false);
		}
		
		class SaveBtnListener implements ActionListener {
			
			private void saveCourseToDatabase() {
				try {
					int index = semesterPanelList.indexOf(semesterPanel);
					String sql;
					if(leftCourseBtnList.get(index).size() == 0) {
						sql = String.format("INSERT INTO SC(studentID, semester, courseName) VALUES('%s', '%s', '%s')", studentID, semesterLabel.getText(), "");
						doSql(sql, "execute");
					}else {
						for(JButton clickedCourseBtn:leftCourseBtnList.get(index)) {
							String courseName = clickedCourseBtn.getText();
							sql = String.format("INSERT INTO SC(studentID, semester, courseName) VALUES('%s', '%s', '%s')", studentID, semesterLabel.getText(), courseName);
							doSql(sql, "execute");
							clickedCourseBtn.setEnabled(false);
						}
					}
				}catch(Exception e) {
					System.out.println("Error occur!");
					e.printStackTrace();
				}
			}
			
			private void refreshCourseList() {
				coursePanelList.clear();
				courseTypeBtnList.clear();
				rightCoursePanelList.clear();
				rightCourseScrollPaneList.clear();
				rightCourseBtnList.clear();
				if(year != previousYear || semester != previousSemester) {
					if(semester == 1) {
						for(String courseType:courseTypeList) {
							createCoursePanel(courseType, year + "" + 2);
						}
					}else {
						for(String courseType:courseTypeList) {
							createCoursePanel(courseType, (year + 1) + "" + 1);
						}
					}
				}else {
					for(String courseType:courseTypeList) {
						createCoursePanel(courseType, (year + 2) + "" + 1);
					}
					generalComboBox.setEnabled(false);
					searchTextField.setEnabled(false);
					searchBtn.setEnabled(false);
				}
			}
			
			public void actionPerformed(ActionEvent event) {
				saveBtn.setEnabled(false);
				updateBtn.setEnabled(true);
				if(year < previousYear) {
					if(semester == 1) {
						saveCourseToDatabase();
						createSemesterPanel(year + "" + 2);
						refreshCourseList();
					}else if(semester == 2) {
						saveCourseToDatabase();
						createSemesterPanel((year + 1) + "" + 1);
						refreshCourseList();
					}
				}else if(year == previousYear){
					if(semester != previousSemester) {
						saveCourseToDatabase();
						createSemesterPanel(year + "" + 2);
						refreshCourseList();
					}else {
						saveCourseToDatabase();
						refreshCourseList();
						nextBtn.setEnabled(true);
					}
				}
				arrange("Required");
				JScrollBar leftScrollBar = leftScrollPane.getVerticalScrollBar();
				leftScrollBar.setValue(leftScrollBar.getMaximum());
			}
			
		}
		
		ActionListener saveBtnListener = new SaveBtnListener();
		saveBtn.addActionListener(saveBtnListener);
		
		class UpdateBtnListener implements ActionListener {
			
			private void refreshCourseList() {
				coursePanelList.clear();
				courseTypeBtnList.clear();
				rightCoursePanelList.clear();
				rightCourseScrollPaneList.clear();
				rightCourseBtnList.clear();
				for(String courseType:courseTypeList) {
					createCoursePanel(courseType, yearAndSemester);
				}
			}
			
			public void actionPerformed(ActionEvent event) {
				updateBtn.setEnabled(false);
				saveBtn.setEnabled(true);
				if(year == previousYear && semester == previousSemester) {
					nextBtn.setEnabled(false);
				}
				int index = semesterPanelList.indexOf(semesterPanel);
				int size = semesterPanelList.size();
				int removeIndex;
				for(int i = 0; i < size; i++) {
					removeIndex = semesterPanelList.size() - 1;
					if(i > index) {
						try {
							String sql = String.format("DELETE FROM SC WHERE studentID = '%s' AND semester = '%s'", studentID, semesterLabelList.get(removeIndex).getText());
							doSql(sql, "execute");
						}catch(Exception e) {
							System.out.println("Error occur!");
							e.printStackTrace();
						}
						semesterPanelList.remove(removeIndex);
						semesterLabelList.remove(removeIndex);
						leftCoursePanelList.remove(removeIndex);
						leftCourseScrollPaneList.remove(removeIndex);
						leftCourseBtnList.remove(removeIndex);
						saveBtnList.remove(removeIndex);
						updateBtnList.remove(removeIndex);
					}else if(i == index) {
						try {
							String sql = String.format("DELETE FROM SC WHERE studentID = '%s' AND semester = '%s'", studentID, semesterLabelList.get(i).getText());
							doSql(sql, "execute");
						}catch(Exception e) {
							System.out.println("Error occur!");
							e.printStackTrace();
						}
					}
				}
				leftCourseBtnList.get(index).clear();
				refreshCourseList();
				arrange("Required");
			}
			
		}
		
		ActionListener updateBtnListener = new UpdateBtnListener();
		updateBtn.addActionListener(updateBtnListener);
		this.semesterPanelList.add(semesterPanel);
		this.leftCourseBtnList.add(leftCourseBtnListInList);
		this.semesterLabelList.add(semesterLabel);
		this.leftCoursePanelList.add(leftCoursePanel);
		this.leftCourseScrollPaneList.add(leftCourseScrollPane);
		this.saveBtnList.add(saveBtn);
		this.updateBtnList.add(updateBtn);
	}

	private void createCoursePanelList() {
		this.coursePanelList = new ArrayList<>();
		this.courseTypeBtnList = new ArrayList<>();
		this.rightCoursePanelList = new ArrayList<>();
		this.rightCourseScrollPaneList = new ArrayList<>();
		this.rightCourseBtnList = new ArrayList<>();
		try {
			String sql = String.format("SELECT semester FROM SC WHERE studentID = '%s' GROUP BY semester", studentID);
			ResultSet resultSet = doSql(sql, "executeQuery");
			ArrayList<String> yearAndSemesterList = new ArrayList<>();
			while(resultSet.next()) {
				yearAndSemesterList.add(resultSet.getString("semester"));
			}
			if(yearAndSemesterList.size() != 0) {
				int lastYear = Integer.parseInt(yearAndSemesterList.get(yearAndSemesterList.size() - 1).substring(0, 3));
				int lastSemester = Integer.parseInt(yearAndSemesterList.get(yearAndSemesterList.size() - 1).substring(3));
				for(int i = this.enrollYear; i <= lastYear; i++) {
					for(int j = this.enrollSemester; j <= 2; j++) {
						if(i == lastYear && j == lastSemester) {
							if(lastYear != this.previousYear || lastSemester != this.previousSemester) {
								if(j == 1) {
									for(String courseType:this.courseTypeList) {
										this.createCoursePanel(courseType, i + "" + 2);
									}
								}else {
									for(String courseType:this.courseTypeList) {
										this.createCoursePanel(courseType, (i + 1) + "" + 1);
									}
								}
							}else {
								for(String courseType:this.courseTypeList) {
									this.createCoursePanel(courseType, (i + 2) + "" + 1);
								}
								generalComboBox.setEnabled(false);
								searchTextField.setEnabled(false);
								searchBtn.setEnabled(false);
							}
						}
					}
				}
			}else {
				for(String courseType:this.courseTypeList) {
					this.createCoursePanel(courseType, this.enrollYear + "" + this.enrollSemester);
				}
			}
		}catch(Exception e) {
			System.out.println("Error occur!");
			e.printStackTrace();
		}
	}
	
	// 1.各系必修、跨領域通識(資料筆數變少？)資料表尚未建置完成 2.尚未判斷是否修習擋修 3.學年課因為上下學期名稱相同，選過一次之後就會被篩掉 4.updateBtn功能需要再調整 5.選修判斷在轉系生、雙主修會有誤
	private void createCoursePanel(String courseType, String yearAndSemester) {
		String year = yearAndSemester.substring(0, 3);
		String semester = yearAndSemester.substring(3);
		JPanel coursePanel = new JPanel();
		JButton courseTypeBtn = new JButton(courseType);
		
		class CourseTypeBtnListener implements ActionListener {
			
			public void actionPerformed(ActionEvent event) {
				lastCourseType = courseType;
				for(int i = 0; i < coursePanelList.size(); i++) {
					if(courseTypeList[i].equals(courseType)) {
						coursePanelList.get(i).setPreferredSize(new Dimension(295, 670));
						rightCourseScrollPaneList.get(i).setVisible(true);
					}else {
						coursePanelList.get(i).setPreferredSize(new Dimension(295, 30));
						rightCourseScrollPaneList.get(i).setVisible(false);
					}
				}
				validate();
				repaint();
			}
			
		}
		
		ActionListener courseTypeBtnListener = new CourseTypeBtnListener();
		courseTypeBtn.addActionListener(courseTypeBtnListener);
		try {
			switch(courseType) {
			case "Required":
				this.sql1 = String.format("SELECT subNam FROM Course WHERE y = '%s' AND s = '%s' AND subNam NOT IN (SELECT courseName FROM SC WHERE studentID = '%s') AND subNam IN (SELECT courseName FROM RequiredCourse WHERE (", year, semester, this.studentID);
				int i = 0;
				for(String major:majorArray) {
					if(i == 0) {
						this.sql1 += String.format("%s = 1", major);
					}else {
						this.sql1 += String.format(" OR %s = 1", major);
					}
					i++;
				}
				this.sql1 += ")) GROUP BY subNam";
				break;
			case "General":
				this.generalComboBox = new JComboBox();
				this.generalComboBox.addItem("---請選擇通識類型---");
				this.generalComboBox.addItem("中文通識");
				this.generalComboBox.addItem("外文通識");
				this.generalComboBox.addItem("人文通識");
				this.generalComboBox.addItem("社會通識");
				this.generalComboBox.addItem("自然通識");
				this.generalComboBox.addItem("(105、106學年)跨領域通識");
				this.generalComboBox.addItem("書院通識");
				DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();
			    listRenderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER);
			    this.generalComboBox.setRenderer(listRenderer);
			    this.sql1 = String.format("SELECT subNam FROM Course WHERE y = '%s' AND s = '%s' AND lmtKind = '0'", year, semester);
				
				class GeneralComboBoxListener implements ActionListener {
					
					public void actionPerformed(ActionEvent event) {
						String selectedType = (String)generalComboBox.getSelectedItem();
						sql1 = String.format("SELECT subNam FROM Course WHERE y = '%s' AND s = '%s' AND lmtKind LIKE '%%%s%%' AND subNam NOT IN (SELECT courseName FROM SC WHERE studentID = '%s')", year, semester, selectedType, studentID);
						for(String major:majorArray) {
							sql1 += String.format(" AND subNam NOT IN (SELECT courseName FROM RequiredCourse WHERE %s = 1)", major);
						}
						sql1 += " GROUP BY subNam";
						rightCoursePanelList.remove(1);
						rightCourseScrollPaneList.remove(1);
						rightCourseBtnList.remove(1);
						rightCoursePanelList.remove(1);
						rightCourseScrollPaneList.remove(1);
						rightCourseBtnList.remove(1);
						query(sql1, courseType);
						query(String.format("SELECT subNam FROM Course WHERE y = '%s' AND s = '%s' AND lmtKind = '0'", year, semester), "Selective");
						arrange(courseType);
					}
					
				}
				
				ActionListener generalComboBoxListener = new GeneralComboBoxListener();
				this.generalComboBox.addActionListener(generalComboBoxListener);
				break;
			case "Selective":
				this.searchTextField = new JTextField();
				this.searchTextField.setHorizontalAlignment(JTextField.CENTER);
				this.searchBtn = new JButton("Search");
				this.sql1 = String.format("SELECT subNam FROM Course WHERE y = '%s' AND s = '%s' AND lmtKind = '0'", year, semester);
				
				class SearchBtnListener implements ActionListener {
					
					public void actionPerformed(ActionEvent event) {
						if(searchTextField.getText().equals("")) {
							sql1 = String.format("SELECT subNam FROM Course WHERE y = '%s' AND s = '%s' AND subNam LIKE '找不到'", year, semester);
						}else {
//							sql1 = String.format("SELECT subNam FROM Course WHERE y = '%s' AND s = '%s' AND subNam NOT IN (SELECT courseName FROM SC WHERE studentID = '%s') AND subNam NOT IN (SELECT subNam FROM Course WHERE lmtKind = '%%%s%%')", year, semester, studentID, "通識");
							sql1 = String.format("SELECT subNam FROM Course WHERE y = '%s' AND s = '%s' AND subNam NOT IN (SELECT courseName FROM SC WHERE studentID = '%s')", year, semester, studentID);
//							for(String major:majorArray) {
//								sql1 += String.format(" AND subNam NOT IN (SELECT courseName FROM RequiredCourse WHERE %s = 1)", major);
//							}
							sql1 += String.format(" AND subNam LIKE '%%%s%%' GROUP BY subNam", searchTextField.getText());
						}
						rightCoursePanelList.remove(2);
						rightCourseScrollPaneList.remove(2);
						rightCourseBtnList.remove(2);
						query(sql1, courseType);
						arrange(courseType);
					}
					
				}
				
				ActionListener searchBtnListener = new SearchBtnListener();
				this.searchBtn.addActionListener(searchBtnListener);
				break;
			}
			query(sql1, courseType);
			this.coursePanelList.add(coursePanel);
			this.courseTypeBtnList.add(courseTypeBtn);
		}catch(Exception e) {
			System.out.println("Error occur!");
			e.printStackTrace();
		}
	}
	
	private void query(String sql, String courseType) {
		System.out.println(sql);
		JPanel rightCoursePanel = new JPanel();
		ArrayList<JButton> rightCourseBtnListInList = new ArrayList<>();
		try {
			ResultSet resultSet = doSql(sql, "executeQuery");
			while(resultSet.next()) {
				String courseName = resultSet.getString("subNam");
				System.out.println(courseName);
				JButton courseBtn = new JButton(courseName);
				courseBtn.setPreferredSize(new Dimension(270, 30));
				
				class CourseBtnListener implements ActionListener {
					
					public void actionPerformed(ActionEvent event) {
						courseBtn.setEnabled(false);
						int index = leftCoursePanelList.size() - 1;
						JButton clickedCourseBtn = new JButton(courseBtn.getText());
						
						class ClickedCourseBtnListener implements ActionListener {
							
							public void actionPerformed(ActionEvent event) {
								courseBtn.setEnabled(true);
								leftCourseBtnList.get(index).remove(clickedCourseBtn);
								arrange(lastCourseType);
								JScrollBar leftCoursePanelScrollBar = leftCourseScrollPaneList.get(index).getVerticalScrollBar();
								leftCoursePanelScrollBar.setValue(leftCoursePanelScrollBar.getMaximum());
							}
							
						}
						
						ActionListener clickedCourseBtnListener = new ClickedCourseBtnListener();
						clickedCourseBtn.addActionListener(clickedCourseBtnListener);
						leftCourseBtnList.get(index).add(clickedCourseBtn);
						arrange(lastCourseType);
						JScrollBar leftCoursePanelScrollBar = leftCourseScrollPaneList.get(index).getVerticalScrollBar();
						leftCoursePanelScrollBar.setValue(leftCoursePanelScrollBar.getMaximum());
					}
					
				}
				
				ActionListener courseBtnListener = new CourseBtnListener();
				courseBtn.addActionListener(courseBtnListener);
				rightCourseBtnListInList.add(courseBtn);
			}
			JScrollPane rightScrollPane = new JScrollPane(rightCoursePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			this.rightCoursePanelList.add(rightCoursePanel);
			this.rightCourseScrollPaneList.add(rightScrollPane);
			this.rightCourseBtnList.add(rightCourseBtnListInList);
		}catch(Exception e) {
			System.out.println("Error occur!");
			e.printStackTrace();
		}
	}
	
	private void createTable() {
		doSql("DELETE FROM Course1082_user", "execute");
		String sql = String.format("INSERT INTO Course1082_user SELECT * FROM Course WHERE y = '%s' AND s = '%s' AND subNam NOT IN (SELECT courseName FROM SC WHERE studentID = '%s')", this.currentYear, this.currentSemester, studentID);
		doSql(sql, "execute");
	}
	
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
	
	private void arrange(String currentCourseType) {
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.setBackground(new Color(35, 37, 52));
		this.leftPanel.removeAll();
		this.leftPanel.setPreferredSize(new Dimension(480, this.semesterPanelList.size() * 165));
		this.leftPanel.setBackground(new Color(35, 37, 52));
 		this.leftPanel.setAutoscrolls(true);
 		this.leftScrollPane.setPreferredSize(new Dimension(500, 790));
 		this.leftScrollPane.setBackground(new Color(35, 37, 52));
// 		this.leftScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
 		this.leftScrollPane.setBorder(BorderFactory.createLineBorder(new Color(35, 37, 52), 1, true));
		for(int i = 0; i < this.semesterPanelList.size(); i++) {
			this.semesterPanelList.get(i).setLayout(new FlowLayout(FlowLayout.RIGHT));
			this.semesterPanelList.get(i).setPreferredSize(new Dimension(480, 160));
			this.semesterPanelList.get(i).setBackground(new Color(35, 37, 52));
//			this.semesterPanelList.get(i).setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
			this.semesterLabelList.get(i).setPreferredSize(new Dimension(470, 20));
			this.semesterLabelList.get(i).setForeground(Color.WHITE);
			this.semesterLabelList.get(i).setBackground(new Color(55, 60, 85));
			this.semesterLabelList.get(i).setBorder(BorderFactory.createLineBorder(new Color(55, 60, 85), 1, true));
			this.semesterLabelList.get(i).setOpaque(true);
			this.leftCoursePanelList.get(i).setLayout(new WrapLayout(WrapLayout.LEFT));
			this.leftCoursePanelList.get(i).setBackground(new Color(55, 60, 85));
			this.leftCoursePanelList.get(i).setAutoscrolls(true);
			this.leftCoursePanelList.get(i).removeAll();
			for(JButton clickedCourseBtn:this.leftCourseBtnList.get(i)) {
				this.leftCoursePanelList.get(i).add(clickedCourseBtn);
			}
			this.leftCourseScrollPaneList.get(i).setPreferredSize(new Dimension(470, 100));
			this.leftCourseScrollPaneList.get(i).setBackground(new Color(55, 60, 85));
			this.leftCourseScrollPaneList.get(i).setBorder(BorderFactory.createLineBorder(new Color(55, 60, 85), 1, true));
			this.saveBtnList.get(i).setPreferredSize(new Dimension(50, 20));
			this.saveBtnList.get(i).setForeground(Color.WHITE);
			this.saveBtnList.get(i).setBackground(new Color(55, 60, 85));
			this.saveBtnList.get(i).setBorder(BorderFactory.createLineBorder(new Color(55, 60, 85), 1, true));
			this.saveBtnList.get(i).setOpaque(true);
			this.updateBtnList.get(i).setPreferredSize(new Dimension(50, 20));
			this.updateBtnList.get(i).setForeground(Color.WHITE);
			this.updateBtnList.get(i).setBackground(new Color(55, 60, 85));
			this.updateBtnList.get(i).setBorder(BorderFactory.createLineBorder(new Color(55, 60, 85), 1, true));
			this.updateBtnList.get(i).setOpaque(true);
			this.semesterPanelList.get(i).add(this.semesterLabelList.get(i));
			this.semesterPanelList.get(i).add(this.leftCourseScrollPaneList.get(i));
			this.semesterPanelList.get(i).add(this.saveBtnList.get(i));
			this.semesterPanelList.get(i).add(this.updateBtnList.get(i));
			this.leftPanel.add(this.semesterPanelList.get(i));
		}
		this.add(this.leftScrollPane);
		this.rightPanel.removeAll();
		this.rightPanel.setPreferredSize(new Dimension(300, 790));
		this.rightPanel.setBackground(new Color(35, 37, 52));
		this.rightPanel.setBorder(BorderFactory.createLineBorder(new Color(35, 37, 52), 1, true));
		for(int i = 0; i < this.coursePanelList.size(); i++) {
			if(currentCourseType.equals(courseTypeList[i])) {
				this.coursePanelList.get(i).setPreferredSize(new Dimension(295, 670));
			}else {
				this.coursePanelList.get(i).setPreferredSize(new Dimension(295, 30));
				this.rightCourseScrollPaneList.get(i).setVisible(false);
			}
			this.coursePanelList.get(i).setLayout(new FlowLayout());
			this.coursePanelList.get(i).setBackground(new Color(35, 37, 52));
//			this.coursePanelList.get(i).setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
			this.courseTypeBtnList.get(i).setPreferredSize(new Dimension(285, 20));
			this.courseTypeBtnList.get(i).setForeground(Color.WHITE);
			this.courseTypeBtnList.get(i).setBackground(new Color(55, 60, 85));
			this.courseTypeBtnList.get(i).setBorder(BorderFactory.createLineBorder(new Color(55, 60, 85), 1, true));
			this.courseTypeBtnList.get(i).setContentAreaFilled(false);
			this.courseTypeBtnList.get(i).setOpaque(true);
			this.rightCoursePanelList.get(i).setLayout(new WrapLayout(WrapLayout.LEFT));
			this.rightCoursePanelList.get(i).setBackground(new Color(55, 60, 85));
			this.rightCoursePanelList.get(i).setAutoscrolls(true);
			this.rightCoursePanelList.get(i).removeAll();
			for(JButton courseBtn:this.rightCourseBtnList.get(i)) {
				this.rightCoursePanelList.get(i).add(courseBtn);
			}
			if(i == 0) {
				this.rightCourseScrollPaneList.get(i).setPreferredSize(new Dimension(285, 635));
			}else {
				this.rightCourseScrollPaneList.get(i).setPreferredSize(new Dimension(285, 610));
			}
			this.rightCourseScrollPaneList.get(i).setBackground(new Color(55, 60, 85));
			this.rightCourseScrollPaneList.get(i).setBorder(BorderFactory.createLineBorder(new Color(55, 60, 85), 1, true));
			this.coursePanelList.get(i).removeAll();
			this.coursePanelList.get(i).add(this.courseTypeBtnList.get(i));
			if(i == 1) {
				this.generalComboBox.setPreferredSize(new Dimension(295, 20));
//				this.generalComboBox.setForeground(Color.WHITE);
//				this.generalComboBox.setBackground(new Color(55, 60, 85));
				this.coursePanelList.get(i).add(this.generalComboBox);
			}else if(i == 2) {
				this.searchTextField.setPreferredSize(new Dimension(230, 20));
				this.searchTextField.setForeground(Color.WHITE);
				this.searchTextField.setBackground(new Color(55, 60, 85));
				this.searchTextField.setBorder(BorderFactory.createLineBorder(new Color(55, 60, 85), 1, true));
				this.searchBtn.setPreferredSize(new Dimension(50, 20));
				this.searchBtn.setForeground(Color.WHITE);
				this.searchBtn.setBackground(new Color(55, 60, 85));
				this.searchBtn.setBorder(BorderFactory.createLineBorder(new Color(55, 60, 85), 1, true));
				this.searchBtn.setOpaque(true);
				this.coursePanelList.get(i).add(this.searchTextField);
				this.coursePanelList.get(i).add(this.searchBtn);
			}
			this.coursePanelList.get(i).add(this.rightCourseScrollPaneList.get(i));
			this.rightPanel.add(this.coursePanelList.get(i));
		}
		JPanel p = new JPanel();
		p.setPreferredSize(new Dimension(295, 30));
		p.setBackground(new Color(35, 37, 52));
		this.nextBtn.setPreferredSize(new Dimension(285, 20));
		this.nextBtn.setForeground(Color.WHITE);
		this.nextBtn.setBackground(new Color(55, 60, 85));
		this.nextBtn.setBorder(BorderFactory.createLineBorder(new Color(55, 60, 85), 1, true));
		this.nextBtn.setOpaque(true);
		p.add(this.nextBtn);
		this.rightPanel.add(p);
		this.add(this.rightPanel);
		this.validate();
		this.repaint();
	}

	private HomeFrame getFrame() {
		for(Frame frame:JFrame.getFrames()) {
			if(frame.getTitle().equals("Course Registration Guide")) {
				HomeFrame homeFrame = (HomeFrame) frame;
				return homeFrame;
			}
		}
		return null;
	}
	
}