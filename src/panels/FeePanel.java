package panels;

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

import dataBase.DBConnection;
import dataBase.MyModel;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class FeePanel extends JPanel {

	Connection connect 		= null;
	PreparedStatement state = null;
	ResultSet result 		= null;

	int id;
	String personID;

	JPanel upPanel   = new JPanel();
	JPanel midPanel  = new JPanel();
	JPanel downPanel = new JPanel();

	JLabel contributionL = new JLabel("Вноска:", SwingConstants.CENTER);
	JLabel numberContrL  = new JLabel("Брой вноски:", SwingConstants.CENTER);
	JLabel personL 		 = new JLabel("За човек:", SwingConstants.CENTER);

	JTextField contributionTF = new JTextField();
	JTextField numberContrTF  = new JTextField();

	JButton addBt 	  = new JButton("Добави");
	JButton deleteBt  = new JButton("Изтрий");
	JButton editBt 	  = new JButton("Редактирай");
	JButton searchBt  = new JButton("Търси по по-голяма вноска");
	JButton refreshBt = new JButton("Обнови");

	JTable table = new JTable();
	JScrollPane myScroll = new JScrollPane(table);

	public FeePanel() {

		this.setLayout(new GridLayout(3, 1));

		// --------------------------------------------------------

		upPanel.setLayout(new GridLayout(3, 2));

		upPanel.add(contributionL);
		upPanel.add(contributionTF);

		upPanel.add(numberContrL);
		upPanel.add(numberContrTF);

		upPanel.add(personL);
		upPanel.add(PersonPanel.personTwoCombo);

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
		String sql = "SELECT FEES.FEE_ID, FEES.CONTRIBUTION, FEES.NUMBER_CONTR, FEES.TOTAL, "
				   + "PERSONS.FNAME, PERSONS.LNAME "
				   + "FROM PERSONS "
				   + "INNER JOIN FEES ON PERSONS.ID = FEES.FOR_PERSON";

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
		
		contributionTF.setText("");
		numberContrTF.setText("");
		PersonPanel.personTwoCombo.setSelectedIndex(0);
	}

	class AddAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			connect = DBConnection.getConnection();
			String sql = "INSERT INTO "
					   + "FEES(CONTRIBUTION, NUMBER_CONTR, TOTAL, FOR_PERSON) "
					   + "VALUES(?, ?, ?, ?)";

			try {
				state = connect.prepareStatement(sql);
				
				state.setFloat(1, Float.parseFloat(contributionTF.getText()));
				state.setInt(2, Integer.parseInt(numberContrTF.getText()));
				state.setFloat(3,Float.parseFloat(contributionTF.getText()) * Integer.parseInt(numberContrTF.getText()));
				state.setInt(4, Integer.parseInt(PersonPanel.personTwoCombo.getSelectedItem().toString().replaceAll("[\\D]", "")));

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
			contributionTF.setText(table.getValueAt(row, 1).toString());
			numberContrTF.setText(table.getValueAt(row, 2).toString());

			String sql = "SELECT FOR_PERSON "
					   + "FROM FEES "
					   + "WHERE FEE_ID = ?";

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

			int size = PersonPanel.personTwoCombo.getItemCount();
			for (int i = 0; i < size; i++) {
				
				if (Integer.parseInt(PersonPanel.personTwoCombo.getItemAt(i).toString().replaceAll("[\\D]","")) == Integer.parseInt(personID)) {
					
					PersonPanel.personTwoCombo.setSelectedIndex(i);
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
			String sql = "UPDATE FEES "
					   + "SET CONTRIBUTION = ?, "
					   + "NUMBER_CONTR = ?, "
					   + "TOTAL = ?, "
					   + "FOR_PERSON = ? "
					   + "WHERE FEE_ID = ?";

			try {
				state = connect.prepareStatement(sql);
				
				state.setFloat(1, Float.parseFloat(contributionTF.getText()));
				state.setInt(2, Integer.parseInt(numberContrTF.getText()));
				state.setFloat(3,Float.parseFloat(contributionTF.getText()) * Integer.parseInt(numberContrTF.getText()));
				state.setInt(4, Integer.parseInt(PersonPanel.personTwoCombo.getSelectedItem().toString().replaceAll("[\\D]", "")));
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
					   + "FROM FEES "
					   + "WHERE FEE_ID = ?";

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

			String sql = "SELECT FEES.FEE_ID, FEES.CONTRIBUTION, FEES.NUMBER_CONTR, FEES.TOTAL, "
					   + "PERSONS.FNAME, PERSONS.LNAME " + "FROM PERSONS "
					   + "INNER JOIN FEES ON PERSONS.ID = FEES.FOR_PERSON "
					   + "WHERE CONTRIBUTION >= ?";

			try {
				state = connect.prepareStatement(sql);
				
				state.setFloat(1, Float.parseFloat(contributionTF.getText()));
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