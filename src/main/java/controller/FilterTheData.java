package controller;

import config.FilterOption;
import config.Filtering;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
public class FilterTheData {

    /**
     * The filteringList ArrayList which stores the filterValues from the configuration file.
     */
    public static List<String> filteringList = new ArrayList<>();

    /**
     * The filterList Array which stores the filterValues from the configuration file,
     * if there are more than one filter values
     */
    public static String[] filterList;

    /**
     * The columnNumber which stores the ColumnName, which is used throughout the whole class.
     * For example to read in the data, from a specific column and replace the cells in that column.
     */
    public static Integer columnNumber;

    /**
     * The columnName result supportive variable, it is used with conjunction with the ExcelFunctions at the bottom of the file.
     * It is for example used to read in the data from a specific column.
     */
    public static int columnNameResult = 0;

    /**
     * The rowIndexResult supportive variable, it is used with conjunction with the ExcelFunctions at the bottom of the file.
     * It is for example used to read in a specific row of our liking.
     */
    public static int rowIndexResult = 0;


    /**
     * The sheetOne, which is used to read in an Excel Sheet with the given index.
     */
    public static XSSFSheet sheetOne;


    /**
     * Excel workbook, it will contain the relevant output data
     */
    public static Workbook workbook;

    /**
     * Starting parameter filteringListInt. It is used to iterate over the filteringList so that relevant filterValues can be derived.
     */
    public static int filteringListInt = 0;


    /**
     * * Filters the data, taking into account a specific file.
     * The function must accept the input parameters from the config file so that it can
     * perform the filtering of the data based on a specific criteria.
     *
     * @param outputFile   it is the file to which the data is being written into taking into account the whole function. The end results are stored there.
     * @param newInputList it is the input list containing the individual filters, which are then used to transform the data from the input ExcelFile.
     * @throws IOException an exception thrown whenever an input or output operation is failed or interpreted.
     */
    public static void filterTheData(String outputFile, List<Filtering> newInputList) throws IOException {

        final int firstRowNumber = sheetOne.getFirstRowNum();
        final int lastRowNumber = sheetOne.getLastRowNum();

        for (Filtering filtering : newInputList) {

            if (filtering.getFilterValue().contains(",")) {

                String result = filtering.getFilterValue();

                filterList = result.split(",");

                for (String filterValue : filterList) filteringList.add(filterValue);
            } else {
                filteringList.clear();
                filteringListInt = 0;
                filteringList.add(filtering.getFilterValue());
            }


            Map<String, Integer> columnNamesHashMap = new HashMap<>();
            Row rowForHashMap = sheetOne.getRow(0); //Get first row
            short minColIx = rowForHashMap.getFirstCellNum(); //get the first column index for a row
            short maxColIx = rowForHashMap.getLastCellNum(); //get the last column index for a row
            for (short colIx = minColIx; colIx < maxColIx; colIx++) { //loop from first to last index
                Cell cell = rowForHashMap.getCell(colIx); //get the cell
                columnNamesHashMap.put(cell.getStringCellValue(), cell.getColumnIndex());
                //add the cell contents (name of column) and cell index to the map
            }

            for (Map.Entry<String, Integer> entry : columnNamesHashMap.entrySet()) {

                String key = entry.getKey();
                Integer value = entry.getValue();

                if (key.equals(filtering.getColumnName())) {
                    columnNumber = value;

                    Iterator<Row> iterator = sheetOne.iterator();
                    FilterOption filteringOptionResult = filtering.getFilterOption();
                    String columnName = filtering.getColumnName();
                    String filterValueResult = filtering.getFilterValue();

                    while (iterator.hasNext()) {
                        Row currentRow = iterator.next();
                        Cell cellResult = currentRow.createCell(currentRow.getLastCellNum(), CellType.STRING);
                        if (currentRow.getRowNum() == 0) {
                            cellResult.setCellValue("Results for: |Column Name: " + columnName + " |Filter Value: " + filterValueResult + " |Filtering Option: " + filteringOptionResult);

                            CellStyle cs = workbook.createCellStyle();
                            cs.setWrapText(true);
                            cellResult.setCellStyle(cs);

                            columnNameResult = cellResult.getColumnIndex();
                            rowIndexResult = cellResult.getRowIndex();
                        }

                    }

                }

            }

            for (String filterValue : filteringList) {

                for (int rowNumber = firstRowNumber; rowNumber < lastRowNumber; rowNumber++) {

                    int result = rowNumber + 1;

                    final Row row = sheetOne.getRow(result);


                    if (row != null) {
                        final Cell cell = row.getCell(columnNumber);

                        FilterOption filteringOption = filtering.getFilterOption();

                        switch (filteringOption) {
                            case CONTAINS:
                                ExcelContains(cell, filterValue);
                                break;
                            case NOT_CONTAINS:
                                ExcelDoesNotContain(cell, filterValue);
                                break;
                            case EQUALS:
                                ExcelEqualsFunction(cell, filterValue);
                                break;
                            case NOT_EQUALS:
                                ExcelDoesNotEqualFunction(cell, filterValue);
                                break;
                            case LESS:
                                ExcelIsLessThan(cell, filterValue);
                                break;
                            case GREATER:
                                ExcelIsGreaterThan(cell, filterValue);
                                break;
                            case BEFORE:
                                System.out.println("BEFORE");
                                break;
                            case AFTER:
                                System.out.println("AFTER");
                                break;
                            case BETWEEN:
                                System.out.println("BETWEEN");
                                break;
                            case MAX:
                                System.out.println("MAX");
                                ExcelMaxFunction(cell,filterValue);
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + filteringOption);
                        }

                    }
                }
            }

        }


