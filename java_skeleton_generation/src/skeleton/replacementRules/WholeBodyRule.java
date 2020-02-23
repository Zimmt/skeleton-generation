package skeleton.replacementRules;

import skeleton.SpinePart;
import skeleton.elements.SkeletonPart;
import skeleton.elements.joints.SpineOrientedJoint;
import skeleton.elements.nonterminal.BackPart;
import skeleton.elements.nonterminal.FrontPart;
import skeleton.elements.nonterminal.WholeBody;
import skeleton.elements.terminal.RootVertebra;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Generates
 * - terminal root vertebra in the middle of the back spine
 * - non terminal front part (root vertebra to head)
 * - non terminal back part (root vertebra to tail)
 */
public class WholeBodyRule extends ReplacementRule {

    private final String inputID = "whole body";

    public String getInputID() {
        return inputID;
    }

    public List<SkeletonPart> apply(SkeletonPart skeletonPart) {
        // if rule is not compatible return element unchanged
        if (!isApplicableTo(skeletonPart)) {
            return Arrays.asList(skeletonPart);
        }
        WholeBody wholeBody = (WholeBody) skeletonPart;
        Vector3f rootVertebraScales = new Vector3f(1f, 1f, 1f);

        List<SkeletonPart> generatedParts = new ArrayList<>();

        RootVertebra root = generateRootVertebra(wholeBody, rootVertebraScales);
        generatedParts.add(root);

        FrontPart frontPart = new FrontPart(root, wholeBody, 0.5f);
        root.addChild(frontPart);
        generatedParts.add(frontPart);

        BackPart backPart = new BackPart(root, wholeBody, 0.5f);
        root.addChild(backPart);
        generatedParts.add(backPart);

        return generatedParts;
    }

    /**
     * position: the center of the back spine is in the middle of the generated vertebra
     * joints: left and right side in the middle
     */
    private RootVertebra generateRootVertebra(WholeBody ancestor, Vector3f vertebraScales) {
        BoundingBox boundingBox = BoundingBox.defaultBox();
        boundingBox.scale(vertebraScales);

        float spinePosition = 0.5f;
        Point3f center = ancestor.getGenerator().getSkeletonMetaData().getSpine().getBack().apply3d(spinePosition);
        Point3f position = new Point3f(center);
        position.x = position.x - boundingBox.getXLength() / 2f;
        position.y = position.y - boundingBox.getYLength() / 2f;
        position.z = position.z - boundingBox.getZLength() / 2f;
        TransformationMatrix transform = new TransformationMatrix(new Vector3f(position));

        // todo: spine position for joints not correct
        Point3f leftJointPosition = new Point3f(position.x, center.y, center.z);
        Point3f rightJointPosition = new Point3f(position.x + boundingBox.getXLength(), center.y, center.z);
        SpineOrientedJoint leftJoint = new SpineOrientedJoint(leftJointPosition, SpinePart.BACK, spinePosition, false, ancestor.getGenerator());
        SpineOrientedJoint rightJoint = new SpineOrientedJoint(rightJointPosition, SpinePart.BACK, spinePosition, true, ancestor.getGenerator());

        return new RootVertebra(transform, boundingBox, null, ancestor, leftJoint, rightJoint);
    }
}
