import config.ConfigFile;
import config.ReadConfigFile;
import controller.CopyData;
import controller.FilterAndReplaceTheData;
import controller.FilterTheData;
import lombok.SneakyThrows;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {
    static Logger logger = Logger.getLogger(Main.class);
    public static ConfigFile configFileWithData;
    private static final String MISSING_ARGUMENTS_ERROR = "Missing arguments";
    private static final String FILE_NOT_FOUND_ERROR = "Unable to find the configuration file";
    private static final String WRONG_FORMAT_ERROR = "Unable to read the configuration file";

    @SneakyThrows
    public static void main(String[] args) {
        logger.warn("Application has started");

        if (args == null || args.length == 0) {
            System.out.println(MISSING_ARGUMENTS_ERROR);
            logger.error("The file is missing arguments");
            System.exit(0);
        }
        String pathToConfig = args[0];
        ReadConfigFile readConfigFile = new ReadConfigFile();

        try {
            configFileWithData = readConfigFile.getConfigFile(pathToConfig);
            System.out.println("Starting the application...");
            TimeUnit.SECONDS.sleep(2);
            System.out.println("The configuration file: " + pathToConfig + " was successfully loaded.");
            TimeUnit.SECONDS.sleep(5);
            System.out.println("The input file specified by the user is:" + configFileWithData.getInputFile());
            TimeUnit.SECONDS.sleep(5);
            System.out.println("The output file specified by the user is: " + configFileWithData.getOutputFile().getNewFileName());
            TimeUnit.SECONDS.sleep(5);
            logger.info("The file was loaded");
            System.out.println();


            System.out.println("Performing the relevant operations on the data...");
            TimeUnit.SECONDS.sleep(5);
            CopyData.copyData(configFileWithData.getInputFile(), configFileWithData.getOutputFile().getNewFileName());
            System.out.println("A copy of the input file was created as an output file - it now has the relevant extension.");
            TimeUnit.SECONDS.sleep(5);
            System.out.println();

            System.out.println("Filtering the data, with the use of the Excel Functions, taking into account the output file...");
            FilterTheData.setTheExcelFileParameters(configFileWithData.getOutputFile().getNewFileName());
            FilterTheData.filterTheData(configFileWithData.getOutputFile().getNewFileName(), configFileWithData.getFilteringList());
            logger.info("The file has been filtered.");
            System.out.println("The data was successfully filtered and altered with the use of the Excel Functions.");
            TimeUnit.SECONDS.sleep(5);
            System.out.println();
            System.out.println("Filtering and replacing the data in the output file...");
            FilterAndReplaceTheData.filterAndReplaceTheData(configFileWithData.getOutputFile().getNewFileName(), configFileWithData.getReplacements());
            System.out.println("The data in the output file was successfully filtered and replaced.");
            TimeUnit.SECONDS.sleep(5);
            logger.info("The file has been modified");
            System.out.println();
            System.out.println("The application finished it's run.");
            TimeUnit.SECONDS.sleep(5);
            System.out.println("Exiting the application...");

        } catch (FileNotFoundException e) {
            System.out.println(FILE_NOT_FOUND_ERROR);
            logger.error("File has not been found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(WRONG_FORMAT_ERROR);
            logger.error("You have provided the wrong file format");
            logger.error(e);
            System.exit(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}