package skeleton.replacementRules;

import skeleton.elements.SkeletonPart;
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
 * - non terminal arm TODO
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

        Shoulder shoulder = generateShoulder(shoulderGirdle, new Vector3f(1.5f, 0.5f, 1.5f));
        generatedParts.add(shoulder);

        return generatedParts;
    }

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
}
