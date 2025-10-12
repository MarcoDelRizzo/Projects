import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class KiviBoard extends JPanel implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final int SIZE = 7;
    private int squareSize = 80;
    private Square[][] squares;
    private static final Color LIGHT_PINK = new Color(255, 176, 203);
    private static final Color DARK_PINK = new Color(211, 64, 114);
    private static final Color WHITE = new Color(230, 230, 232);
    private Map<Integer, byte[]> imageMap = new HashMap<>();
    private transient JFrame frame;
    private transient JLabel rollsLeftLabel;
    private transient List<JLabel> scoreLabels = new ArrayList<>();
    private List<Player> participants;
    private Player currentPlayer;
    private Color validSquareColor = Color.YELLOW;    
    private DicePanel dicePanel;
    
    private Map<String, Integer> combinations = new HashMap<>();
    private int[] whiteIndex = {0,3,6,9,11,14,20,22,26,28,34,37,39, 42, 45, 48};
    private int[] lightPinkIndex = {1,2,4,5,7,13,15,16,17,19,21,23,25,27,29,31,32,33,35,41,43,44,46,47};
    private Set<Integer> twoPairs = Set.of(0, 26, 34, 45);
    private Set<Integer> threeOfAKind = Set.of(3, 6, 9, 28);
    private Set<Integer> twoThreeOfAKind = Set.of(12, 24, 36);
    private Set<Integer> fourOfAKind = Set.of(15,32,44,47);
    private Set<Integer> threePairs = Set.of(10, 30, 40);
    private Set<Integer> littleStraight = Set.of(11, 14, 37,42,47);
    private Set<Integer> fourOfAKindAndAPair = Set.of(8 ,18, 38);
    private Set<Integer> fullHouse = Set.of(20, 22, 39,48);
    private Set<Integer> largeStraight = Set.of(1,17,27, 29);
    private Set<Integer> total12OrUnder = Set.of(2,21,25,41);
    private Set<Integer> allOdd = Set.of(4,19,31, 35,46);
    private Set<Integer> allEven = Set.of(5,7,23);
    private Set<Integer> total30OrOver = Set.of();
 
 
    public KiviBoard(int squareSize, List<Player> participants) {
        this.participants = participants;
        this.squareSize = squareSize;
        this.squares = new Square[SIZE][SIZE];
        dicePanel = new DicePanel(this);
        loadImages();
        setPreferredSize(new Dimension(SIZE * squareSize, SIZE * squareSize));
        BoardPanel();
        combinations.put("twoPairs", 10);
        combinations.put("threeOfAKind", 12);
        combinations.put("twoThreeOfAKind", 13);
        combinations.put("fourOfAKind", 14);
        combinations.put("threePairs", 20);
        combinations.put("littleStraight", 35);
        combinations.put("fourOfAKindAndAPair", 40);
        combinations.put("fullHouse", 45);
        combinations.put("largeStraight", 50);
        combinations.put("total12OrUnder", 5);
        combinations.put("allOdd", 6);
        combinations.put("allEven", 7);
        currentPlayer = participants.get(0);
        currentPlayer.setIsTurn(true);
        dicePanel.setRollButtonEnabled(true);
        dicePanel.setConfirmButtonEnabled(false);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!currentPlayer.getIsTurn()) return;
                int row = e.getY() / squareSize;
                int col = e.getX() / squareSize;
                if (squares[row][col].stoneColor == null && squares[row][col].backgroundColor == validSquareColor) {
                    if (currentPlayer.getStones() > 0) {
                        squares[row][col].stoneColor = currentPlayer.getColor();
                        currentPlayer.decrementStones();
                        squares[row][col].backgroundColor = squares[row][col].originalColor;
                        int index = row * SIZE + col;
                        addScoreForClickedSquare(index);
                        refreshScores();
                        clearHighlights();
                        passTurn();
                        repaint();
                    }
                }
            }
        });
    }

    private void clearHighlights() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (squares[row][col].stoneColor == null) {
                    squares[row][col].backgroundColor = squares[row][col].originalColor;
                }
            }
        }
        repaint();
    }

    private void addScoreForClickedSquare(int index) {
        if (twoPairs.contains(index)) {
            currentPlayer.addScore(combinations.get("twoPairs"));
        } else if (threeOfAKind.contains(index)) {
            currentPlayer.addScore(combinations.get("threeOfAKind"));
        } else if (twoThreeOfAKind.contains(index)) {
            currentPlayer.addScore(combinations.get("twoThreeOfAKind"));
        } else if (fourOfAKind.contains(index)) {
            currentPlayer.addScore(combinations.get("fourOfAKind"));
        } else if (threePairs.contains(index)) {
            currentPlayer.addScore(combinations.get("threePairs"));
        } else if (littleStraight.contains(index)) {
            currentPlayer.addScore(combinations.get("littleStraight"));
        } else if (fourOfAKindAndAPair.contains(index)) {
            currentPlayer.addScore(combinations.get("fourOfAKindAndAPair"));
        } else if (fullHouse.contains(index)) {
            currentPlayer.addScore(combinations.get("fullHouse"));
        } else if (largeStraight.contains(index)) {
            currentPlayer.addScore(combinations.get("largeStraight"));
        } else if (total12OrUnder.contains(index)) {
            currentPlayer.addScore(combinations.get("total12OrUnder"));
        } else if (allOdd.contains(index)) {
            currentPlayer.addScore(combinations.get("allOdd"));
        } else if (allEven.contains(index)) {
            currentPlayer.addScore(combinations.get("allEven"));
        }
    }

    public void highlightSquaresForDice(List<Integer> diceValues) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (squares[row][col].stoneColor == null) {
                    squares[row][col].backgroundColor = squares[row][col].originalColor;
                }
            }
        }
        if (checkTwoPairs(diceValues)) {
            highlightSet(twoPairs);
        }
        if (checkThreeOfAKind(diceValues)) {
            highlightSet(threeOfAKind);
        }
        if (checkTwoTimesThreeOfAKind(diceValues)) {
            highlightSet(twoThreeOfAKind);
        }
        if (checkFourOfAKind(diceValues)) {
            highlightSet(fourOfAKind);
        }
        if (checkThreePairs(diceValues)) {
            highlightSet(threePairs);
        }
        if (checkLittleStraight(diceValues)) {
            highlightSet(littleStraight);
        }
        if (checkFourOfAKindAndAPair(diceValues)) {
            highlightSet(fourOfAKindAndAPair);
        }
        if (checkFullHouse(diceValues)) {
            highlightSet(fullHouse);
        }
        if (checkLargeStraight(diceValues)) {
            highlightSet(largeStraight);
        }
        if (checkTwelveOrFewer(diceValues)) {
            highlightSet(total12OrUnder);
        }
        if (check30OrOver(diceValues)) {
            highlightSet(total30OrOver);
        }
        if (checkAllOdd(diceValues)) {
            highlightSet(allOdd);
        }
        if (checkAllEven(diceValues)) {
            highlightSet(allEven);
        }
        repaint();
    }

    private boolean checkTwoPairs(List<Integer> dice) {
        int[] freq = buildFrequency(dice);
        int pairs = 0;
        for (int i = 1; i <= 6; i++) {
            if (freq[i] >= 2) {
                pairs++;
            }
        }
        return pairs == 2;
    }

    private boolean checkThreeOfAKind(List<Integer> dice) {
        int[] freq = buildFrequency(dice);
        for (int i = 1; i <= 6; i++) {
            if (freq[i] >= 3) {
                return true;
            }
        }
        return false;
    }

    private boolean checkLittleStraight(List<Integer> dice) {
        return longestConsecutiveRun(dice) >= 4;
    }

    private boolean checkFullHouse(List<Integer> dice) {
        int[] freq = buildFrequency(dice);
        int tripleCount = 0;
        int pairCount = 0;
        int singleCount = 0;
        
        for (int i = 1; i <= 6; i++) {
            if (freq[i] == 3) {
                tripleCount++;
            } else if (freq[i] == 2) {
                pairCount++;
            } else if (freq[i] == 1) {
                singleCount++;
            } else if (freq[i] > 3) {
                // If you have 4+ of any value, that isn't a "3-2-1" full house
                return false;
            }
        }
        
        return (tripleCount == 1 && pairCount == 1 && singleCount == 1);
    }

    private boolean checkFourOfAKind(List<Integer> dice) {
        int[] freq = buildFrequency(dice);
        for (int i = 1; i <= 6; i++) {
            if (freq[i] >= 4) {
                return true;
            }
        }
        return false;
    }

    private boolean checkLargeStraight(List<Integer> dice) {
        return longestConsecutiveRun(dice) >= 5;
    }

    private boolean checkAllEven(List<Integer> dice) {
        for (int d : dice) {
            if (d % 2 != 0) {
                return false;
            }
        }
        return true;
    }

    private boolean checkAllOdd(List<Integer> dice) {
        for (int d : dice) {
            if (d % 2 == 0) {
                return false;
            }
        }
        return true;
    }

    private boolean checkTwelveOrFewer(List<Integer> dice) {
        int sum = dice.stream().mapToInt(Integer::intValue).sum();
        return sum <= 12;
    }

    private boolean check30OrOver(List<Integer> dice) {
        int sum = dice.stream().mapToInt(Integer::intValue).sum();
        return sum >= 30;
    }

    private boolean checkThreePairs(List<Integer> dice) {
        int[] freq = buildFrequency(dice);
        int pairs = 0;
        for (int i = 1; i <= 6; i++) {
            if (freq[i] >= 2) {
                pairs++;
            }
        }
        return pairs == 3;
    }

    private boolean checkTwoTimesThreeOfAKind(List<Integer> dice) {
        int[] freq = buildFrequency(dice);
        int tripleCount = 0;
        for (int i = 1; i <= 6; i++) {
            if (freq[i] >= 3) {
                tripleCount++;
            }
        }
        return tripleCount >= 2;
    }

    private boolean checkFourOfAKindAndAPair(List<Integer> dice) {
        int[] freq = buildFrequency(dice);
        boolean foundFour = false;
        boolean foundPair = false;
        for (int i = 1; i <= 6; i++) {
            if (freq[i] >= 4) {
                foundFour = true;
            } else if (freq[i] >= 2) {
                foundPair = true;
            }
        }
        return foundFour && foundPair;
    }

    private int[] buildFrequency(List<Integer> dice) {
        int[] freq = new int[7];
        for (int d : dice) {
            freq[d]++;
        }
        return freq;
    }

    private int longestConsecutiveRun(List<Integer> diceValues) {
        List<Integer> sorted = new ArrayList<>(diceValues);
        Collections.sort(sorted);
        int longest = 1;
        int current = 1;
        for (int i = 1; i < sorted.size(); i++) {
            if (sorted.get(i) == sorted.get(i - 1) + 1) {
                current++;
            } else if (!sorted.get(i).equals(sorted.get(i - 1))) {
                current = 1;
            }
            longest = Math.max(longest, current);
        }
        return longest;
    }

    private void highlightSet(Set<Integer> indices) {
        for (Integer idx : indices) {
            int row = idx / SIZE;
            int col = idx % SIZE;
            if (squares[row][col].stoneColor == null) {
                squares[row][col].backgroundColor = validSquareColor;
            }
        }
    }
    
    
    public void passTurn() {
    	dicePanel.resetDice();
    	dicePanel.setRollButtonEnabled(true);
        currentPlayer.setIsTurn(false);
        int nextPlayerIndex = (participants.indexOf(currentPlayer) + 1) % participants.size();
        currentPlayer = participants.get(nextPlayerIndex);
        currentPlayer.setIsTurn(true);
        repaint();
    }
    public void actionPerformed(ActionEvent e) {
       dicePanel.setRollButtonEnabled(true);                    
    }

    private byte[] imageToBytes(String path) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("Error loading image: Resource not found for path: " + path);
                return null;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            System.err.println("Error reading image file: " + path);
        }
        return null;
    }
    

    private void loadImages() {
        imageMap.put(0, imageToBytes("images/3pair.png"));
        imageMap.put(1, imageToBytes("images/2pair.png"));
        imageMap.put(2, imageToBytes("images/2of3ofAKind.png"));
        imageMap.put(3, imageToBytes("images/3pairs.png"));
        imageMap.put(4, imageToBytes("images/4inARow.png"));
        imageMap.put(5, imageToBytes("images/4and2.png"));
        imageMap.put(6, imageToBytes("images/FullHouse.png"));
        imageMap.put(7, imageToBytes("images/straight.png"));
        imageMap.put(8, imageToBytes("images/total12OrUnder.png"));
        imageMap.put(9, imageToBytes("images/total135.png"));
        imageMap.put(10, imageToBytes("images/total246.png"));
        imageMap.put(11, imageToBytes("images/four.png"));
        imageMap.put(12, imageToBytes("images/TotalOver30.png"));
    }
    private Image bytesToImage(byte[] bytes) {
        if (bytes != null) {
            return new ImageIcon(bytes).getImage();
        }
        return null;
    }

    private Image loadImage(String path) {
        try {
            return new ImageIcon(getClass().getClassLoader().getResource(path)).getImage();
        } catch (Exception e) {
            System.err.println("Error loading image: " + path);
            return null;
        }
    }
    
    private void BoardPanel() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                int index = row * SIZE + col;
                Color bgColor = DARK_PINK;
                byte[] imgBytes = imageMap.get(12);
                Image img = bytesToImage(imgBytes);  // Convert byte array to image

                if (twoPairs.contains(index)) {
                    imgBytes = imageMap.get(1);
                }
                if (threeOfAKind.contains(index)) {
                    imgBytes = imageMap.get(0);
                }
                if (twoThreeOfAKind.contains(index)) {
                    imgBytes = imageMap.get(2);
                }
                if (threePairs.contains(index)) {
                    imgBytes = imageMap.get(3);
                }
                if (littleStraight.contains(index)) {
                    imgBytes = imageMap.get(4);
                }
                if (fourOfAKindAndAPair.contains(index)) {
                    imgBytes = imageMap.get(5);
                }
                if (fullHouse.contains(index)) {
                    imgBytes = imageMap.get(6);
                }
                if (largeStraight.contains(index)) {
                    imgBytes = imageMap.get(7);
                }
                if (total12OrUnder.contains(index)) {
                    imgBytes = imageMap.get(8);
                }
                if (allOdd.contains(index)) {
                    imgBytes = imageMap.get(9);
                }
                if (allEven.contains(index)) {
                    imgBytes = imageMap.get(10);
                }
                if (fourOfAKind.contains(index)) {
                    imgBytes = imageMap.get(11);
                }

                img = bytesToImage(imgBytes);  // Convert byte array to image

                if (contains(whiteIndex, index)) {
                    bgColor = WHITE;
                } else if (contains(lightPinkIndex, index)) {
                    bgColor = LIGHT_PINK;
                }

                squares[row][col] = new Square(bgColor, img);
            }
        }
    }

    private boolean contains(int[] array, int value) {
        for (int num : array) {
            if (num == value) {
                return true;
            }
        }
        return false;
    }
    public void refreshScores() {
        for (int i = 0; i < participants.size(); i++) {
            Player player = participants.get(i);
            JLabel label = scoreLabels.get(i);
            label.setText("Score: " + player.getScore());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                int x = col * squareSize;
                int y = row * squareSize;

                g2.setColor(squares[row][col].backgroundColor);
                g2.fillRect(x, y, squareSize, squareSize);

                if (squares[row][col].image != null) {
                    g2.drawImage(squares[row][col].image, x + 10, y + 10, squareSize - 20, squareSize - 20, this);
                }

                if (squares[row][col].stoneColor != null) {
                    Color translucentStoneColor = new Color(squares[row][col].stoneColor.getRed(),
                                                            squares[row][col].stoneColor.getGreen(),
                                                            squares[row][col].stoneColor.getBlue(), 128);
                    g2.setColor(translucentStoneColor);
                    int stoneDiameter = (int) (squareSize * 0.7);
                    g2.fillOval(x + (squareSize - stoneDiameter) / 2, y + (squareSize - stoneDiameter) / 2, stoneDiameter, stoneDiameter);
                }

                g2.setColor(Color.BLACK);
                g2.drawRect(x, y, squareSize, squareSize);
            }
        }
    }

    public void leftPanelGUI(JPanel leftPanel) {
        leftPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        Font labelFont = new Font("Arial", Font.BOLD, 16);
        scoreLabels.clear();

        for (int i = 0; i < participants.size(); i++) {
            Player players = participants.get(i);
            JLabel playerLabel = new JLabel(players.getName());
            playerLabel.setFont(labelFont);
            
            gbc.gridx = 0;
            gbc.gridy = i * 3;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            leftPanel.add(playerLabel, gbc);

            JLabel scoreLabel = new JLabel("Score: " + players.getScore());
            scoreLabel.setFont(labelFont);
            gbc.gridy = i * 3 + 1;
            leftPanel.add(scoreLabel, gbc);
            
            scoreLabels.add(scoreLabel);

            JPanel colorPanel = new JPanel();
            colorPanel.setBackground(players.getColor());
            colorPanel.setPreferredSize(new Dimension(90, 30));
            gbc.gridy = i * 3 + 2;
            leftPanel.add(colorPanel, gbc);
        }
    }
    public void JMenuBarGUI(JMenuBar menuBar)
    {
        //Add file options
        JMenu file = new JMenu("File");
        JMenuItem saveGame = new JMenuItem("Save Game");
        saveGame.addActionListener(_ -> {
            new SaveGameGUI(this);
        });
        JMenuItem loadGame = new JMenuItem("Load Game");
        loadGame.addActionListener(_ -> {
            new LoadGameGUI(this);
        });
        file.add(saveGame);
        file.add(loadGame);

        //Add settings options
        JMenu settings = new JMenu("Settings");
        JMenuItem displaySettings = new JMenuItem("Display Settings");
        displaySettings.addActionListener(_ -> {
            new DisplaySettingsGUI(this);
        });
        settings.add(displaySettings);

     
        JMenu rulesMenu = new JMenu("Rules");
        JMenuItem showRulesItem = new JMenuItem("Show Rules");
        showRulesItem.addActionListener(e -> IntroScreenGUI.dialog());
        rulesMenu.add(showRulesItem);

        menuBar.add(file);
        menuBar.add(settings);
        
        menuBar.add(rulesMenu);
    }

    public void rightPanelGUI(JPanel rightPanel) {
        rightPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        rollsLeftLabel = new JLabel("Rerolls left: " + (dicePanel.getRollCount()-1) + "   ");
        rollsLeftLabel.setFont(new Font("Arial", Font.BOLD, 16));

        gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 10, 0);
        rightPanel.add(rollsLeftLabel, gbc);

    }
    public void updateRollCountLabel() {
        rollsLeftLabel.setText("Rerolls left:  " + (dicePanel.getRollCount()-1) + "   ");
    }
    public void updateResolution(int width, int height) {
        if (frame != null) {
            frame.setSize(width, height);
            frame.revalidate(); // Ensure layout updates
        }
    }

    public void buildGUI() {
        frame = new JFrame("Kivi Board");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());


        JPanel centerPanel = new JPanel();
        centerPanel.add(this);

        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(dicePanel, BorderLayout.SOUTH);
        
        JPanel leftPanel = new JPanel();
        leftPanelGUI(leftPanel);
        frame.add(leftPanel, BorderLayout.WEST);
        
        JPanel rightPanel = new JPanel();
        rightPanelGUI(rightPanel);
        frame.add(rightPanel, BorderLayout.EAST);
        
        JMenuBar menubar = new JMenuBar();
        JMenuBarGUI(menubar);
        frame.setJMenuBar(menubar);

       
        frame.pack();
        frame.setVisible(true);
    }

    //
    public void saveGame(String filename) {
        // Get the project directory (which is the root directory of your project)
        String projectDir = System.getProperty("user.dir");
    
        // Define the path to the 'saves' folder inside the 'src' directory
        File savesDir = new File(projectDir, "saves");
    
        // Create the 'saves' directory if it doesn't exist
        if (!savesDir.exists()) {
            savesDir.mkdirs(); // Create the directory
        }
    
        // Define the file path inside the 'saves' folder
        File f = new File(savesDir, filename);
    
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
            oos.writeObject(this); // Write the current object to the file
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
        }
    }

    public void loadGame(String filename) {

        String gameState;
        // Get the project directory (which is the root directory of your project)
        String projectDir = System.getProperty("user.dir");
    
        // Define the path to the 'saves' folder inside the project directory
        File savesDir = new File(projectDir, "saves");
    
        // Check if the 'saves' directory exists
        if (!savesDir.exists()) {
            System.err.println("The 'saves' directory does not exist.");
            return;
        }
    
        // Define the file path inside the 'saves' folder
        File saveFile = new File(savesDir, filename);
    
        // Check if the save file exists
        if (!saveFile.exists()) {
            System.err.println("Save file does not exist.");
            return;
        }
    
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile))) {
            // Read the saved object (assuming the object saved is of the Game type)
            KiviBoard loadedGame = (KiviBoard) ois.readObject();
            
            // Set the current game state to the loaded game state
            System.out.println("Game loaded successfully!");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading the game: " + e.getMessage());
        }
        
    }      
   
}
    

