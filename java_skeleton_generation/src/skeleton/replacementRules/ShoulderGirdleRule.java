package skeleton.replacementRules;

import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.Arm;
import skeleton.elements.nonterminal.ShoulderGirdle;
import skeleton.elements.terminal.Rib;
import skeleton.elements.terminal.Shoulder;
import skeleton.elements.terminal.ShoulderVertebra;
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
    private static Vector3f shoulderScale = new Vector3f(60f, 50f, 50f);

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

        Shoulder shoulder = generateShoulder(shoulderGirdle);
        generatedParts.add(shoulder);

        Arm arm = new Arm(shoulder, shoulderGirdle);
        shoulder.addChild(arm);
        generatedParts.add(arm);

        return generatedParts;
    }

    private Shoulder generateShoulder(ShoulderGirdle shoulderGirdle) {
        BoundingBox boundingBox = new BoundingBox(shoulderScale);
        TransformationMatrix transform;
        if (shoulderGirdle.getParent() instanceof ShoulderVertebra) {
            transform = ((ShoulderVertebra) shoulderGirdle.getParent()).getShoulderJoint().calculateChildTransform(boundingBox);
        } else {
            transform = ((Rib) shoulderGirdle.getParent()).getShoulderJoint().calculateChildTransform(boundingBox);
        }
        transform.translate(Shoulder.getLocalTranslationFromJoint(boundingBox));

        Shoulder shoulder = new Shoulder(transform, boundingBox, shoulderGirdle.getParent(), shoulderGirdle, shoulderGirdle.getExtremityPositioning(), shoulderGirdle.isOnNeck());
        shoulderGirdle.getParent().replaceChild(shoulderGirdle, shoulder);
        return shoulder;
    }
}
