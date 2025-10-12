
import javax.swing.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;
import java.awt.*;


public class IntroScreenGUI extends JFrame {
    public IntroScreenGUI() {
        setTitle("KIVI");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel("Welcome To KIVI Board Game!");
        titleLabel.setFont(new Font("Arial",Font.BOLD,18));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        
        //adding icon
        ImageIcon image= new ImageIcon("KiviBoard.png");
        setIconImage(image.getImage()); //change icon of frame
        
        //add background color
        getContentPane().setBackground( new Color(255,105,180));
        

        JButton createGameButton = new JButton("Create Game");
        JButton loadGameButton = new JButton("Load Game");
        
        // Make buttons the same size
        Dimension buttonSize = new Dimension(200, 40);
        createGameButton.setMaximumSize(buttonSize);
        loadGameButton.setMaximumSize(buttonSize);
        
        //center the buttons
        createGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        
        

        createGameButton.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SetUpAGameGUI().setVisible(true);
                dispose(); // Close the IntroScreenGUI
            }
        });

        loadGameButton.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the LoadGameGUI (to be implemented)
                // new LoadGameGUI().setVisible(true);
                dispose(); // Close the IntroScreenGUI
            }
        });
        
       
        add(Box.createVerticalStrut(50)); // Space above title
        add(titleLabel);
        add(Box.createVerticalStrut(30)); // Space below title
        add(createGameButton);
        add(Box.createVerticalStrut(20)); // Space between buttons
        add(loadGameButton);
        setVisible(true);
    }

    
}