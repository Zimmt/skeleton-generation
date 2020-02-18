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
        float footHeight = 10f;

        float thighHeight = (leg.getParent().getWorldPosition().y - footHeight) * thighShinRate;
        float shinHeight = (leg.getParent().getWorldPosition().y - footHeight) - thighHeight;

        Vector3f thighScale = new Vector3f(
                0.6f * leg.getParent().getBoundingBox().getXLength(),
                thighHeight,
                0.2f * leg.getParent().getBoundingBox().getZLength());
        Thigh thigh = generateThigh(thighScale, leg);
        generatedParts.add(thigh);

        Vector3f shinScale = new Vector3f(
                0.8f * thigh.getBoundingBox().getXLength(),
                shinHeight,
                0.8f * thigh.getBoundingBox().getZLength());
        Shin shin = generateShin(shinScale, leg, thigh);
        generatedParts.add(shin);

        Vector3f footScale = new Vector3f(
                4f * shin.getBoundingBox().getXLength(),
                footHeight,
                2f * shin.getBoundingBox().getZLength());
        Foot foot = generateFoot(footScale, leg, shin);
        generatedParts.add(foot);

        return generatedParts;
    }

    /**
     * position: joint rotation point is on top side in the middle
     * joint rotation point: as set by leg
     */
    private Thigh generateThigh(Vector3f scale, Leg leg) {

        BoundingBox boundingBox = BoundingBox.defaultBox();
        boundingBox.scale(scale);

        Vector3f relativePosition = new Vector3f(leg.getJointRotationPoint());
        relativePosition.add(new Vector3f(-boundingBox.getXLength()/2f, -boundingBox.getYLength(), -boundingBox.getZLength()/2f));
        TransformationMatrix transform = new TransformationMatrix(relativePosition);

        Thigh thigh = new Thigh(transform, leg.getJointRotationPoint(), boundingBox, leg.getParent(), leg);
        leg.getParent().replaceChild(leg, thigh);

        return thigh;
    }

    /**
     * position: center of up side is at joint rotation point
     * joint rotation point: down side of thigh in the middle
     */
    private Shin generateShin(Vector3f scale, Leg leg, Thigh thigh) {

        BoundingBox boundingBox = BoundingBox.defaultBox();
        boundingBox.scale(scale);

        Point3f jointRotationPoint = new Point3f(
                thigh.getBoundingBox().getXLength()/2,
                0f,
                thigh.getBoundingBox().getZLength()/2);

        Vector3f relativePosition = new Vector3f(jointRotationPoint);
        relativePosition.add(new Point3f(-boundingBox.getXLength()/2f, -boundingBox.getYLength(), -boundingBox.getZLength()/2f));
        TransformationMatrix transform = new TransformationMatrix(relativePosition);

        Shin shin = new Shin(transform, jointRotationPoint, boundingBox, thigh, leg);
        thigh.addChild(shin);

        return shin;
    }

    /**
     * position: down side on the floor, right side continuing right side of shin
     * joint rotation point: down side of shin in the middle
     */
    private Foot generateFoot(Vector3f scale, Leg leg, Shin shin) {

        BoundingBox boundingBox = BoundingBox.defaultBox();
        boundingBox.scale(scale);

        Point3f jointRotationPoint = new Point3f(
                shin.getBoundingBox().getXLength()/2,
                0f,
                shin.getBoundingBox().getZLength()/2);

        TransformationMatrix shinWorldTransform = shin.calculateWorldTransform();
        Point3f worldPosition = new Point3f(); // local origin of shin
        shinWorldTransform.applyOnPoint(worldPosition); // global origin of shin
        worldPosition.y = 0f; // projected on xz plane
        worldPosition.x = worldPosition.x + shin.getBoundingBox().getXLength() - scale.x;
        worldPosition.z = worldPosition.z + shin.getBoundingBox().getZLength()/2 - scale.z/2;

        TransformationMatrix footWorldTransform = new TransformationMatrix(new Vector3f(worldPosition));
        TransformationMatrix inverseParentWorldTransform = TransformationMatrix.getInverse(shinWorldTransform);
        TransformationMatrix localFootTransform = TransformationMatrix.multiply(inverseParentWorldTransform, footWorldTransform);

        Foot foot = new Foot(localFootTransform, jointRotationPoint, boundingBox, shin, leg);
        shin.addChild(foot);

        return foot;
    }
}
