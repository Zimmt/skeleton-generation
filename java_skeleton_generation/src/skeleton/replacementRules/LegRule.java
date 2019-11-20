package skeleton.replacementRules;

import skeleton.elements.SkeletonPart;
import skeleton.elements.terminal.Foot;
import skeleton.elements.terminal.Shin;
import skeleton.elements.terminal.Thigh;

import java.util.Arrays;
import java.util.List;

public class LegRule extends ReplacementRule {

    private final String inputID = "leg";

    public String getInputID() {
        return inputID;
    }

    public List<SkeletonPart> apply(SkeletonPart leg) {
        // if rule is not compatible return element unchanged
        if (!isApplicableTo(leg)) {
            return Arrays.asList(leg);
        }

        //System.out.println("Apply " + inputID + " rule");

        if (leg.hasChildren()) {
            System.err.println("Leg should not have children before this rule is applied.");
        }

        Thigh thigh = new Thigh(leg.getParent());
        leg.getParent().replaceChild(leg, thigh);
        Shin shin = new Shin(thigh);
        thigh.addChild(shin);
        Foot foot = new Foot(shin);
        shin.addChild(foot);

        return Arrays.asList(thigh, shin, foot);
    }
}
