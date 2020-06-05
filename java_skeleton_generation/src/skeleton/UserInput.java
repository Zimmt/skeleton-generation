package skeleton;

import java.util.Random;

public class UserInput {
    private final Integer flooredLegs;
    private final Integer wings;
    private final Integer arms;
    private final Integer fins;
    private final boolean allowTwoExtremitiesPerGirdle;
    private int totalExtremityCount = 0;

    private Boolean secondShoulder;
    private final Double neckYLength;
    private final Double tailXLength;

    private final String head;

    private final Random random = new Random();

    public UserInput() {
        this.flooredLegs = null;
        this.wings = null;
        this.arms = null;
        this.fins = null;
        this.allowTwoExtremitiesPerGirdle = true;
        this.neckYLength = null;
        this.tailXLength = null;
        this.head = "horse_skull";
    }

    public UserInput(Integer flooredLegs, Integer wings, Integer arms, Integer fins, boolean allowTwoExtremitiesPerGirdle,
                     Boolean secondShoulder, Double neckYLength, Double tailXLength, String head) {
        this.flooredLegs = flooredLegs;
        this.wings = wings;
        this.arms = arms;
        this.fins = fins;
        this.allowTwoExtremitiesPerGirdle = allowTwoExtremitiesPerGirdle;
        this.tailXLength = tailXLength;
        this.neckYLength = neckYLength;
        this.head = head;
        if (flooredLegs != null) totalExtremityCount += flooredLegs;
        if (wings != null) totalExtremityCount += wings;
        if (arms != null) totalExtremityCount += arms;
        if (fins != null) totalExtremityCount += fins;
        setSecondShoulder(secondShoulder);
        checkValidityOfValues();
    }

    public Boolean getSecondShoulder() {
        return secondShoulder;
    }

    /**
     * min( userInput, 4)  +- 0.5
     * if input > 4 there is a probability that it is reduced by two
     * if input > 3 there is a probability that it is reduced by one
     * @return null or a value in [-0.5, 4.5]
     */
    public Double getLegConditionForPCA() {
        Double legCondition = null;
        if (flooredLegs != null) {
            double variance = random.nextDouble() - 0.5;
            legCondition = Math.min(4.0, flooredLegs.doubleValue()) + variance;
            if (legCondition >= 4.0 && random.nextDouble() > 0.5) {
                legCondition -= 2.0;
            } else if (legCondition >= 3.0 && random.nextDouble() > 0.5) {
                legCondition -= 1.0;
            }
        }
        Double pcaLegCondition = legCondition;
        //System.out.println("PCA leg condition: " + pcaLegCondition);
        return legCondition;
    }

    /**
     * min( 1, userInput) +- 0.5
     * @return null or a value in [-0.5, 1.5]
     */
    public Double getWingConditionForPCA() {
        Double wingCondition = null;
        if (wings != null) {
            double variance = random.nextDouble() - 0.5;
            wingCondition = Math.min(1.0, wings.doubleValue()) + variance;
        }
        Double pcaWingCondtion = wingCondition;
        //System.out.println("PCA wing condition: " + pcaWingCondtion);
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

    public boolean twoExtremitiesPerGirdleAllowed() {
        return allowTwoExtremitiesPerGirdle;
    }

    public Double getNeckYLength() {
        if (neckYLength == null && getSecondShoulder() != null && getSecondShoulder()) {
            return 200.0 + random.nextDouble() * 100;
        }
        return neckYLength;
    }

    public Double getTailXLength() {
        return tailXLength;
    }

    public String getHead() {
        return head;
    }

    /**
     * extremities and total extremity count have to be set before this is called
     * second shoulder is set by user or, if null, it is set true when it is needed (by number of extremities)
     * else it is left null
     */
    private void setSecondShoulder(Boolean userInputSecondShoulder) {
        if (userInputSecondShoulder != null) {
            secondShoulder = userInputSecondShoulder;
        } else if ((wings != null && (wings > 2 || !allowTwoExtremitiesPerGirdle && wings > 1)) ||
                (arms != null && (arms > 2 || !allowTwoExtremitiesPerGirdle && arms > 1)) ||
                (wings != null && arms != null && (wings + arms > 2 || !allowTwoExtremitiesPerGirdle && wings + arms > 1)) ||
                totalExtremityCount > 4 || (!allowTwoExtremitiesPerGirdle && totalExtremityCount > 2)) {
            secondShoulder = true;
        } else {
            secondShoulder = null;
        }
    }

    private void checkValidityOfValues() {
        boolean secondShoulderDisallowed = secondShoulder != null && !secondShoulder;
        if (totalExtremityCount > 6 || (secondShoulderDisallowed && totalExtremityCount > 4) ||
                (!allowTwoExtremitiesPerGirdle && totalExtremityCount > 3) ||
                (secondShoulderDisallowed && !allowTwoExtremitiesPerGirdle && totalExtremityCount > 2)) {
            System.err.println("Too many extremities found!");
        }
        if (flooredLegs != null && (flooredLegs < 0 || flooredLegs > 4 || (!allowTwoExtremitiesPerGirdle && flooredLegs > 2))) {
            System.err.println("Invalid user input for legs");
        }
        if (wings != null && (wings < 0 || wings > 4 ||
                ((!allowTwoExtremitiesPerGirdle || secondShoulderDisallowed) && wings > 2) ||
                (!allowTwoExtremitiesPerGirdle && secondShoulderDisallowed && wings > 1))) {
            System.err.println("Invalid user input for wings");
        }
        if (arms != null && (arms < 0 || arms > 4 ||
                ((!allowTwoExtremitiesPerGirdle || secondShoulderDisallowed) && arms > 2) ||
                (!allowTwoExtremitiesPerGirdle && secondShoulderDisallowed && arms > 1))) {
            System.err.println("Invalid user input for arms");
        }
        if (fins != null && (fins < 0 || fins > 6 || (secondShoulderDisallowed && fins > 4) || (!allowTwoExtremitiesPerGirdle && fins > 3) ||
                (secondShoulderDisallowed && !allowTwoExtremitiesPerGirdle && fins > 2))) {
            System.err.println("Invalid user input for fins");
        }
    }
}
