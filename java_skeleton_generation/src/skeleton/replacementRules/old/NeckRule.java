package skeleton.replacementRules.old;

import skeleton.elements.SkeletonPart;
import skeleton.elements.terminal.Vertebra;
import skeleton.replacementRules.ReplacementRule;

import java.util.*;

public class NeckRule extends ReplacementRule {

    private final String inputID = "neck";
    private final int minVertebraCount = 1;
    private final int maxVertebraCount = 1;
    private Random random = new Random();

    public String getInputID() {
        return inputID;
    }

    public List<SkeletonPart> apply(SkeletonPart neck) {
        // if rule is not compatible return element unchanged
        if (!isApplicableTo(neck)) {
            return Arrays.asList(neck);
        }

        //System.out.println("Apply " + inputID + " rule");

        if (!neck.hasChildren()) {
            System.err.println("Neck should have children before this rule is applied.");
        }

        int vertebraCount = random.nextInt(maxVertebraCount + 1 - minVertebraCount) + minVertebraCount;
        Vertebra parent = new Vertebra(neck.getParent(), neck);
        neck.getParent().replaceChild(neck, parent);
        ArrayList<SkeletonPart> generatedParts = new ArrayList<>();
        generatedParts.add(parent);

        for (int i = 1; i < vertebraCount; i++) {
            Vertebra child = new Vertebra(parent, neck);
            parent.addChild(child);
            generatedParts.add(child);
            parent = child;
        }

        generatedParts.get(generatedParts.size() - 1).addChildren(neck.getChildren());

        return generatedParts;
    }
}
