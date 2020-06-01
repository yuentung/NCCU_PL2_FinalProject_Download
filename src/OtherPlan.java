
public class OtherPlan {

	private String name;
	private String day;
	private String time;
	
	public OtherPlan(String name, String day) {
		this.name = name;
		this.day = day;
		this.time = null;

	}

	public String getName() {
		return name;
	}

//	public void setName(String name) {
//		this.name = name;
//	}

	public String getDay() {
		return day;
	}

//	public void setDay(String day) {
//		this.day = day;
//	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	public String otherPlanDetail() {
		return String.format("[Other Plan: %s, %s, %s]", name, day, time);
	}
	
	
	
}
