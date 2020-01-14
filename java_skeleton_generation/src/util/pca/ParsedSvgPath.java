package util.pca;

import javax.vecmath.Point2d;
import java.util.List;

public class ParsedSvgPath {

    private String label;
    private List<Point2d> points;

    public ParsedSvgPath(String label, List<Point2d> points) {
        this.label = label;
        this.points = points;
    }

    public String getLabel() {
        return label;
    }

    public List<Point2d> getPoints() {
        return points;
    }
}
