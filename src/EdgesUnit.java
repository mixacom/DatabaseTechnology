import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public class EdgesUnit {

    private static double p;
    private static double q;

    public static int IDnum = 0;

    public final ArrayList<Edge> sampledEdges = new ArrayList<Edge>();
    public final ArrayList<WedgeSimple> sampledPOLT = new ArrayList<WedgeSimple>();

    public ArrayList<Edge> adjacentExternal = new ArrayList<Edge>();

    public final ArrayList<TriangleSimple> sampledTriangles = new ArrayList<TriangleSimple>();

    int countTriangles = 0;

    int countWedges = 0;

    private String readName;

    private static String results;

    public EdgesUnit(String readName) {
        this.readName = readName;
    }

    private class Edge implements Comparable<Edge> {
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

        public int compareTo(Edge anotherEdge) {
            if (startingPoint < anotherEdge.startingPoint) return -1;
            else if (startingPoint > anotherEdge.startingPoint) return 1;
            else if (finalPoint < anotherEdge.finalPoint) return -1;
            else if (finalPoint > anotherEdge.finalPoint) return 1;
            else return 0;
        }

        public String toString() {
            return startingPoint + " " + finalPoint;
        }
    }

    private class TriangleSimple {
        private int sideA;
        private int sideB;
        private int sideC;

        public TriangleSimple(int sA, int sB, int sC) {
            sideA = sA;
            sideB = sB;
            sideC = sC;
        }
    }

    private class WedgeSimple {
        private int sideA;
        private int sideB;

        public WedgeSimple(int sA, int sB) {
            sideA = sA;
            sideB = sB;
        }
    }

    public static void main(String[] args) {
        // write your code here

        // path to the file you want to work with
        String fullPath = "C:/Users/Mikhail/Dropbox/2ID35 Data Tech/Data/processed files/Data-Google.txt";

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

            for (int i = 0; i < 5; i++) {

/*                if (i % 2 == 0) p = p + 0.001;
                else q = q + 0.001;*/

                int step = i + 1;

                results = "";

/*
                results = "Information about step " + step + " for " + fileWithExtensionArray[0] + " data set. \r\n\r\n";
*/

                EdgesUnit eun = new EdgesUnit(fullPath);
                eun.pickUpFile();

                bw.write(results);
                bw.flush();

                double variance =  eun.findVariance();
                System.out.println(variance);
            }

            bw.write("\r\n" + "Calculations are finished. \r\n");
            bw.write("Thank you " + yourName + " that you run the calculations and help your team to make a wonderful project.");

            bw.close();

        }
        catch (IOException e) {
            e.printStackTrace(System.out);
        }
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

                if (lineCount > 10000000) {
                    bf.close();
                    break;
                }

                sampleEdges(edgeInFile);
            }
            bf.close();
        }
        catch (Exception e) {
            e.printStackTrace(System.out);
        }
        finally {

        }
    }

    public void showEstimateResults() {

        System.out.println();

        String formattedString = "";

        int estEdges = 0;
        for (Edge e: sampledEdges) {
            estEdges += 1/e.probabilitySample;
        }
        estEdges = Math.round(estEdges);

/*
        results += "Number of sampled edges is " + sampledEdges.size() + ".\r\n";
        results += "Estimated number of edges " + estEdges + ".\r\n";
*/

        formattedString += p + "\t" + q + "\t";

        formattedString += sampledEdges.size() + "\t";
        formattedString += estEdges + "\t";

        countWedges = countPathOfLengthTwo();
/*
        results += "Number of sampled wedges is " + countWedges + ".\r\n";
*/

        formattedString += countWedges + "\t";

        long estimateWedges = 0;
        for (int i = 0; i < sampledPOLT.size(); i++) {
            WedgeSimple wedge = sampledPOLT.get(i);
            Edge a = sampledEdges.get(wedge.sideA);
            Edge b = sampledEdges.get(wedge.sideB);
            estimateWedges += (1/a.probabilitySample) * (1/b.probabilitySample);
        }

/*
        results += "Estimated estimation of wedges is " + estimateWedges + ".\r\n";
*/

        formattedString += estimateWedges + "\t";

/*
        results += "Number of sampled triangles is " + countTriangles + ".\r\n";
*/

        formattedString += countTriangles + "\t";

        int estimateTriangles = 0;
        for (int i = 0; i < sampledTriangles.size(); i++) {
            TriangleSimple triangle = sampledTriangles.get(i);
            Edge a = sampledEdges.get(triangle.sideA);
            Edge b = sampledEdges.get(triangle.sideB);
            Edge c = sampledEdges.get(triangle.sideC);
            estimateTriangles += ((1/a.probabilitySample) * (1/b.probabilitySample) * (1/c.probabilitySample) / 2);
        }

/*
        results += "Estimated number of triangles is " + estimateTriangles + ".\r\n";
*/
        formattedString += estimateTriangles + "\t";

        double clusterCoefficient = 3.0 * estimateTriangles / estimateWedges;
/*
        results += "Estimated global cluster coefficient is " + clusterCoefficient + ". \r\n\r\n\r\n";
*/

        formattedString += clusterCoefficient + "\t";

        results += formattedString + "\r\n";

/*        showSampledEdges(); */
    }

    public void sampleEdges(String fileString) {
        String[] edgeInFile = fileString.split("\t");

        int startingPoint = Integer.parseInt(edgeInFile[0].trim());
        int finalPoint = Integer.parseInt(edgeInFile[1].trim());

        Edge arrivedEdge = findAdjacency(startingPoint, finalPoint);

        if (Math.random() <= arrivedEdge.probabilitySample) {

            for (Edge e: adjacentExternal) {
                sampledPOLT.add(new WedgeSimple(e.ID, arrivedEdge.ID));
            }

            sampledEdges.add(arrivedEdge);
        }
    }

    public Edge findAdjacency(int startingPoint, int finalPoint) {
        ArrayList<Edge> adjacentLocal = new ArrayList<Edge>();

        Edge edgeToBack = new Edge(startingPoint, finalPoint, 0);
        edgeToBack.ID = sampledEdges.size();

        boolean isTriangle = false;

        for (Edge e: sampledEdges) {
            if (startingPoint == e.startingPoint || startingPoint == e.finalPoint || finalPoint == e.startingPoint || finalPoint == e.finalPoint) {
                adjacentLocal.add(e);
            }
        }

        adjacentExternal = adjacentLocal;

        for (Edge e: adjacentLocal) {
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

            for (Edge thirdEdge: adjacentLocal) {
                if ((thirdEdge.startingPoint == freeVertexA && thirdEdge.finalPoint == freeVertexB) ||
                    (thirdEdge.startingPoint == freeVertexB && thirdEdge.finalPoint == freeVertexA)) {

                    edgeToBack.probabilitySample = 1;

                    int[] edgesOfTriangle = new int[3];
                    edgesOfTriangle[0] = edgeToBack.ID;
                    edgesOfTriangle[1] = e.ID;
                    edgesOfTriangle[2] = thirdEdge.ID;

                    Arrays.sort(edgesOfTriangle);
                    sampledTriangles.add(new TriangleSimple(edgesOfTriangle[0], edgesOfTriangle[1], edgesOfTriangle[2]));
                    isTriangle = true;

                    countTriangles++;
                }
            }

        }

        if (!isTriangle){
            if (adjacentLocal.size() > 0) edgeToBack.probabilitySample = q;
            else edgeToBack.probabilitySample = p;
        }

        return edgeToBack;
    }

    public double findVariance() {
        int numberOfCopies = 0;

        double sumVariance = 0;

        for (int i = 0; i < sampledTriangles.size(); i++) {
            TriangleSimple first = sampledTriangles.get(i);

            for (int j = i + 1; j < sampledTriangles.size(); j++) {
                TriangleSimple second = sampledTriangles.get(j);
                if (first.sideA == second.sideA || first.sideB == second.sideB || first.sideA == second.sideB) {
                    int common = 0;
                    double sampleProbability = 0;

                    if (first.sideA == second.sideA)
                        common = first.sideA;
                    if (first.sideB == second.sideB)
                        common = first.sideB;
                    if (first.sideC == second.sideC)
                        common = first.sideC;

                    for (Edge e: sampledEdges) {
                        if (e.ID == common) {
                            sampleProbability = e.probabilitySample;

                            break;

                        }
                    }

                    if (sampleProbability == p)
                        sumVariance +=  1/((p * q) + (p * q) - sampleProbability) * (1/sampleProbability - 1);
                    else if (sampleProbability == q)
                        sumVariance +=  1/((p * q) + (p * q) - sampleProbability) * (1/sampleProbability - 1);
                    else if (sampleProbability == 1)
                        sumVariance +=  1/((p * q) + (p * q) - sampleProbability) * (1/sampleProbability - 1);

                    break;
                }
            }
        }

        return sumVariance;
    }

    public void findRealNumbers(String file) {
        try {

            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

            Edge arrivedEdge = null;

            while (bf.ready()) {
                String fileString = bf.readLine();
                String[] edgeInFile = fileString.split("\t");

                int startingPoint = Integer.parseInt(edgeInFile[0].trim());
                int finalPoint = Integer.parseInt(edgeInFile[1].trim());

                if (startingPoint < finalPoint) {
                    arrivedEdge = new Edge(startingPoint, finalPoint, 1);
                }
                else {
                    arrivedEdge = new Edge(finalPoint, startingPoint, 1);
                }

                sampledEdges.add(arrivedEdge);

             }

            Collections.sort(sampledEdges);

            int idn = 0;
            for (int i = 0; i < sampledEdges.size(); i++) {
                Edge n = sampledEdges.get(i);
                n.ID = idn++;
                sampledEdges.set(i, n);
            }

            int numOfWedges = countPathOfLengthTwo();
            System.out.println(numOfWedges);


            System.out.println(sampledEdges.size());

            bf.close();

        }
        catch (IOException e) {
            e.printStackTrace(System.out);
        }
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

        for (int i = 0; i < sampledEdges.size(); i++) {
            for (int j = i+1; j < sampledEdges.size(); j++) {
                Edge firstEdge = sampledEdges.get(i);
                Edge secondEdge = sampledEdges.get(j);
/*
                if (firstEdge.ID < secondEdge.ID)
*/
                {
                    if (isPathOfLengthTwo(firstEdge, secondEdge)) {
/*
                        ArrayList<Edge> newWedge = new ArrayList<Edge>();
                        newWedge.add(firstEdge);
                        newWedge.add(secondEdge);
                        sampledPOLT.add(newWedge);
*/
                        numOfWedges++;

                        if (numOfWedges % 1000000 == 0)
                            System.out.println(new Date() + ", " + i + ", num of wedges " + numOfWedges + "." );
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
                            if (newEdge.compareTo(e) == 0) {
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
