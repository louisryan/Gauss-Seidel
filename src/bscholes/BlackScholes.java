/**
 * This class will create a matrix A and vector b
 */
package ie.ucd.mscba.bscholes;

import java.util.Formatter;

import ie.ucd.mscba.io.InputFile;
import ie.ucd.mscba.io.OutputFile;


public class BlackScholes 
{
	// Hard code the initial parameters - request on input.
	private Formatter  f = new Formatter(); 			// For formatted writing to output file
	private OutputFile outFile;
	double stockPrice =    100;
	double strikePrice =   100;
	int intervalSize = 	     4;		
	double r = 		      0.03;
	double deviation =     0.4;
	int T = 				 6;
	int M = 	  intervalSize;
	double k = 		       T/M;							
	
	
	// Create and initialise a matrix with pre-defined size
	double[][] A = new double[intervalSize][intervalSize];
	double[] b = new double[intervalSize+1];
	
	
	// Constructor which initializes the output file to class wide
	public BlackScholes(OutputFile outFile, InputFile inFile)
	{
		this.outFile = outFile;
		
	}
	
	
	
	public void createBlackScholesMatrix()
	{
		
		f = new Formatter();
		for(int i = 0; i < intervalSize; i++)	{
			for(int j = 0; j < intervalSize ; j++)	{
				// position to the left of the diagonal
				if((i-1) == j)
				{
					double lowerElement = lowerCalculation(i);	
					//System.out.println("location = (" + i + "," +j +") = " +lowerElement  );
					A[i][j] = lowerElement;
					//f.format( "%20e",  lowerElement);	 
			        //outFile.write( f.toString() );
				}
				
				// position on the diagonal
				else if(i == j)	
				{
					double diagElement = diagonalCalculation(i);
					A[i][j] = diagElement;
					//f.format( "%20e",  diagElement);	 
			        //outFile.write( f.toString() );
				}
				
				// position to the right of the diagonal
				else if((i+1) == j)
				{
					double upperElement = upperCalculation(i);
					A[i][j] = upperElement;
					//f.format( "%20e",  upperElement);	 
			        //outFile.write( f.toString() );			 
				}
				
				else
				{
					f.format( "%20e",  0.000);	 
				}
		        
			}
			outFile.write( f.toString() );
			f.format( "\n");

		}
	}
	
	
	
	public void createVectorB()	{

		for(int i=1; i<=intervalSize; i++)	
		{
			double interval = stockPrice/intervalSize;
			b[i] = interval*i;
			System.out.println("b"+ b[i]);
		}
	}

	
	private double upperCalculation(int i) {
		
		int num = i+1;
		double upperElement = (-(num*k)/2)*(num*(deviation*deviation)+r);
		return upperElement;
	}
	
	
	private double diagonalCalculation(int i) {
		
		double diagElement = 1+(k*r)+(k*(deviation*deviation))*((i+1)*(i+1));
		return diagElement;
	}
	
	
	private double lowerCalculation(int i) {

		int num = i+1;
		double upperElement = (-((num)*k)/2)*(num*(deviation*deviation)-r);
		return upperElement;
	}

	
	private void printB()
	{
		for(int i = 0; i < intervalSize; i++)	{
			System.out.print(b[i] + " ");
		}
	}
	
	
	private void printA()
	{
		for(int i = 0; i < intervalSize; i++)	{
			for(int j = 0; j < intervalSize; j++)	{
				System.out.print(A[i][j] + "   ");
			}
			System.out.println();
		}
		//System.out.println("\n\n");
	}
	
	
	public void performBlackScholes()
	{
		createBlackScholesMatrix();
		createVectorB();
		printA();
		printB();	
	}
}