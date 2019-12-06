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

        List<Float> intervals = wholeBody.getGenerator().getSpineLocation().getIntervalsByGradientEpsilon(0.5f);
        System.out.println("spine intervals: " + intervals);

        Tuple2f spineInterval = null;
        if (intervals.isEmpty()) {
            spineInterval = new Point2f(1f/3f, 2f/3f); // this should not happen
            System.out.println("no appropriate spine interval found");

        } else if (intervals.size() == 2) {
            spineInterval = new Point2f(intervals.get(0), intervals.get(1));

        } else { // find interval that contains 0.5 or is first after 0.5
            for (int i = 0; i < intervals.size(); i += 2) {
                if (intervals.get(i) >= 0.5f) {
                    spineInterval = new Point2f(intervals.get(i), intervals.get(i+1));
                    break;
                }
            }
            if (spineInterval == null) {
                int i = intervals.size() - 2;
                spineInterval = new Point2f(intervals.get(i), intervals.get(i+1));
            }
        }

        Torso torso = new Torso(spineInterval, wholeBody.getTransform(), wholeBody.getBoundingBox(), wholeBody);
        return Arrays.asList(torso);
    }
}
