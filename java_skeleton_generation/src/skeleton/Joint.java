package skeleton;

import util.Position;

public class Joint implements TerminalElement, SkeletonPart {
    private Position position;
    // here constraints can be added


    public Joint(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }
}
