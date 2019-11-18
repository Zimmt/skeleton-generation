package skeleton.elements;

public class BackPart extends NonTerminalElement {

    private final String id = "back part";

    public BackPart(SkeletonPart parent) {
        super(parent);
    }

    public String getID() {
        return id;
    }
}
