package skeleton;

import util.BoundingBox;

import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3f;

public class InitialElement implements NonTerminalElement {

    private Transform3D relativePosition;
    private BoundingBox boundingBox;
    private String id;
    private double weight;

    public InitialElement() {
        this.relativePosition = new Transform3D(); // identity
        this.boundingBox = new BoundingBox(new Vector3f(), new Vector3f(2, 0, 0), new Vector3f(0, 1, 0), new Vector3f(0,0,1));
        this.id = "initialElement";
        this.weight = 10;
    }

    /* for testing */
    public SimpleBone toSimpleBone() {
        return new SimpleBone(relativePosition, boundingBox, id, weight, null);
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public Transform3D getTransform() {
        return relativePosition;
    }

    public String getID() {
        return id;
    }

    public double getWeight() {
        return weight;
    }

    public SkeletonPart getParent() {
        return null;
    }

    public boolean hasParent() {
        return false;
    }
}
