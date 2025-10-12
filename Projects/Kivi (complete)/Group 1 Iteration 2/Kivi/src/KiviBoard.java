import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;

public class KiviBoard extends JPanel {
    private static final int SIZE = 7;
    private int squareSize = 80;
    private Square[][] squares;
    private static final Color LIGHT_PINK = new Color(255, 176, 203);
    private static final Color DARK_PINK = new Color(211, 64, 114);
    private static final Color WHITE = new Color(230, 230, 232);
    private Map<Integer, Image> imageMap = new HashMap<>();
    private JFrame frame;

    private int[] whiteIndex = {0,3,6,9,11,14,20,22,26,28,34,37,39, 42, 45, 48};
    private int[] lightPinkIndex = {1,2,4,5,7,13,15,16,17,19,21,23,25,27,29,31,32,33,35,41,43,44,46,47};
    private Set<Integer> twoPair = Set.of(0, 26, 34, 45);
    private Set<Integer> threePair = Set.of(3, 6, 9, 28);
    private Set<Integer> TwoOf3ofAKind = Set.of(12, 24, 36);
    private Set<Integer> four = Set.of(15,32,44,47);
    private Set<Integer>  threePairs = Set.of(10, 30, 40);
    private Set<Integer> fourInARow = Set.of(11, 14, 37,42,47);
    private Set<Integer> fourAndTwo = Set.of(8 ,18, 38);
    private Set<Integer> fullHouse = Set.of(20, 22, 39,48);
    private Set<Integer> straight = Set.of(1,17,27, 29);
    private Set<Integer> total12OrUnder = Set.of(2,21,25,41);
    private Set<Integer> total135 = Set.of(4,19,31, 35,46);
    private Set<Integer> total246 = Set.of(5,7,23);
    
    private List<Player> participants;
    private Player currentPlayer;
    
    private Color validSquareColor = Color.YELLOW;    
    private DicePanel dicePanel = new DicePanel();
 
    public KiviBoard(int squareSize, List<Player> participants) {
    	this.participants = participants;
    	this.squareSize = squareSize;
        this.squares = new Square[SIZE][SIZE];
        loadImages();
        setPreferredSize(new Dimension(SIZE * squareSize, SIZE * squareSize));
        initializeBoard();
        currentPlayer = participants.get(0);
        currentPlayer.setIsTurn(true);
        generateValidSquares();
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
                        passTurn();
                        generateValidSquares();
                        repaint();
                    }
                }
            }
        });
    }

    private void passTurn() {
        currentPlayer.setIsTurn(false);
        int nextPlayerIndex = (participants.indexOf(currentPlayer) + 1) % participants.size();
        currentPlayer = participants.get(nextPlayerIndex);
        currentPlayer.setIsTurn(true); 
        generateValidSquares();
        repaint();
    }

    private void loadImages() {
        imageMap.put(0, loadImage("images/3pair.png"));
        imageMap.put(1, loadImage("images/2pair.png"));
        imageMap.put(2, loadImage("images/2of3ofAKind.png"));
        imageMap.put(3, loadImage("images/3pairs.png"));
        imageMap.put(4, loadImage("images/4inARow.png"));
        imageMap.put(5, loadImage("images/4and2.png"));
        imageMap.put(6, loadImage("images/FullHouse.png"));
        imageMap.put(7, loadImage("images/straight.png"));
        imageMap.put(8, loadImage("images/total12OrUnder.png"));
        imageMap.put(9, loadImage("images/total135.png"));
        imageMap.put(10, loadImage("images/total246.png"));
        imageMap.put(11, loadImage("images/four.png"));
        imageMap.put(12, loadImage("images/TotalOver30.png"));
    }

    private Image loadImage(String path) {
        try {
            return new ImageIcon(getClass().getClassLoader().getResource(path)).getImage();
        } catch (Exception e) {
            System.err.println("Error loading image: " + path);
            return null;
        }
    }
    
    private void initializeBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                int index = row * SIZE + col;
                Color bgColor = DARK_PINK;
                Image img = imageMap.get(12);

                if (twoPair.contains(index)) {
                    img = imageMap.get(1);
                }
                if (threePair.contains(index)) {
                    img = imageMap.get(0);
                }
                if (TwoOf3ofAKind.contains(index)) {
                    img = imageMap.get(2);
                }
                if (threePairs.contains(index)) {
                    img = imageMap.get(3);
                }
                if (fourInARow.contains(index)) {
                    img = imageMap.get(4);
                }
                if (fourAndTwo.contains(index)) {
                    img = imageMap.get(5);
                }
                if (fullHouse.contains(index)) {
                    img = imageMap.get(6);
                }
                if (straight.contains(index)) {
                    img = imageMap.get(7);
                }
                if (total12OrUnder.contains(index)) {
                    img = imageMap.get(8);
                }
                if (total135.contains(index)) {
                    img = imageMap.get(9);
                }
                if (total246.contains(index)) {
                    img = imageMap.get(10);
                }
                if (four.contains(index)) {
                    img = imageMap.get(11);
                }

                if (contains(whiteIndex, index)) {
                    bgColor = WHITE;
                } else if (contains(lightPinkIndex, index)) {
                    bgColor = LIGHT_PINK;
                }

                squares[row][col] = new Square(bgColor, img);
            }
        }
        generateValidSquares();
    }

    private boolean contains(int[] array, int value) {
        for (int num : array) {
            if (num == value) {
                return true;
            }
        }
        return false;
    }

    private void generateValidSquares() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (squares[row][col].stoneColor == null) {
                    squares[row][col].backgroundColor = squares[row][col].originalColor;
                }
            }
        }

        Set<Integer> validSquareIndices = new HashSet<>();
        while (validSquareIndices.size() < 5) {
            int randomIndex = (int) (Math.random() * SIZE * SIZE);
            if (squares[randomIndex / SIZE][randomIndex % SIZE].stoneColor == null) {
                validSquareIndices.add(randomIndex);
            }
        }

        for (Integer index : validSquareIndices) {
            int row = index / SIZE;
            int col = index % SIZE;
            squares[row][col].backgroundColor = validSquareColor;
        }

        repaint();
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
        JMenuItem loadGame = new JMenuItem("Load Game");
        file.add(saveGame);
        file.add(loadGame);

        //Add settings options
        JMenu settings = new JMenu("Settings");
        JMenuItem displaySettings = new JMenuItem("Display Settings");
        displaySettings.addActionListener(_ -> {
            new DisplaySettingsGUI(this);
        });
        settings.add(displaySettings);

        menuBar.add(file);
        menuBar.add(settings);
    }

    public void rightPanelGUI(JPanel rightPanel) {
        rightPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel rollsLeftLabel = new JLabel("Roll count: Under Construction", SwingConstants.CENTER);
        rollsLeftLabel.setFont(new Font("Arial", Font.BOLD, 14));

        gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 10, 0);
        rightPanel.add(rollsLeftLabel, gbc);

//        JButton confirmButton = new JButton("C");
//        confirmButton.setFont(new Font("Arial", Font.BOLD, 16));
//
//        gbc.gridy = 2;
//        gbc.insets = new Insets(10, 0, 10, 0);
//        rightPanel.add(confirmButton, gbc);
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

        int squareSize = 100;
        KiviBoard boardPanel = new KiviBoard(squareSize, participants);

        JPanel centerPanel = new JPanel();
        centerPanel.add(boardPanel);

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
}
