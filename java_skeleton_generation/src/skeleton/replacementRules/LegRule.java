package skeleton.replacementRules;

import skeleton.elements.SkeletonPart;
import skeleton.elements.joints.DummyJoint;
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

    private Thigh generateThigh(Vector3f scale, Leg leg) {

        BoundingBox boundingBox = BoundingBox.defaultBox();
        boundingBox.scale(scale);

        TransformationMatrix transform = leg.getParent().getLegJoint().calculateChildTransform(leg.getParent());
        transform.translate(new Vector3f(-boundingBox.getXLength()/2f, -boundingBox.getYLength(), -boundingBox.getZLength()/2f));

        Point3f jointPosition = new Point3f(boundingBox.getXLength()/2f, 0f, boundingBox.getZLength()/2f);
        DummyJoint thighJoint = new DummyJoint(jointPosition);

        Thigh thigh = new Thigh(transform, boundingBox, leg.getParent(), leg, thighJoint);
        leg.getParent().replaceChild(leg, thigh);

        return thigh;
    }

    private Shin generateShin(Vector3f scale, Leg leg, Thigh thigh) {

        BoundingBox boundingBox = BoundingBox.defaultBox();
        boundingBox.scale(scale);

        TransformationMatrix transform = thigh.getJoint().calculateChildTransform(thigh);
        transform.translate(new Vector3f(-boundingBox.getXLength()/2f, -boundingBox.getYLength(), -boundingBox.getZLength()/2f));

        Point3f jointPosition = new Point3f(boundingBox.getXLength()/2f, 0f, boundingBox.getZLength()/2f);
        DummyJoint joint = new DummyJoint(jointPosition);

        Shin shin = new Shin(transform, boundingBox, thigh, leg, joint);
        thigh.addChild(shin);

        return shin;
    }

    private Foot generateFoot(Vector3f scale, Leg leg, Shin shin) {

        BoundingBox boundingBox = BoundingBox.defaultBox();
        boundingBox.scale(scale);

        TransformationMatrix transform = shin.getJoint().calculateChildTransform(shin);
        transform.translate(new Vector3f(-boundingBox.getXLength(), -boundingBox.getYLength(), -boundingBox.getZLength()/2f));

        Foot foot = new Foot(transform, boundingBox, shin, leg);
        shin.addChild(foot);

        return foot;
    }
}
