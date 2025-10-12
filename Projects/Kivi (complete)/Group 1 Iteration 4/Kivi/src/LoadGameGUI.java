import java.awt.*;
import java.io.File;
import javax.swing.*;

public class LoadGameGUI extends JFrame{
    public LoadGameGUI(KiviBoard board) {
        setTitle("Load Game");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        String projectDir = System.getProperty("user.dir");
        File savesDir = new File(projectDir, "saves");
        if (!savesDir.exists() || !savesDir.isDirectory()) {
            JOptionPane.showMessageDialog(this, "The 'saves' folder doesn't exist or is not a directory.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] files = savesDir.list((dir, name) -> name.endsWith(".kivi")); // Assuming the save files have a ".sav" extension
        DefaultListModel<String> listModel = new DefaultListModel<>();
        if (files != null) {
            for (String file : files) {
                listModel.addElement(file);
            }
        }

        JList<String> fileList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(fileList);
        add(scrollPane, BorderLayout.CENTER);
        JButton loadButton = new JButton("Load Game");
        add(loadButton, BorderLayout.SOUTH);

        loadButton.addActionListener(_ -> {
                String selectedFile = fileList.getSelectedValue();
                if (selectedFile != null) {
                    board.loadGame(selectedFile);
                } else {
                    JOptionPane.showMessageDialog(LoadGameGUI.this, "Please select a file to load.", "Error", JOptionPane.ERROR_MESSAGE);
                }
        });
        setVisible(true);
    }
}