package skeleton.replacementRules;

import skeleton.elements.SkeletonPart;
import skeleton.elements.terminal.Vertebra;

import java.util.*;

public class NeckRule extends ReplacementRule {

    private final String inputID = "neck";
    private final int minVertebraCount = 6;
    private final int maxVertebraCount = 31;
    private Random random = new Random();

    public String getInputID() {
        return inputID;
    }

    public List<SkeletonPart> apply(SkeletonPart neck) {
        // if rule is not compatible return element unchanged
        if (!isApplicableTo(neck)) {
            return Collections.singletonList(neck);
        }

        if (!neck.hasChildren()) {
            System.err.println("Neck should have children before this rule is applied.");
        }

        int vertebraCount = random.nextInt(maxVertebraCount - minVertebraCount) + minVertebraCount;
        Vertebra parent = new Vertebra(neck.getParent());
        List<SkeletonPart> generatedParts = Collections.singletonList(parent);

        for (int i = 1; i < vertebraCount + 1; i++) {
            Vertebra child = new Vertebra(parent);
            parent.addChild(child);
            generatedParts.add(child);
            parent = child;
        }

        return generatedParts;
    }
}
