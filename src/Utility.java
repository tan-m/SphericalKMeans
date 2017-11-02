import java.util.HashMap;

public class Utility {

    void normalize(HashMap<Integer, Double> dim) {

        double total = 0.0, sqrt;
        for (Integer key : dim.keySet()) {
            total += (dim.get(key) * dim.get(key));
        }
        sqrt = Math.sqrt(total);
        for (Integer key : dim.keySet()) {
            double temp = dim.get(key) / sqrt;
            dim.put(key, temp);
        }
    }

    double getConsineSimilarity(HashMap<Integer, Double> a, HashMap<Integer, Double> b) {

        double cosineSim = 0;
        HashMap<Integer, Double> s, l;
        if (a.keySet().size() < b.keySet().size()) {
            s = a;
            l = b;
        } else {
            s = b;
            l = a;
        }

        for (Integer key : s.keySet()) {
            if (l.containsKey(key)) {
                cosineSim += (s.get(key) * l.get(key));
            }
        }

        return cosineSim;
    }
}