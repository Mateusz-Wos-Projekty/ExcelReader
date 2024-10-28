package controller;

import config.Replacement;
import org.apache.poi.ss.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static controller.FilterTheData.sheetOne;
import static controller.FilterTheData.workbook;

public class FilterAndReplaceTheData {

    /**
     * The columnNumber which stores the ColumnName, which is used throughout the whole class.
     */
    private static Integer columnNumber;

    /**
     * Filters and replaces the data, taking into account a specific file.
     * The function must accept the input parameters from the config file, so that it can
     * perform the filtering and replacement of the data based on the specific criteria.
     *
     * @param outputFile   it is the output file to which the data is going to be written into
     * @param newInputList contains the relevant filter inputs, which are then used to transform the data in the input file
     * @throws IOException an exception thrown whenever an input or output operation is failed or interpreted.
     */
    public static void filterAndReplaceTheData(String outputFile, List<Replacement> newInputList) throws IOException {

        final int firstRowNumber = sheetOne.getFirstRowNum();
        final int lastRowNumber = sheetOne.getLastRowNum();

        for (Replacement replacement : newInputList) {

            Map<String, Integer> columnNamesHashMap = new HashMap<>();
            Row rowForHashMap = sheetOne.getRow(0); //Get first row
            short minColIx = rowForHashMap.getFirstCellNum(); //get the first column index for a row
            short maxColIx = rowForHashMap.getLastCellNum(); //get the last column index for a row
            for (short colIx = minColIx; colIx < maxColIx; colIx++) { //loop from first to last index
                Cell cell = rowForHashMap.getCell(colIx);

                columnNamesHashMap.put(cell.getStringCellValue(), cell.getColumnIndex());
            }

            for (Map.Entry<String, Integer> entry : columnNamesHashMap.entrySet()) {

                String key = entry.getKey();
                Integer value = entry.getValue();

                if (key.equals(replacement.getColumnName())) {
                    columnNumber = value;

                }

            }

            for (int rowNumber = firstRowNumber; rowNumber < lastRowNumber; rowNumber++) {

                final Row row = sheetOne.getRow(rowNumber);

                if (row != null) {
                    final Cell cell = row.getCell(columnNumber);

                    if (cell != null && cell.getCellType() == CellType.STRING) {
                        if (cell.getStringCellValue().equals(replacement.getBefore())) {
                            cell.setCellValue(replacement.getAfter());
                        }
                    }

                    if (cell != null && cell.getCellType() == CellType.NUMERIC) {

                        int conversion = Integer.parseInt(replacement.getBefore());
                        if (cell.getNumericCellValue() == conversion) {
                            cell.setCellValue(replacement.getAfter());
                        }
                    }

                }
            }

        }
        OutputStream newOutputStream = new FileOutputStream(outputFile);
        workbook.write(newOutputStream);
    }
}
