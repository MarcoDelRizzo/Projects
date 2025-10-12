import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

public class SetUpAGameGUI extends JFrame {
    private JRadioButton[][] playerTypeRadioButtons;
    private JTextField[] nameFields;
    private JLabel[] playerIcons; 
    private JComboBox<String> squareSizeDropdown;
    @SuppressWarnings("unchecked")
    private JComboBox<String>[] colorDropdowns = new JComboBox[4];
    private JButton startGameButton, restartSetupButton, displaySettingsButton;
    private Set<String> selectedColors = new HashSet<>();
    // image icon
    private ImageIcon defaultIcon = new ImageIcon(getClass().getResource("/images/help.png"));
    private ImageIcon humanIcon = new ImageIcon(getClass().getResource("/images/user.png"));
    private ImageIcon easyComputerIcon = new ImageIcon(getClass().getResource("/images/agents.png"));
    private ImageIcon hardComputerIcon = new ImageIcon(getClass().getResource("/images/robot.png"));
    
    public SetUpAGameGUI() {
        setTitle("Kivi Game Set Up");
        setSize(1000, 600);
        scaleIconsToSize(128, 128);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        // GradientPanel
        GradientPanel gradientPanel = new GradientPanel();
        gradientPanel.setLayout(new BorderLayout(10, 10));
        setContentPane(gradientPanel);

        // Main panel for players
        JPanel playerColumnsPanel = new TransparentPanel();
        playerColumnsPanel.setLayout(new GridLayout(1, 4, 10, 10));
        playerColumnsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        playerTypeRadioButtons = new JRadioButton[4][4];
        nameFields = new JTextField[4];
        playerIcons = new JLabel[4];

        for (int i = 0; i < 4; i++) {
            playerColumnsPanel.add(createPlayerColumn(i + 1));
        }

        JPanel bottomPanel = new TransparentPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        String[] sizes = {"Small", "Medium", "Large"};
        squareSizeDropdown = new JComboBox<>(sizes);
        squareSizeDropdown.setSelectedIndex(1); //default to Medium
        JPanel squareSizePanel = new JPanel();
        squareSizePanel.setOpaque(false);;
        
        squareSizePanel.add(new JLabel("Square Size:"));
        squareSizePanel.add(squareSizeDropdown);
        bottomPanel.add(squareSizePanel);

        startGameButton = new JButton("Start Game");
        restartSetupButton = new JButton("Restart Setup");
        bottomPanel.add(startGameButton);
        bottomPanel.add(restartSetupButton);

        gradientPanel.add(playerColumnsPanel, BorderLayout.CENTER);
        gradientPanel.add(bottomPanel, BorderLayout.SOUTH);

        restartSetupButton.addActionListener(e -> restartSetup());
        startGameButton.addActionListener(e -> startGame());

        for (int i = 0; i < 4; i++) {
            updatePlayerColumn(i);
        }
    }

    // for icon sizing
    private void scaleIconsToSize(int width, int height) {
    // Load original images
    Image defaultImage = new ImageIcon(getClass().getResource("/images/help.png")).getImage();
    Image humanImage = new ImageIcon(getClass().getResource("/images/user.png")).getImage();
    Image hardComputerImage = new ImageIcon(getClass().getResource("/images/robot.png")).getImage();
    Image easyComputerImage = new ImageIcon(getClass().getResource("/images/agents.png")).getImage();
    
    // Scale with smooth rendering
    defaultIcon = new ImageIcon(defaultImage.getScaledInstance(128, 128, Image.SCALE_SMOOTH));
    humanIcon = new ImageIcon(humanImage.getScaledInstance(128, 128, Image.SCALE_SMOOTH));
    hardComputerIcon = new ImageIcon(hardComputerImage.getScaledInstance(128, 128, Image.SCALE_SMOOTH));
    easyComputerIcon = new ImageIcon(easyComputerImage.getScaledInstance(128, 128, Image.SCALE_SMOOTH));
}

