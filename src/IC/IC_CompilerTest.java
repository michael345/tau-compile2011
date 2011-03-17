package IC;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.Test;

public class IC_CompilerTest {
    
    private String[] filePrefixes = { 
            "Sieve",
            "Quicksort_bad",
            "Quicksort",
            "bad_num",
            "badid",
            "goodlex_badsyn",
            "one_line_comment",
            "unexpected_char",
            "unexpected_eof"
    };
    
    
    @Test
    public void test() throws Exception {
        
        PrintStream systemOut = System.out;
        for (String filename : filePrefixes) {
            
            OutputStream out = new ByteArrayOutputStream();
            PrintStream printOut = new PrintStream(out);

            System.setOut(printOut);
            IC.Compiler.main(new String[]{"test/" + filename + ".ic"});
            System.setOut(systemOut);

            String mainOutput = out.toString();
            printOut.close();
            
            String expectedOutput = Utils.readFileAsString("test/" + filename + ".out");
            
            String[] mainLines = mainOutput.split("\n");
            String[] expectedLines = expectedOutput.split("\n");  
            for (int i=0; i<mainLines.length; i++) {
                assertEquals("File: " + filename + ", Line: " + i ,expectedLines[i], mainLines[i]);
            }
            
        }
        
        
    }
    
}
