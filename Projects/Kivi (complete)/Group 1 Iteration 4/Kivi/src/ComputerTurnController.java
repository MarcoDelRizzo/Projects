import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.Timer;

public class ComputerTurnController {
    private KiviBoard board;
    private Computer computer;
    private DicePanel dicePanel; 

    public ComputerTurnController(KiviBoard board, Computer computer) {
        this.board = board;
        this.computer = computer;
        this.dicePanel = board.getDicePanel();  
    }

    public void performEasyTurn() {
        if (board.isGameEnded()) return;
    	board.checkGameOver();
        if (board.isGameEnded()) return;
        if (board.getCurrentPlayer() != computer) return;
        board.getFrame().setTitle(computer.getName() + " (Computer Easy) is Rolling...");
        dicePanel.rollDiceAnimation(new ArrayList<>()); 
        
        Timer timer1 = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (board.getCurrentPlayer() != computer) {
                    ((Timer)e.getSource()).stop();
                    return;
                }
                List<Integer> diceValues = new ArrayList<>();
                for (Dice d : dicePanel.getRoll().getDice()) {
                    diceValues.add(d.getDiceValue());
                }
                board.highlightSquaresForDice(diceValues);
                
                Timer timer2 = new Timer(3000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e2) {
                        if (board.getCurrentPlayer() != computer) {
                            ((Timer)e2.getSource()).stop();
                            return;
                        }
                        List<Integer> validIndices = board.getValidIndices();
                        if (validIndices.isEmpty()) {
                            System.out.println(computer.getName() + " has no valid move. Stone returned to the box.");
                            computer.decrementStones(); 
                        } else {
                            int chosenIndex = validIndices.get(new Random().nextInt(validIndices.size()));
                            board.placeStone(chosenIndex, computer);
                        }
                        board.clearHighlights();
                        board.updateLeaderBoard();
                        board.passTurn();
                        board.getFrame().setTitle("Kivi Board");
                        ((Timer)e2.getSource()).stop();
                    }
                });
                timer2.setRepeats(false);
                timer2.start();
                ((Timer)e.getSource()).stop();
            }
        });
        timer1.start();
    }

    public void performHardTurn() {
        
        if (board.isGameEnded()) return;
    	board.checkGameOver();
        if (board.isGameEnded()) return;
        if (board.getCurrentPlayer() != computer) return;
        board.getFrame().setTitle(computer.getName() + " (Computer Hard) is Rolling...");
        dicePanel.rollDiceAnimation(new ArrayList<>()); 
        
        Timer timer1 = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (board.getCurrentPlayer() != computer) {
                    ((Timer)e.getSource()).stop();
                    return;
                }
                //wait an extra 1000 ms for the dice values to update.
                Timer timerInner = new Timer(1000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent eInner) {
                        if (board.getCurrentPlayer() != computer) {
                            ((Timer)eInner.getSource()).stop();
                            return;
                        }
                        List<Integer> diceValues = new ArrayList<>();
                        for (Dice d : dicePanel.getRoll().getDice()) {
                            diceValues.add(d.getDiceValue());
                        }
                        board.highlightSquaresForDice(diceValues);
                        
                        Timer timer2 = new Timer(3000, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e2) {
                                if (board.getCurrentPlayer() != computer) {
                                    ((Timer)e2.getSource()).stop();
                                    return;
                                }
                                List<Integer> validIndices = board.getValidIndices();
                                if (validIndices.isEmpty()) {
                                    if (dicePanel.getRoll().canReroll()) {
                                        System.out.println(computer.getName() + " has no valid free square. Re-rolling...");
                                        board.clearHighlights();
                                        Timer timer3 = new Timer(500, new ActionListener() {
                                            @Override
                                            public void actionPerformed(ActionEvent e3) {
                                                if (board.getCurrentPlayer() == computer) {
                                                    performHardTurn(); 
                                                }
                                                ((Timer)e3.getSource()).stop();
                                            }
                                        });
                                        timer3.setRepeats(false);
                                        timer3.start();
                                    } else {
                                        System.out.println(computer.getName() + " has no valid free square and no re-rolls left. Passing turn.");
                                        board.clearHighlights();
                                        board.passTurn();
                                        board.getFrame().setTitle("Kivi Board");
                                    }
                                } else {
                                    int bestIndex = -1;
                                    int bestScore = -1;
                                    for (Integer index : validIndices) {
                                        int score = board.getCombinationScoreForIndex(index);
                                        if (score > bestScore) {
                                            bestScore = score;
                                            bestIndex = index;
                                        }
                                    }
                                    board.placeStone(bestIndex, computer);
                                    board.clearHighlights();
                                    board.updateLeaderBoard();
                                    board.passTurn();
                                    board.getFrame().setTitle("Kivi Board");
                                }
                                ((Timer)e2.getSource()).stop();
                            }
                        });
                        timer2.setRepeats(false);
                        timer2.start();
                        ((Timer)eInner.getSource()).stop();
                    }
                });
                timerInner.setRepeats(false);
                timerInner.start();
                ((Timer)e.getSource()).stop();
            }
        });
        timer1.start();
    }
}
