/*
	Run as:
	java sphkmeans bag.csv bag.clabel 10 10 output.txt
 */

public class SphKmeans {

    /**
     * @param args args[0]: input-file (bag.csv)
     *             args[1]: class-file (bag.clabel)
     *             args[2]: No. of clusters
     *             args[3]: No. of trials
     *             args[4]: output-file
     */
    public static void main(String[] args) {

        if (args.length != 5) {
            System.err.println("Incorrect number of arguments. \nFormat is: " +
                    "input-file class-file #clusters #trials output-file");
        }

        Clustering c = new Clustering(args);
        System.out.println("Done!");

    }
}