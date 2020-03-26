package skeleton.elements.terminal;

import skeleton.SpinePart;
import skeleton.elements.joints.SpineOrientedJoint;
import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.Optional;

/**
 * Wirbel
 */
public class RootVertebra extends TerminalElement {

    private final String kind = "root vertebra";
    SpineOrientedJoint frontPartJoint;
    SpineOrientedJoint backPartJoint;

    /**
     * root vertebra has zero extend (bounding box is a point)
     */
    public RootVertebra(TransformationMatrix transform, NonTerminalElement ancestor, float spinePosition) {
        super(transform, new BoundingBox(new Vector3f(), new Vector3f(), new Vector3f()), null, ancestor);
        Point3f center = ancestor.getGenerator().getSkeletonMetaData().getSpine().getBack().apply3d(spinePosition);
        this.frontPartJoint = new SpineOrientedJoint(this, center, SpinePart.BACK, spinePosition, ancestor.getGenerator());
        this.backPartJoint = new SpineOrientedJoint(this, center, SpinePart.BACK, spinePosition, ancestor.getGenerator());
    }

    public String getKind() {
        return kind;
    }

    public SpineOrientedJoint getFrontPartJoint() {
        return frontPartJoint;
    }

    public SpineOrientedJoint getBackPartJoint() {
        return backPartJoint;
    }

    public boolean canBeMirrored() { return false; }

    public RootVertebra calculateMirroredElement(TerminalElement parent, Optional<TerminalElement> mirroredParent) {
        System.out.println("Tried to mirror an element that should not be mirrored!");
        return null;
    }
}
