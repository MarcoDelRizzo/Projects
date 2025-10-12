import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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

    public DicePanel(KiviBoard board) {
        
    	this.board = board;
    	
    	setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        //initialize Roll class with 6 dice
        roll = new Roll(6);

        //initialize dice images
        for (int i = 0; i < 6; i++) {
            diceLabels[i] = new JLabel();
            diceLabels[i].setIcon(resizeImage("images/dice1.png", 60, 60)); 
            diceLabels[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); 
            int finalI = i;
            diceLabels[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    toggleDiceSelection(finalI);
                }
            });
            add(diceLabels[i]);
        }

        // Add roll button
        confirmButton = new JButton("Confirm");
        rollButton = new JButton("Roll Dice");
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
                confirmButton.setEnabled(true);
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
                
                //print line to debugging
                System.out.println("Confirmed dice: " + finalDiceValues);
                board.updateRollCountLabel();
                board.highlightSquaresForDice(finalDiceValues);
                
            }
        });

        add(rollButton);
        add(confirmButton);
    }

    private ImageIcon resizeImage(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(path));
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

   
    private void rollDiceAnimation(List<Integer> indicesToReroll) {
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
                }
            }
        });
        timer.start();
    }
    public void setRollButtonEnabled(boolean enabled) {
        rollButton.setEnabled(enabled);
        revalidate();
        repaint();
        return;
    }
    public void setConfirmButtonEnabled(boolean enabled) {
        confirmButton.setEnabled(enabled);
        revalidate();
        repaint();
        return;
    }


    private void rollDice(List<Integer> indicesToReroll) {
        // If user selected none, pass `null` or an empty list to roll them all
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
        // Clear any selection
        selectedDice.clear();

        // Reset each die
        for (Dice d : roll.getDice()) {
            d.reset(); // sets value=1 and imagePath=images/dice1.png
        }

        // Update the displayed images in the panel
        updateDiceImages();

        // Reset the borders
        for (JLabel diceLabel : diceLabels) {
            diceLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        }

        // Reset how many rerolls remain
        roll.resetRerollCount();
        setRollButtonEnabled(true);
        setConfirmButtonEnabled(false);
    }

    private List<Integer> getSelectedDiceIndices() {
        return new ArrayList<>(selectedDice); 
    }
    public int getRollCount()
    {
    	return roll.getRemainingRerolls();
    }
}
