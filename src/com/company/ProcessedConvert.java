package com.company;

import java.io.*;
import java.util.Date;

/**
 * Created by Mikhail on 25.05.2015.
 */
public class ProcessedConvert {
    public static void main(String[] args) {
        try {
            String fileInput = "C:\\Users\\Mikhail\\Dropbox\\2ID35 Data Tech\\Data\\processed files\\Data-BerkStan.txt";

            String fileOutput = "C:\\Users\\Mikhail\\Dropbox\\2ID35 Data Tech\\Data\\processed files\\Data-new.txt";

            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(fileInput)));

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOutput)));

            int lineCount = 0;

            while (bf.ready()) {
                String[] edgeInFile = bf.readLine().split(",");

                if (++lineCount % 500000 == 0) System.out.println(lineCount + " " + new Date());

                if (lineCount > 10000000) {
                    bf.close();
                    break;
                }

                String startingPoint = edgeInFile[0].substring(1);
                String finalPoint = edgeInFile[1].substring(0, edgeInFile[1].length() - 1);

                String newEdge = startingPoint + "\t" + finalPoint + "\r\n";

                bw.write(newEdge);
            }
            bf.close();
            bw.close();
        }
        catch (Exception e) { }
        finally {

        }
    }
}
