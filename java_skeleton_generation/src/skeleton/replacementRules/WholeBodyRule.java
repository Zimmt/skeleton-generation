package skeleton.replacementRules;

import skeleton.SkeletonGenerator;
import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.BackPart;
import skeleton.elements.nonterminal.FrontPart;
import skeleton.elements.nonterminal.WholeBody;
import skeleton.elements.terminal.TerminalElement;
import skeleton.elements.terminal.Vertebra;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.*;
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

        Vertebra root = generateRootVertebra(wholeBody, rootVertebraScales);
        generatedParts.add(root);

        FrontPart frontPart = generateFrontPart(wholeBody, 0.5f, root);
        generatedParts.add(frontPart);

        BackPart backPart = generateBackPart(wholeBody, 0.5f, root);
        generatedParts.add(backPart);

        return generatedParts;
    }

    /**
     * position: the center of the back spine is in the middle of the generated vertebra
     * joint rotation point: none
     */
    private Vertebra generateRootVertebra(WholeBody ancestor, Vector3f vertebraScales) {
        BoundingBox boundingBox = BoundingBox.defaultBox();
        boundingBox.scale(vertebraScales);

        Point3f position = ancestor.getGenerator().getSkeletonMetaData().getSpine().getBack().apply3d(0.5f);
        position.x = position.x - boundingBox.getXLength() / 2f;
        position.y = position.y - boundingBox.getYLength() / 2f;
        position.z = position.z - boundingBox.getZLength() / 2f;
        TransformationMatrix transform = new TransformationMatrix(new Vector3f(position));

        return new Vertebra(transform, null, boundingBox, null, ancestor);
    }

    /**
     * position: same as parent
     * joint rotation point: left side of parent in the middle (on the spine)
     */
    private FrontPart generateFrontPart(WholeBody wholeBody, float endPosition, TerminalElement parent) {

        TransformationMatrix transform = new TransformationMatrix();
        Point3f jointRotationPoint = new Point3f(0f, parent.getBoundingBox().getYLength() / 2f, parent.getBoundingBox().getZLength() / 2f);

        FrontPart frontPart = new FrontPart(transform, jointRotationPoint, parent, wholeBody, endPosition);
        parent.addChild(frontPart);

        return frontPart;
    }

    /**
     * position: same as parent
     * joint rotation point: right side of parent in the middle (on the spine)
     */
    private BackPart generateBackPart(WholeBody wholeBody, float startPosition, TerminalElement parent) {

        TransformationMatrix transform = new TransformationMatrix();
        Point3f jointRotationPoint = new Point3f(parent.getBoundingBox().getXLength(), parent.getBoundingBox().getYLength() / 2f, parent.getBoundingBox().getZLength() / 2f);

        BackPart backPart = new BackPart(transform, jointRotationPoint, parent, wholeBody, startPosition);
        parent.addChild(backPart);

        return backPart;
    }
}
