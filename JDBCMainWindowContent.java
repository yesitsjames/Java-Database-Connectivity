package jdbcliverpool;

import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.swing.*;
import javax.swing.border.*;
import java.sql.*;

@SuppressWarnings("serial")
public class JDBCMainWindowContent extends JInternalFrame implements ActionListener {
    // DB Connectivity Attributes
    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    private Container content;

    private JPanel detailsPanel;
    private JPanel exportButtonPanel;
    private JScrollPane dbContentsPanel;

    private Border lineBorder;

    // Player Details
    private JLabel IDLabel = new JLabel("ID: ");
    private JLabel NameLabel = new JLabel("Name: ");
    private JLabel PositionLabel = new JLabel("Position: ");
    private JLabel AgeLabel = new JLabel("Age: ");
    private JLabel NationalityLabel = new JLabel("Nationality: ");
    private JLabel JerseyNumberLabel = new JLabel("Jersey Number: ");
    private JLabel ContractUntilLabel = new JLabel("Contract Until: ");

    private JTextField IDTF = new JTextField(10);
    private JTextField NameTF = new JTextField(10);
    private JTextField PositionTF = new JTextField(10);
    private JTextField AgeTF = new JTextField(10);
    private JTextField NationalityTF = new JTextField(10);
    private JTextField JerseyNumberTF = new JTextField(10);
    private JTextField ContractUntilTF = new JTextField(10);

    // Player Statistics
    private JLabel AppearancesLabel = new JLabel("Appearances: ");
    private JLabel GoalsLabel = new JLabel("Goals: ");
    private JLabel AssistsLabel = new JLabel("Assists: ");
    private JLabel YellowCardsLabel = new JLabel("Yellow Cards: ");
    private JLabel RedCardsLabel = new JLabel("Red Cards: ");

    private JTextField AppearancesTF = new JTextField(10);
    private JTextField GoalsTF = new JTextField(10);
    private JTextField AssistsTF = new JTextField(10);
    private JTextField YellowCardsTF = new JTextField(10);
    private JTextField RedCardsTF = new JTextField(10);

    private static QueryTableModel TableModel = new QueryTableModel();
    private JTable TableofDBContents = new JTable(TableModel);

    // CRUD operation buttons
    private JButton updateButton = new JButton("Update");
    private JButton insertButton = new JButton("Insert");
    private JButton deleteButton = new JButton("Delete");
    private JButton clearButton = new JButton("Clear");
    private JButton exportButton = new JButton("Export");
    private JButton exportStatisticsButton = new JButton("Export Statistics"); // Export statistics button

    // Export query buttons
    private JButton listAllPositionsButton = new JButton("List All Positions");
    private JButton countPlayersByPositionButton = new JButton("Count Players by Position");
    private JTextField countPlayersByPositionTF = new JTextField(15);

    public JDBCMainWindowContent(String aTitle) {
        super(aTitle, false, false, false, false);
        setEnabled(true);

        initiate_db_conn();

        content = getContentPane();
        content.setLayout(null);
        content.setBackground(Color.black);
        lineBorder = BorderFactory.createEtchedBorder(15, Color.red, Color.black);

        // Details Panel
        detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridLayout(13, 2)); // Adjust for additional statistics fields
        detailsPanel.setBackground(Color.pink);
        detailsPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Player Details"));

        // Add Player Fields
        detailsPanel.add(IDLabel);
        detailsPanel.add(IDTF);
        detailsPanel.add(NameLabel);
        detailsPanel.add(NameTF);
        detailsPanel.add(PositionLabel);
        detailsPanel.add(PositionTF);
        detailsPanel.add(AgeLabel);
        detailsPanel.add(AgeTF);
        detailsPanel.add(NationalityLabel);
        detailsPanel.add(NationalityTF);
        detailsPanel.add(JerseyNumberLabel);
        detailsPanel.add(JerseyNumberTF);
        detailsPanel.add(ContractUntilLabel);
        detailsPanel.add(ContractUntilTF);

        // Add Player Statistics Fields
        detailsPanel.add(AppearancesLabel);
        detailsPanel.add(AppearancesTF);
        detailsPanel.add(GoalsLabel);
        detailsPanel.add(GoalsTF);
        detailsPanel.add(AssistsLabel);
        detailsPanel.add(AssistsTF);
        detailsPanel.add(YellowCardsLabel);
        detailsPanel.add(YellowCardsTF);
        detailsPanel.add(RedCardsLabel);
        detailsPanel.add(RedCardsTF);

