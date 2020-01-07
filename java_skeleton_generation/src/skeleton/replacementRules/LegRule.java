package skeleton.replacementRules;

import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.Leg;
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
 * - terminal shin and foot TODO
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
        float footHeight = 1f;

        Thigh thigh = generateThigh(1.5f, 1.5f, leg, thighShinRate, footHeight);
        generatedParts.add(thigh);


        return generatedParts;
    }

    private Thigh generateThigh(float xWidth, float zWidth, Leg leg, float thighShinRate, float footHeight) {

        float height = (leg.getParent().getWorldPosition().y - footHeight) * thighShinRate;

        Vector3f relativePosition = new Vector3f(leg.getJointRotationPoint());
        relativePosition.add(new Vector3f(-xWidth / 2f, -height, -zWidth / 2f));
        TransformationMatrix transform = new TransformationMatrix(relativePosition);

        BoundingBox boundingBox = BoundingBox.defaultBox();
        boundingBox.scale(new Vector3f(xWidth, height, zWidth));

        Thigh thigh = new Thigh(transform, leg.getJointRotationPoint(), boundingBox, leg.getParent(), leg);
        leg.getParent().replaceChild(leg, thigh);

        return thigh;
    }
}
