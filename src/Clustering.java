import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

class Clustering {

    private int noOfClusters, noOfTrials;
    private String outFile;
    private HashMap<Integer, Double> max = new HashMap<Integer, Double>();
    private HashMap<Integer, HashMap<Integer, Double>> points = new HashMap<Integer, HashMap<Integer, Double>>();
    private HashMap<Integer, String> topicsInfo = new HashMap<Integer, String>();

    // { newid : { x1:val, x2:val, } }

    Clustering(String args[]) {
        //input-file class-file #clusters #trials output-file

        String inpFile, classFile;
        inpFile = args[0];
        classFile = args[1];
        noOfClusters = Integer.parseInt(args[2]);
        noOfTrials = Integer.parseInt(args[3]);
        outFile = args[4];

        GetInput getInp = new GetInput(inpFile, classFile);
        getInp.getClassInfo(topicsInfo);
        getInp.getPoints(points, max);
        Utility helper = new Utility();
        for (Integer key : points.keySet()) {
            helper.normalize(points.get(key));
        }
        start();
    }


    void start() {

        Results best, curr;
        best = new Results();
        best.purity = 0;

        for (int i = 0; i < noOfTrials; i++) {
            Kmeans kmn = new Kmeans(points, topicsInfo, max, noOfClusters, i);
            curr = kmn.getResults();
            if ((curr.purity - best.purity) > 0.001) {
                best = curr;
            }
        }
        outputResults(best);
    }

    void outputResults(Results best) {

        String op = "";
        for (int i = 0; i < best.allClusters.length; i++) {
            Cluster c = best.allClusters[i];
            for (Integer m : c.members) {
                op += m + "," + i + "\n";
            }
        }
        System.out.println("value of the objective function = " + best.sse + " the entropy = " + best.entropy + " and purity = " + best.purity);
        writeToFile(op);
    }

    void writeToFile(String output) {
        Writer writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)));
            writer.write(output);
        } catch (IOException exp) {
            System.out.println(exp);
        } finally {
            try {
                writer.close();
            } catch (Exception exp) {
                System.out.println(exp);
            }
        }
    }

}