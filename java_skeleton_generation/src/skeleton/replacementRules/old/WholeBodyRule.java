package skeleton.replacementRules.old;

import skeleton.elements.nonterminal.BackPart;
import skeleton.elements.nonterminal.FrontPart;
import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.Torso;
import skeleton.replacementRules.ReplacementRule;

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

        Torso torso = new Torso(wholeBody);
        FrontPart front = new FrontPart(torso, wholeBody);
        BackPart back = new BackPart(torso, wholeBody);
        torso.addChildren(front, back);

        return Arrays.asList(torso, front, back);
    }
}
