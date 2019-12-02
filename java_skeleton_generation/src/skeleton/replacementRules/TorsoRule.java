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
    private final int minVertebraCount = 5;
    private final int maxVertebraCount = 5;
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

        CubicBezierCurve spine = torso.getGenerator().getSpineLocation();

        // transform
        TransformationMatrix parentTransform = new TransformationMatrix();
        float parentAngle = (float) Math.atan(spine.applyDerivation(0f).y);
        parentTransform.rotateAroundZ(parentAngle);

        Vector3f position = new Vector3f(spine.apply3d(0f)); // world position (= local position)
        parentTransform.translate(position);


        Vertebra parent = new Vertebra(parentTransform, parentBoundingBox, null, torso); // root
        ArrayList<SkeletonPart> generatedParts = new ArrayList<>();
        generatedParts.add(parent);

        for (int i = 1; i < vertebraCount; i++) {
            float childT = (float) i / (float) vertebraCount;

            // we have the world position of the spine and we have to get something that is relative to parent
            TransformationMatrix childTransform = TransformationMatrix.getInverse(parent.getWorldTransform());

            float childAngle = (float) Math.atan(spine.applyDerivation(childT).y);
            childTransform.rotateAroundZ(childAngle);

            Vector3f childPosition = new Vector3f(spine.apply3d(childT)); // world position
            childTransform.translate(childPosition);

            BoundingBox childBox = parentBoundingBox.cloneBox();

            Vertebra child = new Vertebra(childTransform, childBox, parent, torso);
            parent.addChild(child);
            generatedParts.add(child);
            parent = child;
        }

        return generatedParts;
    }
}
