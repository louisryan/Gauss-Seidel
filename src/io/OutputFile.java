package ie.ucd.mscba.io;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


/**
 * General purpose class to support buffered writing a line at a time to 
 * a named file.  The output line is a String, produced, e.g., by a 
 * Formatter object.  The writeln() method here adds end-of-line character(s).
 * 
 * @author  Louis Ryan (louis)
 * @version 1.0
 * 
 * Type:      OutputFile
 * Date:Time: 28 Oct 2011 at 01:02:34
 */
public class OutputFile
{
    private String outFile;
    private BufferedWriter bw;

    
    /**
     * Constructor. Sets the path of the output, and creates a writer object for it.
     * 
     * @param outFile the name of the file to which output is written
     */
    public OutputFile( String outFile )
    {
        this.outFile = outFile;
        
        try
        {
            bw = new BufferedWriter( new FileWriter( outFile ) );
        }
        catch ( IOException o )
        {
            System.out.println( "Error creating file writer for " + outFile );
            o.printStackTrace( );
        }
    }
    
    
    
    /**
     * Writes the specified string to the output file the writer has been 
     * created for
     * 
     * @param s the string to be written
     */
    public void write( String s )
    {
        try
        {
            bw.write( s );
        }
        catch ( IOException o )
        {
            System.out.println( "Error writing to output file: "+ outFile );
            o.printStackTrace( );
            closeWriter();
        }
    }
    
    
     
    /**
     * Writes the specified string to the output file the writer has been 
     * created for, appending a newline \n
     * 
     * @param s the string to be written
     */
    public void writeln( String s )
    {
        write( s + "\n" );
    }
    
    
    
    /** 
     * Closes the writer object 
     */
    public void closeWriter( )
    {
        try
        {
            bw.close();
            //System.out.println( "Writer to file " + outFile + " closed" );
        }
        catch ( IOException o )
        {
            System.out.println( "Error closing writer to output file: "+ outFile );
            o.printStackTrace( );
        }
    }

}