    private JPanel createPlayerColumn(int playerNumber) {
        JPanel columnPanel = new TransparentPanel();
        columnPanel.setLayout(new BoxLayout(columnPanel, BoxLayout.Y_AXIS));
        columnPanel.setBorder(BorderFactory.createTitledBorder("Player " + playerNumber));

        Font boldFont = new Font("Arial", Font.BOLD, 14);

        JLabel typeLabel = new JLabel("Player Type:");
        typeLabel.setFont(boldFont);
        typeLabel.setForeground(Color.BLACK);
        columnPanel.add(typeLabel);

        ButtonGroup playerTypeGroup = new ButtonGroup();
        String[] playerTypes = { "None", "Human Player", "Computer Hard", "Computer Easy" };
        for (int i = 0; i < playerTypes.length; i++) {
            playerTypeRadioButtons[playerNumber - 1][i] = new JRadioButton(playerTypes[i]);
            playerTypeRadioButtons[playerNumber - 1][i].setOpaque(false);
            playerTypeRadioButtons[playerNumber - 1][i].setFont(boldFont);
            playerTypeRadioButtons[playerNumber - 1][i].setForeground(Color.BLACK);
            playerTypeGroup.add(playerTypeRadioButtons[playerNumber - 1][i]);
            columnPanel.add(playerTypeRadioButtons[playerNumber - 1][i]);
            columnPanel.add(Box.createVerticalStrut(10));

            playerTypeRadioButtons[playerNumber - 1][i].addActionListener(e -> {
                updatePlayerColumn(playerNumber - 1);
                updatePlayerIcon(playerNumber -1);
            });
        }

        // Default None
        playerTypeRadioButtons[playerNumber - 1][0].setSelected(true);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(boldFont);
        nameLabel.setForeground(Color.BLACK);
        columnPanel.add(nameLabel);
   
        nameFields[playerNumber - 1] = new JTextField(10);
        nameFields[playerNumber - 1].setOpaque(false);
        nameFields[playerNumber - 1].setFont(boldFont);
        nameFields[playerNumber - 1].setForeground(Color.BLACK);
        nameFields[playerNumber - 1].setBorder(BorderFactory.createLineBorder(Color.BLACK));
        nameFields[playerNumber - 1].setMaximumSize(new Dimension(300, 40));
        nameFields[playerNumber - 1].setAlignmentX(Component.CENTER_ALIGNMENT);
        columnPanel.add(nameFields[playerNumber - 1]);
        columnPanel.add(Box.createVerticalStrut(10)); 
        

        // Player Icon
        JPanel iconPanel = new TransparentPanel();
        iconPanel.setLayout(new BoxLayout(iconPanel, BoxLayout.X_AXIS)); 
        iconPanel.setAlignmentX(Component.CENTER_ALIGNMENT); 
        playerIcons[playerNumber - 1] = new JLabel(defaultIcon);
        playerIcons[playerNumber - 1].setOpaque(false);
        playerIcons[playerNumber - 1].setAlignmentX(Component.CENTER_ALIGNMENT);
        playerIcons[playerNumber - 1].setPreferredSize(new Dimension(200, 200));
        iconPanel.add(Box.createHorizontalGlue()); 
        iconPanel.add(playerIcons[playerNumber - 1]);
        iconPanel.add(Box.createHorizontalGlue());
        columnPanel.add(Box.createVerticalStrut(10));
        columnPanel.add(iconPanel);
        columnPanel.add(Box.createVerticalStrut(10));

        // Color Dropdown
        JLabel colorLabel = new JLabel("Color:");
        colorLabel.setFont(boldFont);
        colorLabel.setForeground(Color.BLACK);
        columnPanel.add(colorLabel);
        colorDropdowns[playerNumber - 1] = new JComboBox<>();
        colorDropdowns[playerNumber - 1].addItem("Select Color");
        colorDropdowns[playerNumber - 1].addItem("Red");
        colorDropdowns[playerNumber - 1].addItem("Blue");
        colorDropdowns[playerNumber - 1].addItem("Green");
        colorDropdowns[playerNumber - 1].addItem("Yellow");
        colorDropdowns[playerNumber - 1].addItem("Magenta");
        colorDropdowns[playerNumber - 1].addItem("Orange");
        colorDropdowns[playerNumber - 1].addItem("Pink");
        colorDropdowns[playerNumber - 1].setSelectedIndex(0);
        colorDropdowns[playerNumber - 1].setOpaque(false);
        colorDropdowns[playerNumber - 1].setFont(boldFont);
        colorDropdowns[playerNumber - 1].setForeground(Color.BLACK);
        colorDropdowns[playerNumber - 1].setBackground(new Color(255, 105, 180));
        colorDropdowns[playerNumber - 1].addActionListener(e -> updateColorDropdowns());
        columnPanel.add(colorDropdowns[playerNumber - 1]);

        return columnPanel;
    }

    private void updatePlayerColumn(int playerIndex) {
        String playerType = getSelectedPlayerType(playerIndex);

        if (playerType.equals("None")) {
            nameFields[playerIndex].setText("");
            nameFields[playerIndex].setEditable(false);
            colorDropdowns[playerIndex].setEnabled(false);
        } else if (playerType.equals("Human Player")) {
            nameFields[playerIndex].setText("");
            nameFields[playerIndex].setEditable(true);
            colorDropdowns[playerIndex].setEnabled(true);
        } else {
            nameFields[playerIndex].setText("Computer " + (playerIndex + 1));
            nameFields[playerIndex].setEditable(false);
            colorDropdowns[playerIndex].setEnabled(true);
        }

        updateColorDropdowns();
    }

