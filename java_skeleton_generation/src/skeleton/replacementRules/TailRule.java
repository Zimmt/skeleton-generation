package skeleton.replacementRules;

import skeleton.elements.SkeletonPart;
import skeleton.elements.terminal.Vertebra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TailRule extends ReplacementRule {

    private final String inputID = "tail";
    private final int minVertebraCount = 1;
    private final int maxVertebraCount = 1;
    private Random random = new Random();

    public String getInputID() {
        return inputID;
    }

    public List<SkeletonPart> apply(SkeletonPart tail) {
        // if rule is not compatible return element unchanged
        if (!isApplicableTo(tail)) {
            return Arrays.asList(tail);
        }

        //System.out.println("Apply " + inputID + " rule");

        if (tail.hasChildren()) {
            System.err.println("Tail should not have children before this rule is applied.");
        }

        int vertebraCount = random.nextInt(maxVertebraCount + 1 - minVertebraCount) + minVertebraCount;
        Vertebra parent = new Vertebra(tail.getParent());
        tail.getParent().replaceChild(tail, parent);
        ArrayList<SkeletonPart> generatedParts = new ArrayList<>();
        generatedParts.add(parent);

        for (int i = 1; i < vertebraCount; i++) {
            Vertebra child = new Vertebra(parent);
            parent.addChild(child);
            generatedParts.add(child);
            parent = child;
        }

        return generatedParts;
    }
}
