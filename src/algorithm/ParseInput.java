/**
 * Created on 23 Oct 2011
 *
 * @author  Louis Ryan (louis)
 * @version 1.0
 * 
 * File:      ParseInput.java
 * Package:   
 * Project:   NAS_Assignment_1
 */


package ie.ucd.mscba.algorithm;
import ie.ucd.mscba.io.InputFile;
import ie.ucd.mscba.io.OutputFile;
import java.util.StringTokenizer;


public class ParseInput {
	
	// Class Fields
	private OutputFile outFile;
	
	
	
	/**
	 * Constructor: require InputFile inFile and OutputFile outFile as a bare
	 * minimum
	 * 
	 * NB this is not very robust code: it has no exception checking!
	 * 
	 * @param inFile
	 *            : the InputFile to use
	 * @param outFile
	 *            : the OutputFile to use
	 */
	public ParseInput(InputFile inFile, OutputFile outFile) 
	{	
		this.outFile = outFile;
		
	}
	
	
	
	/**
	 * This method traverses input file and places the values in their appropriate arrays/variable. The design decision made was to 
	 * to store the values in their Compressed Row Storage format directly from file. Although this made simple checks such as diagonal
	 * dominance more difficult, their is no Matrix A stored in n*n format
	 * 
	 *  the  matrixSize, t, val[], col[], rowStart[].
	 *
	 * @param inFile
	 * @return 
	 */
	public SOR getInputData(InputFile inFile) {

		String 				line; 										// Store each line of input in 'line'
		StringTokenizer   	  st; 										// StringTokenizer object
		double 			 element;										// To be used to store each token as int or double 
		int 		  matrixSize;										// Size of matrix
		int 			   t = 0; 										// Number of non-zero elements
		int 		   index = 0;										// Used as local 
		
		
		
		// Extract the first line - the size of the matrix A
		line = inFile.readLine(); 								// Read in the first line
		st = new StringTokenizer(line); 						// Breaks string into tokens
		matrixSize = Integer.parseInt(st.nextToken());			// Store as local variable matrixSize - This will be passed to CRS below
		

		// First pass to calculate t - the number of non-zeros
		for (int i = 0; i < matrixSize; i++) {			
			line = inFile.readLine();
			st = new StringTokenizer(line);
			for (int j = 0; j < matrixSize; j++) {

				element = Double.parseDouble(st.nextToken());	
				if (element != 0) {								
					t++;
				}
			}
		}
		

		// Second Pass to populate the CRS arrays
		SOR sor = new SOR(t, matrixSize, outFile);				// Initialize the size of the three CRS arrays by calling CRS constructor in CRS class

		inFile.closeReader(); 									// Close the reader
		InputFile inFile2 = new InputFile("nas_sor.in"); 		// **Hard coded** Create new object of InputFile
		line = inFile2.readLine(); 								// Reading the first line and dismiss due to previous processing
		
		
		int i;
		int j;
		
		// Loop through the tokens in the file and place the appropriate elements in the CRS arrays
		for ( i = 0; i < matrixSize; i++) {
			line = inFile2.readLine();
			st = new StringTokenizer(line);
			for ( j = 0; j < matrixSize; j++) {
				
				element = Double.parseDouble(st.nextToken());	// Get the token and parse it to a double "element"	
				if( i == j){									// Diagonal element when i==j
					if(element == 0)							// If the diagonal element is zero
					{
						String diagZero = "Zero on diagonal";	
						System.out.println("ooooh, you're not going to like this.... Check the output screen");
						sor.writeError(diagZero);				// Pass the string 'diagZero' as parameter to writeError in CRS.
						inFile.closeReader();					// Close up files and exit
						outFile.closeWriter();
						System.exit(0);
					}	
				}
				if (element != 0) {								// When the element is not zero
					sor.populateCRS(element, index, j);			// Populates val[] and col[] arrays
					index++;
				}
			}
			sor.populateRowStart(i, index);						// Populates rowStart[] array
		}
		
		
		//Populate the result vector "b" in class CRS				
		line = inFile2.readLine();	
		st = new StringTokenizer(line);
		
		for (int row = 0; row < matrixSize; row++) {			
			element = Double.parseDouble(st.nextToken());
			sor.populateVectorB(element, row);					// Method in CRS which populates vector b
		}	
		return sor;												// Return the object crs to main
	}	
}