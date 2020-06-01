//import java.util.ArrayList;

public class ScheduledTimeList {

	private String courseName;
	private String day;
	private String fromTime;
	private String endTime;
//	private ArrayList<String> scheduledTimeList;
	
	public ScheduledTimeList(String courseName) {
		this.courseName = courseName;
		this.day = null;
		this.fromTime = null;
		this.endTime = null;
//		this.scheduledTimeList = new ArrayList<String>();

	}

	public String getCourseName() {
		return courseName;
	}
	
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getFromTime() {
		return fromTime;
	}

	public void setFromTime(String fromTime) {
		this.fromTime = fromTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	public void scheduledTimeDetail() {
		System.out.println(String.format("[Schedule: %s, %s, %s, %s]", courseName, day, fromTime, endTime));
	}
	
	public String scheduledTimeDetailStr() {
		return String.format("[Schedule: %s, %s, %s, %s]", courseName, day, fromTime, endTime);
	}


	
}
