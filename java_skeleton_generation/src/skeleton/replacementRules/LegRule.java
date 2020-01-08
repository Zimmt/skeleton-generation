package skeleton.replacementRules;

import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.Leg;
import skeleton.elements.terminal.Foot;
import skeleton.elements.terminal.Shin;
import skeleton.elements.terminal.Thigh;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Generates
 * - terminal thigh
 * - terminal shin
 * - terminal foot
 */
public class LegRule extends ReplacementRule {

    private final String inputID = "leg";

    public String getInputID() {
        return inputID;
    }

    public List<SkeletonPart> apply(SkeletonPart skeletonPart) {
        // if rule is not compatible return element unchanged
        if (!isApplicableTo(skeletonPart)) {
            return Arrays.asList(skeletonPart);
        }

        Leg leg = (Leg) skeletonPart;
        List<SkeletonPart> generatedParts = new ArrayList<>();

        float thighShinRate = 2f / 3f;
        float footHeight = 0.5f;

        float thighHeight = (leg.getParent().getWorldPosition().y - footHeight) * thighShinRate;
        float shinHeight = (leg.getParent().getWorldPosition().y - footHeight) - thighHeight;

        Thigh thigh = generateThigh(thighHeight,0.8f, 0.8f, leg);
        generatedParts.add(thigh);
        Shin shin = generateShin(shinHeight, 0.5f, 0.5f, leg, thigh);
        generatedParts.add(shin);
        Foot foot = generateFoot(footHeight, 2f, 1f, leg, shin);
        generatedParts.add(foot);

        return generatedParts;
    }

    private Thigh generateThigh(float height, float xWidth, float zWidth, Leg leg) {

        Vector3f relativePosition = new Vector3f(leg.getJointRotationPoint());
        relativePosition.add(new Vector3f(-xWidth / 2f, -height, -zWidth / 2f));
        TransformationMatrix transform = new TransformationMatrix(relativePosition);

        BoundingBox boundingBox = BoundingBox.defaultBox();
        boundingBox.scale(new Vector3f(xWidth, height, zWidth));

        Thigh thigh = new Thigh(transform, leg.getJointRotationPoint(), boundingBox, leg.getParent(), leg);
        leg.getParent().replaceChild(leg, thigh);

        return thigh;
    }

    private Shin generateShin(float height, float xWidth, float zWidth, Leg leg, Thigh thigh) {

        Point3f jointRotationPoint = new Point3f(
                thigh.getBoundingBox().getXLength()/2,
                0f,
                thigh.getBoundingBox().getZLength()/2);

        Vector3f relativePosition = new Vector3f(jointRotationPoint);
        relativePosition.add(new Point3f(-xWidth/2, -height, -zWidth/2));

        TransformationMatrix transform = new TransformationMatrix(relativePosition);

        BoundingBox boundingBox = BoundingBox.defaultBox();
        boundingBox.scale(new Vector3f(xWidth, height, zWidth));

        Shin shin = new Shin(transform, jointRotationPoint, boundingBox, thigh, leg);
        thigh.addChild(shin);

        return shin;
    }

    private Foot generateFoot(float height, float xWidth, float zWidth, Leg leg, Shin shin) {

        Point3f jointRotationPoint = new Point3f(
                shin.getBoundingBox().getXLength()/2,
                0f,
                shin.getBoundingBox().getZLength()/2);

        TransformationMatrix shinWorldTransform = shin.calculateWorldTransform();
        Point3f worldPosition = new Point3f(); // local origin of shin
        shinWorldTransform.applyOnPoint(worldPosition); // global origin of shin
        worldPosition.y = 0f; // projected on xz plane
        worldPosition.x = worldPosition.x + shin.getBoundingBox().getXLength() - xWidth;
        worldPosition.z = worldPosition.z + shin.getBoundingBox().getZLength()/2 - zWidth/2;

        TransformationMatrix footWorldTransform = new TransformationMatrix(new Vector3f(worldPosition));
        TransformationMatrix inverseParentWorldTransform = TransformationMatrix.getInverse(shinWorldTransform);
        TransformationMatrix localFootTransform = TransformationMatrix.multiply(inverseParentWorldTransform, footWorldTransform);

        BoundingBox boundingBox = BoundingBox.defaultBox();
        boundingBox.scale(new Vector3f(xWidth, height, zWidth));

        Foot foot = new Foot(localFootTransform, jointRotationPoint, boundingBox, shin, leg);
        shin.addChild(foot);

        return foot;
    }
}
