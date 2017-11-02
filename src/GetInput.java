import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.HashMap;

class GetInput {
    private String inpFile, classFile;

    GetInput(String inpFile, String classFile) {
        this.inpFile = inpFile;
        this.classFile = classFile;
    }

    void getClassInfo(HashMap<Integer, String> topicsInfo) {

        Charset charset = Charset.forName("US-ASCII");
        String line = null;
        BufferedReader reader = null;
        Path inputfile = Paths.get(classFile);
        try {
            reader = Files.newBufferedReader(inputfile, charset);
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
        //(i, label)
        while (true) {
            try {
                line = reader.readLine();
            } catch (IOException x) {
                System.err.format("IOException: %s%n", x);
            }
            if (line == null) break;

            String l[] = line.split(",");
            int l0 = Integer.parseInt(l[0]);
            topicsInfo.put(l0, l[1]);
        }
    }

    void getPoints(HashMap<Integer, HashMap<Integer, Double>> points, HashMap<Integer, Double> max) {

        /*
        * format is:
        * article ID (i.e., the NEWID), dimension #, and corresponding value (frequency).
        * */

        Charset charset = Charset.forName("US-ASCII");
        String line = null;
        BufferedReader reader = null;
        Path inputfile = Paths.get(inpFile);
        try {
            reader = Files.newBufferedReader(inputfile, charset);
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

        HashMap<Integer, Double> dim;

        while (true) {
            try {
                line = reader.readLine();
            } catch (IOException x) {
                System.err.format("IOException: %s%n", x);
            }
            if (line == null) break;

            String l[] = line.split(",");
            int l0 = Integer.parseInt(l[0]);
            int l1 = Integer.parseInt(l[1]);
            double l2 = Double.parseDouble(l[2]);
            if (points.containsKey(l0)) {
                points.get(l0).put(l1, l2);
            } else {
                dim = new HashMap<Integer, Double>();
                dim.put(l1, l2);
                points.put(l0, dim);
            }
            if (max.containsKey(l1)) {
                if ((l2 - max.get(l1)) > 0) {
                    max.put(l1, l2);
                }
            } else {
                max.put(l1, l2);
            }
        }
    }

}
