# Introduction

"Excel Reader" is a Java-based application.

The project's primary purpose is to read data from a large Excel file, 
modify it as per user-defined requirements, 
and then insert the amended data into another Excel file.

# Build and Test

To run the application, you have two options:

1. After downloading the whole of the codebase, refer to the "Excel Reader Tutorial" located in the "Excel Reader Tutorial" data folder (**src/main/resources/Excel Reader Tutorial/Excel Reader Tutorial.docx**).

   Follow the instructions to run the application via the terminal.


2. If you have successfully cloned the project to your desired folder, you can execute the application using your preferred IDE by following the instructions provided below (#GettingStarted).

# Getting Started

1. **Familiarizing Yourself with Excel Reader**

   To get acquainted with the application, first, read the "Excel Reader Tutorial," which can be found in:

   "**src/main/resources/Excel Reader Tutorial/Excel Reader Tutorial.docx**" within the codebase.
   

2. **Starting the Application from the Command Line**

   Refer to the "RUNNING THE APPLICATION" section in the "Excel Reader Tutorial" to learn how to start the application from the command line (CMD).


3. **Understanding the Excel Reader Codebase**

   To launch the application, start by cloning the master branch into a folder of your choice.

   Once the cloning process is complete, follow these steps to run the application:


-Create a new branch from the master branch with a name of your choice.


-In IntelliJ (or any other IDE you prefer), navigate to the main method Main (it is located in the top-most bar). 

Once you click on the name of the method: Main you then need to go to -> Edit Configurations... and create a new configuration. 

Click the "plus" button to "Add a New Configuration."

The configuration type should be "Application." After creating the configuration, select the main class by choosing "Main" in the "Main Class" box.

Next, paste the following line of code (src/main/resources/META-INF/Plik.txt) into the "Program Arguments" field.

   3.Make sure that the the correct version of the JDK is specified in Intellij so that the whole of the program can be run.
     To check if the correct version of the JDK is installed in Intellij you need to go to File -> Project Structure and the correct version of JDK that is supposed to be installed is:
    correto 19: Amazon Correto 19.02. The language level should be specified as SDK default.

   4.Specify the input and output data files to perform the necessary operations.

   To change the input and output files, open the src/main/resources/META-INF folder and the Plik.txt file.

   For this demonstration, the input data file is located in: src/main/resources/DataFiles folder.

   5.After cloning the whole of the codebase please go to the Main function and run the Main method in order to run the whole of the application.  
