package skeleton.replacementRules;

import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.Neck;
import skeleton.elements.nonterminal.ShoulderGirdle;
import skeleton.elements.terminal.Head;

import java.util.Arrays;
import java.util.List;

public class FrontPartRule extends ReplacementRule {

    private final String inputID = "front part";

    public String getInputID() {
        return inputID;
    }

    public List<SkeletonPart> apply(SkeletonPart frontPart) {
        // if rule is not compatible return element unchanged
        if (!isApplicableTo(frontPart)) {
            return Arrays.asList(frontPart);
        }

        //System.out.println("Apply " + inputID + " rule");

        if (frontPart.hasChildren()) {
            System.err.println("front part should not have children before this rule is applied.");
        }

        ShoulderGirdle shoulderGirdle = new ShoulderGirdle(frontPart.getParent());
        frontPart.getParent().replaceChild(frontPart, shoulderGirdle);
        Neck neck = new Neck(shoulderGirdle);
        shoulderGirdle.addChild(neck);
        Head head = new Head(neck);
        neck.addChild(head);

        return Arrays.asList(shoulderGirdle, neck, head);
    }
}
