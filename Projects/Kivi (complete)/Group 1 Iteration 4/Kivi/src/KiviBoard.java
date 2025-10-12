import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.Timer;

import javax.swing.*;
import javax.swing.border.TitledBorder;

public class KiviBoard extends JPanel implements Serializable {
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
    private transient JPanel leaderBoardPanel;
    private transient JLabel playerTurnLabel;

    
    private List<Player> participants;
    private Player currentPlayer;
    private Color validSquareColor = Color.YELLOW;    
    private DicePanel dicePanel;
    
    private transient boolean specialSixActive = false;    
    private transient boolean specialFreeActive = false;
    private transient boolean waitingForReplacement = false; 
    private transient int replacedStoneIndex = -1;          
    private transient Color replacedStoneColor = null;       


    private Map<String, Integer> combinations = new HashMap<>();
    private int[] whiteIndex = {0,3,6,9,11,14,20,22,26,28,34,37,39,42,45,48};
    private int[] lightPinkIndex = {1,2,4,5,7,13,15,16,17,19,21,23,25,27,29,31,32,33,35,41,43,44,46,47};
    private Set<Integer> twoPairs = Set.of(0, 26, 34, 45);
    private Set<Integer> threeOfAKind = Set.of(3, 6, 9, 28);
    private Set<Integer> twoThreeOfAKind = Set.of(12, 24, 36);
    private Set<Integer> fourOfAKind = Set.of(15,32,44,47);
    private Set<Integer> threePairs = Set.of(10, 30, 40);
    private Set<Integer> littleStraight = Set.of(11, 14, 37,42);
    private Set<Integer> fourOfAKindAndAPair = Set.of(8 ,18, 38);
    private Set<Integer> fullHouse = Set.of(20, 22, 39,48);
    private Set<Integer> largeStraight = Set.of(1,17,27,29);
    private Set<Integer> total12OrUnder = Set.of(2,21,25,41);
    private Set<Integer> allOdd = Set.of(4,19,31,35,46);
    private Set<Integer> allEven = Set.of(5,7,23);
    private Set<Integer> total30OrOver = Set.of();
    
    private boolean colorblindModeEnabled = false;

    private boolean isGameEnded = false;
    
    public KiviBoard(int squareSize, List<Player> participants) {
        this.participants = participants;
        this.squareSize = squareSize;
        this.squares = new Square[SIZE][SIZE];
        dicePanel = new DicePanel(this);
        loadImages();
        setPreferredSize(new Dimension(SIZE * squareSize, SIZE * squareSize));
        BoardPanel();
        
        combinations.put("twoPairs", 1);
        combinations.put("threeOfAKind", 1);
        combinations.put("twoThreeOfAKind", 3);
        combinations.put("fourOfAKind", 2);
        combinations.put("threePairs", 3);
        combinations.put("littleStraight", 1);
        combinations.put("fourOfAKindAndAPair", 3);
        combinations.put("fullHouse", 2);
        combinations.put("largeStraight", 2);
        combinations.put("total12OrUnder", 2);
        combinations.put("allOdd", 2);
        combinations.put("allEven", 2);
        combinations.put("total30orOver", 2);
        
        currentPlayer = participants.get(0);
        currentPlayer.setIsTurn(true);
        dicePanel.setRollButtonEnabled(true);
        dicePanel.setConfirmButtonEnabled(false);
        initializeMouseListener();
       
    }
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    public boolean isColorblindModeEnabled() {
        return colorblindModeEnabled;
    }
    
    private void initTooltips() {
        ToolTipManager.sharedInstance().registerComponent(this);
        ToolTipManager.sharedInstance().setInitialDelay(100);
        ToolTipManager.sharedInstance().setDismissDelay(5000);
    }
    
    private void initializeMouseListener() {
    	initTooltips();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isGameEnded || !currentPlayer.getIsTurn()) return;
                int row = e.getY() / squareSize;
                int col = e.getX() / squareSize;
                Square sq = squares[row][col];
                
