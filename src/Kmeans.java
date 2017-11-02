import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

class Kmeans {
    private HashMap<Integer, Article> allArticles;
    private int noOfClusters;
    private HashMap<Integer, Double> max;
    private HashMap<Integer, HashMap<Integer, Double>> points;
    private HashMap<Integer, String> topicsInfo;
    private Cluster[] allClusters;
    private double clusteringPurity = 0;
    private double pointsSize;
    private double clusteringEntrophy = 0;
    private double best_sse = 0;
    private int numOfPointsMoved;

    Kmeans(HashMap<Integer, HashMap<Integer, Double>> points, HashMap<Integer, String> topicsInfo, HashMap<Integer, Double> max, int noOfClusters, int trialNo) {
        this.points = points;
        this.noOfClusters = noOfClusters;
        this.max = max;
        this.topicsInfo = topicsInfo;

        allClusters = new Cluster[noOfClusters];
        allArticles = new HashMap<Integer, Article>();
        pointsSize = points.size();

        initializeArticles();
        computeInitialCentroids(trialNo);

        reassignClusters();
        computeCentroids();

        // until few points move
        while (numOfPointsMoved > 2) {
            reassignClusters();
            computeCentroids();
        }
        computePurity();
        clusteringPurity = (clusteringPurity / pointsSize);
    }

    Results getResults() {
        Results r = new Results();
        r.sse = best_sse;
        r.entropy = clusteringEntrophy;
        r.purity = clusteringPurity;
        r.allClusters = allClusters;
        return r;
    }

    void computePurity() {

        for (int i = 0; i < allClusters.length; i++) {

            HashMap<String, Integer> topicCountsforArticles = new HashMap<String, Integer>();
            int max = -1;
            // for all members of ONE cluster
            for (Integer m : allClusters[i].members) {
                String t = topicsInfo.get(m);
                if (topicCountsforArticles.containsKey(t)) {
                    int temp = topicCountsforArticles.get(t) + 1;
                    topicCountsforArticles.put(t, temp);

                } else {
                    topicCountsforArticles.put(t, 1);
                }
            }
            for (String key : topicCountsforArticles.keySet()) {
                int count = topicCountsforArticles.get(key);
                if (max < count) {
                    max = count;
                }
            }

            double mj = allClusters[i].members.size();
            double ej = 0;
            for (String key : topicCountsforArticles.keySet()) {
                int count = topicCountsforArticles.get(key);
                double pij = (count / mj);
                double temp = -(pij * (Math.log(pij) / Math.log(2)));
                ej += temp;
            }

            allClusters[i].entrophy = ej;
            clusteringEntrophy += (mj / pointsSize) * ej;
            clusteringPurity += max;
            allClusters[i].purity = max / pointsSize;

        }
    }

    void initializeArticles() {

        for (Integer newid : points.keySet()) {
            Article a = new Article();
            a.simToCluster = 0;
            allArticles.put(newid, a);
        }

    }

    void computeInitialCentroids(int trialNo) {

        int seeds[] = {1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 31, 33, 35, 37, 39};
        Random rand = new Random(seeds[trialNo]);
        for (int i = 0; i < noOfClusters; i++) {
            HashMap<Integer, Double> dim = new HashMap<Integer, Double>();
            for (Integer key : max.keySet()) {
                double upperbound = max.get(key);
                double temp = rand.nextDouble() * upperbound;
                dim.put(key, temp);
            }
            Utility helper = new Utility();
            helper.normalize(dim);
            Cluster c = new Cluster(dim);
            allClusters[i] = c;
        }
    }

    void reassignClusters() {

        Utility helper = new Utility();

        for (int i = 0; i < allClusters.length; i++) {
            allClusters[i].members = new Vector<Integer>();
        }
        double sse = 0;
        numOfPointsMoved = 0;

        //for each point
        for (Integer key : points.keySet()) {
            double maxSim = 0.0;
            Article a = allArticles.get(key);

            // for each cluster
            for (int i = 0; i < allClusters.length; i++) {
                Cluster c = allClusters[i];

                double cosSim = helper.getConsineSimilarity(points.get(key), c.dim);
                if (a.simToCluster < cosSim) {
                    numOfPointsMoved++;
                    a.simToCluster = cosSim;
                    a.clusterIndex = i;
                }
            }
            sse += a.simToCluster;
            allClusters[a.clusterIndex].members.add(key);
        }
        if (best_sse < sse)
            best_sse = sse;
    }

    void computeCentroids() {

        for (int i = 0; i < allClusters.length; i++) {
            Cluster c = allClusters[i];

            // temporary variable
            HashMap<Integer, Double> centroid = new HashMap<Integer, Double>();

            for (Integer id : c.members) {

                // for each element in the cluster's list
                HashMap<Integer, Double> eachElement = points.get(id);

                // this for each dimension
                for (Integer d : eachElement.keySet()) {

                    // running total
                    if (centroid.containsKey(d)) {
                        double temp = centroid.get(d) + eachElement.get(d);
                        centroid.put(d, temp);
                    } else {
                        centroid.put(d, eachElement.get(d));
                    }
                }
            }

            // dividing running total by size
            int noOfMembers = c.members.size();

            // for each dimension
            for (Integer d : centroid.keySet()) {
                double temp = centroid.get(d) / noOfMembers;
                centroid.put(d, temp);
            }

            c.dim = centroid;
        }
    }

}