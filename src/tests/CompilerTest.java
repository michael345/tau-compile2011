package tests;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.Test;
import static org.junit.Assert.*;


public class CompilerTest {
	    
	    String[] ICfileNames = {"Quicksort","badid","Sieve","goodlex_badsyn",
	    		"one_line_comment","Quicksort_bad","unexpected_char","unexpected_eof",
	    		"oor","backslash_str","bad_num","bad_num_negative","bad_num_space"
	    };
	    
	    @Test
	    public void test() throws Exception {
	        PrintStream systemOut = System.out;
	        for (String file : ICfileNames) {
	            
	            OutputStream out = new ByteArrayOutputStream();
	            PrintStream compilerOutput = new PrintStream(out);

	            System.setOut(compilerOutput);//assigning the out to our compilerOutput printstream
	            String[] ICsrc = new String[]{"test/"+file+".ic"};
	            IC.Compiler.main(ICsrc);
	            System.setOut(systemOut); //setting the regular system.out again
	            String actualOutput = out.toString();
	            compilerOutput.close();
	            
	          
	            String expectedFile = "test/"+file+".out";
	            FileReader freader = new FileReader(expectedFile);
	            BufferedReader breader = new BufferedReader(freader);
	            StringBuffer Data = new StringBuffer();
	           
	            char[] cbuf = new char[1000];
	            String charsRead ="";
	            int MAX = 1000;
	            
	            int numOfCharsRead = 0;
	            while ((numOfCharsRead = breader.read(cbuf, 0, MAX)) != -1) {
	            	charsRead = String.valueOf(cbuf, 0, numOfCharsRead);
	                Data.append(charsRead);
	                cbuf = new char[1000];
	            }
	            breader.close();
	            
	            
	            String expectedOutput = Data.toString();
	            
	    
	            String[] actualLines = actualOutput.split("\n");
	            String[] expectedLines = expectedOutput.split("\n");
	           	            
	            for (int i=0; i<actualLines.length; i++) {
	            	System.out.println(file+", "+i+", expected: "+expectedLines[i]+"actual: "+ actualLines[i]);
	                assertEquals(expectedLines[i], actualLines[i]);
	            }
	            
	        }
	  }    
	
}