    private void updatePlayerIcon(int playerIndex) {
        String playerType = getSelectedPlayerType(playerIndex);
        
        if (playerType.equals("None")) {
            playerIcons[playerIndex].setIcon(defaultIcon);
        } else if (playerType.equals("Computer Hard")) {
            playerIcons[playerIndex].setIcon(hardComputerIcon);
        } else if (playerType.equals("Computer Easy")) {
            playerIcons[playerIndex].setIcon(easyComputerIcon);
        }else { // Human player
            playerIcons[playerIndex].setIcon(humanIcon);
        }
    }

    private String getSelectedPlayerType(int playerIndex) {
        for (int i = 0; i < 4; i++) {
            if (playerTypeRadioButtons[playerIndex][i].isSelected()) {
                return playerTypeRadioButtons[playerIndex][i].getText();
            }
        }
        return "None";
    }

    private void updateColorDropdowns() {
        //track selected colors
        selectedColors.clear();
        for (int i = 0; i < 4; i++) {
            if (colorDropdowns[i].isEnabled()) {
                String selectedColor = (String) colorDropdowns[i].getSelectedItem();
                if (selectedColor != null && !selectedColor.equals("Select Color")) {
                    selectedColors.add(selectedColor);
                }
            }
        }

        //disable selected colors in other dropdowns
        for (int i = 0; i < 4; i++) {
            if (colorDropdowns[i].isEnabled()) {
                String currentSelection = (String) colorDropdowns[i].getSelectedItem();
                colorDropdowns[i].removeAllItems();
                colorDropdowns[i].addItem("Select Color"); // Add placeholder item
                String[] allColors = { "Red", "Blue", "Green", "Yellow", "Magenta", "Orange", "Pink" };
                for (String color : allColors) {
                    if (!selectedColors.contains(color) || color.equals(currentSelection)) {
                        colorDropdowns[i].addItem(color);
                    }
                }
                if (currentSelection != null) {
                    colorDropdowns[i].setSelectedItem(currentSelection);
                }
            }
        }
    }

    private void restartSetup() {

        for (int i = 0; i < 4; i++) {
            playerTypeRadioButtons[i][0].setSelected(true);
            nameFields[i].setText("");
            colorDropdowns[i].setSelectedIndex(0);
            playerIcons[i].setIcon(defaultIcon);
            updatePlayerColumn(i);
        }
    }

    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            Color color1 = new Color(255, 105, 180);
            Color color2 = Color.WHITE;

            GradientPaint gradient = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
            g2d.setPaint(gradient);

            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private static class TransparentPanel extends JPanel {
        public TransparentPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {

        }
    }

    private void startGame() {
        int playerCount = 0;
        for (int i = 0; i < 4; i++) {
            if (!getSelectedPlayerType(i).equals("None")) {
                playerCount++;
            }
        }
        if (playerCount < 2) {
            JOptionPane.showMessageDialog(this, "Please select at least 2 players.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        for (int i = 0; i < 4; i++) {
            if (!getSelectedPlayerType(i).equals("None")) {
                String selectedColor = (String) colorDropdowns[i].getSelectedItem();
                if (selectedColor == null || selectedColor.equals("Select Color")) {
                    JOptionPane.showMessageDialog(this, "Please select a color for all players.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }
        List<Player> participants = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            String playerType = getSelectedPlayerType(i);
            if (!playerType.equals("None")) {
                String name = nameFields[i].getText();
                Color color = getColorFromSelection((String) colorDropdowns[i].getSelectedItem());
                if (playerType.equals("Human Player")) {
                    participants.add(new Human(name, color));
                } else if (playerType.equals("Computer Easy")) {
                    participants.add(new Computer(name, color, Computer.Difficulty.EASY));
                } else if (playerType.equals("Computer Hard")) {
                    participants.add(new Computer(name, color, Computer.Difficulty.HARD));
                }
            }
        }
      
        int squareSize = 100; // default
        String sizeSelection = (String) squareSizeDropdown.getSelectedItem();
        if (sizeSelection != null) {
            switch (sizeSelection) {
                case "Small":
                    squareSize = 80;
                    break;
                case "Medium":
                    squareSize = 100;
                    break;
                case "Large":
                    squareSize = 120;
                    break;
            }
        }
        
        new KiviBoard(squareSize, participants).buildGUI();
        dispose();
    }

    private Color getColorFromSelection(String color) {
        return switch (color) {
            case "Red" -> Color.RED;
            case "Blue" -> Color.BLUE;
            case "Green" -> Color.GREEN;
            case "Yellow" -> Color.YELLOW;
            case "Magenta" -> Color.MAGENTA;
            case "Orange" -> Color.ORANGE;
            case "Pink" -> Color.PINK;
            default -> Color.BLACK;
        };
    }

}