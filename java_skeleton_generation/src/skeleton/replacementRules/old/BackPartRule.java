package skeleton.replacementRules;

import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.PelvicGirdle;
import skeleton.elements.nonterminal.Tail;

import java.util.Arrays;
import java.util.List;

public class BackPartRule extends ReplacementRule {

    private final String inputID = "back part";

    public String getInputID() {
        return inputID;
    }

    public List<SkeletonPart> apply(SkeletonPart backPart) {
        // if rule is not compatible return element unchanged
        if (!isApplicableTo(backPart)) {
            return Arrays.asList(backPart);
        }

        //System.out.println("Apply " + inputID + " rule");

        if (backPart.hasChildren()) {
            System.err.println("Back part should not have children before this rule is applied.");
        }

        PelvicGirdle pelvicGirdle = new PelvicGirdle(backPart.getParent(), backPart);
        backPart.getParent().replaceChild(backPart, pelvicGirdle);
        Tail tail = new Tail(pelvicGirdle, backPart);
        pelvicGirdle.addChild(tail);

        return Arrays.asList(pelvicGirdle, tail);
    }
}
