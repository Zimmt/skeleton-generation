package skeleton;

public class UserInput {
    private Integer flooredLegs;
    private Integer wings;
    private Integer arms;
    private Integer fins;
    private int total;

    public UserInput(Integer flooredLegs, Integer wings, Integer arms, Integer fins) {
        this.flooredLegs = flooredLegs;
        this.wings = wings;
        this.arms = arms;
        this.fins = fins;

        this.total = 0;
        if (flooredLegs != null) total += flooredLegs;
        if (wings != null) total += wings;
        if (arms != null) total += arms;
        if (fins != null) total += fins;
        if (total > 4) {
            System.err.println("Too many extremities found!");
        }
    }

    public Integer getLegConditionForPCA() {
        Integer legCondition = flooredLegs;
        if (legCondition != null && legCondition > 2) {
            legCondition = 2;
        }
        return legCondition;
    }

    public Integer getWingConditionForPCA() {
        Integer wingCondition = wings;
        if (wingCondition != null && wingCondition > 1) {
            wingCondition = 1;
        }
        return wingCondition;
    }

    public Integer getFlooredLegs() {
        return flooredLegs;
    }

    public boolean hasFlooredLegs() {
        return flooredLegs != null;
    }

    public Integer getWings() {
        return wings;
    }

    public boolean hasWings() {
        return wings != null;
    }

    public Integer getArms() {
        return arms;
    }

    public boolean hasArms() {
        return arms != null;
    }

    public Integer getFins() {
        return fins;
    }

    public boolean hasFins() {
        return fins != null;
    }

    public int getTotal() {
        return total;
    }
}
