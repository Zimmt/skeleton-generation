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

        Shoulder shoulder = generateShoulder(shoulderGirdle, new Vector3f(50f, 20f, 50f));
        generatedParts.add(shoulder);

        Arm arm = new Arm(shoulder, shoulderGirdle);
        shoulder.addChild(arm);
        generatedParts.add(arm);

        return generatedParts;
    }

    private Shoulder generateShoulder(ShoulderGirdle shoulderGirdle, Vector3f scale) {
        BoundingBox boundingBox = new BoundingBox(scale);
        TransformationMatrix transform;
        if (shoulderGirdle.getParent() instanceof ShoulderVertebra) {
            transform = ((ShoulderVertebra) shoulderGirdle.getParent()).getShoulderJoint().calculateChildTransform(boundingBox);
        } else {
            transform = ((Rib) shoulderGirdle.getParent()).getShoulderJoint().calculateChildTransform(boundingBox);
        }
        transform.translate(Shoulder.getLocalTranslationFromJoint(boundingBox));

        Shoulder shoulder = new Shoulder(transform, boundingBox, shoulderGirdle.getParent(), shoulderGirdle, shoulderGirdle.getExtremityKind(), shoulderGirdle.isOnNeck());
        shoulderGirdle.getParent().replaceChild(shoulderGirdle, shoulder);
        return shoulder;
    }
}
