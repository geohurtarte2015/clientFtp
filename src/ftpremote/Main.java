
package ftpremote;

import java.io.IOException;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;



public class Main {    
   
    public static void main(String[] args) {
          Ftp ftp = new Ftp();
          FormatDate formatDate = new FormatDate();
          
        
        long startTime = System.currentTimeMillis();
        try {
            ftp.startFTP();
        } catch (IOException ex) {
            System.out.println("Error: "+ ex);
        }
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        long time = (totalTime/1000);
        double timeReal = time/60;
        System.out.println(String.valueOf(time)+" seconds");
       
    }
    
}
