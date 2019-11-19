package skeleton.elements.nonterminal;

public class Torso extends NonTerminalElement {

    private final String id = "torso";

    public Torso() {
        super(null);
    }

    public String getID() {
        return id;
    }

    public boolean isMirrored() { return false; }
}
