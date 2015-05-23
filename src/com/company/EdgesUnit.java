package com.company;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

public class EdgesUnit {

    public final static double p = 0.005;
    public final static double q = 0.005;

    public static int IDnum = 0;

    public final ArrayList<Edge> sampledEdges = new ArrayList<Edge>();
    public final ArrayList<Integer> sampledNodes = new ArrayList<Integer>();
    public final ArrayList<ArrayList<Edge>> sampledPOLT = new ArrayList<ArrayList<Edge>>();

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

        EdgesUnit eun = new EdgesUnit();

        Date start = new Date();
        eun.readFile("C:\\Users\\Mikhail\\Dropbox\\2ID35 Data Tech\\Data\\Data-BerkStan.txt");
        Date finish = new Date();

        eun.showEstimateResults(); 

        System.out.println("Calculation time " + (finish.getTime() - start.getTime()) + "ms." );
    }

    public void readFile(String file) {
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

            int lineCount = 0;

            while (bf.ready()) {
                String edgeInFile = bf.readLine();

                if (++lineCount < 0) continue;

                if (lineCount > 2500) {
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
        int estNodes = (int) ((1/((p+q)/2)) * sampledNodes.size());


        double estEdges = 0;
        for (Edge e: sampledEdges) {
            estEdges += 1.0 / e.probabilitySample;
        }
        estEdges = Math.round(estEdges);

        System.out.println("Number of edges " + sampledEdges.size() + ", number of nodes " + sampledNodes.size() + ".");
        System.out.println("Estimated number of edges " + estEdges + ", number of nodes " + estNodes + ".");

        int numOfWedges = countPathOfLengthTwo();
        System.out.println("Estimated number of wedges " + numOfWedges + ".");

        showSampledEdges();
    }

    public void sampleEdges(String fileString) {
        String[] edgeInFile = fileString.split("\t");

        int startingPoint = Integer.parseInt(edgeInFile[0].trim());
        int finalPoint = Integer.parseInt(edgeInFile[1].trim());

        double probabilityOfSampling = findAdjacency(startingPoint, finalPoint);

        sampledEdges.add(new Edge(startingPoint, finalPoint, probabilityOfSampling));
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

            for (Edge thirdEdge: sampledEdges) {
                if ((thirdEdge.startingPoint == freeVertexA && thirdEdge.finalPoint == freeVertexB) ||
                    (thirdEdge.startingPoint == freeVertexB && thirdEdge.finalPoint == freeVertexA)) {
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

    public void countTriangles() {
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
    }

    public int countPathOfLengthTwo() {
        int numOfWedges = 0;

        for (Edge firstEdge: sampledEdges) {
            for (Edge secondEdge: sampledEdges) {
                if (!firstEdge.equals(secondEdge)) {
                    if (isPathOfLengthTwo(firstEdge, secondEdge)) {
                        ArrayList<Edge> newWedge = new ArrayList<Edge>();
                        newWedge.add(firstEdge);
                        newWedge.add(secondEdge);
                        sampledPOLT.add(newWedge);
                        numOfWedges++;
                    }
                }
            }
        }

        return numOfWedges / 2;
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
