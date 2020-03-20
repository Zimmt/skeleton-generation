package skeleton;

import java.util.Random;

public class UserInput {
    private Integer flooredLegs;
    private Integer wings;
    private Integer arms;
    private Integer fins;
    private int totalExtremityCount;
    private Boolean secondShoulder;

    private Double pcaLegCondition = null;
    private Double pcaWingCondtion = null;

    private Random random = new Random();

    public UserInput(Integer flooredLegs, Integer wings, Integer arms, Integer fins, Boolean secondShoulder) {
        if (flooredLegs != null && (flooredLegs < 0 || flooredLegs > 4)) {
            System.err.println("Invalid user input for legs");
        }
        if (wings != null && (wings < 0 || wings > 4)) {
            System.err.println("Invalid user input for wings");
        }
        if (arms != null && (arms < 0 || arms > 4)) {
            System.err.println("Invalid user input for arms");
        }
        if (fins != null && (fins < 0 || fins > 6)) {
            System.err.println("Invalid user input for fins");
        }
        this.flooredLegs = flooredLegs;
        this.wings = wings;
        this.arms = arms;
        this.fins = fins;

        this.totalExtremityCount = 0;
        if (flooredLegs != null) totalExtremityCount += flooredLegs;
        if (wings != null) totalExtremityCount += wings;
        if (arms != null) totalExtremityCount += arms;
        if (fins != null) totalExtremityCount += fins;
        if (totalExtremityCount > 6) {
            System.err.println("Too many extremities found!");
        }

        this.secondShoulder = secondShoulder;
        if (secondShoulder != null && !secondShoulder && ((wings != null && wings > 2) || (arms != null && arms > 2) || (fins != null && fins > 4))) {
            System.err.println("Too many extremities, need second shoulder!");
        }
    }

    public boolean hasSecondShoulder() {
        if (secondShoulder != null) {
            return secondShoulder;
        } else {
            return totalExtremityCount > 4;
        }
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
        pcaLegCondition = legCondition;
        System.out.println("PCA leg condition: " + pcaLegCondition);
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
        pcaWingCondtion = wingCondition;
        System.out.println("PCA wing condition: " + pcaWingCondtion);
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
}
