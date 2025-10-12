import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DicePanel extends JPanel implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient JLabel[] diceLabels = new JLabel[6];
    private List<Integer> selectedDice = new ArrayList<>();
    private transient JButton rollButton;
    private transient JButton confirmButton;
    private Roll roll; 

    private transient KiviBoard board;
    private boolean colorblindModeEnabled = false;

    public DicePanel(KiviBoard board) {
        this.board = board;
        roll = new Roll(6);
        initComponents();
    }
    public void setColorblindMode(boolean enabled) {
        colorblindModeEnabled = enabled;
        repaint();
    }
    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        JPanel dicePanelContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        diceLabels = new JLabel[6];
        for (int i = 0; i < 6; i++) {
            diceLabels[i] = new JLabel();
            Dimension fixedSize = new Dimension(66, 66);
            diceLabels[i].setPreferredSize(fixedSize);
            diceLabels[i].setMinimumSize(fixedSize);
            diceLabels[i].setMaximumSize(fixedSize);
            diceLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
            diceLabels[i].setVerticalAlignment(SwingConstants.CENTER);
            diceLabels[i].setIcon(resizeImage("images/dice1.png", 60, 60));
            diceLabels[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
            int finalI = i;
            diceLabels[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    toggleDiceSelection(finalI);
                }
            });
            dicePanelContainer.add(diceLabels[i]);
        }
        addDiceTooltips();
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        rollButton = new JButton("Roll Dice");
        confirmButton = new JButton("Confirm");
        
        rollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!roll.canReroll()) {
                    setRollButtonEnabled(false);
                    return;
                }
                List<Integer> indicesToReroll = getSelectedDiceIndices();
                rollDiceAnimation(indicesToReroll);
                board.updateRollCountLabel();
//                confirmButton.setEnabled(true);
                if (!roll.canReroll()) {
                    setRollButtonEnabled(false);
                }
            }
        });
        
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setRollButtonEnabled(false);
                confirmButton.setEnabled(false);
                List<Integer> finalDiceValues = new ArrayList<>();
                for (Dice d : roll.getDice()) {
                    finalDiceValues.add(d.getDiceValue());
                }
                roll.resetRerollCount();
                System.out.println("Confirmed dice: " + finalDiceValues);
                board.updateRollCountLabel();
                board.highlightSquaresForDice(finalDiceValues);
                
                if (!board.hasValidCombination(finalDiceValues)) {
                    System.out.println("No valid combination rolled, passing turn automatically.");
                    board.passTurn();
                } else {
                    List<Integer> validIndices = board.getValidIndices();
                    if (validIndices.isEmpty()) {
                        System.out.println("Valid combination, but no free valid squares available. Passing turn automatically.");
                        board.passTurn();
                    }
                }
            }
        });


        buttonPanel.add(rollButton);
        buttonPanel.add(confirmButton);
        
        add(dicePanelContainer);
        add(buttonPanel);
    }
    public Roll getRoll() {
        return roll;
    }

    private void addDiceTooltips() {
        for (int i = 0; i < diceLabels.length; i++) {
            final int diceIndex = i;
            diceLabels[i].setToolTipText("Loading...");
            
            diceLabels[i].addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int value = 1; // Default
                    if (roll != null && roll.getDice() != null && diceIndex < roll.getDice().size()) {
                        value = roll.getDice().get(diceIndex).getDiceValue();
                    }
                    String selection = selectedDice.contains(diceIndex) ? "Yes" : "No";
                    diceLabels[diceIndex].setToolTipText("Value: " + value + ", Selected: " + selection);
                }
            });
        }
    }
    

    
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        initComponents();
    }

    private ImageIcon resizeImage(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(path));
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    public void rollDiceAnimation(List<Integer> indicesToReroll) {
    	confirmButton.setEnabled(false);
    	List<Integer> diceToAnimate = indicesToReroll.isEmpty() ? List.of(0,1,2,3,4,5) : indicesToReroll;
        Timer timer = new Timer(100, new ActionListener() {
            private int count = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                count++;
                int[] diceValues = new int[6];
                for (int i = 0; i < 6; i++) {
                    diceValues[i] = roll.getDice().get(i).getDiceValue();
                }
                for (int i : diceToAnimate) {
                    diceValues[i] = (int) (Math.random() * 6) + 1;
                }
                updateDiceImages(diceValues);
                if (count >= 10) {
                    ((Timer) e.getSource()).stop();
                    rollDice(indicesToReroll);
                    updateDiceImages();
                    confirmButton.setEnabled(true);
                }
            }
        });
        timer.start();
    }
    
    public void setRollButtonEnabled(boolean enabled) {
        rollButton.setEnabled(enabled);
        revalidate();
        repaint();
    }
    
    public void setConfirmButtonEnabled(boolean enabled) {
        confirmButton.setEnabled(enabled);
        revalidate();
        repaint();
    }

    private void rollDice(List<Integer> indicesToReroll) {
        if (indicesToReroll == null || indicesToReroll.isEmpty()) {
            roll.rollDice(null);
        } else {
            roll.rollDice(indicesToReroll);
        }
        updateDiceImages();
    }

    private void updateDiceImages() {
        List<Dice> diceList = roll.getDice();
        for (int i = 0; i < diceList.size(); i++) {
            String imagePath = diceList.get(i).getDiceImage();
            diceLabels[i].setIcon(resizeImage(imagePath, 60, 60));
        }
    }

    private void updateDiceImages(int[] diceValues) {
        for (int i = 0; i < diceValues.length; i++) {
            diceLabels[i].setIcon(resizeImage("images/dice" + diceValues[i] + ".png", 60, 60));
        }
    }

    private void toggleDiceSelection(int index) {
        if (selectedDice.contains(index)) {
            selectedDice.remove(Integer.valueOf(index));
            
            diceLabels[index].setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        } else {
            selectedDice.add(index);
            
            diceLabels[index].setBorder(BorderFactory.createLineBorder(Color.RED, 3));
        }
    }


    
    public void resetDice() {
        selectedDice.clear();
        for (Dice d : roll.getDice()) {
            d.reset();
        }
        updateDiceImages();
        for (JLabel diceLabel : diceLabels) {
            diceLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        }
        roll.resetRerollCount();
        setRollButtonEnabled(true);
        setConfirmButtonEnabled(false);
    }
    
    private List<Integer> getSelectedDiceIndices() {
        return new ArrayList<>(selectedDice);
    }
    
    public int getRollCount() {
        return roll.getRemainingRerolls();
    }
}
