package skeleton.elements;

import util.BoundingBox;

import javax.media.j3d.Transform3D;

public class SimpleBone extends TerminalElement {
    private Transform3D relativePosition;
    private BoundingBox boundingBox;
    private String id;

    public SimpleBone(SkeletonPart parent, Transform3D relativePosition, BoundingBox boundingBox, String id) {
        super(parent);
        this.relativePosition = relativePosition;
        this.boundingBox = boundingBox;
        this.id = id;
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
}