        workbook.write(new FileOutputStream(outputFile));

    }


    public static void setTheExcelFileParameters(String inputFile) throws IOException {

        File xlFile = new File(inputFile);

        if (xlFile.toString().endsWith(".xls")) {
            workbook = new HSSFWorkbook(new FileInputStream(inputFile));
        } else if (xlFile.toString().endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(new FileInputStream(inputFile));
        } else if (xlFile.toString().endsWith(".xlsb")) {
            workbook = new XSSFWorkbook(new FileInputStream(inputFile));
        }

        sheetOne = (XSSFSheet) workbook.getSheet("Sheet1");

    }
    /**
     * The function allows the user to compare two cells in Excel,
     * and return a value in the third corresponding cell,
     * if the two values are the same
     *
     * @param inputCell   this is the cell which we want to alter.
     *                    After the relevant operation on data has been conducted it's value will be altered.
     * @param filterValue This is the value which we are going to check each cell in the Excel file against.
     */
    public static void ExcelEqualsFunction(Cell inputCell, String filterValue) {

        String resultString;
        resultString = filteringList.get(filteringListInt);

        boolean booleanResult = !filterValue.equals(resultString);
        if (booleanResult) {

            rowIndexResult = 0;
            filteringListInt++;
        }

        boolean booleanInputCell = inputCell != null && inputCell.getStringCellValue().equals(filterValue);

        if (inputCell != null && inputCell.getCellType() == CellType.STRING) {
            if (booleanInputCell) {

                String colName = CellReference.convertNumToColString(inputCell.getColumnIndex());
                int rowIndex = inputCell.getRowIndex() + 1;
                rowIndexResult = rowIndexResult + 1;
                final Row row = sheetOne.getRow(rowIndexResult);
                row.getLastCellNum();
                if (row != null) {

                    final Cell cell = row.getCell(columnNameResult);


                    if (cell != null) {
                        cell.setCellFormula("IF(" + colName + rowIndex + "=\"" + filterValue + "\",\"" + filterValue + "\",\"\")");

                        FormulaEvaluator formulaEvaluator =
                                workbook.getCreationHelper().createFormulaEvaluator();
                        formulaEvaluator.evaluateFormulaCell(cell);

                        CellStyle cs = workbook.createCellStyle();
                        cs.setWrapText(true);
                        cell.setCellStyle(cs);

                    }
                }
            } else {
                rowIndexResult = rowIndexResult + 1;
            }
        }
    }

    /**
     * Checks if two values in corresponding cells are not equal.
     * If the above statement is true it will return a true value
     *
     * @param inputCell   this is the cell which we want to alter.
     *                    After the relevant operation on data has been conducted it's value will be altered.
     * @param filterValue This is the value which we are going to check each cell in the Excel file against.
     */
    public static void ExcelDoesNotEqualFunction(Cell inputCell, String filterValue) {

        String resultString;
        resultString = filteringList.get(filteringListInt);

        boolean booleanResult = !filterValue.equals(resultString);
        if (booleanResult) {
            rowIndexResult = 0;
            filteringListInt++;
        }

        boolean booleanNew;
        int size = filteringList.size();


        booleanNew = switch (size) {
            case 1 -> inputCell != null && !inputCell.getStringCellValue().equals(filteringList.get(0));
            case 2 -> inputCell != null && !inputCell.getStringCellValue().equals(filteringList.get(0)) &&
                    !inputCell.getStringCellValue().contains(filteringList.get(1));
            case 3 -> inputCell != null && !inputCell.getStringCellValue().equals(filteringList.get(0)) &&
                    !inputCell.getStringCellValue().equals(filteringList.get(1)) &&
                    !inputCell.getStringCellValue().equals(filteringList.get(2));
            case 4 -> inputCell != null && !inputCell.getStringCellValue().equals(filteringList.get(0)) &&
                    !inputCell.getStringCellValue().equals(filteringList.get(1)) &&
                    !inputCell.getStringCellValue().equals(filteringList.get(2)) &&
                    !inputCell.getStringCellValue().equals(filteringList.get(3));
            case 5 -> inputCell != null && !inputCell.getStringCellValue().equals(filteringList.get(0)) &&
                    !inputCell.getStringCellValue().equals(filteringList.get(1)) &&
                    !inputCell.getStringCellValue().equals(filteringList.get(2)) &&
                    !inputCell.getStringCellValue().equals(filteringList.get(3)) &&
                    !inputCell.getStringCellValue().equals(filteringList.get(4));
            default -> throw new IllegalStateException("Unexpected value: " + size);
        };

        if (inputCell != null && inputCell.getCellType() == CellType.STRING) {
            if (booleanNew) {

                String colName = CellReference.convertNumToColString(inputCell.getColumnIndex());
                int rowIndex = inputCell.getRowIndex() + 1;
                rowIndexResult = rowIndexResult + 1;
                final Row row = sheetOne.getRow(rowIndexResult);


                if (row != null) {

                    final Cell cell = row.getCell(columnNameResult);

                    if (cell != null) {
                        cell.setCellFormula("IF(" + colName + rowIndex + "<>\"" + filterValue + "\",\"" + inputCell.getStringCellValue() + "\",\"\")");

                        row.getLastCellNum();
                        sheetOne.getLastRowNum();
                        FormulaEvaluator formulaEvaluator =
                                workbook.getCreationHelper().createFormulaEvaluator();
                        formulaEvaluator.evaluateFormulaCell(cell);

                        CellStyle cs = workbook.createCellStyle();
                        cs.setWrapText(true);   //Wrapping text
                        cell.setCellStyle(cs);
                    }
                }
            } else {
                rowIndexResult = rowIndexResult + 1;
            }
        }
    }

    /**
     * Checks if a cell with a given value contains another cell.
     * If the above statement is true it will return a true value.
     *
     * @param inputCell   this is the cell which we want to alter.
     *                    After the relevant operation on data has been conducted it's value will be altered.
     * @param filterValue This is the value which we are going to check each cell in the Excel file against.
     */
    public static void ExcelContains(Cell inputCell, String filterValue) {

        String resultString;
        resultString = filteringList.get(filteringListInt);

        boolean booleanResult = !filterValue.equals(resultString);
        if (booleanResult) {

            rowIndexResult = 0;
            filteringListInt++;
        }

        boolean booleanNew = inputCell != null && inputCell.getStringCellValue().contains(filterValue);

        if (inputCell != null && inputCell.getCellType() == CellType.STRING) {
            if (booleanNew) {
                String colName = CellReference.convertNumToColString(inputCell.getColumnIndex());
                int rowIndex = inputCell.getRowIndex() + 1;
                rowIndexResult = rowIndexResult + 1;
                final Row row = sheetOne.getRow(rowIndexResult);

                if (row != null) {
                    final Cell cell = row.getCell(columnNameResult);

                    String resultForInputCell = inputCell.getStringCellValue();

                    if (cell != null) {
                        cell.setCellFormula("IF(ISNUMBER(SEARCH(\"" + filterValue + "\"," + colName + rowIndex + ")), \"" + resultForInputCell + "\", \"\")");
                        FormulaEvaluator formulaEvaluator =
                                workbook.getCreationHelper().createFormulaEvaluator();
                        formulaEvaluator.evaluateFormulaCell(cell);

                        CellStyle cs = workbook.createCellStyle();
                        cs.setWrapText(true);
                        cell.setCellStyle(cs);
                    }
                }

            } else {

                rowIndexResult = rowIndexResult + 1;
            }
        }
    }
    /**
     * Checks if a cell is not contained within another cell.
     * If the above statement is true, a true value will be returned in the third corresponding cell.
     *
     * @param inputCell   this is the cell which we want to alter.
     *                    After the relevant operation on data has been conducted it's value will be altered.
     * @param filterValue This is the value which we are going to check each cell in the Excel file against.
     */
    public static void ExcelDoesNotContain(Cell inputCell, String filterValue) {

        String resultString;
        resultString = filteringList.get(filteringListInt);

        boolean booleanResult = !filterValue.equals(resultString);
        if (booleanResult) {
            rowIndexResult = 0;
            filteringListInt++;
        }

        boolean booleanNew;
        int size = filteringList.size();

        booleanNew = switch (size) {
            case 1 -> inputCell != null && !inputCell.getStringCellValue().contains(filteringList.get(0));
            case 2 ->
                    inputCell != null && !inputCell.getStringCellValue().contains(filteringList.get(0)) && !inputCell.getStringCellValue().contains(filteringList.get(1));
            case 3 -> inputCell != null && !inputCell.getStringCellValue().contains(filteringList.get(0)) &&
                    !inputCell.getStringCellValue().contains(filteringList.get(1)) &&
                    !inputCell.getStringCellValue().contains(filteringList.get(2));
            case 4 -> inputCell != null && !inputCell.getStringCellValue().contains(filteringList.get(0)) &&
                    !inputCell.getStringCellValue().contains(filteringList.get(1)) &&
                    !inputCell.getStringCellValue().contains(filteringList.get(2)) &&
                    !inputCell.getStringCellValue().contains(filteringList.get(3));
            case 5 -> inputCell != null && !inputCell.getStringCellValue().contains(filteringList.get(0)) &&
                    !inputCell.getStringCellValue().contains(filteringList.get(1)) &&
                    !inputCell.getStringCellValue().contains(filteringList.get(2)) &&
                    !inputCell.getStringCellValue().contains(filteringList.get(3)) &&
                    !inputCell.getStringCellValue().contains(filteringList.get(4));
            default -> throw new IllegalStateException("Unexpected value: " + size);
        };

        if (inputCell != null && inputCell.getCellType() == CellType.STRING) {
            if (booleanNew) {

                String colName = CellReference.convertNumToColString(inputCell.getColumnIndex());
                int rowIndex = inputCell.getRowIndex() + 1;
                rowIndexResult = rowIndexResult + 1;
                final Row row = sheetOne.getRow(rowIndexResult);

                if (row != null) {

                    final Cell cell = row.getCell(columnNameResult);

                    if (cell != null) {
                        cell.setCellFormula("IF(ISERROR(SEARCH(\"" + filterValue + "\"," + colName + rowIndex + ")), \"" + inputCell.getStringCellValue() + "\", \"\")");

                        FormulaEvaluator formulaEvaluator =
                                workbook.getCreationHelper().createFormulaEvaluator();
                        formulaEvaluator.evaluateFormulaCell(cell);

                        CellStyle cs = workbook.createCellStyle();
                        cs.setWrapText(true);   //Wrapping text
                        cell.setCellStyle(cs);
                    }

                }
            } else {

                rowIndexResult = rowIndexResult + 1;
            }
        }
    }
    /**
     * Checks what happens if an Excel value is less than a certain value.
     * If the above statement is correct a TRUE value will be returned in the third corresponding cell.
     *
     * @param inputCell   this is the cell which we want to alter.
     *                    After the relevant operation on data has been conducted it's value will be altered.
     * @param filterValue This is the value which we are going to check each cell in the Excel file against.
     */
    public static void ExcelIsLessThan(Cell inputCell, String filterValue) {

        String resultString;
        resultString = filteringList.get(filteringListInt);

        boolean booleanResult = !filterValue.equals(resultString);
        if (booleanResult) {
            rowIndexResult = 0;
            filteringListInt++;
        }

        int conversion = Integer.parseInt(filterValue);

        if (inputCell != null && inputCell.getCellType() == CellType.NUMERIC) {
            if (inputCell.getNumericCellValue() < conversion) {

                String colName = CellReference.convertNumToColString(inputCell.getColumnIndex());
                int rowIndex = inputCell.getRowIndex() + 1;

                ++rowIndexResult;
                final Row row = sheetOne.getRow(rowIndexResult);

                if (row != null) {

                    final Cell cell = row.getCell(columnNameResult);

                    if (cell != null) {

                        cell.setCellFormula("IF(" + colName + rowIndex + "<" + filterValue + ",\"" + inputCell.getNumericCellValue() + "\",\"\")");
                        FormulaEvaluator formulaEvaluator =
                                workbook.getCreationHelper().createFormulaEvaluator();
                        formulaEvaluator.evaluateFormulaCell(cell);

                        CellStyle cs = workbook.createCellStyle();
                        cs.setWrapText(true);
                        cell.setCellStyle(cs);
                    }
                }
            } else {
                ++rowIndexResult;
            }

        }
    }
    /**
     * * Checks what happens if an Excel value is greater than a certain value.
     * If the above statement is correct a TRUE value will be returned in the third corresponding cell.
     *
     * @param inputCell   this is the cell which we want to alter.
     *                    After the relevant operation on data has been conducted it's value will be altered.
     * @param filterValue This is the value which we are going to check each cell in the Excel file against.
     */
    public static void ExcelIsGreaterThan(Cell inputCell, String filterValue) {


        String resultString;
        resultString = filteringList.get(filteringListInt);

        boolean booleanResult = !filterValue.equals(resultString);
        if (booleanResult) {

            rowIndexResult = 0;
            filteringListInt++;
        }

        int conversion = Integer.parseInt(filterValue);

        if (inputCell != null && inputCell.getCellType() == CellType.NUMERIC) {
            if (inputCell.getNumericCellValue() > conversion) {

                String colName = CellReference.convertNumToColString(inputCell.getColumnIndex());
                int rowIndex = inputCell.getRowIndex() + 1;

                ++rowIndexResult;
                final Row row = sheetOne.getRow(rowIndexResult);
                row.getLastCellNum();


                if (row != null) {
                    final Cell cell = row.getCell(columnNameResult);

                    if (cell != null) {
                        cell.setCellFormula("IF(" + colName + rowIndex + ">" + filterValue + ",\"" + inputCell.getNumericCellValue() + "\",\"\")");
                        FormulaEvaluator formulaEvaluator =
                                workbook.getCreationHelper().createFormulaEvaluator();
                        formulaEvaluator.evaluateFormulaCell(cell);

                        CellStyle cs = workbook.createCellStyle();
                        cs.setWrapText(true);
                        cell.setCellStyle(cs);
                    }
                }

            } else {
                ++rowIndexResult;
            }
        }
    }

    public static void ExcelMaxFunction(Cell inputCell, String filterValue) {


        String resultString;
        resultString = filteringList.get(filteringListInt);

        boolean booleanResult = !filterValue.equals(resultString);
        if (booleanResult) {
            rowIndexResult = 0;
            filteringListInt++;
        }

        boolean booleanNew;
        int size = filteringList.size();


        booleanNew = switch (size) {
            case 1 -> inputCell != null && !inputCell.getStringCellValue().equals(filteringList.get(0));
            case 2 -> inputCell != null && !inputCell.getStringCellValue().equals(filteringList.get(0)) &&
                    !inputCell.getStringCellValue().contains(filteringList.get(1));
            case 3 -> inputCell != null && !inputCell.getStringCellValue().equals(filteringList.get(0)) &&
                    !inputCell.getStringCellValue().equals(filteringList.get(1)) &&
                    !inputCell.getStringCellValue().equals(filteringList.get(2));
            case 4 -> inputCell != null && !inputCell.getStringCellValue().equals(filteringList.get(0)) &&
                    !inputCell.getStringCellValue().equals(filteringList.get(1)) &&
                    !inputCell.getStringCellValue().equals(filteringList.get(2)) &&
                    !inputCell.getStringCellValue().equals(filteringList.get(3));
            case 5 -> inputCell != null && !inputCell.getStringCellValue().equals(filteringList.get(0)) &&
                    !inputCell.getStringCellValue().equals(filteringList.get(1)) &&
                    !inputCell.getStringCellValue().equals(filteringList.get(2)) &&
                    !inputCell.getStringCellValue().equals(filteringList.get(3)) &&
                    !inputCell.getStringCellValue().equals(filteringList.get(4));
            default -> throw new IllegalStateException("Unexpected value: " + size);
        };

        if (inputCell != null && inputCell.getCellType() == CellType.STRING) {
            if (booleanNew) {

                String colName = CellReference.convertNumToColString(inputCell.getColumnIndex());
                int rowIndex = inputCell.getRowIndex() + 1;
                rowIndexResult = rowIndexResult + 1;
                final Row row = sheetOne.getRow(rowIndexResult);

                if(isRowContainsData(row)){

                    int result =  row.getRowNum() -1;
                };


                if (row != null) {

                    final Cell cell = row.getCell(columnNameResult);

                    if (cell != null) {
                        cell.setCellFormula("MAX(" + colName + "1:" + colName + "10)");

                        row.getLastCellNum();
                        sheetOne.getLastRowNum();
                        FormulaEvaluator formulaEvaluator =
                                workbook.getCreationHelper().createFormulaEvaluator();
                        formulaEvaluator.evaluateFormulaCell(cell);

                        CellStyle cs = workbook.createCellStyle();
                        cs.setWrapText(true);   //Wrapping text
                        cell.setCellStyle(cs);
                    }
                }
            } else {
                rowIndexResult = rowIndexResult + 1;
            }
        }





    }
    /**
     * This function needs to be implemented.
     * It  will get and return a list of "Before" replacements.
     */
    public static void ExcelGetBeforeReplacements(Cell inputCell, String filterValue) {

        String resultString;
        resultString = filteringList.get(filteringListInt);

        boolean booleanResult = !filterValue.equals(resultString);
        if (booleanResult) {
            rowIndexResult = 0;
            filteringListInt++;
        }

        boolean conditionMet = false;

        if (inputCell != null && inputCell.getCellType() == CellType.NUMERIC) {
            double cellValue = inputCell.getNumericCellValue();
            double filterValueNumeric = Double.parseDouble(filterValue);
            conditionMet = cellValue < filterValueNumeric;
        } else if (inputCell != null && inputCell.getCellType() == CellType.STRING) {
            try {
                Date cellDate = inputCell.getDateCellValue();
                Date filterDate = new SimpleDateFormat("yyyy-MM-dd").parse(filterValue);
                conditionMet = cellDate.before(filterDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (conditionMet) {
            String colName = CellReference.convertNumToColString(inputCell.getColumnIndex());
            int rowIndex = inputCell.getRowIndex() + 1;

            ++rowIndexResult;
            final Row row = sheetOne.getRow(rowIndexResult);

            if (row != null) {
                final Cell cell = row.getCell(columnNameResult);

                if (cell != null) {
                    cell.setCellFormula("IF(" + colName + rowIndex + "<\"" + filterValue + "\",\"" + inputCell.getStringCellValue() + "\",\"\")");

                    FormulaEvaluator formulaEvaluator =
                            workbook.getCreationHelper().createFormulaEvaluator();
                    formulaEvaluator.evaluateFormulaCell(cell);

                    CellStyle cs = workbook.createCellStyle();
                    cs.setWrapText(true);
                    cell.setCellStyle(cs);
                }
            }
        } else {
            ++rowIndexResult;
        }
    }
    /**
     * This function needs to be implemented.
     * It  will get and return a list of "After" replacements.
     */
        public static void ExcelGetAfterReplacements(Cell inputCell, String afterValue) {
            if (inputCell != null) {
                if (inputCell.getCellType() == CellType.NUMERIC) {
                    double cellValue = inputCell.getNumericCellValue();
                    double threshold = Double.parseDouble(afterValue);

                    if (cellValue > threshold) {
                        // Handle logic for writing the cell value to the output
                        writeCellValue(inputCell, cellValue);
                    }
                } else if (inputCell.getCellType() == CellType.STRING) {
                    // Assuming it's a date string, parse and compare
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date cellDate = sdf.parse(inputCell.getStringCellValue());
                        Date thresholdDate = sdf.parse(afterValue);

                        if (cellDate.after(thresholdDate)) {
                            writeCellValue(inputCell, cellDate);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /**
     * This function is left to be implemented.
     * Checks if a number is between a certain data range.
     */
    public static void ExcelBetweenFunction() throws IOException {
        FileInputStream inputStream = new FileInputStream("C:\\ReaderExcelAdditionalFunctionality\\src\\main\\java\\data\\Output.xlsx");
        XSSFWorkbook excel = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = excel.getSheetAt(0);

        final Row row = sheet.getRow(0);
        final Cell cell = row.getCell(3);

        sheetOne.getActiveCell();

        cell.setCellFormula("IF(AND(C1>=MIN(A1,B1), C1 <= MAX(A1,B1)), \"Yes\",\"No\")");

        XSSFFormulaEvaluator formulaEvaluator =
                excel.getCreationHelper().createFormulaEvaluator();
        formulaEvaluator.evaluateFormulaCell(cell);

        //SupportiveFunctions.writeTo(excel, "C:\\ReaderExcelAdditionalFunctionality\\src\\main\\java\\data\\Output.xlsx");
    }
    private static void writeCellValue(Cell inputCell, Object value) {
        String colName = CellReference.convertNumToColString(inputCell.getColumnIndex());
        int rowIndex = inputCell.getRowIndex() + 1;

        ++rowIndexResult;
        final Row row = sheetOne.getRow(rowIndexResult);

        if (row != null) {
            final Cell cell = row.getCell(columnNameResult);

            if (cell != null) {
                cell.setCellValue(value.toString());
                CellStyle cs = workbook.createCellStyle();
                cs.setWrapText(true);
                cell.setCellStyle(cs);
            }
        }
    }

    // Helper method to check if a row contains non-empty data
    private static boolean isRowContainsData(Row row) {
        for (Cell cell : row) {
            if (cell.getCellType() != CellType.BLANK) {
                return false;  // Row contains some data
            }
        }
        return true;  // No data in this row
    }

}