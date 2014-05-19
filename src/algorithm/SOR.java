/** 
 * @author louisryan
//rowStart[0] = 0;
 * Testing the algorithm with the following example of linear equations
 *
 *					A				x			b	
 *
 *				12	3	-5			1			1	
 *		Ax=b	1	5	3			0			28
 *				3	7	13			1			76
 *
 */
	
package ie.ucd.mscba.algorithm;
import ie.ucd.mscba.io.OutputFile;
import java.lang.Math;							
import java.util.Formatter;
	
		
// Class to analyse the CRS Arrays and perform the SOR algorithm
public class SOR
{	
	// Class Fields
	private Formatter  f = new Formatter(); 			// For formatted writing to output file
	private OutputFile 		outFile;      					
	private double []           val;						// Array of values of the nonzero elements of the matrix
	private int    [] 			col;						// Array of column locations of the elements in the val vector
	private int    []  	   rowStart;						// Array of locations in the val vector that start a row
	private double [] 			  b;						// Solution vector
	private double []			  x;						// This is the initial guess that is calculated below
	private double []		   xOld;
	private double 		omega = 1.0;						// Omega is determined to be between 1.0 and 1.4 - We choose 1.2
	private final int 	maxits = 50;						// Total number of iterations before halting algorithm	
	private int 		 matrixSize;						
	private double 		machEpsilon;						// Calculate using method below
	private int      	 sorCounter;						// Number of iterations performed
	private double 	       xTol = 1;						// Need to set to 1 for first iteration
	private double     residVal = 1;						
	private boolean 	diagonalDom;						// Need to implement column diagonal dominance, do so from input file and change row/col_pointer
	
	
	
	/**
	 * Constructor that sets the size of field variables to their appropriate values
	**/
	public SOR( int t, int matrixSize, OutputFile outFile)
	{
		this.outFile = 					outFile;
		this.matrixSize = 			 matrixSize;			
		val = 	   		      	  new double[t];
		col = 		  			     new int[t];
		rowStart =      new int[matrixSize + 1];
		b  = 			 new double[matrixSize];	
	}
	
	
	/** 
	 * This method populates the fields above where the initial guess is calculated. This method will benefit the 
	 * greatest when the matrix is a band matrix
	 * 
	 */
	public void calculateInitialGuess()
	{	
		x = divide();
		xOld = divide();
		
		//for(int i=0; i<matrixSize; i++)
		//	System.out.println("initial guess element["+ i + "] = " + x[i]);
	}
	
	
	
	/**
	 * Determines if the matrix A(or CRS arrays in this case) is both column and
	 * row diagonally dominant
	 */
	public void testDiagonal() {
		
		double diagVal = 0; 											// Variable to store the diagonal element
		double sumrow; 													
		int count = 0;

		
		// Loop to check for row diagonal dominance
		for (int i = 0; i <= (matrixSize - 1); i++) 					// Each row
		{
			sumrow = 0; 												
			for (int j = rowStart[i]; j <= (rowStart[i + 1] - 1); j++) 
			{
				if (col[j] == i) { 										// Diagonal elements of the matrix
					diagVal = Math.abs(val[j]); 						// Store in variable d in 'abs' form
				} 
				else 
				{
					sumrow += (Math.abs(val[j]));						// If not diagonal, store in sumrow
				}
			}
			if (diagVal > sumrow) {										// Test against each other
				count++;				
			}
		}
		
		if (count == matrixSize) { 										// If all rows diag dom, count will match matrixSize																		
			diagonalDom = true;											// The algorithm will converge 
		} 
		else {		
			diagonalDom = false;										// The algorithm may or may not converge
		}	
	}
	
	
	
	/**
	 * calculate machine epsilon to a finite degree of precision
	 */
	private double calculateMachineEpsilon() {
		double machEps = 1.0;

		do {
			machEps /= 2.0;
		} while ((double) (1.0 + (machEps / 2.0)) != 1.0);

		return machEps;
	}
	
	
	
