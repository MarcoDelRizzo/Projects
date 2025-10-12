import java.awt.*;
import javax.swing.*;

public class SaveGameGUI {

    public SaveGameGUI(KiviBoard board){
        JFrame frame = new JFrame("Choose File Name");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 150);
        frame.setLayout(new FlowLayout());

        JLabel label = new JLabel("Choose file name (without extension):");
        JTextField textField = new JTextField(15);
        JButton saveButton = new JButton("Save");
        JButton discardButton = new JButton("Discard");

        saveButton.addActionListener(_ -> {
            String fileName = textField.getText();
            if (fileName.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a file name.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else{
                board.saveGame(fileName + ".kivi");
                frame.dispose();
            }
        });

        discardButton.addActionListener(_ -> {
            frame.dispose();
        });

        frame.add(label);
        frame.add(textField);
        frame.add(saveButton);
        frame.add(discardButton);

        frame.setVisible(true);
    }
}

