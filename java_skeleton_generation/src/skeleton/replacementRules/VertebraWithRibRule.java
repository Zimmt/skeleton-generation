package skeleton.replacementRules;

import skeleton.elements.SkeletonPart;
import skeleton.elements.terminal.Rib;
import skeleton.elements.terminal.Vertebra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VertebraWithRibRule extends ReplacementRule {

    private final String inputID = "vertebra with rib";

    public String getInputID() {
        return inputID;
    }

    public List<SkeletonPart> apply(SkeletonPart vertebraWithRib) {
        // if rule is not compatible return element unchanged
        if (!isApplicableTo(vertebraWithRib)) {
            return Arrays.asList(vertebraWithRib);
        }

        //System.out.println("Apply " + inputID + " rule");

        if (!vertebraWithRib.hasChildren()) {
            System.err.println("'Vertebra with rib' should have children before this rule is applied.");
        }

        Vertebra vertebra = new Vertebra(vertebraWithRib.getParent());
        vertebraWithRib.getParent().replaceChild(vertebraWithRib, vertebra);
        vertebra.addChildren(vertebraWithRib.getChildren());
        for (SkeletonPart child : vertebraWithRib.getChildren()) {
            child.setParent(vertebra);
        }
        Rib rib = new Rib(vertebra);
        vertebra.addChild(rib);

        return Arrays.asList(vertebra, rib);
    }
}
