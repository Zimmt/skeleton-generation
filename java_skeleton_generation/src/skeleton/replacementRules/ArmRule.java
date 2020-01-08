package skeleton.replacementRules;

import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.Arm;
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
        float handHeight = 0.5f;

        float upperArmHeight = (arm.getParent().getWorldPosition().y - handHeight) * upperLowerArmRate;
        float lowerArmHeight = (arm.getParent().getWorldPosition().y - handHeight) - upperArmHeight;

        UpperArm upperArm = generateUpperArm(upperArmHeight, 0.8f, 0.8f, arm);
        generatedParts.add(upperArm);
        LowerArm lowerArm = generateLowerArm(lowerArmHeight, 0.5f, 0.5f, arm, upperArm);
        generatedParts.add(lowerArm);
        Hand hand = generateHand(handHeight, 2f, 1f, arm, lowerArm);
        generatedParts.add(hand);

        return generatedParts;
    }

    // copied code from thigh
    private UpperArm generateUpperArm(float height, float xWidth, float zWidth, Arm arm) {

        Vector3f relativePosition = new Vector3f(arm.getJointRotationPoint());
        relativePosition.add(new Vector3f(-xWidth / 2f, -height, -zWidth / 2f));
        TransformationMatrix transform = new TransformationMatrix(relativePosition);

        BoundingBox boundingBox = BoundingBox.defaultBox();
        boundingBox.scale(new Vector3f(xWidth, height, zWidth));

        UpperArm upperArm = new UpperArm(transform, arm.getJointRotationPoint(), boundingBox, arm.getParent(), arm);
        arm.getParent().replaceChild(arm, upperArm);

        return upperArm;
    }

    // copied code from shin
    private LowerArm generateLowerArm(float height, float xWidth, float zWidth, Arm arm, UpperArm upperArm) {

        Point3f jointRotationPoint = new Point3f(
                upperArm.getBoundingBox().getXLength()/2,
                0f,
                upperArm.getBoundingBox().getZLength()/2);

        Vector3f relativePosition = new Vector3f(jointRotationPoint);
        relativePosition.add(new Point3f(-xWidth/2, -height, -zWidth/2));

        TransformationMatrix transform = new TransformationMatrix(relativePosition);

        BoundingBox boundingBox = BoundingBox.defaultBox();
        boundingBox.scale(new Vector3f(xWidth, height, zWidth));

        LowerArm lowerArm = new LowerArm(transform, jointRotationPoint, boundingBox, upperArm, arm);
        upperArm.addChild(lowerArm);

        return lowerArm;
    }

    // copied code from foot
    private Hand generateHand(float height, float xWidth, float zWidth, Arm arm, LowerArm lowerArm) {

        Point3f jointRotationPoint = new Point3f(
                lowerArm.getBoundingBox().getXLength()/2,
                0f,
                lowerArm.getBoundingBox().getZLength()/2);

        TransformationMatrix shinWorldTransform = lowerArm.calculateWorldTransform();
        Point3f worldPosition = new Point3f(); // local origin of shin
        shinWorldTransform.applyOnPoint(worldPosition); // global origin of shin
        worldPosition.y = 0f; // projected on xz plane
        worldPosition.x = worldPosition.x + lowerArm.getBoundingBox().getXLength() - xWidth;
        worldPosition.z = worldPosition.z + lowerArm.getBoundingBox().getZLength()/2 - zWidth/2;

        TransformationMatrix footWorldTransform = new TransformationMatrix(new Vector3f(worldPosition));
        TransformationMatrix inverseParentWorldTransform = TransformationMatrix.getInverse(shinWorldTransform);
        TransformationMatrix localFootTransform = TransformationMatrix.multiply(inverseParentWorldTransform, footWorldTransform);

        BoundingBox boundingBox = BoundingBox.defaultBox();
        boundingBox.scale(new Vector3f(xWidth, height, zWidth));

        Hand hand = new Hand(localFootTransform, jointRotationPoint, boundingBox, lowerArm, arm);
        lowerArm.addChild(hand);

        return hand;
    }
}
