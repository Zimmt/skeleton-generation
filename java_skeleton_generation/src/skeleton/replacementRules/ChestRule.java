package skeleton.replacementRules;

import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.VertebraWithRib;

import java.util.*;

public class ChestRule extends ReplacementRule {

    private final String inputID = "chest";
    private final int minVertebraCount = 1;
    private final int maxVertebraCount = 1;
    private Random random = new Random();

    public String getInputID() {
        return inputID;
    }

    public List<SkeletonPart> apply(SkeletonPart chest) {
        // if rule is not compatible return element unchanged
        if (!isApplicableTo(chest)) {
            return Arrays.asList(chest);
        }

        //System.out.println("Apply " + inputID + " rule");

        if (!chest.hasChildren()) {
            System.err.println("Chest should not have children before this rule is applied.");
        }

        int vertebraCount = random.nextInt(maxVertebraCount + 1 - minVertebraCount) + minVertebraCount;
        VertebraWithRib parent = new VertebraWithRib(chest.getParent());
        chest.getParent().replaceChild(chest, parent);
        ArrayList<SkeletonPart> generatedParts = new ArrayList<>();
        generatedParts.add(parent);

        for (int i = 1; i < vertebraCount; i++) {
            VertebraWithRib child = new VertebraWithRib(parent);
            parent.addChild(child);
            generatedParts.add(child);
            parent = child;
        }

        SkeletonPart last = generatedParts.get(generatedParts.size() - 1);
        last.addChildren(chest.getChildren());
        for (SkeletonPart child : chest.getChildren()) {
            child.setParent(last);
        }

        return generatedParts;
    }
}
