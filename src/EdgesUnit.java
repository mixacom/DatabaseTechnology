import java.io.*;
import java.util.ArrayList;
import java.util.Date;

public class EdgesUnit {

    private static double p;
    private static double q;

    public static int IDnum = 0;

    public final ArrayList<Edge> sampledEdges = new ArrayList<Edge>();
    public final ArrayList<ArrayList<Edge>> sampledPOLT = new ArrayList<ArrayList<Edge>>();

    int countTriangles = 0;

    private String readName;

    private static String results;

    public EdgesUnit(String readName) {
        this.readName = readName;
    }

    private class Edge {
        private int ID;
        private int startingPoint;
        private int finalPoint;
        private double probabilitySample;

        public Edge(int SP, int FP, double PS) {
            ID = IDnum++;
            startingPoint = SP;
            finalPoint = FP;
            probabilitySample = PS;
        }

        public boolean compareTo(Edge anotherEdge) {
            return (startingPoint == anotherEdge.startingPoint && finalPoint == anotherEdge.finalPoint) ||
                   (startingPoint == anotherEdge.finalPoint && finalPoint == anotherEdge.startingPoint);
        }

        public String toString() {
            return "(" + startingPoint + ", " + finalPoint + ")";
        }
    }

    public static void main(String[] args) {
        // write your code here

        // path to the file you want to work with
        String fullPath = "C:/Users/Mikhail/Dropbox/2ID35 Data Tech/Data/processed files/Data-Stanford.txt";

        // type personal name
        String yourName = "Mikhail";

        // point correct values
        p = 0.005;
        q = 0.008;

        String[] nameComponents = fullPath.split("/");
        String path = "";
        String shortPath = "";
        for (int i = 0; i < nameComponents.length - 1; i++) {
            path += nameComponents[i] + "/";
            if (i < nameComponents.length - 2) shortPath += nameComponents[i] + "/";
            else shortPath += "results/";
        }
        String fileWithExtension = nameComponents[nameComponents.length-1];
        String[] fileWithExtensionArray = fileWithExtension.split("\\.");

        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(shortPath + fileWithExtensionArray[0] + " results " + yourName + ".txt")));

            for (int i = 0; i < 30; i++) {
                int step = i + 1;
                results = "Information about step " + step + " for " + fileWithExtensionArray[0] + " data set. \r\n\r\n";
                EdgesUnit eun = new EdgesUnit(fullPath);
                eun.pickUpFile();

                bw.write(results);
            }

            bw.write("Calculations are finished. \r\n");
            bw.write("Thank you " + yourName + " that you run the calculations and help your team to make a wonderful project.");

            bw.close();
        }
        catch (IOException e) { }
    }

    public void pickUpFile() {

        Date start = new Date();
        readFile(readName);
        Date finish = new Date();

        showEstimateResults();

        System.out.println("Calculation time " + (finish.getTime() - start.getTime()) + "ms." );
    }


    public void readFile(String file) {
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

            int lineCount = 0;

            while (bf.ready()) {
                String edgeInFile = bf.readLine();

                if (++lineCount % 500000 == 0) System.out.println(lineCount + " " + new Date());

                if (lineCount > 10000000 ) {
                    bf.close();
                    break;
                }

                sampleEdges(edgeInFile);
            }
            bf.close();
        }
        catch (Exception e) { }
        finally {

        }
    }

    public void showEstimateResults() {

        System.out.println();

        int estEdges = 0;
        for (Edge e: sampledEdges) {
            estEdges += 1/e.probabilitySample;
        }
        estEdges = Math.round(estEdges);

        results += "Number of sampled edges is " + sampledEdges.size() + ".\r\n";
        results += "Estimated number of edges " + estEdges + ".\r\n";

        int countWedges = countPathOfLengthTwo();
        results += "Number of sampled wedges is " + countWedges + ".\r\n";

        long estimateWedges = 0;
        for (int i = 0; i < countWedges; i++) {
            estimateWedges += (1/p) * (1/q);
        }

        results += "Estimated estimation of wedges is " + estimateWedges + ".\r\n";

        results += "Number of sampled triangles is " + countTriangles + ".\r\n";

        int estimateTriangles = 0;
        for (int i = 0; i < countTriangles; i++) {
            estimateTriangles += (1/p) * (1/q) * 1;
        }

        results += "Estimated number of triangles is " + estimateTriangles + ".\r\n";

        double clusterCoefficient = 3.0 * estimateTriangles / estimateWedges;
        results += "Estimated global cluster coefficient is " + clusterCoefficient + ". \r\n\r\n\r\n";

/*        showSampledEdges(); */
    }

    public void sampleEdges(String fileString) {
        String[] edgeInFile = fileString.split("\t");

        int startingPoint = Integer.parseInt(edgeInFile[0].trim());
        int finalPoint = Integer.parseInt(edgeInFile[1].trim());

        double probabilityOfSampling = findAdjacency(startingPoint, finalPoint);

        if (Math.random() <= probabilityOfSampling) {
            sampledEdges.add(new Edge(startingPoint, finalPoint, probabilityOfSampling));
        }
    }

    public double findAdjacency(int startingPoint, int finalPoint) {
        ArrayList<Edge> adjacent = new ArrayList<Edge>();

        for (Edge e: sampledEdges) {
            if (startingPoint == e.startingPoint || startingPoint == e.finalPoint || finalPoint == e.startingPoint || finalPoint == e.finalPoint) {
                adjacent.add(e);
            }
        }

        for (Edge e: adjacent) {
            int matchVertex = 0;
            int freeVertexA = 0;
            int freeVertexB = 0;

            if (startingPoint == e.startingPoint) {
                matchVertex = startingPoint;
                freeVertexA = finalPoint;
                freeVertexB = e.finalPoint;
            }

            if (startingPoint == e.finalPoint) {
                matchVertex = startingPoint;
                freeVertexA = finalPoint;
                freeVertexB = e.startingPoint;
            }

            if (finalPoint == e.startingPoint) {
                matchVertex = finalPoint;
                freeVertexA = startingPoint;
                freeVertexB = e.finalPoint;
            }

            if (finalPoint == e.finalPoint) {
                matchVertex = finalPoint;
                freeVertexA = startingPoint;
                freeVertexB = e.startingPoint;
            }

            for (Edge thirdEdge: adjacent) {
                if ((thirdEdge.startingPoint == freeVertexA && thirdEdge.finalPoint == freeVertexB) ||
                    (thirdEdge.startingPoint == freeVertexB && thirdEdge.finalPoint == freeVertexA)) {
                    countTriangles++;
                    return 1;
                }
            }

        }

        if (adjacent.size() > 0) return q;
        else return p;
    }

    public void showSampledEdges() {
        for (Edge e: sampledEdges) {
            System.out.println(e.toString());
        }
    }

