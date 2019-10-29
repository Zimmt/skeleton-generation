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
        SimpleBone bone1 = new SimpleBone(new Position(0f, 0f, 0f), new Position(1f, 0f, 0f));
        SimpleBone bone2 = new SimpleBone(new Position(1f, 0f, 0f), new Position(2f, 0f, 0f));
        Joint joint = new Joint(new Position(1f, 0f, 0f));

        parts.add(bone1);
        parts.add(bone2);
        parts.add(joint);
        return parts;
    }
}
