package controller;

import com.aspose.cells.Workbook;

public class CopyData {


    /**
     * Copies the data from a specified input file into a desired output file ,
     * and at the same time transforms the file so that it is visible in xlsb format instead of xlsx.
     *
     * @param inputFile,  it is the file from which the data is going to be read in and transformed
     * @param outputFile, it is the file to which the data is going to be written into - the user can see the end results there.
     * @throws Exception throws an exception, if a program is not able to read data from the input file, or write data to an output file.
     */
    public static void copyData(String inputFile, String outputFile) throws Exception {

        Workbook asposeWorkbook = new Workbook(inputFile);
        asposeWorkbook.save(outputFile);

    }
}
