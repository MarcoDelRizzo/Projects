import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class IntroScreenGUI extends JFrame {
    public IntroScreenGUI() {
        setTitle("KIVI");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel("KIVI");
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        
        // Adding icon
        ImageIcon image = new ImageIcon("KiviBoard.png");
        setIconImage(image.getImage());
        
        // Set background color
        getContentPane().setBackground(Color.WHITE);
        
        JButton createGameButton = new JButton("New Game");
        JButton loadGameButton = new JButton("Load Save File");
        JButton rulesButton = new JButton("Rules");
        JButton controlsButton = new JButton("Controls");
        
        // Make buttons the same size
        Dimension buttonSize = new Dimension(200, 40);
        createGameButton.setMaximumSize(buttonSize);
        loadGameButton.setMaximumSize(buttonSize);
        rulesButton.setMaximumSize(buttonSize);
        controlsButton.setMaximumSize(buttonSize);
        // Center the buttons
        createGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rulesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlsButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        createGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SetUpAGameGUI().setVisible(true);
                dispose(); // Close the IntroScreenGUI
            }
        });
        
        loadGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
            	//temp board
            	List<Player> players = new ArrayList<>();
                players.add(new Human("Player 1", Color.BLUE));
                players.add(new Human("Player 2", Color.RED));
                KiviBoard board = new KiviBoard(80, players);
                new LoadGameGUI(board);
                dispose();
            }
        });
        
        rulesButton.addActionListener(_ -> {
            dialog();
        });
        controlsButton.addActionListener(_ -> { controlsDialog(); });
        add(Box.createVerticalStrut(50)); // Space above title
        add(titleLabel);
        add(Box.createVerticalStrut(30)); // Space below title
        add(createGameButton);
        add(Box.createVerticalStrut(20)); // Space between buttons
        add(loadGameButton);
        add(Box.createVerticalStrut(20));
        add(rulesButton);
        add(Box.createVerticalStrut(20));
        add(controlsButton);
        setVisible(true);
    }
    public static void controlsDialog() {
        String htmlText = "<html>" +
                          "<body style='font-family:sans-serif;'>" +
                          "<h1>Kivi Game Controls</h1>" +
                          "<p><b>Rolling the Dice:</b> Click on the <i>Roll</i> button to roll all the dice.</p>" +
                          "<p><b>Re-rolling Specific Dice:</b> If you wish to re-roll certain dice, simply click on the dice you want to change.</p>" +
                          "<p><b>Placing a Stone:</b> After rolling, confirm your roll. Once the valid squares are highlighted, click on a highlighted square to place your stone.</p>" +
                          "<h2>Game Strategy and Tips</h2>" +
                          "<p>The Kivi game is a mix of strategy and luck. Here are some tips to help you succeed:</p>" +
                          "<ul>" +
                          "<li><b>Plan Ahead:</b> Think about your moves and try to anticipate your opponent’s strategy.</li>" +
                          "<li><b>Manage Your Dice:</b> Deciding which dice to re-roll can turn the tide of the game.</li>" +
                          "<li><b>Observe the Board:</b> Notice the highlighted squares and use them to your advantage when placing your stone.</li>" +
                          "<li><b>Adapt Your Strategy:</b> Every roll is different, so be ready to change your approach as the game unfolds.</li>" +
                          "</ul>" +
                          "<p>* Accessibility Mode can be used for people who are color deficient, this mode will change the patterns of the pieces." +
                          "<p>* Square size can be selected for users who have their screen scaled up or below the native resolution." +
                          "<p>" +
                          "<p>Enjoy playing Kivi, and may the best strategist win!</p>" +
                          "</body></html>";

        javax.swing.JOptionPane.showMessageDialog(null, htmlText, "Game Controls", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    public static void dialog() {
        String htmlText = 
                "<html>" +
                "<body style='width:600px; font-family:sans-serif;'>" +
                "<h2>Kivi Rules</h2>" +
                "<p>Kivi draws inspiration from the classic dice game Yahtzee. The game combines Yahtzee’s dice with a board " +
                "spanning 7×7 squares, for a total of 49. Each player has ten stone counters made of glass (hence the name Kivi). " +
                "Unlike Yahtzee which uses five dice, Kivi uses six dice.</p>" +
                "<p>On their turn, a player throws all six dice and decides what combination will be used. As in Yahtzee, the " +
                "player can use up to two full or partial rethrows if unsatisfied with the combination. Upon finishing a turn, " +
                "the player places one of their stones on the board.</p>" +
                "<p>Each of the 49 squares is marked with a particular combination, and a stone can only be placed on a square if " +
                "the dice satisfy the combination. Squares are marked in different colours: pink squares score three points, " +
                "black squares score two points and white squares score one point.</p>" +
                "<h3>Combinations</h3>" +
                "<ul>" +
                "<li><strong>Two pairs:</strong> Two dice show the same value and another two dice also show the same value. " +
                "Worth 1 point.</li>" +
                "<li><strong>Three of a kind:</strong> Three dice show the same value. Worth 1 point.</li>" +
                "<li><strong>Little straight:</strong> Four dice show consecutive values. Worth 1 point.</li>" +
                "<li><strong>Full house:</strong> Three dice show the same value and another two dice show the same value. " +
                "Worth 1 point.</li>" +
                "<li><strong>Four of a kind:</strong> Four dice show the same value. Worth 2 points.</li>" +
                "<li><strong>Large straight:</strong> Five dice show consecutive values. Worth 2 points.</li>" +
                "<li><strong>All even:</strong> Each of the six dice shows an even value. Worth 2 points.</li>" +
                "<li><strong>All odd:</strong> Each of the six dice shows an odd value. Worth 2 points.</li>" +
                "<li><strong>12 or fewer:</strong> The sum of the values is 12 or fewer. Worth 2 points.</li>" +
                "<li><strong>30 or more:</strong> The sum of the values is 30 or more. Worth 2 points.</li>" +
                "<li><strong>Three pairs:</strong> Two dice show the same value, another two dice also show the same value, and the final two dice also show the same value. Worth 3 points.</li>" +
                "<li><strong>Two times three of a kind:</strong> Three dice show the same value, and the final three dice also show the same value. Worth 3 points.</li>" +
                "<li><strong>Four of a kind and a pair:</strong> Four dice show the same value, and the final two dice also show the same value. Worth 3 points.</li>" +
                "</ul>" +
                "<p>There are also three special combinations: Rolling a five of a kind or a straight of six consecutive numbers from 1 to 6 " +
                "allows a player to place their stone on any free square. Rolling a six of a kind (all six dice show the same value) " +
                "lets a player place a stone on any square; if the square is already occupied, that stone is moved elsewhere.</p>" +
                "<p>If a player cannot place a stone anywhere, they place their stone back in the box.</p>" +
                "<h3>Scoring</h3>" +
                "<p>The game lasts exactly ten rounds, one for each of the ten stones available to the players. After ten rounds, the stones " +
                "on the board are scored.</p>" +
                "<p>Stones are scored by contiguous rows (horizontal or vertical, but not diagonal). Each row is scored as the sum of the " +
                "points its squares award, multiplied by the length of that row. Thus, longer rows score increasingly more points. An " +
                "isolated stone counts as a row of one stone. After all stones have been scored, the player with the most points wins.</p>" +
                "</body>" +
                "</html>";
        
        JLabel label = new JLabel(htmlText);
        JScrollPane scrollPane = new JScrollPane(label);
        scrollPane.setPreferredSize(new Dimension(800, 600));
        
        JOptionPane optionPane = new JOptionPane(
            scrollPane,
            JOptionPane.INFORMATION_MESSAGE,
            JOptionPane.DEFAULT_OPTION
        );
        
        JDialog dialog = optionPane.createDialog("Kivi Rules");
        dialog.setResizable(true);
        dialog.setVisible(true);
    }
}
