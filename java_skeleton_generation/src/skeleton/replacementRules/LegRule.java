package skeleton.replacementRules;

import skeleton.ExtremityData;
import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.Leg;
import skeleton.elements.terminal.Foot;
import skeleton.elements.terminal.Shin;
import skeleton.elements.terminal.Thigh;
import util.BoundingBox;
import util.TransformationMatrix;

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
        ExtremityData extremityData = leg.getGenerator().getSkeletonMetaData().getExtremities();

        Vector3f thighScale = new Vector3f(
                0.6f * leg.getParent().getBoundingBox().getXLength(),
                extremityData.getLengthUpperLeg(),
                0.2f * leg.getParent().getBoundingBox().getZLength());
        Thigh thigh = generateThigh(thighScale, leg);
        generatedParts.add(thigh);

        Vector3f shinScale = new Vector3f(
                0.8f * thigh.getBoundingBox().getXLength(),
                extremityData.getLengthLowerLeg(),
                0.8f * thigh.getBoundingBox().getZLength());
        Shin shin = generateShin(shinScale, leg, thigh);
        generatedParts.add(shin);

        Vector3f footScale = new Vector3f(
                extremityData.getLengthFoot(),
                0.8f * shin.getBoundingBox().getXLength(),
                2f * shin.getBoundingBox().getZLength());
        Foot foot = generateFoot(footScale, leg, shin);
        generatedParts.add(foot);

        return generatedParts;
    }

    private Thigh generateThigh(Vector3f scale, Leg leg) {
        BoundingBox boundingBox = new BoundingBox(scale);
        TransformationMatrix transform = leg.getParent().getLegJoint().calculateChildTransform(boundingBox);

        Thigh thigh = new Thigh(transform, boundingBox, leg.getParent(), leg, false);
        leg.getParent().replaceChild(leg, thigh);
        return thigh;
    }

    private Shin generateShin(Vector3f scale, Leg leg, Thigh thigh) {
        BoundingBox boundingBox = new BoundingBox(scale);
        TransformationMatrix transform = thigh.getJoint().calculateChildTransform(boundingBox);

        Shin shin = new Shin(transform, boundingBox, thigh, leg, false);
        thigh.addChild(shin);
        return shin;
    }

    private Foot generateFoot(Vector3f scale, Leg leg, Shin shin) {
        BoundingBox boundingBox = new BoundingBox(scale);
        TransformationMatrix transform = shin.getJoint().calculateChildTransform(boundingBox);

        Foot foot = new Foot(transform, boundingBox, shin, leg);
        shin.addChild(foot);
        return foot;
    }
}
