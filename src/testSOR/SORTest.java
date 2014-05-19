package ie.ucd.mscba.testSOR;
import java.util.Formatter;
import java.io.*;
import ie.ucd.mscba.algorithm.SOR;
import ie.ucd.mscba.algorithm.ParseInput;
import ie.ucd.mscba.bscholes.BlackScholes;
import ie.ucd.mscba.io.InputFile;
import ie.ucd.mscba.io.OutputFile;
import java.util.Scanner;


public class SORTest { 
	
	private static Formatter f2 = new Formatter(); 							// For formatted writing to output file   						

	
	public static void main(String[] args) {

		double startTime = System.currentTimeMillis();							// Start the timer	
		Scanner input = new Scanner(System.in);
        boolean checkFileExists=true;
        
        // Default file names
        String inputFileName = "nas_sor.in";		
        String outputFileName = "nas_sor.out";
	

		while (checkFileExists) {
			System.out.println("enter the file name(without extension) or -1 for default");
			String theName = input.nextLine(); 									// Input the name of the file								
			if (!(theName.equals("-1"))) {
				inputFileName = theName + ".in";
				outputFileName = theName + ".out";
			}
			else {
				// use defaults
				inputFileName = "nas_sor.in"; 
				outputFileName = "nas_sor.out";
			}
			checkFileExists = !checkFile(inputFileName);
		}

		
        // Create objects of the Input and Output files 
        InputFile  inFile  = new InputFile( inputFileName );
        OutputFile outFile = new OutputFile( outputFileName );
        
        //Black scholes algorithm - This works seperately to the other program. For the matrix A and vector B to be 
        //created based on BS equations, remove the comments for the following two lines.a
        
        //BlackScholes bs = new BlackScholes(outFile, inFile);
    	//bs.performBlackScholes();
    	
        // Start of execution of algorithm
        ParseInput parser = new ParseInput( inFile, outFile );					// Call the constructor of the parseInput class	
    	SOR sor = parser.getInputData(inFile);									// Call method to get input file, parse and store as CRS form returning object of the class
    	
    	sor.testDiagonal();														// Test the diagonal for both column and row diagonal dominance. return true if either true
    	sor.performSOR();														// Perform SOR algorithm on CRS arrays


    	//Print the execution time to the output file in format form
    	double endTime = System.currentTimeMillis(); 
    	double executionTime = endTime - startTime;
    	outFile.write(  "\n\n\n\n  Execution Time\n" + " ------------------  ");
    	f2 = new Formatter(); 
        f2.format( "\n %13.5e ms", executionTime);
        outFile.write( f2.toString() );


		// Clean up by closing the reader and writer
		inFile.closeReader();
		outFile.closeWriter();	
			
	}
		
	
	 private static boolean checkFile(String fileName) {
	      
		 File src = new File(fileName);
		 if (src.exists()) {
			 if (src.canRead()) {
				 if (src.isFile()) 
					 return(true);
				 else
					 System.out.println("ERROR 3: File is a directory"); 
			 }
			 else 
				 System.out.println("ERROR 2: Access denied");
		 }
		 else 
			 System.out.println("ERROR 1: No such file"); 
		 return(false);  
	 }

}	//end of class