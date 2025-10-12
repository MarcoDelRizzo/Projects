import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

//start button check
//move color blind button

public class SetUpAGameGUI extends JFrame {
	private JComboBox<String> playerCountDropdown;
    private JComboBox<String> modeDropdown;
    private JComboBox<String> humanPlayersDropdown;
    private JPanel computerDifficultyPanel;
    private JTextField[] nameFields;
    private JComboBox<String>[] colorDropdowns;
    private JButton startGameButton, restartSetupButton;
    private Set<String> selectedColors = new HashSet<>();

    public SetUpAGameGUI() {
        setTitle("Kivi Game Set Up");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 3, 10, 10)); // 3 rows, 3 columns, with spacing
        
        //adding icon
        ImageIcon image= new ImageIcon("KiviBoard.png");
        setIconImage(image.getImage()); //change icon of frame
        

        // Row 1
        add(createPlayerCountPanel()); // (1,1)
        add(createModePanel()); // (1,2)
        add(createHumanPlayersPanel()); // (1,3)

        // Row 2
        add(createComputerDifficultyPanel()); // (2,1)
        add(createNameInputPanel()); // (2,2)
        add(createColorSelectionPanel()); // (2,3)

       
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        startGameButton = new JButton("Start Game");
        restartSetupButton = new JButton("Restart Setup");
        bottomPanel.add(startGameButton);
        bottomPanel.add(restartSetupButton);
        add(bottomPanel);

        
        playerCountDropdown.addActionListener(e -> updateUI());
        modeDropdown.addActionListener(e -> updateUI());
        humanPlayersDropdown.addActionListener(e -> updateUI());
        restartSetupButton.addActionListener(e-> restartSetup());
        
        
        startGameButton.addActionListener(e-> startGameButton());
        updateUI();
    }
    
    private void startGameButton() {
        int playerCount = Integer.parseInt((String) playerCountDropdown.getSelectedItem());
        int humanPlayers = Integer.parseInt((String) humanPlayersDropdown.getSelectedItem());
        int computerPlayers = playerCount - humanPlayers; 
        List<Player> participants = new ArrayList<>(); 

        String[] validColors = {"Red", "Blue", "Green", "Yellow", "Magenta", "Cyan", "Orange"};

       
        for (int i = 0; i < humanPlayers; i++) {
            String name = nameFields[i].getText();  
            Color color = getColorFromSelection((String) colorDropdowns[i].getSelectedItem());
            participants.add(new Human(name, color)); 
        }

        //duplicate tracking
        Set<String> usedColors = new HashSet<>();
        for (int i = 0; i < humanPlayers; i++) {
            usedColors.add((String) colorDropdowns[i].getSelectedItem());
        }

        //add computer players to the list
        for (int i = 0; i < computerPlayers; i++) {
            String computerName = "Computer " + (i + 1);

            
            String randomColor = getRandomColor(validColors, usedColors);
            Color color = getColorFromSelection(randomColor);  

            participants.add(new Computer(computerName, color));  //add Computer with random color
            usedColors.add(randomColor);  }

        
        new KiviBoard(100, participants).buildGUI();
        dispose();
    }
    private String getRandomColor(String[] validColors, Set<String> usedColors) {
        List<String> availableColors = new ArrayList<>();
        
       
        for (String color : validColors) {
            if (!usedColors.contains(color)) {
                availableColors.add(color);
            }
        }

       
        if (!availableColors.isEmpty()) {
            Random rand = new Random();
            return availableColors.get(rand.nextInt(availableColors.size()));  // Get a random color
        }

        return "Gray";
    }



    private Color getColorFromSelection(String color) {
        return switch (color) {
            case "Red" -> Color.RED;
            case "Blue" -> Color.BLUE;
            case "Green" -> Color.GREEN;
            case "Yellow" -> Color.YELLOW;
            case "Magenta" -> Color.MAGENTA;
            case "Orange" -> Color.ORANGE;
            default -> Color.BLACK;
        };
    }
    private JPanel createPlayerCountPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createTitledBorder("Number of Players"));
        playerCountDropdown = new JComboBox<>(new String[]{"2", "3", "4"});
        panel.add(playerCountDropdown);
        return panel;
    }

    private JPanel createModePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createTitledBorder("Game Mode"));
        modeDropdown = new JComboBox<>(new String[]{"Human vs. Human", "Human vs. Computer"});
        panel.add(modeDropdown);
        return panel;
    }

    private JPanel createHumanPlayersPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createTitledBorder("Human Players"));
        humanPlayersDropdown = new JComboBox<>(new String[]{"1", "2", "3", "4"});
        panel.add(humanPlayersDropdown);
        return panel;
    }

    private JPanel createComputerDifficultyPanel() {
        computerDifficultyPanel = new JPanel();
        computerDifficultyPanel.setBorder(BorderFactory.createTitledBorder("Computer Difficulty"));
        computerDifficultyPanel.setLayout(new BoxLayout(computerDifficultyPanel, BoxLayout.Y_AXIS));
        return computerDifficultyPanel;
    }

    private JPanel createNameInputPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Player Names"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        nameFields = new JTextField[4];
        for (int i = 0; i < 4; i++) {
            nameFields[i] = new JTextField(10);
            panel.add(new JLabel("Player " + (i + 1) + ":"));
            panel.add(nameFields[i]);
        }
        return panel;
    }

    private JPanel createColorSelectionPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Player Colors"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        colorDropdowns = new JComboBox[4];
        String[] colors = {"Red", "Blue", "Green", "Yellow", "Magenta", "Orange", "Pink"};
        for (int i = 0; i < 4; i++) {
            colorDropdowns[i] = new JComboBox<>(colors);
            colorDropdowns[i].addActionListener(e -> updateColorDropdowns());
            panel.add(new JLabel("Player " + (i + 1) + ":"));
            panel.add(colorDropdowns[i]);
        }
        JButton colorBlindModeButton = new JButton("Color Blind Mode");
        panel.add(colorBlindModeButton);
        return panel;
    }

    private void updateUI() {
        int playerCount = Integer.parseInt((String) playerCountDropdown.getSelectedItem());
        String mode = (String) modeDropdown.getSelectedItem();
        int humanPlayers = Integer.parseInt((String) humanPlayersDropdown.getSelectedItem());

        // Update Computer Difficulty Panel
        computerDifficultyPanel.removeAll();
        if (mode.equals("Human vs. Computer")) {
            int computerPlayers = playerCount - humanPlayers;
            for (int i = 1; i <= computerPlayers; i++) {
                JLabel label = new JLabel("Computer " + i + ":");
                JComboBox<String> difficultyDropdown = new JComboBox<>(new String[]{"Easy", "Hard"});
                computerDifficultyPanel.add(label);
                computerDifficultyPanel.add(difficultyDropdown);
            }
        }
        computerDifficultyPanel.revalidate();
        computerDifficultyPanel.repaint();

       
     
        for (int i = 0; i < 4; i++) {
            nameFields[i].setVisible(i < humanPlayers); 
            nameFields[i].setEnabled(i < humanPlayers); 
            colorDropdowns[i].setVisible(i < humanPlayers); 
            colorDropdowns[i].setEnabled(i < humanPlayers); 
        }
        
        //reset selected colors
        selectedColors.clear();
        updateColorDropdowns();
    }
    
    private void updateColorDropdowns() {
        //track selected colors
        selectedColors.clear();
        for (int i = 0; i < 4; i++) {
            if (colorDropdowns[i].isVisible() && colorDropdowns[i].isEnabled()) {
                String selectedColor = (String) colorDropdowns[i].getSelectedItem();
                if (selectedColor != null) {
                    selectedColors.add(selectedColor);
                }
            }
        }

        //disable selected colors in other dropdowns
        for (int i = 0; i < 4; i++) {
            if (colorDropdowns[i].isVisible() && colorDropdowns[i].isEnabled()) {
                String currentSelection = (String) colorDropdowns[i].getSelectedItem();
                colorDropdowns[i].removeAllItems();
                String[] allColors = {"Red", "Blue", "Green", "Yellow", "Magenta", "Orange", "Pink"}; // Expanded list of colors
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
        //reset all components to their initial state
        playerCountDropdown.setSelectedIndex(0);
        modeDropdown.setSelectedIndex(0);
        humanPlayersDropdown.setSelectedIndex(0);
        updateUI();
    }
    
    }
