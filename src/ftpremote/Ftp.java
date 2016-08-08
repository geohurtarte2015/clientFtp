package ftpremote;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;


public class Ftp {
            
private final ResourceBundle rb = ResourceBundle.getBundle("properties.configuration"); 
private final String path = rb.getString("pathindex");
private final String serverAddress = rb.getString("gtip");
private final String userId = rb.getString("gtuser");
private final String password = rb.getString("gtpassword");
private final String remoteDirectory = rb.getString("gtsource");
private final String localDirectory = rb.getString("gtlocal");
private final String dateBegin = rb.getString("date_begin");
private final String staticInterval = rb.getString("interval");
private final String minutesIntervval = rb.getString("minutesinterval");
private FTPFile[] ftpFiles; 
private OutputStream output;

private List listFiles = new ArrayList();

private ArrayList<String> listObject = new ArrayList<String>();

private FTPClient connFTP;
private FTPClient ftp;
private boolean ftpDebug;


public boolean startFTP() {

        //new ftp client
        ftp = new FTPClient();

        try {
            //try to connect
            ftp.connect(serverAddress);
        } catch (IOException ex) {
            System.out.println("Error connection:"+ex);
        }
        try {
            //login to server
            if (!ftp.login(userId, password)) {
                ftp.logout();
                return false;
            }
        } catch (IOException ex) {
            System.out.println("Error validate user and password:"+ex);
        }
        int reply = ftp.getReplyCode();
        //FTPReply stores a set of constants for FTP reply codes. 
        if (!FTPReply.isPositiveCompletion(reply)) {
            try {
                ftp.disconnect();
            } catch (IOException ex) {
                System.out.println("Error connection ending FTP:"+ex);
            }
            return false;
        }

        //enter passive mode
        ftp.enterLocalPassiveMode();
        try {
            //get system name
            System.out.println("Remote system is " + ftp.getSystemType());
        } catch (IOException ex) {
            System.out.println("Error System Remote:"+ex);
        }
        
        try {
            //change current directory
            ftp.changeWorkingDirectory(remoteDirectory);
        } catch (IOException ex) {
            System.out.println("Error Change Workin Directory Remote:"+ex);
        }
        
        
        try {
            System.out.println("Current directory is " + ftp.printWorkingDirectory());
        } catch (IOException ex) {
            System.out.println("Error Working Directory:"+ex);
        }

        //get matchesPattern
        final String matchesPatternTime = this.getMatches(minutesIntervval.toUpperCase());
   

        try {
            ftp.setFileType(FTP.ASCII_FILE_TYPE);
        } catch (IOException ex) {
            System.out.println("Error ASCII Type File:"+ex);
        }

        for (String retval : matchesPatternTime.split(";")) {
            try {

                    this.listFiles = Arrays.asList(ftp.listNames("*" + retval + "*"));
                    int index;
                    for (index = 0; index < listFiles.size(); index++) {
                        
                        System.out.println(listFiles.get(index));
                        listObject.add((String) listFiles.get(index));

                    }
            } catch (NullPointerException ex) {
                System.out.println("Null Result: "+ex);
            } catch (IOException ex) {
                System.out.println("Error IO: "+ex);
            }

        }

        this.getFiles();

        try {
            ftp.logout();
        } catch (IOException ex) {
            System.out.println("Error Close Session FTP:"+ex);
        }

        try {
            ftp.disconnect();
        } catch (IOException ex) {
            System.out.println("Error Disconnect FTP:"+ex);
        }

        return true;
    }
    
public void recordDate(String path,String arrayData[]){
      
        FileWriter fichero = null;
        PrintWriter pw = null;
        try
        {
            fichero = new FileWriter(path,false);
            pw = new PrintWriter(fichero);
            
            
            for(String write : arrayData){
                pw.println(write);
            }    
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
           try {     
           if (null != fichero)
              fichero.close();
           } catch (Exception e2) {
              e2.printStackTrace();
           }
        }

    }  
    
public String readDate(){
        FormatDate formatDate = new FormatDate();
        String dateRead=null;
        String fileDate=null;
        BufferedReader buffReader=null;
        ResourceBundle rb = ResourceBundle.getBundle("properties.configuration");
        String path = rb.getString("pathindex");
        try {
        
            buffReader = new BufferedReader (new FileReader(path));            
        
            try {
                while ((dateRead = buffReader.readLine()) != null) {
                    fileDate=dateRead;
                    System.out.println("Last reading download " +dateRead);
                }
            } catch (IOException ex) {
                System.out.println("Advertencia: File not exist "+ex);  
                Date today = formatDate.getDateToday();
                String[] newDateToday = {formatDate.dateToString(today)};
                this.recordDate(path, newDateToday);
                fileDate=formatDate.dateToString(today);
                return fileDate;
            }
        
            return fileDate;
        } catch (FileNotFoundException ex) {
            System.out.println("Error: "+ex);
            return fileDate;
        }
     
    }
    
public String getLastDownloadPattern(){
        FormatDate formatDate = new FormatDate();
        String pattern=null;
        String fileDate=this.readDate();
         if(fileDate==null){
                String[] newDateToday = {formatDate.dateToString(formatDate.getDateToday())};
                this.recordDate(path, newDateToday);
                System.out.println("New file is created");
                pattern=formatDate.getFormat(formatDate.getDateToday());
                System.out.println("Pattern match "+pattern );
            }else{
                pattern=formatDate.getFormat(formatDate.stringToDate(fileDate));
                System.out.println("Pattern match "+pattern );
            }
        
        return pattern;
    }
    
public String saveDatePattern(){
        String pattern=null;
        FormatDate formatDate = new FormatDate();
        Date today = formatDate.getDateToday();
        String[] newDateToday = {formatDate.dateToString(today)};
        this.recordDate(path, newDateToday);
        pattern=formatDate.getFormat(today);        
        return pattern;
    }
    
public String getMatches(String minutes){
        
        String patternUltimateDate = null;
        FormatDate formatDate = new FormatDate();
        String match = null;
        int min = Integer.parseInt(minutes);

        Calendar cal = Calendar.getInstance();
        Date tempDate;

        if (staticInterval.equals("T")) {
            Date date = formatDate.stringToDate(dateBegin);
            cal.setTime(date);

            //Desplegamos la fecha de hoy
            tempDate = date;
        } else {

            //read ultimate date download         
            patternUltimateDate = this.getLastDownloadPattern();

            //save date and time present into file and return format present string date
            this.saveDatePattern();

            cal.setTime(new Date());
            //Desplegamos la fecha de hoy
            tempDate = formatDate.getDateToday();
        }

        String newMatch = null;
        String repeat = "";
        for (int i = 0; i <= min; i++) {
            tempDate = cal.getTime();
            match = formatDate.getFormat(tempDate);

            if (newMatch != null) {
                if (!repeat.equals(match)) {
                    newMatch = newMatch + ";" + match;
                    repeat = match;
                }
            } else {
                newMatch = match;
                repeat = match;
            }
            //Le cambiamos la hora y minutos  
            cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) - 1);
        }

        if (staticInterval.equals("F")) {
            newMatch = newMatch + ";" +  patternUltimateDate;
        }

        System.out.println(newMatch);
        return newMatch;

    }

