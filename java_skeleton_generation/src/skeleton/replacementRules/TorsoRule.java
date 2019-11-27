package skeleton.replacementRules;

import skeleton.elements.SkeletonPart;
import skeleton.elements.terminal.Vertebra;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;
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

        int vertebraCount = random.nextInt(maxVertebraCount + 1 - minVertebraCount) + minVertebraCount;

        BoundingBox parentBoundingBox = torso.getBoundingBox().cloneBox();
        parentBoundingBox.scale(new Vector3f(1f / vertebraCount, 1f, 1f));

        Vertebra parent = new Vertebra(torso.getTransform(), parentBoundingBox, null, torso); // root
        ArrayList<SkeletonPart> generatedParts = new ArrayList<>();
        generatedParts.add(parent);

        for (int i = 1; i < vertebraCount; i++) {
            Matrix3f identity = new Matrix3f(); // all zero matrix
            identity.setIdentity();

            TransformationMatrix childTransform = new TransformationMatrix(new Vector3f(parentBoundingBox.getXLength(), 0f, 0f));
            BoundingBox childBox = parentBoundingBox.cloneBox();

            Vertebra child = new Vertebra(childTransform, childBox, parent, torso);
            parent.addChild(child);
            generatedParts.add(child);
            parent = child;
        }

        return generatedParts;
    }
}
