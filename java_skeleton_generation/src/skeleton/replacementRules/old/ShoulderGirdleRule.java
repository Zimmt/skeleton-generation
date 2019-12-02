package skeleton.replacementRules.old;

import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.Arm;
import skeleton.elements.terminal.Shoulder;
import skeleton.replacementRules.ReplacementRule;

import java.util.Arrays;
import java.util.List;

public class ShoulderGirdleRule extends ReplacementRule {

    private final String inputID = "shoulder girdle";

    public String getInputID() {
        return inputID;
    }

    public List<SkeletonPart> apply(SkeletonPart shoulderGirdle) {
        // if rule is not compatible return element unchanged
        if (!isApplicableTo(shoulderGirdle)) {
            return Arrays.asList(shoulderGirdle);
        }

        //System.out.println("Apply " + inputID + " rule");

        if (!shoulderGirdle.hasChildren()) {
            System.err.println("Shoulder girdle should have children before this rule is applied.");
        }

        Shoulder shoulder = new Shoulder(shoulderGirdle.getParent(), shoulderGirdle);
        shoulderGirdle.getParent().replaceChild(shoulderGirdle, shoulder);
        shoulder.addChildren(shoulderGirdle.getChildren());
        Arm arm = new Arm(shoulder, shoulderGirdle);
        shoulder.addChild(arm);

        return Arrays.asList(shoulder, arm);
    }
}
