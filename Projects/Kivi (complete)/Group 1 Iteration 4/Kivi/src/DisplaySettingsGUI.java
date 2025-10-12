import java.awt.*;
import javax.swing.*;

public class DisplaySettingsGUI extends JFrame {
    
    private KiviBoard board;
    
    public DisplaySettingsGUI(KiviBoard board) {
        this.board = board;
        
        setTitle("Display Settings");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setSize(300, 250);
        setLayout(new GridLayout(4, 1));
    
        final int defaultResolutionIndex = 0;
        String[] resolutions = {"1080x900","1080x860", "1160x900","1620x1050", "1920x1080"};
        JComboBox<String> resolutionDropdown = new JComboBox<>(resolutions);
        resolutionDropdown.setSelectedIndex(defaultResolutionIndex);
        JPanel resolutionPanel = new JPanel();
        resolutionPanel.add(new JLabel("Resolution:"));
        resolutionPanel.add(resolutionDropdown);
        add(resolutionPanel);
    
        JPanel colorblindPanel = new JPanel();
        colorblindPanel.add(new JLabel("Accessibility Mode:"));
        JCheckBox colorBlindOn = new JCheckBox("On");
        JCheckBox colorBlindOff = new JCheckBox("Off");
        
        if (board != null && board.isColorblindModeEnabled()) {
            colorBlindOn.setSelected(true);
            colorBlindOff.setSelected(false);
        } else {
            colorBlindOn.setSelected(false);
            colorBlindOff.setSelected(true);
        }
    
        colorBlindOn.addActionListener(_ -> {
            if (colorBlindOn.isSelected()) {
                colorBlindOff.setSelected(false);
                if (board != null) {
                    board.setColorblindMode(true);
                    DicePanel dicePanel = board.getDicePanel();
                    if (dicePanel != null) {
                        dicePanel.setColorblindMode(true);
                    }
                    showColorblindinfo();
                }
            }
        });
    
        colorBlindOff.addActionListener(_ -> {
            if (colorBlindOff.isSelected()) {
                colorBlindOn.setSelected(false);
                if (board != null) {
                    board.setColorblindMode(false);
                    DicePanel dicePanel = board.getDicePanel();
                    if (dicePanel != null) {
                        dicePanel.setColorblindMode(false);
                    }
                }
            }
        });
    
        colorblindPanel.add(colorBlindOn);
        colorblindPanel.add(colorBlindOff);
        add(colorblindPanel);
        
        
        JPanel helpPanel = new JPanel();
        JButton helpButton = new JButton("Accessibility Mode Help");
        helpButton.addActionListener(_ -> {
            showColorblindinfo();
        });
        helpPanel.add(helpButton);
        add(helpPanel);
    
  // Create a panel for the buttons
        // Create the buttons and add action listeners      
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save Changes");
        JButton discardButton = new JButton("Discard Changes");
    
        discardButton.addActionListener(e -> {
            resolutionDropdown.setSelectedIndex(defaultResolutionIndex);
            colorBlindOff.setSelected(true);
            colorBlindOn.setSelected(false);
            if (board != null) {
                board.setColorblindMode(false);
                DicePanel dicePanel = board.getDicePanel();
                if (dicePanel != null) {
                    dicePanel.setColorblindMode(false);
                }
            }
        });
        
        saveButton.addActionListener(e -> {
            String selectedResolution = (String) resolutionDropdown.getSelectedItem();
            applyResolution(selectedResolution);
            dispose();
        });
    
        buttonPanel.add(saveButton);
        buttonPanel.add(discardButton);
        add(buttonPanel);
    
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
    private void applyResolution(String resolution) {
        String[] dimensions = resolution.split("x");
        int width = Integer.parseInt(dimensions[0]);
        int height = Integer.parseInt(dimensions[1]);

        if (board != null) {
            board.updateResolution(width, height);
        }
    }

    private void showColorblindinfo() {
        JPanel cbPanel = new JPanel();
        cbPanel.setLayout(new BoxLayout(cbPanel, BoxLayout.Y_AXIS));
        cbPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        int leftInset = 10;
        
        Box titleBox = Box.createHorizontalBox();
        titleBox.add(Box.createHorizontalStrut(leftInset));
        JLabel titleLabel = new JLabel("Accessibility Mode (Color deficient) Pattern Guide");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleBox.add(titleLabel);
        titleBox.add(Box.createHorizontalGlue());
        cbPanel.add(titleBox);
        cbPanel.add(Box.createVerticalStrut(10));
        
        
        Box boardBox = Box.createHorizontalBox();
        boardBox.add(Box.createHorizontalStrut(leftInset));
        JLabel boardPatternLabel = new JLabel("Board Square Patterns:");
        boardPatternLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        boardBox.add(boardPatternLabel);
        boardBox.add(Box.createHorizontalGlue());
        cbPanel.add(boardBox);
        
        
        cbPanel.add(createPatternExplanation("No Pattern", "1 point squares"));
        cbPanel.add(createPatternExplanation("Horizontal Lines", "2 point squares"));
        cbPanel.add(createPatternExplanation("Checkered Pattern", "3 point squares"));
        cbPanel.add(Box.createVerticalStrut(10));
        
       
        Box stoneBox = Box.createHorizontalBox();
        stoneBox.add(Box.createHorizontalStrut(leftInset));
        JLabel stonePatternLabel = new JLabel("Player Stone Patterns:");
        stonePatternLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        stoneBox.add(stonePatternLabel);
        stoneBox.add(Box.createHorizontalGlue());
        cbPanel.add(stoneBox);
        
        cbPanel.add(createPatternExplanation("Zigzag Pattern", "Player 1"));
        cbPanel.add(createPatternExplanation("Diagonal Lines", "Player 2"));
        cbPanel.add(createPatternExplanation("Dots", "Player 3"));
        cbPanel.add(createPatternExplanation("Concentric Circles", "Player 4"));
        cbPanel.add(Box.createVerticalStrut(10));
        
        
        Box diceBox = Box.createHorizontalBox();
        diceBox.add(Box.createHorizontalStrut(leftInset));
        JLabel dicePatternLabel = new JLabel("Dice Selection:");
        dicePatternLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        diceBox.add(dicePatternLabel);
        diceBox.add(Box.createHorizontalGlue());
        cbPanel.add(diceBox);
        
        cbPanel.add(createPatternExplanation("Black Solid Border", "Unselected"));
        cbPanel.add(createPatternExplanation("Red Solid Border", "Selected"));
        cbPanel.add(Box.createVerticalStrut(10));
        
        
        Box infoBox = Box.createHorizontalBox();
        infoBox.add(Box.createHorizontalStrut(leftInset));
        JLabel tooltipHeader = new JLabel("Additional Information:");
        tooltipHeader.setFont(new Font("SansSerif", Font.BOLD, 14));
        infoBox.add(tooltipHeader);
        infoBox.add(Box.createHorizontalGlue());
        cbPanel.add(infoBox);
        
        cbPanel.add(createPatternExplanation("Tooltips", "Hover your mouse over board squares and dice to see detailed information"));
        
        JScrollPane scrollPane = new JScrollPane(cbPanel);
        scrollPane.setPreferredSize(new Dimension(400, 400));
        
        JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "Colorblind Mode Details",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private JPanel createPatternExplanation(String pattern, String meaning) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("• " + pattern + ":"));
        panel.add(new JLabel(meaning));
        return panel;
    }

    
}