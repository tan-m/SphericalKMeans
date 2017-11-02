import java.util.HashMap;
import java.util.Vector;

class Cluster {
    HashMap<Integer, Double> dim = new HashMap<Integer, Double>();
    Vector<Integer> members = new Vector<Integer>();
    double purity, entrophy;

    Cluster(HashMap<Integer, Double> dim) {
        this.dim = dim;
    }

    @Override
    public String toString() {
        String s = "" + dim;
        return s;
    }
}