	/**
	 *  Method that calculates the SOR solution vector. Determine best way to 
	 *  choose appropriate Omega value. Gauss-Seidel omega = 1.0
	 */
	public void performSOR()
	{
		double 	  				  sum;		  															
		double 			 diagonal = 0;
		sorCounter	 			  = 0;											// The number of iterations
		
		//caluclate the initial guess based on A matrix and b vector
		calculateInitialGuess();
		// Calculate the machine epsilon to be used as tolerance
		machEpsilon = calculateMachineEpsilon();								
			
		// Keep on iteration if the redidTol or xTol are greater than machine Epsilon and no. of iterations is less than max iterations
		while (((residVal >= machEpsilon) && (xTol >= machEpsilon))  && (sorCounter <= maxits) )				
		{
			for (int i = 0; i <= (matrixSize-1); i++) 							// Each row
			{
				sum = 0;														// Reset the variable sum after each row 
				for (int j = rowStart[i]; j <= (rowStart[i + 1]-1); j++) 		
				{		
					sum = sum + val[j] * x[col[j]];								
					
					if (col[j] == i){											// Diagonal elements of the matrix
						diagonal = val[j];										// Store in variable d to  be used in equation
					}
				}
				x[i] = x[i] + omega * (b[i] - sum) /diagonal;					// Successive Over Relaxation equation
				System.out.println("Iteration " + (sorCounter+1) + ", element " 
						+ i + " : \t\t" + x[i] );					
			}
			sorCounter++;														// Increment the number of iterations performed
			if(sorCounter >= maxits)											// When max iterations are performed, write to file as reason
			{
				writeSummary("Max Iterations");
				outFile.closeWriter();											// Clean up and exit
				System.exit(0);	
			}
			
			// Call method to check for divergence or convergence
			checkConvergence_Divergence();
		}
		// Needs to determine if it was residual/xSequence convergence
		if(residVal<xTol)	{
			writeSummary(" Residual Converge");
		}
		else
			writeSummary(" xSeq. Converge");
		
	}	
	
	
	
	/**
	 * This method will check for convergence or divergence
	 */
	private void checkConvergence_Divergence() {
		
		// Calculate both the Residual and Successive Approximation
		residVal = calculateResidual(x);
		xTol = successiveApprox(x);	
	}
	


	/** 
	 * @param x
	 * @return
	 * 
	 * Method that calculates the successive approximation of the algorithm. It will indicate if the solution is being converged to or not
	 * There is also an alternative method of calculating successive approximation. Therefore, there are three methods overall
	 */
	private double successiveApprox(double[] x)
	{
		double 							   successiveNorm;							
		double subtraction [] =    new double[matrixSize];
		double error[] = 		   new double[matrixSize];								// This array is for alternate approach
		
		// Calculate the error
		for(int i = 0; i<matrixSize; i++)	
		{			
			subtraction[i] = ((x[i] - xOld[i]));														
			error[i] = Math.abs((x[i] - xOld[i])/x[i])*100;								// Alternative method of calculating the absolute error
			xOld[i] = x[i];																//re-assign the current array as the old one 
		}
		successiveNorm = calculateNorm(subtraction);									// Call method to calculate the norm of the array
		//System.out.println("The successive approximation is: \t" + successiveNorm);				
		//System.out.println("The alternative approximation is: \t" + calculateMax(error) + "%\n");
		return successiveNorm;
	}
	
	
	
	/**
	 * This method calculates the Residual(b-Ax) and returns the norm of r(Residual). 
	 * The Residual value(r) will approach zero as the answer converges
	 */
	private double calculateResidual(double[] x)
	{
		double residual[] = new double[matrixSize];				
		double[] mult = new double[matrixSize];								
		double normResidual;								// Returned after norm calculated
		
		// Call product method that will traverse through CRS and multiply A*x. Will return mult of size matrixSize
		mult = product(x);									 
	
		// To calculate the Residual, subtract the result of product from b
		for(int i=0; i<matrixSize; i++){
			residual[i] = b[i] - mult[i];	
		}
		
		normResidual = calculateNorm(residual);				// Finally, calculate the norm of the result b-Ax and return
		//System.out.println("The residual is: \t\t\t" + normResidual);
		double bNorm = calculateNorm(b);					// This needs to be performed somewhere more centralized and once!!!
		return normResidual/bNorm;
	}	//end calculateResidual method
	
	
	
