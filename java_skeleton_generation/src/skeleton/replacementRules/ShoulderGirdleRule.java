package skeleton.replacementRules;

import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.Arm;
import skeleton.elements.nonterminal.ShoulderGirdle;
import skeleton.elements.terminal.Shoulder;
import skeleton.elements.terminal.TerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Generates
 * - terminal shoulder
 * - non terminal arm
 */
public class ShoulderGirdleRule extends ReplacementRule {

    private final String inputID = "shoulder girdle";

    public String getInputID() {
        return inputID;
    }

    public List<SkeletonPart> apply(SkeletonPart skeletonPart) {
        // if rule is not compatible return element unchanged
        if (!isApplicableTo(skeletonPart)) {
            return Arrays.asList(skeletonPart);
        }

        ShoulderGirdle shoulderGirdle = (ShoulderGirdle) skeletonPart;
        List<SkeletonPart> generatedParts = new ArrayList<>();

        Shoulder shoulder = generateShoulder(shoulderGirdle, new Vector3f(40f, 10f, 50f));
        generatedParts.add(shoulder);
        Arm arm = generateArm(shoulderGirdle, shoulder);
        generatedParts.add(arm);

        return generatedParts;
    }

    /**
     * position: middle of back side is the joint rotation point
     * joint rotation point: joint rotation point that was set by shoulder girdle (front side of parent in the middle)
     */
    private Shoulder generateShoulder(ShoulderGirdle shoulderGirdle, Vector3f dimensions) {

        BoundingBox boundingBox = BoundingBox.defaultBox();
        boundingBox.scale(dimensions);

        TerminalElement parent = shoulderGirdle.getParent();
        Point3f jointRotationPoint = new Point3f(shoulderGirdle.getJointRotationPoint());

        TransformationMatrix transform = new TransformationMatrix();
        Vector3f translation = new Vector3f(shoulderGirdle.getJointRotationPoint());
        translation.add(new Vector3f(-dimensions.x/2, -dimensions.y/2, -dimensions.z));
        transform.translate(translation);

        Shoulder shoulder = new Shoulder(transform, jointRotationPoint, boundingBox, parent, shoulderGirdle);
        parent.replaceChild(shoulderGirdle, shoulder);

        return shoulder;
    }

    /**
     * position: same as shoulder
     * joint rotation point: front side of parent in the middle
     */
    private Arm generateArm(ShoulderGirdle shoulderGirdle, Shoulder shoulder) {
        TransformationMatrix transform = new TransformationMatrix();

        Point3f jointRotationPoint = new Point3f(shoulder.getBoundingBox().getXLength()/2, 0f, shoulder.getBoundingBox().getZLength()/2);

        Arm arm = new Arm(transform, jointRotationPoint, shoulder, shoulderGirdle);
        shoulder.addChild(arm);

        return arm;
    }
}
