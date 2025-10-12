import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Roll implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<Dice> diceList;
    private int rerollCount;
    public static final int ROLLS = 3;

    public Roll(int numberOfDice) {
        if (numberOfDice <= 0) throw new IllegalArgumentException("Number of dice must be positive.");
        this.diceList = new ArrayList<>();
        this.rerollCount = 0;
        
        for (int i = 0; i < numberOfDice; i++) {
            diceList.add(new Dice());
        }
    }

    public void rollDice(List<Integer> indicesToReroll) {
        if (rerollCount >= ROLLS) {
            System.out.println("Maximum rerolls reached. No further rerolls allowed.");
            return; //no more rerolls allowed if max reroll count is reached
        }

        if (indicesToReroll == null || indicesToReroll.isEmpty()) {
            rollAllDice();
       
        } else {
            rerollSelectedDice(indicesToReroll);
            
        }
        rerollCount++;
    }

    private void rollAllDice() {
        for (Dice die : diceList) {
            die.roll();
        }
    }

    private void rerollSelectedDice(List<Integer> indices) {
        for (int index : indices) {
            if (index >= 0 && index < diceList.size()) {
                diceList.get(index).roll();
            }
        }
    }

    public List<Dice> getDice() {
        return diceList;
    }

    public int getRemainingRerolls() {
        return ROLLS - rerollCount;
    }

    public boolean canReroll() {
    	if(rerollCount < ROLLS) {
        
    		return true;
    	}
    	else {
    		
    		return false;
    	}
    }

    public int getRollCount() {
        return rerollCount + 1;
    }
    public void resetRerollCount() {
    	rerollCount = 0;
    }

    public void resetForNewTurn() {
        rerollCount = 0;
        for (Dice die : diceList)
        {
        	die.reset();
        }
    }
}