                if (specialSixActive) {
                    if (!waitingForReplacement) {
                        if (sq.stoneColor == null) {
                            sq.stoneColor = currentPlayer.getColor();
                            sq.backgroundColor = sq.originalColor;
                            int index = row * SIZE + col;
                            addScoreForClickedSquare(index);
                            clearHighlights();
                            specialSixActive = false;
                            updateLeaderBoard();
                            passTurn();
                            repaint();
                        } else {
                            if (sq.stoneColor.equals(currentPlayer.getColor())) {
                                JOptionPane.showMessageDialog(frame, "You cannot replace your own stone.");
                                return; 
                            }
                            int choice = JOptionPane.showConfirmDialog(frame,
                                "This square is occupied by an opponent's stone.\nDo you want to replace it? (The replaced stone will be moved to a free square of your choosing.)",
                                "Replace Stone", JOptionPane.YES_NO_OPTION);
                            if (choice == JOptionPane.YES_OPTION) {
                                replacedStoneIndex = row * SIZE + col;
                                replacedStoneColor = sq.stoneColor;
                                sq.stoneColor = currentPlayer.getColor();
                                
                                sq.backgroundColor = sq.originalColor;
                                waitingForReplacement = true;
                                JOptionPane.showMessageDialog(frame,
                                    "Now, click on a free square where you want to move the replaced stone.");
                                repaint();
                            }
                        }
                    } else { 
                        if (sq.stoneColor != null) {
                            JOptionPane.showMessageDialog(frame, "Please select a free square to move the replaced stone.");
                            return;
                        }
                        sq.stoneColor = replacedStoneColor;
                        waitingForReplacement = false;
                        specialSixActive = false;
                        clearHighlights();
                        updateLeaderBoard();
                        passTurn();
                        repaint();
                    }
                    return;  
                }
                
                if (specialFreeActive) {
                    if (sq.stoneColor == null) {
                        sq.stoneColor = currentPlayer.getColor();
                        sq.backgroundColor = sq.originalColor;
                        int index = row * SIZE + col;
                        addScoreForClickedSquare(index);
                        clearHighlights();
                        specialFreeActive = false;
                        updateLeaderBoard();
                        passTurn();
                        repaint();
                    }
                    return;
                }
                