        // Add Buttons for CRUD
        insertButton.setSize(80, 30);
        updateButton.setSize(80, 30);
        deleteButton.setSize(80, 30);
        clearButton.setSize(80, 30);
        exportButton.setSize(80, 30);

        insertButton.setLocation(380, 10);
        updateButton.setLocation(380, 60);
        deleteButton.setLocation(380, 110);
        clearButton.setLocation(380, 160);
        exportButton.setLocation(380, 210);

        insertButton.addActionListener(this);
        updateButton.addActionListener(this);
        deleteButton.addActionListener(this);
        clearButton.addActionListener(this);
        exportButton.addActionListener(this);

        content.add(insertButton);
        content.add(updateButton);
        content.add(deleteButton);
        content.add(clearButton);
        content.add(exportButton);

        // Table for DB Content
        TableofDBContents.setPreferredScrollableViewportSize(new Dimension(900, 300));
        dbContentsPanel = new JScrollPane(TableofDBContents, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        dbContentsPanel.setBackground(Color.pink);
        dbContentsPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Database Content"));

        detailsPanel.setSize(360, 400);
        detailsPanel.setLocation(3, 0);
        dbContentsPanel.setSize(700, 300);
        dbContentsPanel.setLocation(477, 0);

        content.add(detailsPanel);
        content.add(dbContentsPanel);

        // Export Button Panel
        exportButtonPanel = new JPanel();
        exportButtonPanel.setLayout(new GridLayout(4, 2)); // Adjusted to fit all buttons
        exportButtonPanel.setBackground(Color.pink);
        exportButtonPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Export Data"));

        exportButtonPanel.add(listAllPositionsButton);
        exportButtonPanel.add(new JLabel("")); // Spacer
        exportButtonPanel.add(countPlayersByPositionButton);
        exportButtonPanel.add(countPlayersByPositionTF);
        exportButtonPanel.add(exportStatisticsButton); // Add Export Statistics button here

        listAllPositionsButton.addActionListener(this);
        countPlayersByPositionButton.addActionListener(this);
        exportStatisticsButton.addActionListener(this);

        exportButtonPanel.setSize(400, 150);
        exportButtonPanel.setLocation(10, 420);

        content.add(exportButtonPanel);

        setSize(982, 645);
        setVisible(true);

        TableModel.refreshFromDB(stmt);
    }

	public void initiate_db_conn()
	{
		try
		{
			// Load the JConnector Driver
			Class.forName("com.mysql.cj.jdbc.Driver");
			// Specify the DB Name
            String url = "jdbc:mysql://localhost:3306/liverpoolfc";			
			// Connect to DB using DB URL, Username and password
			con = DriverManager.getConnection(url, "root", "");
			
			//Create a generic statement which is passed to the TestInternalFrame1
			stmt = con.createStatement();
		}
		catch(Exception e)
		{
			System.out.println("Error: Failed to connect to database\n"+e.getMessage());
		}
	}