/*    public void countTriangles() {
        int haveTriangles = 0;

        for (Edge e: sampledEdges) {
            int firstEdgeStart = e.startingPoint;
            int firstEdgeFinal = e.finalPoint;

            for (int vertex: sampledNodes) {
                Edge sEdge = new Edge(vertex, firstEdgeStart, 0);
                Edge fEdge = new Edge(vertex, firstEdgeFinal, 0);

                boolean hasSecondEdge = false;
                boolean hasThirdEdge = false;

                for (Edge e2: sampledEdges) {
                    if (e2.compareTo(sEdge)) hasSecondEdge = true;
                    if (e2.compareTo(fEdge)) hasThirdEdge = true;
                }

                if (hasSecondEdge && hasThirdEdge)
                    haveTriangles++;
            }
        }

        haveTriangles /= 3;
        System.out.println("Estimated number of triangles " + haveTriangles + ".");
    } */

    public int countPathOfLengthTwo() {
        int numOfWedges = 0;

        for (Edge firstEdge: sampledEdges) {
            for (Edge secondEdge: sampledEdges) {
                if (firstEdge.ID < secondEdge.ID) {
                    if (isPathOfLengthTwo(firstEdge, secondEdge)) {
/*
                        ArrayList<Edge> newWedge = new ArrayList<Edge>();
                        newWedge.add(firstEdge);
                        newWedge.add(secondEdge);
                        sampledPOLT.add(newWedge);
*/
                        numOfWedges++;
                    }
                }
            }
        }

        return numOfWedges;
    }

    public int calculateDuplicate(String file) {
        int countDuplicates = 0;

        ArrayList<Edge> allEdges = new ArrayList<Edge>();
        int seenEdges = 0;

        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            while(bf.ready()) {
                String[] newEdgeLine = bf.readLine().split("\t");

                seenEdges++;
                if (seenEdges % 50000 == 0) {
                    System.out.println(seenEdges + " edges, time is " + new Date());
                    break;
                }

                if (newEdgeLine.length > 1) {
                    Edge newEdge = new Edge(Integer.parseInt(newEdgeLine[0]), Integer.parseInt(newEdgeLine[1]), 0);
                    boolean needAdd = true;

                    if (allEdges.size() != 0) {
                        for (Edge e : allEdges) {
                            if (newEdge.compareTo(e)) {
                                countDuplicates++;
                                needAdd = false;
                            }
                        }
                        if (needAdd) allEdges.add(newEdge);
                    }
                    else {
                        allEdges.add(newEdge);
                    }
                }
            }

            bf.close();
        }
        catch (Exception e) {

        }

        System.out.println(countDuplicates);
        System.out.println(allEdges.size());

        return countDuplicates;
    }

    public boolean isPathOfLengthTwo(Edge edgeFirst, Edge edgeSecond) {
        if (edgeFirst.equals(edgeSecond)) return false; // check for the node loop

        if (edgeFirst.startingPoint == edgeSecond.startingPoint || edgeFirst.startingPoint == edgeSecond.finalPoint) return true;
        else if (edgeFirst.finalPoint == edgeSecond.startingPoint || edgeFirst.finalPoint == edgeSecond.finalPoint) return true;
        else return false;
    }
}
