package skeleton.replacementRules;

import skeleton.ExtremityData;
import skeleton.elements.SkeletonPart;
import skeleton.elements.joints.ExtremityKind;
import skeleton.elements.nonterminal.Arm;
import skeleton.elements.nonterminal.ShoulderGirdle;
import skeleton.elements.terminal.Shoulder;
import util.BoundingBox;
import util.TransformationMatrix;

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

        Shoulder shoulder = generateShoulder(shoulderGirdle, new Vector3f(80f, 20f, 100f));
        generatedParts.add(shoulder);

        Arm arm = new Arm(shoulder, shoulderGirdle);
        shoulder.addChild(arm);
        generatedParts.add(arm);

        return generatedParts;
    }

    private Shoulder generateShoulder(ShoulderGirdle shoulderGirdle, Vector3f scale) {
        BoundingBox boundingBox = new BoundingBox(scale);
        TransformationMatrix transform = shoulderGirdle.getParent().getShoulderJoint().calculateChildTransform(boundingBox);
        transform.translate(Shoulder.getLocalTranslationFromJoint(boundingBox));

        ExtremityData extremityData = shoulderGirdle.getGenerator().getSkeletonMetaData().getExtremities();
        ExtremityKind extremityKind;
        if (extremityData.getFlooredLegs() > 1) {
            extremityKind = ExtremityKind.FLOORED_LEG;
        } else if (extremityData.getWings() > 0) {
            extremityKind = ExtremityKind.WING;
        } else if (extremityData.getArms() >= 1) {
            extremityKind = ExtremityKind.NON_FLOORED_LEG;
        } else {
            extremityKind = ExtremityKind.FIN;
        }

        Shoulder shoulder = new Shoulder(transform, boundingBox, shoulderGirdle.getParent(), shoulderGirdle, false, extremityKind);
        shoulderGirdle.getParent().replaceChild(shoulderGirdle, shoulder);
        return shoulder;
    }
}
