package skeleton.replacementRules;

import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.Torso;

import javax.vecmath.Point2f;
import javax.vecmath.Tuple2f;
import java.util.Arrays;
import java.util.List;

public class WholeBodyRule extends ReplacementRule {

    private final String inputID = "whole body";

    public String getInputID() {
        return inputID;
    }

    public List<SkeletonPart> apply(SkeletonPart wholeBody) {
        // if rule is not compatible return element unchanged
        if (!isApplicableTo(wholeBody)) {
            return Arrays.asList(wholeBody);
        }

        //System.out.println("Apply " + inputID + " rule");

        if (wholeBody.hasChildren()) {
            System.err.println("Whole body should not have children before this rule is applied.");
        }

        Tuple2f spineInterval = new Point2f(1f/3f, 2f/3f); // TODO think of a rule
        Torso torso = new Torso(spineInterval, wholeBody.getTransform(), wholeBody.getBoundingBox(), wholeBody);
        return Arrays.asList(torso);
    }
}
