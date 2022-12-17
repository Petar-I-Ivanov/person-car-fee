import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import panels.CarPanel;
import panels.FeePanel;
import panels.PersonPanel;
import panels.ReportPanel;

public class MyFrame extends JFrame {

	JTabbedPane tab = new JTabbedPane();
	
	JPanel personP 	  = new PersonPanel();
	JPanel carP 	  = new CarPanel();
	JPanel feeP 	  = new FeePanel();
	JPanel reportP	  = new ReportPanel();

	public MyFrame () {
		
		this.setSize(600,600);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		tab.add("Хора", personP);
		tab.add("Коли", carP);
		tab.add("Вноски", feeP);
		tab.add("Справка", reportP);
		
		this.add(tab);
		this.setVisible(true);
	}
}