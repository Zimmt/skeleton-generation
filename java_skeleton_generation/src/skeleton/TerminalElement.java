package skeleton;

public interface TerminalElement extends SkeletonPart {

    default boolean isTerminal() {
        return true;
    }
}
