package skeleton;

import util.Position;

import java.util.ArrayList;
import java.util.List;

public class TestRule implements ReplacementRule {

    private NonTerminalElement owner;

    public TestRule(NonTerminalElement owner) {
        this.owner = owner;
    }

    @Override
    public List<SkeletonPart> apply() {

        List<SkeletonPart> parts = new ArrayList<>();
        SimpleBone bone = new SimpleBone(new Position(0.0, 0.0, 0.0), new Position(1.0, 0.0, 0.0));
        parts.add(bone);
        return parts;
    }
}
