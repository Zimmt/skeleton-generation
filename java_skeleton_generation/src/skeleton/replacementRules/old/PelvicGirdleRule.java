package skeleton.replacementRules.old;

import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.Leg;
import skeleton.elements.terminal.Pelvic;
import skeleton.replacementRules.ReplacementRule;

import java.util.Arrays;
import java.util.List;

public class PelvicGirdleRule extends ReplacementRule {

    private final String inputID = "pelvic girdle";

    public String getInputID() {
        return inputID;
    }

    public List<SkeletonPart> apply(SkeletonPart pelvicGirdle) {
        // if rule is not compatible return element unchanged
        if (!isApplicableTo(pelvicGirdle)) {
            return Arrays.asList(pelvicGirdle);
        }

        //System.out.println("Apply " + inputID + " rule");

        if (!pelvicGirdle.hasChildren()) {
            System.err.println("Pelvic girdle should have children before this rule is applied.");
        }

        Pelvic pelvic = new Pelvic(pelvicGirdle.getParent(), pelvicGirdle);
        pelvicGirdle.getParent().replaceChild(pelvicGirdle, pelvic);
        pelvic.addChildren(pelvicGirdle.getChildren());
        for (SkeletonPart child : pelvicGirdle.getChildren()) {
            child.setParent(pelvic);
        }
        Leg leg = new Leg(pelvic, pelvicGirdle);
        pelvic.addChild(leg);

        return Arrays.asList(pelvic, leg);
    }
}