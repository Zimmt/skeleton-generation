package skeleton;

import util.BoundingBox;

import javax.media.j3d.Transform3D;

public interface SkeletonPart {

    BoundingBox getBoundingBox();
    Transform3D getTransform();
    String getID();
    double getWeight();
    SkeletonPart getParent();
    boolean hasParent();
}
