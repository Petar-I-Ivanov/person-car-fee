package panels;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import dataBase.DBConnection;
import dataBase.MyModel;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonPanel extends JPanel {

	Connection connect 	= null;
	PreparedStatement state = null;
	ResultSet result 	= null;
	
	int id;

	JPanel upPanel 	 = new JPanel();
	JPanel midPanel  = new JPanel();
	JPanel downPanel = new JPanel();

	JLabel fnameL  = new JLabel("Име:", SwingConstants.CENTER);
	JLabel lnameL  = new JLabel("Фамилия:", SwingConstants.CENTER);
	JLabel sexL    = new JLabel("Пол:", SwingConstants.CENTER);
	JLabel ageL    = new JLabel("Възраст:", SwingConstants.CENTER);
	JLabel salaryL = new JLabel("Заплата:", SwingConstants.CENTER);

	JTextField fnameTF 	= new JTextField();
	JTextField lnameTF 	= new JTextField();
	JTextField ageTF 	= new JTextField();
	JTextField salaryTF = new JTextField();

	String[] gender = { "Мъж", "Жена" };
	JComboBox<String> sexCombo = new JComboBox<String>(gender);

	JButton addBt 	  = new JButton("Добави");
	JButton deleteBt  = new JButton("Изтрих");
	JButton editBt 	  = new JButton("Редактирай");
	JButton searchBt  = new JButton("Търси по име");
	JButton refreshBt = new JButton("Обнови");

	JTable table = new JTable();
	JScrollPane myScroll = new JScrollPane(table);
	
	static JComboBox<String> personCombo = new JComboBox<String>();
	static JComboBox<String> personTwoCombo = new JComboBox<String>();

	public PersonPanel() {

		this.setLayout(new GridLayout(3, 1));

		// --------------------------------------------------------

		upPanel.setLayout(new GridLayout(5, 2));

		upPanel.add(fnameL);
		upPanel.add(fnameTF);

		upPanel.add(lnameL);
		upPanel.add(lnameTF);

		upPanel.add(sexL);
		upPanel.add(sexCombo);

		upPanel.add(ageL);
		upPanel.add(ageTF);

		upPanel.add(salaryL);
		upPanel.add(salaryTF);

		this.add(upPanel);

		// --------------------------------------------------------

		midPanel.add(addBt);
		midPanel.add(deleteBt);
		midPanel.add(editBt);
		midPanel.add(searchBt);
		midPanel.add(refreshBt);

		this.add(midPanel);

		// --------------------------------------------------------

		myScroll.setPreferredSize(new Dimension(550, 200));
		downPanel.add(myScroll);

		this.add(downPanel);

		table.addMouseListener(new MouseAction());
		addBt.addActionListener(new AddAction());
		editBt.addActionListener(new UpdateAction());
		deleteBt.addActionListener(new DeleteAction());
		searchBt.addActionListener(new SearchAction());
		refreshBt.addActionListener(new RefreshAction());

		refreshTable();
	}

	public void refreshCombo() {

		personCombo.removeAllItems();
		personTwoCombo.removeAllItems();

		String sql = "SELECT ID, FNAME, LNAME "
				   + "FROM PERSONS";
		String item = "";

		connect = DBConnection.getConnection();

		try {
			state = connect.prepareStatement(sql);
			result = state.executeQuery();
			
			while (result.next()) {
				
				item = result.getObject(1).toString() + ". " + result.getObject(2).toString() + " "
						+ result.getObject(3).toString();

				personCombo.addItem(item);
				personTwoCombo.addItem(item);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void refreshTable() {

		connect = DBConnection.getConnection();
		String sql = "SELECT * "
				   + "FROM PERSONS";

		try {
			state = connect.prepareStatement(sql);
			result = state.executeQuery();
			table.setModel(new MyModel(result));

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		refreshCombo();
	}

	public void clearForm() {
		
		fnameTF.setText("");
		lnameTF.setText("");
		ageTF.setText("");
		salaryTF.setText("");
	}

	class AddAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			connect = DBConnection.getConnection();
			String sql = "INSERT INTO "
					   + "PERSONS(FNAME, LNAME, SEX, AGE, SALARY) "
					   + "VALUES(?, ?, ?, ?, ?)";

			try {
				state = connect.prepareStatement(sql);
				
				state.setString(1, fnameTF.getText());
				state.setString(2, lnameTF.getText());
				state.setString(3, sexCombo.getSelectedItem().toString());
				state.setInt(4, Integer.parseInt(ageTF.getText()));
				state.setFloat(5, Float.parseFloat(salaryTF.getText()));

				state.execute();
				refreshTable();
				clearForm();

			} catch (SQLException el) {
				el.printStackTrace();
			}
		}
	}

	class MouseAction implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {

			int row = table.getSelectedRow();

			id = Integer.parseInt(table.getValueAt(row, 0).toString());
			fnameTF.setText(table.getValueAt(row, 1).toString());
			lnameTF.setText(table.getValueAt(row, 2).toString());
			ageTF.setText(table.getValueAt(row, 4).toString());
			salaryTF.setText(table.getValueAt(row, 5).toString());

			if (table.getValueAt(row, 3).toString().equals("Мъж")) {
				sexCombo.setSelectedIndex(0);
			} else {
				sexCombo.setSelectedIndex(1);
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}
	}

	class UpdateAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			connect = DBConnection.getConnection();
			String sql = "UPDATE PERSONS "
					   + "SET FNAME = ?, "
					   + "LNAME = ?, "
					   + "SEX = ?, "
					   + "AGE = ?,"
					   + "SALARY = ? "
					   + "WHERE ID = ?";

			try {
				state = connect.prepareStatement(sql);
				
				state.setString(1, fnameTF.getText());
				state.setString(2, lnameTF.getText());
				state.setString(3, sexCombo.getSelectedItem().toString());
				state.setInt(4, Integer.parseInt(ageTF.getText()));
				state.setFloat(5, Float.parseFloat(salaryTF.getText()));
				state.setInt(6, id);

				state.execute();
				refreshTable();
				clearForm();
				id = -1;

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

	class DeleteAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			connect = DBConnection.getConnection();
			String sql = "DELETE "
					   + "FROM PERSONS "
					   + "WHERE ID = ?";

			try {
				state = connect.prepareStatement(sql);
				
				state.setInt(1, id);
				state.execute();
				
				refreshTable();
				clearForm();
				id = -1;

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

	class SearchAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			connect = DBConnection.getConnection();
			String sql = "SELECT * "
					   + "FROM PERSONS "
					   + "WHERE FNAME = ?";

			try {
				state = connect.prepareStatement(sql);
				
				state.setString(1, fnameTF.getText());
				result = state.executeQuery();
				table.setModel(new MyModel(result));

			} catch (SQLException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	class RefreshAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			refreshTable();
			clearForm();
		}
	}
}