package skeleton.replacementRules;

import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.BackPart;
import skeleton.elements.nonterminal.FrontPart;
import skeleton.elements.nonterminal.WholeBody;
import skeleton.elements.terminal.RootVertebra;
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
        List<SkeletonPart> generatedParts = new ArrayList<>();

        RootVertebra root = generateRootVertebra(wholeBody);
        generatedParts.add(root);

        FrontPart frontPart = new FrontPart(root, wholeBody);
        root.addChild(frontPart);
        generatedParts.add(frontPart);

        BackPart backPart = new BackPart(root, wholeBody);
        root.addChild(backPart);
        generatedParts.add(backPart);

        return generatedParts;
    }

    /**
     * Generates root vertebra with zero extend
     */
    private RootVertebra generateRootVertebra(WholeBody ancestor) {
        float spinePosition = 0.5f;
        Point3f center = ancestor.getGenerator().getSkeletonMetaData().getSpine().getBack().apply3d(spinePosition);

        TransformationMatrix transform = new TransformationMatrix(new Vector3f(center));
        return new RootVertebra(transform, ancestor, spinePosition);
    }
}