                if (sq.stoneColor == null && validSquareColor.equals(sq.backgroundColor)) {
                    if (currentPlayer.getStones() > 0) {
                        sq.stoneColor = currentPlayer.getColor();                        
                        sq.backgroundColor = sq.originalColor;
                        int index = row * SIZE + col;
                        addScoreForClickedSquare(index);
                        clearHighlights();
                        updateLeaderBoard();
                        passTurn();
                        repaint();
                    }
                }
            }
        });
    }


    public void clearHighlights() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                squares[row][col].backgroundColor = squares[row][col].originalColor;
            }
        }
        repaint();
    }
    public void setColorblindMode(boolean enabled) {
        this.colorblindModeEnabled = enabled;
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
        } else {
             currentPlayer.addScore(combinations.get("total30OrOver"));
        }
    }
    public boolean hasValidCombination(List<Integer> dice) {
        if (checkTwoPairs(dice)) return true;
        if (checkThreeOfAKind(dice)) return true;
        if (checkTwoTimesThreeOfAKind(dice)) return true;
        if (checkFourOfAKind(dice)) return true;
        if (checkThreePairs(dice)) return true;
        if (checkLittleStraight(dice)) return true;
        if (checkFourOfAKindAndAPair(dice)) return true;
        if (checkFullHouse(dice)) return true;
        if (checkLargeStraight(dice)) return true;
        if (checkTwelveOrFewer(dice)) return true;
        if (checkAllOdd(dice)) return true;
        if (checkAllEven(dice)) return true;
        return false;
    }

    public void highlightSquaresForDice(List<Integer> diceValues) {
        if (isGameEnded) return;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (squares[row][col].stoneColor == null) {
                    squares[row][col].backgroundColor = squares[row][col].originalColor;
                }
            }
        }
        
        //reset special flags.
        specialSixActive = false;
        specialFreeActive = false;
        
        if (isSixOfAKind(diceValues)) {
            specialSixActive = true;
            //highlight every square.
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    squares[row][col].backgroundColor = validSquareColor;
                }
            }
            repaint();
            return;
        } else if (isFiveOfAKind(diceValues) || isStraightSix(diceValues)) {
            specialFreeActive = true;
            //highlight only free squares.
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    if (squares[row][col].stoneColor == null) {
                        squares[row][col].backgroundColor = validSquareColor;
                    }
                }
            }
            repaint();
            return;
        }
        repaint();

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
    private boolean isSixOfAKind(List<Integer> dice) {
        int[] freq = buildFrequency(dice);
        for (int i = 1; i <= 6; i++) {
            if (freq[i] == 6) {
                return true;
            }
        }
        return false;
    }

    private boolean isFiveOfAKind(List<Integer> dice) {
        int[] freq = buildFrequency(dice);
        for (int i = 1; i <= 6; i++) {
            if (freq[i] == 5) {
                return true;
            }
        }
        return false;
    }

    private boolean isStraightSix(List<Integer> dice) {
        List<Integer> sorted = new ArrayList<>(dice);
        Collections.sort(sorted);
        List<Integer> straight = Arrays.asList(1, 2, 3, 4, 5, 6);
        return sorted.equals(straight);
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
        if (isGameEnded) return;
    	currentPlayer.decrementStones();
    	checkGameOver();
        if (isGameEnded) return;
    	dicePanel.resetDice();
        currentPlayer.setIsTurn(false);
        int nextPlayerIndex = (participants.indexOf(currentPlayer) + 1) % participants.size();
        currentPlayer = participants.get(nextPlayerIndex);
        currentPlayer.setIsTurn(true);
        refreshScores();
        if (playerTurnLabel != null) {
            playerTurnLabel.setText("Turn: " + currentPlayer.getName());
        }
        if (currentPlayer instanceof Computer) {
            dicePanel.setRollButtonEnabled(false);
            new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ComputerTurnController controller = new ComputerTurnController(KiviBoard.this, (Computer) currentPlayer);
                    if (((Computer) currentPlayer).getDifficulty() == Computer.Difficulty.EASY) {
                        controller.performEasyTurn();
                    } else {
                        controller.performHardTurn();
                    }
                    ((Timer)e.getSource()).stop();
                }
            }).start();
        } else {
            dicePanel.setRollButtonEnabled(true);
        }
        
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
                Image img = bytesToImage(imgBytes);
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
                img = bytesToImage(imgBytes);
                if (contains(whiteIndex, index)) {
                    bgColor = WHITE;
                } else if (contains(lightPinkIndex, index)) {
                    bgColor = LIGHT_PINK;
                }
                squares[row][col] = new Square(bgColor, img);
            }
        }
    }
    private void reinitializeSquareImages() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Square sq = squares[row][col];
                if (sq != null && sq.image == null) { //only update if image is missing.
                    int index = row * SIZE + col;
                    byte[] imgBytes = imageMap.get(12); //default
                    if (twoPairs.contains(index)) {
                        imgBytes = imageMap.get(1);
                    } else if (threeOfAKind.contains(index)) {
                        imgBytes = imageMap.get(0);
                    } else if (twoThreeOfAKind.contains(index)) {
                        imgBytes = imageMap.get(2);
                    } else if (threePairs.contains(index)) {
                        imgBytes = imageMap.get(3);
                    } else if (littleStraight.contains(index)) {
                        imgBytes = imageMap.get(4);
                    } else if (fourOfAKindAndAPair.contains(index)) {
                        imgBytes = imageMap.get(5);
                    } else if (fullHouse.contains(index)) {
                        imgBytes = imageMap.get(6);
                    } else if (largeStraight.contains(index)) {
                        imgBytes = imageMap.get(7);
                    } else if (total12OrUnder.contains(index)) {
                        imgBytes = imageMap.get(8);
                    } else if (allOdd.contains(index)) {
                        imgBytes = imageMap.get(9);
                    } else if (allEven.contains(index)) {
                        imgBytes = imageMap.get(10);
                    } else if (fourOfAKind.contains(index)) {
                        imgBytes = imageMap.get(11);
                    }
                    sq.image = bytesToImage(imgBytes);
                }
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
        updateLeaderBoard();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                int x = col * squareSize;
                int y = row * squareSize;
                
                g2.setColor(squares[row][col].backgroundColor);
                g2.fillRect(x, y, squareSize, squareSize);
                
                int index = row * SIZE + col;
                if (colorblindModeEnabled) {
                    g2.setColor(new Color(0, 0, 0, 40)); //semi-transparent black for patterns
                    
                    //apply patterns based on square type (point value)
                    if (contains(lightPinkIndex, index)) {
                        //light pink (2 points) - horizontal lines
                        for (int i = 5; i < squareSize; i += 10) {
                            g2.drawLine(x, y + i, x + squareSize, y + i);
                        }
                    } else if (!contains(whiteIndex, index)) {
                        //dark pink (3 points) - checkered pattern
                        int checkSize = 10;
                        for (int i = 0; i < squareSize; i += checkSize) {
                            for (int j = 0; j < squareSize; j += checkSize) {
                                if ((i/checkSize + j/checkSize) % 2 == 0) {
                                    g2.fillRect(x + i, y + j, checkSize, checkSize);
                                }
                            }
                        }
                    }
                    // White squares (1 point) - no pattern needed
                }
                
                //draw the combination image
                if (squares[row][col].image != null) {
                    g2.drawImage(squares[row][col].image, x + 10, y + 10, squareSize - 20, squareSize - 20, this);
                }
                
                //draw stones with patterns in colorblind mode
                if (squares[row][col].stoneColor != null) {
                    Color stoneColor = squares[row][col].stoneColor;
                    Color translucentStoneColor = new Color(
                        stoneColor.getRed(),
                        stoneColor.getGreen(),
                        stoneColor.getBlue(), 
                        128);
                    g2.setColor(translucentStoneColor);
                    
                    int stoneDiameter = (int) (squareSize * 0.7);
                    int stoneX = x + (squareSize - stoneDiameter) / 2;
                    int stoneY = y + (squareSize - stoneDiameter) / 2;
                    
                    g2.fillOval(stoneX, stoneY, stoneDiameter, stoneDiameter);
                    
                    if (colorblindModeEnabled) {
                        g2.setColor(new Color(0, 0, 0, 100)); //semi-transparent black
                        
                        int playerIndex = -1;
                        for (int i = 0; i < participants.size(); i++) {
                            if (stoneColor.equals(participants.get(i).getColor())) {
                                playerIndex = i;
                                break;
                            }
                        }
                        Shape clip = g2.getClip();
                        //apply a different pattern based on player index
                        switch (playerIndex % 4) {
                            case 0: // Player 1 - zigzag pattern
                        
                                    g2.setClip(new Ellipse2D.Double(stoneX, stoneY, stoneDiameter, stoneDiameter));
                                    int spacing = 10;
                                    for (int i = stoneY; i < stoneY + stoneDiameter; i += spacing) {
                                        for (int j = stoneX; j < stoneX + stoneDiameter - spacing; j += spacing * 2) {
                                            g2.drawLine(j, i, j + spacing, i + spacing);
                                            g2.drawLine(j + spacing, i + spacing, j + spacing * 2, i);
                                        }
                                    }
                                    g2.setClip(clip);
                                    break;

                                
                            case 1: // Player 2 - diagonal lines
                                for (int i = -stoneDiameter; i <= stoneDiameter; i += 10) {
                                
                                    g2.setClip(new Ellipse2D.Double(stoneX, stoneY, stoneDiameter, stoneDiameter));
                                    g2.drawLine(stoneX + Math.max(0, -i), stoneY + Math.max(0, i), 
                                               stoneX + Math.min(stoneDiameter, stoneDiameter - i), 
                                               stoneY + Math.min(stoneDiameter, stoneDiameter + i));
                                    g2.setClip(clip);
                                }
                                break;
                            case 2: // Player 3 - dots
                                
                                g2.setClip(new Ellipse2D.Double(stoneX, stoneY, stoneDiameter, stoneDiameter));
                                int dotSize = 5;
                                for (int i = stoneX + 10; i < stoneX + stoneDiameter - 10; i += dotSize * 3) {
                                    for (int j = stoneY + 10; j < stoneY + stoneDiameter - 10; j += dotSize * 3) {
                                        g2.fillOval(i, j, dotSize, dotSize);
                                    }
                                }
                                g2.setClip(clip);
                                break;
                            case 3: // Player 4 - concentric circles
                                
                                g2.setClip(new Ellipse2D.Double(stoneX, stoneY, stoneDiameter, stoneDiameter));
                                // Draw concentric circles
                                for (int rad = stoneDiameter/10; rad < stoneDiameter/2; rad += stoneDiameter/10) {
                                    g2.drawOval(stoneX + stoneDiameter/2 - rad, 
                                                 stoneY + stoneDiameter/2 - rad, 
                                                 rad * 2, rad * 2);
                                }
                                g2.setClip(clip);
                                break;
                        }
                    }
                }
                
                //highlighted squares for valid moves
                if (colorblindModeEnabled && validSquareColor.equals(squares[row][col].backgroundColor)) {
                    //add a stronger highlight for valid moves in colorblind mode
                    g2.setColor(new Color(0, 0, 0, 120));
                    g2.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 
                                               0, new float[]{9}, 0));
                    g2.drawRect(x + 3, y + 3, squareSize - 6, squareSize - 6);
                }
                
                //draw grid lines
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(1));
                g2.drawRect(x, y, squareSize, squareSize);
            }
        }
    }
    private String getSquareInfo(int row, int col, int index) {
        StringBuilder info = new StringBuilder("<html>");
        
        //add point value
        if (contains(whiteIndex, index)) {
            info.append("Point value: 1<br>");
        } else if (contains(lightPinkIndex, index)) {
            info.append("Point value: 2<br>");
        } else {
            info.append("Point value: 3<br>");
        }
        
        //add combination type
        if (twoPairs.contains(index)) {
            info.append("Combination: Two Pairs");
        } else if (threeOfAKind.contains(index)) {
            info.append("Combination: Three of a Kind");
        } else if (twoThreeOfAKind.contains(index)) {
            info.append("Combination: Two times Three of a Kind");
        } else if (fourOfAKind.contains(index)) {
            info.append("Combination: Four of a Kind");
        } else if (threePairs.contains(index)) {
            info.append("Combination: Three Pairs");
        } else if (littleStraight.contains(index)) {
            info.append("Combination: Little Straight");
        } else if (fourOfAKindAndAPair.contains(index)) {
            info.append("Combination: Four of a Kind and a Pair");
        } else if (fullHouse.contains(index)) {
            info.append("Combination: Full House");
        } else if (largeStraight.contains(index)) {
            info.append("Combination: Large Straight");
        } else if (total12OrUnder.contains(index)) {
            info.append("Combination: 12 or Fewer");
        } else if (allOdd.contains(index)) {
            info.append("Combination: All Odd");
        } else if (allEven.contains(index)) {
            info.append("Combination: All Even");
        } else if (total30OrOver.contains(index)) {
            info.append("Combination: 30 or More");
        }
        
        //add stone info if present
        if (squares[row][col].stoneColor != null) {
            for (Player p : participants) {
                if (p.getColor().equals(squares[row][col].stoneColor)) {
                    info.append("<br>Occupied by: ").append(p.getName());
                    break;
                }
            }
        }
        
        info.append("</html>");
        return info.toString();
    }


    @Override
    public String getToolTipText(MouseEvent e) {
        if (e == null) {
            return null;
        }
        
        int row = e.getY() / squareSize;
        int col = e.getX() / squareSize;
        
        if (row >= 0 && row < SIZE && col >= 0 && col < SIZE) {
            int index = row * SIZE + col;
            return getSquareInfo(row, col, index);
        }
        return null;
    }
    
    public boolean isGameEnded() {
        return isGameEnded;
    }
    
    public void leftPanelGUI(JPanel leftPanel) {
    	leftPanel.setLayout(new GridBagLayout());
    	leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    	GridBagConstraints gbc = new GridBagConstraints();
    	gbc.insets = new Insets(4, 4, 4, 4);
    	Font labelFont = new Font("Montserrat", Font.BOLD, 15);
    	scoreLabels.clear();
    	for (int i = 0; i < participants.size(); i++) {
    	    Player player = participants.get(i);
    	    JLabel nameLabel = new JLabel(player.getName());
    	    nameLabel.setFont(labelFont);
    	    gbc.gridx = 0;
    	    gbc.gridy = i * 3;
    	    gbc.anchor = GridBagConstraints.WEST;
    	    gbc.fill = GridBagConstraints.HORIZONTAL;
    	    leftPanel.add(nameLabel, gbc);
    	    
    	    JLabel scoreLabel = new JLabel("Score: " + player.getScore());
    	    scoreLabel.setFont(labelFont);
    	    gbc.gridy = i * 3 + 1;
    	    leftPanel.add(scoreLabel, gbc);
    	    scoreLabels.add(scoreLabel);
    	    
    	    JPanel colorPanel = new JPanel();
    	    colorPanel.setBackground(player.getColor());
    	    colorPanel.setPreferredSize(new Dimension(90, 30));
    	    gbc.gridy = i * 3 + 2;
    	    leftPanel.add(colorPanel, gbc);
    	}
    }
    
    public void JMenuBarGUI(JMenuBar menuBar) {
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
        JMenu controlsMenu = new JMenu("Controls");
        JMenuItem showControlsItem = new JMenuItem("Show Controls");
        showControlsItem.addActionListener(e -> IntroScreenGUI.controlsDialog());
        controlsMenu.add(showControlsItem);
        menuBar.add(controlsMenu);
        
    }
    
    public void rightPanelGUI(JPanel rightPanel) {
        rightPanel.setLayout(new BorderLayout(10, 10));
        
        leaderBoardPanel = new JPanel();
        leaderBoardPanel.setLayout(new BoxLayout(leaderBoardPanel, BoxLayout.Y_AXIS));
        leaderBoardPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1), "Leaderboard", 
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 15), Color.DARK_GRAY));
        leaderBoardPanel.setPreferredSize(new Dimension(150, 150));
     
        updateLeaderBoard();
        
        JPanel statusPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        gbc.gridy = 0;
        gbc.weighty = 1.0;
        statusPanel.add(Box.createVerticalGlue(), gbc);

        gbc.gridy = 1;
        gbc.weighty = 0;
        playerTurnLabel = new JLabel("Turn: " + currentPlayer.getName(), SwingConstants.CENTER);
        playerTurnLabel.setFont(new Font("Arial", Font.BOLD, 15));
        statusPanel.add(playerTurnLabel, gbc);

        gbc.gridy = 2;
        rollsLeftLabel = new JLabel("Rerolls left: " + (dicePanel.getRollCount()-1), SwingConstants.CENTER);
        rollsLeftLabel.setFont(new Font("Arial", Font.BOLD, 15));
        statusPanel.add(rollsLeftLabel, gbc);

        gbc.gridy = 3;
        gbc.weighty = 1;
        statusPanel.add(Box.createVerticalGlue(), gbc);

     
        rightPanel.add(leaderBoardPanel, BorderLayout.NORTH);
        rightPanel.add(statusPanel, BorderLayout.CENTER);
    }

    public void updateLeaderBoard() {
        leaderBoardPanel.removeAll();
        List<Player> sortedPlayers = new ArrayList<>(participants);
        sortedPlayers.sort(Comparator.comparingInt(Player::getScore).reversed());
        Font entryFont = new Font("Arial", Font.BOLD, 15);
        for (Player p : sortedPlayers) {
            JLabel label = new JLabel(String.format("%s: %d", p.getName(), p.getScore()));
            label.setFont(entryFont);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            leaderBoardPanel.add(label);
        }
        
        leaderBoardPanel.revalidate();
        leaderBoardPanel.repaint();
    }
    public void checkGameOver() {
        boolean gameOver = true;
        for (Player p : participants) {
            if (p.getStones() > 0) {
                gameOver = false;
                break;
            }
        }
        
        if (gameOver) {
            isGameEnded=true;
            Player winner = participants.get(0);
            for (Player p : participants) {
                if (p.getScore() > winner.getScore()) {
                    winner = p;
                }
            }
         
            String message = "<html><center><h2>Winner!</h2>" +
                             "<h3>" + winner.getName() + "</h3>" +
                             "<p>with " + winner.getScore() + " points</p></center></html>";
                        JOptionPane.showMessageDialog(frame, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);

            frame.dispose();
            new IntroScreenGUI();
        }
    }




    
    public void updateRollCountLabel() {
        if (rollsLeftLabel != null) {
            rollsLeftLabel.setText("Rerolls left:  " + (dicePanel.getRollCount()-1) + "   ");
        }
    }

    
    public void updateResolution(int width, int height) {
        if (frame != null) {
            frame.setSize(width, height);
            frame.revalidate();
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
        updateResolution(1080,900);
        
        if (currentPlayer instanceof Computer) {
            new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ComputerTurnController controller = new ComputerTurnController(KiviBoard.this, (Computer) currentPlayer);
                    if (((Computer)currentPlayer).getDifficulty() == Computer.Difficulty.EASY) {
                        controller.performEasyTurn();
                    } else {
                        controller.performHardTurn();
                    }
                    ((Timer)e.getSource()).stop();
                }
            }).start();
        }

    }
    
    public void saveGame(String filename) {
        String projectDir = System.getProperty("user.dir");
        File savesDir = new File(projectDir, "saves");
        if (!savesDir.exists()) {
            savesDir.mkdirs();
        }
        File f = new File(savesDir, filename);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
        }
    }
    
    public void loadGame(String filename) {
        String projectDir = System.getProperty("user.dir");
        File savesDir = new File(projectDir, "saves");
        if (!savesDir.exists()) {
            System.err.println("The 'saves' directory does not exist.");
            return;
        }
        File saveFile = new File(savesDir, filename);
        if (!saveFile.exists()) {
            System.err.println("Save file does not exist.");
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile))) {
            KiviBoard loadedGame = (KiviBoard) ois.readObject();
            System.out.println("Game loaded successfully!");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading the game: " + e.getMessage());
        }
    }
    
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        scoreLabels = new ArrayList<>();
        dicePanel = new DicePanel(this);
        frame = new JFrame("Kivi Board");
        rollsLeftLabel = new JLabel("Rerolls left: " + (dicePanel.getRollCount()-1));
        
        loadImages();
        reinitializeSquareImages();
        buildGUI();
        initializeMouseListener();
    }
    public JFrame getFrame() {
        return frame;
    }

    public DicePanel getDicePanel() {
        return dicePanel;
    }

    public List<Integer> getValidIndices() {
        List<Integer> valid = new ArrayList<>();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                int index = row * SIZE + col;
                if (squares[row][col].stoneColor == null &&
                    validSquareColor.equals(squares[row][col].backgroundColor)) {
                    valid.add(index);
                }
            }
        }
        return valid;
    }

    public void placeStone(int index, Player player) { //this is for the computer
        int row = index / SIZE;
        int col = index % SIZE;
        if (squares[row][col].stoneColor == null) {
            squares[row][col].stoneColor = player.getColor();
            addScoreForClickedSquare(index);
            refreshScores();
            repaint();
        }
    }
    //getter for computer
    public int getCombinationScoreForIndex(int index) {
        if (twoPairs.contains(index)) {
            return combinations.get("twoPairs");
        } else if (threeOfAKind.contains(index)) {
            return combinations.get("threeOfAKind");
        } else if (twoThreeOfAKind.contains(index)) {
            return combinations.get("twoThreeOfAKind");
        } else if (fourOfAKind.contains(index)) {
            return combinations.get("fourOfAKind");
        } else if (threePairs.contains(index)) {
            return combinations.get("threePairs");
        } else if (littleStraight.contains(index)) {
            return combinations.get("littleStraight");
        } else if (fourOfAKindAndAPair.contains(index)) {
            return combinations.get("fourOfAKindAndAPair");
        } else if (fullHouse.contains(index)) {
            return combinations.get("fullHouse");
        } else if (largeStraight.contains(index)) {
            return combinations.get("largeStraight");
        } else if (total12OrUnder.contains(index)) {
            return combinations.get("total12OrUnder");
        } else if (allOdd.contains(index)) {
            return combinations.get("allOdd");
        } else if (allEven.contains(index)) {
            return combinations.get("allEven");
        } else {
            return combinations.get("total30orOver");
        }
    }



}
