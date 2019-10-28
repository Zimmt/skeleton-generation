package skeleton;

import util.Position;

import java.util.List;

public class SimpleBone implements TerminalElement, SkeletonPart {
    private Position start;
    private Position end;
    private List<Joint> joints;

    public SimpleBone(Position start, Position end) {
        this.start = start;
        this.end = end;
    }

    public void addJoints(List<Joint> addJoints) {
        this.joints.addAll(addJoints);
    }

    public Position getStart() {
        return start;
    }

    public Position getEnd() {
        return end;
    }

    public List<Joint> getJoints() {
        return joints;
    }
}
