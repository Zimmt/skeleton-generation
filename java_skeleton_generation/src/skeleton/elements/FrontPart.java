package skeleton.elements;

public class FrontPart extends NonTerminalElement {

    private final String id = "front part";

    public FrontPart(SkeletonPart parent) {
        super(parent);
    }

    public String getID() {
        return id;
    }
}
