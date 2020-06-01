import javax.swing.JFrame;


public class Viewer {

	public static void main(String[] args) {
		
		HomeFrame homeFrame = new HomeFrame("ScheduleSetup", 820, 805);
		ScheduledPanel panel = new ScheduledPanel();
		homeFrame.add(panel);
		
		homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		homeFrame.setVisible(true);


	}

}