public boolean getFiles() {
    
	File localFile;
        FileOutputStream outFile;
        int index;
        ArrayList<String> tmpListFiles = new ArrayList();
        boolean resultGettingFile;

        if (ftpDebug) {
            System.out.println("[inf]Getting files");
        }
        try {
            if (ftpDebug) {
                System.out.println("[inf]" + listObject.size() + " Posible files");
            }

            for (index = 0; index < listObject.size(); index++) {

                if (ftpDebug) {
                    System.out.print("[inf]file (" + index + " of " + listObject.size() + ") :" + listObject.get(index));
                }

                localFile = new File(this.localDirectory + listObject.get(index));
                outFile = new FileOutputStream(localFile);
                resultGettingFile = ftp.retrieveFile(listObject.get(index), outFile);

                if (resultGettingFile) {
                    tmpListFiles.add(listObject.get(index));
                }

                if (ftpDebug) {
                    System.out.println(" - Result:" + resultGettingFile);
                }
                outFile.close();
            }
            if (ftpDebug) {
                System.out.println("[inf]Saved " + listObject.size() + " files of " + listObject.size() + " possible");
            }

            if (!tmpListFiles.isEmpty()) {
                if (tmpListFiles.size() != listObject.size()) {
                    listObject = tmpListFiles;
                }
            } else {
                listObject = new ArrayList();
            }
            return true;
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
    
public boolean setGetAscii() {
		try {
			connFTP.setFileType(FTP.ASCII_FILE_TYPE);
			return true;
		} catch (IOException e) {
			System.err.println(e.getMessage());
			return false;
		}
	}

public String differenceTime (String date){
    
        FormatDate formatDate = new FormatDate();
        
        final long MILLSECS_PER_DAY = 6*60 * 60 * 60 * 1000; //Milisegundos al día 
       
        java.util.Date hoy = new Date(); //Fecha de hoy    
        
        Date ultimateDate = formatDate.stringToDate(date);
        
        DateFormat dayCalendar = new SimpleDateFormat("dd");
        DateFormat monthCalendar = new SimpleDateFormat("MM");
        DateFormat yearCalendar = new SimpleDateFormat("yyyy");
        DateFormat hourCalendar = new SimpleDateFormat("HH");
        DateFormat minuteCalendar = new SimpleDateFormat("mm");
    
        
        String hourNow = hourCalendar.format(ultimateDate);
        String dayNow = dayCalendar.format(ultimateDate);
        String monthNow = monthCalendar.format(ultimateDate);
        String yearNow = yearCalendar.format(ultimateDate);
        
        int año = Integer.valueOf(yearNow);
        int mes = Integer.valueOf(monthNow); 
        int dia = Integer.valueOf(dayNow);
        int hora = Integer.valueOf(hourNow);
        
        Calendar calendar = new GregorianCalendar(año, mes-1, dia);
        
        java.sql.Date fecha = new java.sql.Date(calendar.getTimeInMillis());

        long diferencia = ( hoy.getTime() - fecha.getTime() )/MILLSECS_PER_DAY; 
        
        System.out.println("Minutos diferencia: " + diferencia); 
        
        return  (String.valueOf(diferencia));
        
}
    
}