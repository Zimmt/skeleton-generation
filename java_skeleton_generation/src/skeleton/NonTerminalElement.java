package skeleton;

public interface NonTerminalElement extends SkeletonPart {

    default boolean isTerminal() {
        return false;
    }
}
