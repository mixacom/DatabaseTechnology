package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Mikhail on 24.05.2015.
 */
public class Preprocess {


    public final ArrayList<Edge> uniqueEdges = new ArrayList<Edge>();
    public final ArrayList<Edge> duplicatesEdges = new ArrayList<Edge>();

    public final ArrayList<Integer> uniqueVertices = new ArrayList<Integer>();

    public static int IDnum = 0;

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
            return startingPoint + "\t" + finalPoint;
        }
    }

    public static void main(String[] args) {
        Preprocess preprocessInstance = new Preprocess();
        preprocessInstance.readFile("C:\\Users\\Mikhail\\Dropbox\\2ID35 Data Tech\\Data\\Data-BerkStan.txt");
        preprocessInstance.saveData("C:\\Users\\Mikhail\\Dropbox\\2ID35 Data Tech\\Data-BerkStan.txt");

        System.out.println("Number of unique edges is " + preprocessInstance.uniqueEdges.size());
        System.out.println("Number of duplicate edges is " + preprocessInstance.duplicatesEdges.size());
        System.out.println("Number of unique vertices is " + preprocessInstance.uniqueVertices.size());
    }

    public void readFile(String fileName) {
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));

            int lineCount = 0;

            while (bf.ready()) {
                String edgeInFile = bf.readLine();

                if (++lineCount % 50000 == 0) System.out.println(lineCount + " " + new Date());

                if (lineCount > 100) {
                    bf.close();
                    break;
                }

                duplicateAnalysis(edgeInFile);
            }
            bf.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {

        }
    }

    public void duplicateAnalysis(String edgeString) {
        String[] edge = edgeString.split("\t");

        int startingPoint = Integer.parseInt(edge[0]);
        int finalPoint = Integer.parseInt(edge[1]);
        Edge newEdge = new Edge(startingPoint, finalPoint, 0);

        if (uniqueEdges.size() > 0) {
            boolean isUnique = true;
            for (Edge arrayEdge: uniqueEdges) {
                if (newEdge.compareTo(arrayEdge)) {
                    duplicatesEdges.add(newEdge);
                    isUnique = false;
                    break;
                }
            }

            if (isUnique) {
                uniqueEdges.add(newEdge);
                if (!uniqueVertices.contains(newEdge.startingPoint)) uniqueVertices.add(newEdge.startingPoint);
                if (!uniqueVertices.contains(newEdge.finalPoint)) uniqueVertices.add(newEdge.finalPoint);
            }
        }
        else {
            uniqueEdges.add(newEdge);
            uniqueVertices.add(startingPoint);
            uniqueVertices.add(finalPoint);
        }

    }

    public void saveData(String fileName) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)));

            for (Edge e: uniqueEdges) {
                bw.write(e.toString() + "\r\n");
            }

            bw.close();
        }
        catch (Exception E) { }
        finally {

        }
    }
}
