package panels;

import javax.swing.JButton;
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

public class CarPanel extends JPanel {

	Connection connect 		= null;
	PreparedStatement state = null;
	ResultSet result 		= null;

	int id;
	String personID;

	JPanel upPanel 	 = new JPanel();
	JPanel midPanel  = new JPanel();
	JPanel downPanel = new JPanel();

	JLabel brandL  = new JLabel("Марка:", SwingConstants.CENTER);
	JLabel modelL  = new JLabel("Модел:", SwingConstants.CENTER);
	JLabel yearL   = new JLabel("Година на производство:", SwingConstants.CENTER);
	JLabel personL = new JLabel("За човек:", SwingConstants.CENTER);

	JTextField brandTF = new JTextField();
	JTextField modelTF = new JTextField();
	JTextField yearTF  = new JTextField();

	JButton addBt 	  = new JButton("Добави");
	JButton deleteBt  = new JButton("Изтрий");
	JButton editBt 	  = new JButton("Редактирай");
	JButton searchBt  = new JButton("Търсене по име");
	JButton refreshBt = new JButton("Обнови");

	JTable table = new JTable();
	JScrollPane myScroll = new JScrollPane(table);

	public CarPanel() {

		this.setLayout(new GridLayout(3, 1));

		// --------------------------------------------------------

		upPanel.setLayout(new GridLayout(4, 2));

		upPanel.add(brandL);
		upPanel.add(brandTF);

		upPanel.add(modelL);
		upPanel.add(modelTF);

		upPanel.add(yearL);
		upPanel.add(yearTF);

		upPanel.add(personL);
		upPanel.add(PersonPanel.personCombo);

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

	public void refreshTable() {

		connect = DBConnection.getConnection();
		String sql = "SELECT CARS.CAR_ID, CARS.BRAND, CARS.MODEL, CARS.CAR_YEAR, "
				   + "PERSONS.FNAME, PERSONS.LNAME "
				   + "FROM PERSONS "
				   + "INNER JOIN CARS ON PERSONS.ID = CARS.FOR_PERSON";

		try {
			state = connect.prepareStatement(sql);
			result = state.executeQuery();
			table.setModel(new MyModel(result));

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void clearForm() {
		
		brandTF.setText("");
		modelTF.setText("");
		yearTF.setText("");
		PersonPanel.personCombo.setSelectedIndex(0);
	}

	class AddAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			connect = DBConnection.getConnection();
			String sql = "INSERT INTO "
					   + "CARS(BRAND, MODEL, CAR_YEAR, FOR_PERSON) "
					   + "VALUES(?, ?, ?, ?)";

			try {
				state = connect.prepareStatement(sql);
				
				state.setString(1, brandTF.getText());
				state.setString(2, modelTF.getText());
				state.setInt(3, Integer.parseInt(yearTF.getText()));
				state.setInt(4,Integer.parseInt(PersonPanel.personCombo.getSelectedItem().toString().replaceAll("[\\D]", "")));

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
			brandTF.setText(table.getValueAt(row, 1).toString());
			modelTF.setText(table.getValueAt(row, 2).toString());
			yearTF.setText(table.getValueAt(row, 3).toString());

			String sql = "SELECT FOR_PERSON "
					   + "FROM CARS "
					   + "WHERE CAR_ID = ?";

			try {
				state = connect.prepareStatement(sql);
				state.setInt(1, id);
				result = state.executeQuery();
				
				while (result.next()) {
					personID = result.getObject(1).toString();
				}
				
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			int size = PersonPanel.personCombo.getItemCount();
			for (int i = 0; i < size; i++) {
				
				if (Integer.parseInt(PersonPanel.personCombo.getItemAt(i).toString().replaceAll("[\\D]", "")) == Integer.parseInt(personID)) {
					PersonPanel.personCombo.setSelectedIndex(i);
					break;
				}
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
			String sql = "UPDATE CARS "
					   + "SET BRAND = ?, "
					   + "MODEL = ?, "
					   + "CAR_YEAR = ?, "
					   + "FOR_PERSON = ? "
					   + "WHERE CAR_ID = ?";

			try {
				state = connect.prepareStatement(sql);
				
				state.setString(1, brandTF.getText());
				state.setString(2, modelTF.getText());
				state.setInt(3, Integer.parseInt(yearTF.getText()));
				state.setInt(4,Integer.parseInt(PersonPanel.personCombo.getSelectedItem().toString().replaceAll("[\\D]", "")));
				state.setInt(5, id);

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
					   + "FROM CARS "
					   + "WHERE CAR_ID = ?";

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
			String sql = "SELECT CARS.CAR_ID, CARS.BRAND, CARS.MODEL, CARS.CAR_YEAR, "
					   + "PERSONS.FNAME, PERSONS.LNAME "
					   + "FROM PERSONS "
					   + "INNER JOIN CARS ON PERSONS.ID = CARS.FOR_PERSON "
					   + "WHERE BRAND = ?";

			try {
				state = connect.prepareStatement(sql);
				
				state.setString(1, brandTF.getText());
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