	/**
	 * Given a vector(array), this method will return the norm of the vector
	 */
	private double calculateNorm(double[] x)
	{
		double sum = 0;
		for(int i=0; i< matrixSize; i++){
			sum = sum + x[i]*x[i];						// Stores the square of each element combined
		}
		return Math.sqrt(sum);							//returns the square root of the whole array(each element squared)
		
	}
	
	
	
	/**
	 * Takes a vector x and returns the product of the matrix stored in the CRS object with x for residual convergence.
	 **/
	private double[] product(double[] x){
		
		double[] product = new double[matrixSize];					//create vector to save product

		for (int i = 0; i < matrixSize; i++){
			for( int j = rowStart[i]; j < rowStart[i+1]; j++){
				product[i] += val[j] * x[col[j]];
			}
		}
		return product;
	}//end of product
	
	
	
	/**
	 * Takes a vector x and returns the division of the matrix stored in the CRS object with b for the initial guess estimation
	 **/
	private double[] divide(){
		
		double[] divided = new double[matrixSize];					// Create vector to save division

		for (int i = 0; i < matrixSize; i++){						
			for( int j = rowStart[i]; j < rowStart[i+1]; j++){
				divided[i] = b[i]/val[j];							// Perform division of diagonal and vector b
			}
		}
		
		return divided;
	}	//end of divide
	
	
	
	/**
	 * This method returns the max of the array. Used for successive approximation where the max of the norm array is calculate
	 * @param error[]
	 * @return the max of the array 
	 */
	public static double calculateMax(double[] error) {
	    double maximum = error[0];   					// Start max with the first value
	    for (int i=1; i<error.length; i++) {
	        if (error[i] > maximum) {					// If element is greater than current max, replace
	            maximum = error[i];   					// New maximum
	        }
	    }
	    return maximum;
	}	//end method calculateMax
	
	
	
	/**The following three methods are used to populate the CRS arrays defined in the fieilds of this class
 		and initialised in the constructor. They are called in class ParseInput- method getInputData */
	public void populateCRS(double element, int index, int j) {
		val[index] = element; 	
		col[index] = j;
	}
	
	public void populateRowStart(int i, int index) {
		rowStart[i+1] = index; 
	}
	
	public void populateVectorB(double element, int row){
		b[row] = element;
	}
	
	
	
	 /**
     * Write the details of the algorithm and stopping reason details
     */    
    public void writeSummary(String stopReason)
    {	
    	
    	outFile.write(  "\n\n   Stopping Reason      Max Iterations       Num of Iteration    " +
    			"Machine Epsilon     X seq. tolerance     Res seq. tolerance       Diag. Dominance(R)\n" 
                      + " --------------------  ------------------   ------------------  ------------------ " +
                      		" ------------------  -----------------------   ---------------------\n" );
          	
    	f = new Formatter(); 
        f.format( "%18s %14d  %20d  %22.7e  %18.7e  %20.7e %20s\n", stopReason,  maxits, sorCounter, machEpsilon, xTol, residVal, diagonalDom);	
        outFile.write( f.toString() );
        
        outFile.write("\n\n The Solution Vector x is: \n" + "----------------------------\n");
        f = new Formatter();
        for(int i=0; i<matrixSize; i++){
        	
        	f.format("\n\tx[" + i + "] = %2.20e", x[i]);	
        }
        outFile.write( f.toString() );
    }
    
    
    
    /**
     * This method is specifically for printing unrecoverable errors to the output file
     *
     */
    public void writeError(String stopReason)
    {
    	outFile.write("\n\n     Stopping Reason: \n" + "----------------------------\n");
    	f = new Formatter(); 
        f.format( "%21s\n\n\n",  stopReason);	 
        outFile.write( f.toString() );
    } 
    
}	//end of class