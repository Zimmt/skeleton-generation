package skeleton;

import util.BoundingBox;

import javax.media.j3d.Transform3D;

public class SimpleBone implements TerminalElement {
    private Transform3D relativePosition;
    private BoundingBox boundingBox;
    private String id;
    private double weight;
    private SkeletonPart parent;

    public SimpleBone(Transform3D relativePosition, BoundingBox boundingBox, String id, double weight, SkeletonPart parent) {
        this.relativePosition = relativePosition;
        this.boundingBox = boundingBox;
        this.id = id;
        this.weight = weight;
        this.parent = parent;
    }

    public Transform3D getTransform() {
        return relativePosition;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public String getID() {
        return id;
    }

    public double getWeight() {
        return weight;
    }

    public SkeletonPart getParent() {
        return parent;
    }

    public boolean hasParent() {
        return parent != null;
    }
}
