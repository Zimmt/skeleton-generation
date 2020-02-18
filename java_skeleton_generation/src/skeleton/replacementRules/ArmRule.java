package skeleton.replacementRules;

import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.Arm;
import skeleton.elements.nonterminal.Leg;
import skeleton.elements.terminal.*;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Generates
 * - terminal upper arm
 * - terminal lower arm
 * - terminal hand
 */
public class ArmRule extends ReplacementRule {

    private final String inputID = "arm";

    public String getInputID() {return inputID; }

    public List<SkeletonPart> apply(SkeletonPart skeletonPart) {
        // if rule is not compatible return element unchanged
        if (!isApplicableTo(skeletonPart)) {
            return Arrays.asList(skeletonPart);
        }

        Arm arm = (Arm) skeletonPart;
        List<SkeletonPart> generatedParts = new ArrayList<>();

        float upperLowerArmRate = 2f / 3f;
        float handHeight = 10f;

        // todo hand and lower arm only touch when arm is vertical
        Point3f jointRotationPointShoulder = new Point3f(arm.getJointRotationPoint());
        arm.getParent().calculateWorldTransform().applyOnPoint(jointRotationPointShoulder);
        float upperArmHeight = (jointRotationPointShoulder.y - handHeight) * upperLowerArmRate;
        float lowerArmHeight = jointRotationPointShoulder.y - handHeight - upperArmHeight;

        Vector3f upperArmScale = new Vector3f(
                0.6f * arm.getParent().getBoundingBox().getXLength(),
                upperArmHeight,
                0.4f * arm.getParent().getBoundingBox().getZLength());
        UpperArm upperArm = generateUpperArm(upperArmScale, arm);
        generatedParts.add(upperArm);

        Vector3f lowerArmScale = new Vector3f(
                0.8f * upperArm.getBoundingBox().getXLength(),
                lowerArmHeight,
                0.8f * upperArm.getBoundingBox().getZLength());
        LowerArm lowerArm = generateLowerArm(lowerArmScale, arm, upperArm);
        generatedParts.add(lowerArm);

        Vector3f handScale = new Vector3f(
                4f * lowerArm.getBoundingBox().getXLength(),
                handHeight,
                2f * lowerArm.getBoundingBox().getZLength());
        Hand hand = generateHand(handScale, arm, lowerArm);
        generatedParts.add(hand);

        return generatedParts;
    }
    /**
     * copied code from leg rule
     * position: joint rotation point is on top side in the middle
     * joint rotation point: as set by arm
     */
    private UpperArm generateUpperArm(Vector3f scale, Arm arm) {

        BoundingBox boundingBox = BoundingBox.defaultBox();
        boundingBox.scale(scale);

        Vector3f relativePosition = new Vector3f(arm.getJointRotationPoint());
        relativePosition.add(new Vector3f(-boundingBox.getXLength()/2f, -boundingBox.getYLength(), -boundingBox.getZLength()/2f));
        TransformationMatrix transform = new TransformationMatrix(relativePosition);

        UpperArm upperArm = new UpperArm(transform, arm.getJointRotationPoint(), boundingBox, arm.getParent(), arm);
        arm.getParent().replaceChild(arm, upperArm);

        return upperArm;
    }

    /**
     * copied code from leg rule
     * position: center of up side is at joint rotation point
     * joint rotation point: down side of upper arm in the middle
     */
    private LowerArm generateLowerArm(Vector3f scale, Arm arm, UpperArm upperArm) {

        BoundingBox boundingBox = BoundingBox.defaultBox();
        boundingBox.scale(scale);

        Point3f jointRotationPoint = new Point3f(
                upperArm.getBoundingBox().getXLength()/2,
                0f,
                upperArm.getBoundingBox().getZLength()/2);

        Vector3f relativePosition = new Vector3f(jointRotationPoint);
        relativePosition.add(new Point3f(-boundingBox.getXLength()/2f, -boundingBox.getYLength(), -boundingBox.getZLength()/2f));
        TransformationMatrix transform = new TransformationMatrix(relativePosition);

        LowerArm lowerArm = new LowerArm(transform, jointRotationPoint, boundingBox, upperArm, arm);
        upperArm.addChild(lowerArm);

        return lowerArm;
    }

    /**
     * copied code from leg rule
     * position: down side on the floor, right side continuing right side of lower arm
     * joint rotation point: down side of lower arm in the middle
     */
    private Hand generateHand(Vector3f scale, Arm arm, LowerArm lowerArm) {

        BoundingBox boundingBox = BoundingBox.defaultBox();
        boundingBox.scale(scale);

        Point3f jointRotationPoint = new Point3f(
                lowerArm.getBoundingBox().getXLength()/2,
                0f,
                lowerArm.getBoundingBox().getZLength()/2);

        TransformationMatrix lowerArmWorldTransform = lowerArm.calculateWorldTransform();
        Point3f worldPosition = new Point3f(); // local origin of lower arm
        lowerArmWorldTransform.applyOnPoint(worldPosition); // global origin of lower arm
        worldPosition.y = 0f; // projected on xz plane
        worldPosition.x = worldPosition.x + lowerArm.getBoundingBox().getXLength() - scale.x;
        worldPosition.z = worldPosition.z + lowerArm.getBoundingBox().getZLength()/2 - scale.z/2;

        TransformationMatrix footWorldTransform = new TransformationMatrix(new Vector3f(worldPosition));
        TransformationMatrix inverseParentWorldTransform = TransformationMatrix.getInverse(lowerArmWorldTransform);
        TransformationMatrix localFootTransform = TransformationMatrix.multiply(inverseParentWorldTransform, footWorldTransform);

        Hand hand = new Hand(localFootTransform, jointRotationPoint, boundingBox, lowerArm, arm);
        lowerArm.addChild(hand);

        return hand;
    }
}
