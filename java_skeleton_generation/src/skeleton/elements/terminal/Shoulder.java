package skeleton.elements.terminal;

import skeleton.elements.ExtremityKind;
import skeleton.elements.joints.arm.ShoulderJoint;
import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Shoulder extends TerminalElement {

    private final String kind = "shoulder";
    private ExtremityKind[] extremityKinds;
    private List<ShoulderJoint> joints;
    private boolean secondShoulder;

    public Shoulder(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor,
                    boolean mirrored, ExtremityKind[] extremityKinds, boolean secondShoulder) {
        super(transform, boundingBox, parent, ancestor);
        this.extremityKinds = extremityKinds;
        this.joints = new ArrayList<>(extremityKinds.length);
        for (ExtremityKind extremityKind : extremityKinds) {
            if (extremityKind != null) {
                joints.add(ShoulderJoint.newSpecificShoulderJoint(this, Shoulder.getJointPosition(boundingBox, mirrored), extremityKind, secondShoulder));
            }
        }
        this.secondShoulder = secondShoulder;
    }

    public String getKind() {
        return kind;
    }

    public List<ShoulderJoint> getJoints() {
        return joints;
    }

    public boolean isMirrored() { return true; }

    public Shoulder calculateMirroredElement(TerminalElement parent, Optional<TerminalElement> mirroredParent) {
        if (parent.isMirrored() && mirroredParent.isEmpty()) {
            System.err.println("Cannot mirror child when mirrored parent is not given!");
        }
        return new Shoulder(
                calculateMirroredTransform(parent),
                this.getBoundingBox().cloneBox(), // coordinate system is reflected so box must not be reflected!
                mirroredParent.orElse(parent), this.getAncestor(),
                true, extremityKinds, secondShoulder);
    }

    /**
     * @return the translation to move the joint between this element and its parent from this origin somewhere else.
     */
    public static Vector3f getLocalTranslationFromJoint(BoundingBox boundingBox) {
        return new Vector3f(-boundingBox.getXLength()/2f, -boundingBox.getYLength()/2f, -boundingBox.getZLength());
    }

    /**
     * @return the relative position for the joint between this element and it's child
     */
    private static Point3f getJointPosition(BoundingBox boundingBox, boolean mirrored) {
        return new Point3f(boundingBox.getXLength()/2f, 0f, boundingBox.getZLength()/2f);
    }
}
