
package ftpremote;

import java.util.Date;
import java.util.ResourceBundle;



public class Main {    
   
    public static void main(String[] args) {
          Ftp ftp = new Ftp();
          FormatDate formatDate = new FormatDate();
          
          String dateUltimate = ftp.readDate();
          Date date = formatDate.getDateToday();
          String dateToday = formatDate.dateToString(date);
          int differenceMinutes = formatDate.differenceTime(dateUltimate, dateToday);
          String expresionRegular = ftp.getMatches(String.valueOf(differenceMinutes));
          System.out.println(expresionRegular);
                  
        
//        long startTime = System.currentTimeMillis();
//        ftp.startFTP();
//        long endTime   = System.currentTimeMillis();
//        long totalTime = endTime - startTime;
//        long time = (totalTime/1000);
//        double timeReal = time/60;
//        System.out.println(String.valueOf(time)+" seconds");
       
    }
    
}
