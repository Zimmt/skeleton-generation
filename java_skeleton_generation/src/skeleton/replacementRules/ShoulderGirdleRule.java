package skeleton.replacementRules;

import skeleton.elements.ExtremityKind;
import skeleton.elements.SkeletonPart;
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

        if (!shoulder.getJoints().isEmpty()) {
            Arm arm = new Arm(shoulder, shoulderGirdle);
            shoulder.addChild(arm);
            generatedParts.add(arm);
        } else {
            System.out.println("no arms generated");
        }

        return generatedParts;
    }

    private Shoulder generateShoulder(ShoulderGirdle shoulderGirdle, Vector3f scale) {
        BoundingBox boundingBox = new BoundingBox(scale);
        TransformationMatrix transform = shoulderGirdle.getParent().getShoulderJoint().calculateChildTransform(boundingBox);
        transform.translate(Shoulder.getLocalTranslationFromJoint(boundingBox));
        ExtremityKind[] extremityKinds = shoulderGirdle.getGenerator().getSkeletonMetaData().getExtremities().getExtremityKindsForStartingPoint(1);

        Shoulder shoulder = new Shoulder(transform, boundingBox, shoulderGirdle.getParent(), shoulderGirdle, false, extremityKinds);
        shoulderGirdle.getParent().replaceChild(shoulderGirdle, shoulder);
        return shoulder;
    }
}
