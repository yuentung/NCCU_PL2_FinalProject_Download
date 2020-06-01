import javax.swing.JFrame;

public class HomeFrame extends JFrame {
	
	public HomeFrame(String title, int width, int height) {
		this.setTitle("Course Registration Guide - " + title);
		this.setSize(width, height);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
	}

}