    public void actionPerformed(ActionEvent e) {
        Object target = e.getSource();

        if (target == clearButton) {
            IDTF.setText("");
            NameTF.setText("");
            PositionTF.setText("");
            AgeTF.setText("");
            NationalityTF.setText("");
            JerseyNumberTF.setText("");
            ContractUntilTF.setText("");
            AppearancesTF.setText("");
            GoalsTF.setText("");
            AssistsTF.setText("");
            YellowCardsTF.setText("");
            RedCardsTF.setText("");
        }

        if (target == insertButton) {
            try {
                String query1 = "INSERT INTO players (name, position, age, nationality, jersey_number, contract_until) VALUES ('" +
           
                        NameTF.getText() + "','" + PositionTF.getText() + "'," + AgeTF.getText() + ",'" + NationalityTF.getText() + "'," +
                        JerseyNumberTF.getText() + ",'" + ContractUntilTF.getText() + "');";
                
                stmt.executeUpdate(query1);

                ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID();");
                
                rs.next();
                
                int playerId = rs.getInt(1);

                String query2 = "INSERT INTO player_statistics (player_id, appearances, goals, assists, yellow_cards, red_cards) VALUES (" +
                        playerId + "," + AppearancesTF.getText() + "," + GoalsTF.getText() + "," + AssistsTF.getText() + "," +
                        YellowCardsTF.getText() + "," + RedCardsTF.getText() + ");";
                stmt.executeUpdate(query2);
                
            } catch (SQLException sqle) {
            	
                System.err.println("Error with insert:\n" + sqle.toString());
                
            } finally {
            	
                TableModel.refreshFromDB(stmt);
            }
        }
		/////////////////////////////////////////////////////////////////////////////////////
		//I have only added functionality of 2 of the button on the lower right of the template
		///////////////////////////////////////////////////////////////////////////////////
        
        if (target == deleteButton) {
            try {
            	
                String playerId = IDTF.getText();

                String query1 = "DELETE FROM player_statistics WHERE player_id = " + playerId + ";";
                stmt.executeUpdate(query1);

                String query2 = "DELETE FROM players WHERE player_id = " + playerId + ";";
                stmt.executeUpdate(query2);
                
            } catch (SQLException sqle) {
            	
                System.err.println("Error with delete:\n" + sqle.toString());
                
            } finally {
            	
                TableModel.refreshFromDB(stmt);
                
            }
        }
        
        if (target == updateButton) {
            try {
            	
                String playerId = IDTF.getText();

                String query1 = "UPDATE players SET name='" + NameTF.getText() +
                        "', position='" + PositionTF.getText() +
                        "', age=" + AgeTF.getText() +
                        ", nationality='" + NationalityTF.getText() +
                        "', jersey_number=" + JerseyNumberTF.getText() +
                        ", contract_until='" + ContractUntilTF.getText() +
                        "' WHERE player_id=" + playerId + ";";
                stmt.executeUpdate(query1);

                String query2 = "UPDATE player_statistics SET appearances=" + AppearancesTF.getText() +
                        ", goals=" + GoalsTF.getText() +
                        ", assists=" + AssistsTF.getText() +
                        ", yellow_cards=" + YellowCardsTF.getText() +
                        ", red_cards=" + RedCardsTF.getText() +
                        " WHERE player_id=" + playerId + ";";
                stmt.executeUpdate(query2);
            } catch (SQLException sqle) {
                System.err.println("Error with update:\n" + sqle.toString());
            } finally {
                TableModel.refreshFromDB(stmt);
            }
        }

        if (target == exportButton) {
            try {
            	
                rs = stmt.executeQuery("SELECT p.player_id, p.name, p.position, p.age, p.nationality, p.jersey_number, p.contract_until, " +
                        "s.appearances, s.goals, s.assists, s.yellow_cards, s.red_cards " +
                        "FROM players p JOIN player_statistics s ON p.player_id = s.player_id;");
                writeToFile(rs, "LiverpoolFC_CompleteData.csv");
            } catch (Exception e1) {
            	
            	
                e1.printStackTrace();
            }
        }

        if (target == exportStatisticsButton) { 
            try {
                rs = stmt.executeQuery("SELECT * FROM player_statistics;");
                writeToFile(rs, "LiverpoolFC_PlayerStatistics.csv");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        if (target == listAllPositionsButton) {
            try {
                rs = stmt.executeQuery("SELECT DISTINCT position FROM players;");
                writeToFile(rs, "LiverpoolFC_Positions.csv");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        if (target == countPlayersByPositionButton) {
            try {
                String position = countPlayersByPositionTF.getText();
                rs = stmt.executeQuery("SELECT position, COUNT(*) AS player_count FROM players WHERE position = '" + position + "' GROUP BY position;");
                writeToFile(rs, "LiverpoolFC_PlayerCount_ByPosition.csv");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////
    private void writeToFile(ResultSet rs, String fileName) {
        try {
            FileWriter outputFile = new FileWriter(fileName);
            PrintWriter printWriter = new PrintWriter(outputFile);
            ResultSetMetaData rsmd = rs.getMetaData();
            int numColumns = rsmd.getColumnCount();

            for (int i = 0; i < numColumns; i++) {
                printWriter.print(rsmd.getColumnLabel(i + 1) + ",");
            }
            printWriter.print("\n");

            while (rs.next()) {
                for (int i = 0; i < numColumns; i++) {
                    printWriter.print(rs.getString(i + 1) + ",");
                }
                printWriter.print("\n");
            }
            printWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
