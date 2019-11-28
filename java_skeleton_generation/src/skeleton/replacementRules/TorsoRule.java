package skeleton.replacementRules;

import skeleton.elements.SkeletonPart;
import skeleton.elements.terminal.Vertebra;
import util.BoundingBox;
import util.CubicBezierCurve;
import util.TransformationMatrix;

import javax.vecmath.Vector3f;
import java.util.*;

public class TorsoRule extends ReplacementRule {

    private final String inputID = "torso";
    private final int minVertebraCount = 10;
    private final int maxVertebraCount = 10;
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

        int vertebraCount = random.nextInt(maxVertebraCount + 1 - minVertebraCount) + minVertebraCount;

        BoundingBox parentBoundingBox = BoundingBox.defaultBox();
        parentBoundingBox.setXLength(1f);

        CubicBezierCurve spinePosition = torso.getGenerator().getSpineLocation();
        TransformationMatrix parentTransform = new TransformationMatrix(torso.getTransform());
        Vector3f diff = new Vector3f(spinePosition.apply(0f));
        diff.sub(torso.getWorldPosition());
        parentTransform.translate(diff);

        Vertebra parent = new Vertebra(parentTransform, parentBoundingBox, null, torso); // root
        ArrayList<SkeletonPart> generatedParts = new ArrayList<>();
        generatedParts.add(parent);

        for (int i = 1; i < vertebraCount; i++) {
            Vector3f childPosition = new Vector3f(spinePosition.apply((float) i / (float) vertebraCount)); // world position
            childPosition.sub(parent.getWorldPosition()); // local position
            TransformationMatrix childTransform = new TransformationMatrix(childPosition);
            BoundingBox childBox = parentBoundingBox.cloneBox();

            Vertebra child = new Vertebra(childTransform, childBox, parent, torso);
            parent.addChild(child);
            generatedParts.add(child);
            parent = child;
        }

        return generatedParts;
    }
}
