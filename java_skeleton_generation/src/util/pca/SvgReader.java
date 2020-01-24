package util.pca;

import javax.vecmath.Point2d;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SvgReader {

    /**
     * Searches the file for all path sections and reads their labels and coordinates
     * the results are stored in ParsedSvgPath objects
     * @param inputPath path to the svg file
     */
    public List<ParsedSvgPath> parseFile(String inputPath) throws IOException {
        File file = new File(inputPath);
        BufferedReader fileReader = new BufferedReader(new FileReader(file));
        List<ParsedSvgPath> result = new ArrayList<>();

        String line;
        while ((line = fileReader.readLine()) != null) {

            for (int i = 0; i < 5 && i < line.length(); i++) { // at position 4 the tag should be

                if (line.length() < i+5) {
                    break;
                }
                if (line.substring(i, i + 5).equals("<path")) {
                    result.add(parsePath(fileReader));
                }
            }
        }
        return result;
    }

    /**
     * Searches inside the path section for the label and the point coordinates (tag: d)
     */
    private ParsedSvgPath parsePath(BufferedReader fileReader) throws IOException {
        boolean foundD = false;
        boolean foundLabel = false;
        String label = "";
        String d = "";

        String line;
        while(!foundLabel && (line = fileReader.readLine()) != null) {

            for (int i = 0; i < 8 && i < line.length(); i++) { // at position 7 the tags should be
                if (Character.isWhitespace(line.charAt(i))) {
                    continue;
                }
                if (!foundD && line.charAt(i) == 'd') {
                    d = line.substring(i+3, line.length()-1);
                    foundD = true;
                    if (Character.isLowerCase(line.charAt(i+3))) {
                        System.err.println("Found relative coordinate!");
                    }
                    break;
                } else if (foundD && line.length() > i+14 && line.substring(i, i + 14).equals("inkscape:label")) {
                    String[] labelParts = line.split("\"");
                    if (labelParts.length < 2) {
                        System.err.println("No valid label found.");
                    }
                    label = labelParts[1];
                    foundLabel = true;
                    break;
                }
            }
        }

        if (!label.isEmpty() && !d.isEmpty()) {
            return parseD(label, d);
        } else {
            System.err.println("Couldn't parse path!");
            return null;
        }
    }

    /**
     * Parses the points inside the d tag.
     * @param label the name of the path
     * @param d the string that contains the control points of the bezier curve
     * @return a parsed svg path with the coordinates in a system with a y axis that points up
     */
    private ParsedSvgPath parseD(String label, String d) {
        StringBuilder stringBuilder = new StringBuilder();
        double firstCoordinate = 0f;
        boolean firstCoordinateFound = false;
        boolean verticalOffset = false;
        List<Point2d> points = new ArrayList<>();

        for (int i = 0; i < d.length(); i++) {

            if (!(d.charAt(i) == '-' || Character.isDigit(d.charAt(i)) || d.charAt(i) == '.')) { // no part of number

                if (stringBuilder.length() > 0) { // end of number found
                    if (!firstCoordinateFound) {
                        firstCoordinate = Double.parseDouble(stringBuilder.toString());
                        stringBuilder.setLength(0);
                        firstCoordinateFound = true;
                        if (verticalOffset) {
                            points.add(getPointWithOffset(points.get(points.size()-1), firstCoordinate));
                            firstCoordinateFound = false;
                            verticalOffset = false;
                        }
                    } else {
                        double secondCoordinate = Double.parseDouble(stringBuilder.toString());
                        stringBuilder.setLength(0);
                        points.add(getPoint(firstCoordinate, secondCoordinate));
                        firstCoordinateFound = false;
                    }

                } else if (d.charAt(i) == 'V') {
                    verticalOffset = true;
                }

            } else {
                stringBuilder.append(d.charAt(i));
            }
        }

        if (stringBuilder.length() > 0) {
            if (firstCoordinateFound) {
                double secondCoordinate = Double.parseDouble(stringBuilder.toString());
                points.add(getPoint(firstCoordinate, secondCoordinate));
            } else if (verticalOffset) {
                points.add(getPointWithOffset(points.get(points.size()-1), firstCoordinate));
            } else {
                System.err.println("Point could not be parsed completely.");
            }
        }

        return new ParsedSvgPath(label, points);
    }

    private Point2d getPoint(double firstCoordinate, double secondCoordinate) {
        double fixedSecondCoordinate = 1000 - secondCoordinate; // the y-coordinate in svg files is switched (points from top to bottom)
        return new Point2d(firstCoordinate, fixedSecondCoordinate);
    }

    private Point2d getPointWithOffset(Point2d previousPoint, double verticalOffset) {
        double firstCoordinate = previousPoint.x;
        double secondCoordinate = previousPoint.y + verticalOffset;
        return getPoint(firstCoordinate, secondCoordinate);
    }
}
