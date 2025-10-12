import java.awt.*;
import java.io.File;
import javax.swing.*;

public class LoadGameGUI extends JFrame{
    // Constructor to setup the GUI
    public LoadGameGUI(KiviBoard board) {
        setTitle("Load Game");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set layout manager
        setLayout(new BorderLayout());

        // Get the path to the "saves" folder inside the "src" folder
        String projectDir = System.getProperty("user.dir");
        File savesDir = new File(projectDir, "saves");

        // Check if the "saves" folder exists
        if (!savesDir.exists() || !savesDir.isDirectory()) {
            JOptionPane.showMessageDialog(this, "The 'saves' folder doesn't exist or is not a directory.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the list of files in the "saves" folder
        String[] files = savesDir.list((dir, name) -> name.endsWith(".kivi")); // Assuming the save files have a ".sav" extension

        // Create a JList to display the file names
        DefaultListModel<String> listModel = new DefaultListModel<>();
        if (files != null) {
            for (String file : files) {
                listModel.addElement(file);
            }
        }

        JList<String> fileList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(fileList);
        add(scrollPane, BorderLayout.CENTER);

        // Add a button to trigger the loading of the selected file
        JButton loadButton = new JButton("Load Game");
        add(loadButton, BorderLayout.SOUTH);

        // Add an action listener to the load button
        loadButton.addActionListener(_ -> {
                String selectedFile = fileList.getSelectedValue();
                if (selectedFile != null) {
                    // Call loadGame when a file is selected
                    board.loadGame(selectedFile);
                } else {
                    JOptionPane.showMessageDialog(LoadGameGUI.this, "Please select a file to load.", "Error", JOptionPane.ERROR_MESSAGE);
                }
        });
        // Show the frame
        setVisible(true);
    }
}
