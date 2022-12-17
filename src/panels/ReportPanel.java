package panels;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import dataBase.DBConnection;
import dataBase.MyModel;

public class ReportPanel extends JPanel {

	Connection connect 		= null;
	PreparedStatement state = null;
	ResultSet result 		= null;

	JPanel upPanel 	 = new JPanel();
	JPanel midPanel  = new JPanel();
	JPanel downPanel = new JPanel();

	JLabel nameL  = new JLabel("Име:", SwingConstants.CENTER);
	JLabel brandL = new JLabel("Марка:", SwingConstants.CENTER);

	JTextField nameTF  = new JTextField();
	JTextField brandTF = new JTextField();

	JButton searchBt = new JButton("Търси по име и марка");

	JTable table = new JTable();
	JScrollPane myScroll = new JScrollPane(table);

	public ReportPanel() {

		this.setLayout(new GridLayout(3, 1));

		// --------------------------------------------------------

		upPanel.setLayout(new GridLayout(2, 2));

		upPanel.add(nameL);
		upPanel.add(nameTF);

		upPanel.add(brandL);
		upPanel.add(brandTF);

		this.add(upPanel);

		// --------------------------------------------------------

		midPanel.add(searchBt);
		this.add(midPanel);

		// --------------------------------------------------------

		myScroll.setPreferredSize(new Dimension(550, 200));
		downPanel.add(myScroll);

		this.add(downPanel);
		searchBt.addActionListener(new SearchAction());
	}

	public void clearForm() {
		
		nameTF.setText("");
		brandTF.setText("");
	}

	class SearchAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			connect = DBConnection.getConnection();
			String sql = "SELECT CARS.CAR_ID, CARS.BRAND, CARS.MODEL, CARS.CAR_YEAR, "
					   + "PERSONS.FNAME, PERSONS.LNAME "
					   + "FROM PERSONS " + "INNER JOIN CARS ON PERSONS.ID = CARS.FOR_PERSON "
					   + "WHERE FNAME = ? AND BRAND = ?";

			try {
				state = connect.prepareStatement(sql);
				
				state.setString(1, nameTF.getText());
				state.setString(2, brandTF.getText());
				result = state.executeQuery();
				table.setModel(new MyModel(result));

			} catch (SQLException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			clearForm();
		}
	}
}