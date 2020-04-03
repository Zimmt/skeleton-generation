package skeleton.elements.terminal;

import skeleton.SpinePart;
import skeleton.elements.joints.DummyJoint;
import skeleton.elements.joints.SpineOrientedJoint;
import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.Optional;

/**
 * scaling:
 * x: is determined by distance on bezier curve (in skeleton generator:generateVertebraeInInterval)
 * y and z: static member in SpineData
 */
public class Vertebra extends TerminalElement {

    private final String kind = "vertebra";
    SpineOrientedJoint spineJoint;
    DummyJoint ribJoint;
    DummyJoint pelvisJoint;

    public Vertebra(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor,
                    boolean positiveXDir, SpinePart spinePart, float jointSpinePosition) {
        super(transform, boundingBox, parent, ancestor);
        this.spineJoint = new SpineOrientedJoint(this, Vertebra.getSpineJointPosition(boundingBox, positiveXDir), spinePart, jointSpinePosition, parent.getGenerator());
        this.ribJoint = new DummyJoint(this, Vertebra.getRibJointPosition(boundingBox));
        this.pelvisJoint = new DummyJoint(this, Vertebra.getPelvisJointPosition(boundingBox));
    }

    public Vertebra(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor, SpineOrientedJoint spineJoint) {
        super(transform, boundingBox, parent, ancestor);
        this.spineJoint = new SpineOrientedJoint(this, spineJoint.getPosition(), spineJoint.getSpinePart(), spineJoint.getSpinePosition(), parent.getGenerator());
        this.ribJoint = new DummyJoint(this, Vertebra.getRibJointPosition(boundingBox));
        this.pelvisJoint = new DummyJoint(this, Vertebra.getPelvisJointPosition(boundingBox));
    }

    public String getKind() {
        return kind;
    }

    public SpineOrientedJoint getSpineJoint() {
        return spineJoint;
    }

    public DummyJoint getRibJoint() {
        return ribJoint;
    }

    public DummyJoint getPelvisJoint() {
        return pelvisJoint;
    }

    public boolean canBeMirrored() { return false; }

    public Vertebra calculateMirroredElement(TerminalElement parent, Optional<TerminalElement> mirroredParent) {
        System.out.println("Tried to mirror an element that should not be mirrored!");
        return null;
    }

    /**
     * @return the translation to move the joint between this element and its parent from this origin somewhere else.
     */
    public static Vector3f getLocalTranslationFromJoint(BoundingBox boundingBox) {
        return new Vector3f(0f, -boundingBox.getYLength() / 2f, -boundingBox.getZLength() / 2f);
    }

    /**
     * @param positiveXDir if the joint is in positive x direction (needed for vertebrae that are spawned in different directions from root)
     * @return the relative position for the joint between this element and it's child
     */
    public static Point3f getSpineJointPosition(BoundingBox boundingBox, boolean positiveXDir) {
        return new Point3f(positiveXDir ? boundingBox.getXLength() : 0f, boundingBox.getYLength()/2f, boundingBox.getZLength()/2f);
    }

    public static Point3f getRibJointPosition(BoundingBox boundingBox) {
        return new Point3f(boundingBox.getXLength()/2f, 0.6f * boundingBox.getYLength(), 0.1f * boundingBox.getZLength());
    }

    public static Point3f getPelvisJointPosition(BoundingBox boundingBox) {
        return new Point3f(boundingBox.getXLength()/2f, boundingBox.getYLength()/2f, boundingBox.getZLength()/2f);
    }
}
