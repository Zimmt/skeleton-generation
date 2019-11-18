package skeleton.replacementRules;

import skeleton.elements.BackPart;
import skeleton.elements.FrontPart;
import skeleton.elements.SkeletonPart;
import skeleton.elements.Torso;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WholeBodyRule extends ReplacementRule {

    private final String inputID = "whole body";

    public String getInputID() {
        return inputID;
    }

    public List<SkeletonPart> apply(SkeletonPart wholeBody) {
        // if rule is not compatible return element unchanged
        if (!isApplicableTo(wholeBody)) {
            return Collections.singletonList(wholeBody);
        }

        if (wholeBody.hasChildren()) {
            System.err.println("Whole body should not have children before this rule is applied.");
        }

        Torso torso = new Torso();
        FrontPart front = new FrontPart(torso);
        BackPart back = new BackPart(torso);
        torso.addChildren(front, back);
        List<SkeletonPart> generatedParts = Arrays.asList(torso, front, back);

        return generatedParts;
    }
}
