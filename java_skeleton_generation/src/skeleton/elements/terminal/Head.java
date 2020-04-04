package skeleton.elements.terminal;

import skeleton.SpineData;
import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.Optional;

/**
 * scaling: free
 * heads in different distortions shall be possible
 */
public class Head extends TerminalElement {

    private final String kind = "head";

    public Head(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor) {
        super(transform, boundingBox, parent, ancestor);
    }

    public String getKind() {
        return getGenerator().getSkeletonMetaData().getHeadKind();
    }

    public boolean canBeMirrored() { return false; }

    public Head calculateMirroredElement(TerminalElement parent, Optional<TerminalElement> mirroredParent) {
        System.out.println("Tried to mirror an element that should not be mirrored!");
        return null;
    }

    public static Point3f getGlobalHeadPosition(SpineData spine, BoundingBox boundingBox) {
        Point3f headPosition = spine.getNeck().apply3d(0f);
        headPosition.add(Head.getLocalTranslationFromJoint(boundingBox));
        return headPosition;
    }

    /**
     * @return the translation to move the joint between this element and its parent from this origin somewhere else.
     */
    public static Vector3f getLocalTranslationFromJoint(BoundingBox boundingBox) {
        return new Vector3f(-boundingBox.getXLength(), -boundingBox.getYLength() / 2f, -boundingBox.getZLength() / 2f);
    }
}
