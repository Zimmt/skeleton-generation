package skeleton.replacementRules;

import skeleton.elements.SkeletonPart;
import skeleton.elements.terminal.Hand;
import skeleton.elements.terminal.LowerArm;
import skeleton.elements.terminal.UpperArm;

import java.util.Arrays;
import java.util.List;

public class ArmRule extends ReplacementRule {

    private final String inputID = "arm";

    public String getInputID() {
        return inputID;
    }

    public List<SkeletonPart> apply(SkeletonPart arm) {
        // if rule is not compatible return element unchanged
        if (!isApplicableTo(arm)) {
            return Arrays.asList(arm);
        }

        //System.out.println("Apply " + inputID + " rule");

        if (arm.hasChildren()) {
            System.err.println("Arm should not have children before this rule is applied.");
        }

        UpperArm upperArm = new UpperArm(arm.getParent());
        arm.getParent().replaceChild(arm, upperArm);
        LowerArm lowerArm = new LowerArm(upperArm);
        upperArm.addChild(lowerArm);
        Hand hand = new Hand(lowerArm);
        lowerArm.addChild(hand);

        return Arrays.asList(upperArm, lowerArm, hand);
    }
}
