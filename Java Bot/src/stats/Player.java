package stats;

import java.util.ArrayList;

import cards.BoardCards;
import cards.Card;
import actions.PerformedAction;
import actions.PerformedActionType;
import actions.Street;

public class Player {
    
    private final String name;
    private final Stats stats;
    private int stackSize;
    private boolean isActive;
    private int seat;
    private Street street;
    private PerformedAction lastAction;
    private boolean setVPIP;
    private boolean setPreFlopRaise;
    private boolean madePreFlopAction;
    
    public Player(String name, int stackSize, int seat){
        this.name = name;
        this.stats = new Stats();
        this.stackSize = stackSize;
        this.isActive = true;
        this.seat = seat;
        this.street = Street.PREFLOP;
        this.lastAction = new PerformedAction(name, PerformedActionType.NONE, 0, new ArrayList<Card>(), Street.PREFLOP);
        setVPIP = false;
        setPreFlopRaise = false;
        madePreFlopAction = false;
    }
    
    public Stats getStats(){
        return stats;
    }
   
    
    public void setStreet(Street street){
        this.street = street;
    }
       
    
    public String getName(){
        return name;
    }
    
    
    public int getStackSize(){
        return stackSize;
    }
    
    
    public void setStackSize(int newStackSize){
        this.stackSize = newStackSize;
    }
    
    /**
     * 
     * @return seat number of player from 1-3
     */
    public int getSeat(){
        return seat;
    }
    
    
    public void setSeat(int newSeat){
        seat = newSeat;
    }
    
    
    /**
     * Whether or not the player is playing the current hand
     * @return true if playing, false otherwise
     */
    public boolean isActive(){
        return isActive;
    }
    
    
    /**
     * Sets whether or not the player is playing the current hand.
     *  Call when NEWHAND message is received.
     * @param active 
     */
    public void setActive(boolean active){
        isActive = active;
        setVPIP = false;
        setPreFlopRaise = false;
        madePreFlopAction = false;
        if(isActive){
            stats.incrementEligibleMatches();
        }
    }
    
    
    /**
     * update the player stats based on the given action
     * @param action
     */
    public void processAction(PerformedAction action){
        this.lastAction = action;
        PerformedActionType type = action.getType();
        
        if(type == PerformedActionType.DEAL){
            street = action.getStreet();   
        }
        if((type == PerformedActionType.CALL || type == PerformedActionType.RAISE || type == PerformedActionType.BET) 
                && street == Street.PREFLOP && !setVPIP){
            stats.VPIP();
            setVPIP = true;
        }
        if(type.isAPlayerAction() && street == Street.PREFLOP && !madePreFlopAction){
            stats.numCouldDoActionPreFlop();
            madePreFlopAction = true;
        }
        if(type == PerformedActionType.RAISE && street == Street.PREFLOP && !setPreFlopRaise){
            stats.PFR();
            setPreFlopRaise = true;
        }
    }
        

    
    public PerformedAction getLastAction(){
        return this.lastAction;
    }
    
    public void setLastAction(PerformedAction action){
        this.lastAction = action;
    }
    
    public boolean limped(){
        return (lastAction.getType() == PerformedActionType.CALL) & isActive;
    }
    
    public boolean raised(){
        return (lastAction.getType() == PerformedActionType.RAISE) & isActive;
    }
}
