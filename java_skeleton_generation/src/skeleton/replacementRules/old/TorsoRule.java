package skeleton.replacementRules;

import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.Chest;
import skeleton.elements.nonterminal.FrontPart;
import skeleton.elements.nonterminal.ShoulderGirdle;
import skeleton.elements.terminal.Shoulder;
import skeleton.elements.terminal.Vertebra;

import java.util.*;

public class TorsoRule extends ReplacementRule {

    private final String inputID = "torso";
    private final int minVertebraCount = 1;
    private final int maxVertebraCount = 1;
    private Random random = new Random();

    public String getInputID() {
        return inputID;
    }

    public List<SkeletonPart> apply(SkeletonPart torso) {
        // if rule is not compatible return element unchanged
        if (!isApplicableTo(torso)) {
            return Arrays.asList(torso);
        }

        //System.out.println("Apply " + inputID + " rule");

        if (!torso.hasChildren()) {
            System.err.println("Torso should have children before this rule is applied.");
        }
        if (!(torso.getChildren().size() == 2)) {
            System.err.println("Torso does not have exactly two children.");
        }

        List<SkeletonPart> children = torso.getChildren();
        SkeletonPart frontChild;
        SkeletonPart backChild;
        if (children.get(0) instanceof FrontPart || children.get(0) instanceof ShoulderGirdle || children.get(0) instanceof Shoulder) {
            frontChild = children.get(0);
            backChild = children.get(1);
        } else {
            frontChild = children.get(1);
            backChild = children.get(0);
        }

        Vertebra parent = new Vertebra(null, torso); // root (TODO: is this always a good position?)
        Chest chest = new Chest(parent, torso);
        chest.addChild(frontChild);
        frontChild.setParent(chest);
        parent.addChild(chest);

        ArrayList<SkeletonPart> generatedParts = new ArrayList<>();
        generatedParts.add(chest);
        generatedParts.add(parent);
        int vertebraCount = random.nextInt(maxVertebraCount + 1 - minVertebraCount) + minVertebraCount;
        for (int i = 1; i < vertebraCount; i++) {
            Vertebra child = new Vertebra(parent, torso);
            parent.addChild(child);
            generatedParts.add(child);
            parent = child;
        }

        SkeletonPart lastVertebra = generatedParts.get(generatedParts.size() -1);
        lastVertebra.addChild(backChild);
        backChild.setParent(lastVertebra);

        return generatedParts;
    }
}
