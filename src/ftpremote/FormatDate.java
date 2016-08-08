
package ftpremote;

import static java.lang.String.format;
import static java.lang.String.format;
import static java.lang.String.format;
import static java.lang.String.format;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public class FormatDate {
    
     private final ResourceBundle rb = ResourceBundle.getBundle("properties.configuration"); 
     private final String dateBegin = rb.getString("date_begin");
     private final String minutesinterval = rb.getString("minutesinterval");
     private final String staticInterval = rb.getString("interval");


    public static void main(String[] args) {
       

        FormatDate formatDate = new FormatDate();

        //Establecemos la fecha que deseamos en un Calendario
        Calendar cal = Calendar.getInstance();
        
        cal.setTime(new Date());

        //Desplegamos la fecha de hoy
        Date tempDate = formatDate.getDateToday();

        System.out.println("Fecha actual: " + tempDate);
        System.out.println("Formato: " + formatDate.getFormat(tempDate));

        //Le cambiamos la hora y minutos
        cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) + 15);
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) - 5);
        tempDate = cal.getTime();

        System.out.println("Hora modificada: " + tempDate);
        System.out.println("Formato: " + formatDate.getFormat(tempDate));

        //Le cambiamos el mes
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
        System.out.println("Fecha modificada: " + cal.getTime());
        System.out.println("Formato: " + formatDate.getFormat(tempDate));
        //De la misma forma se puede cambiar año, semana, etc
    }

    //get string date yymmdd_hhmm
    public String getFormat(Date date) {

        DateFormat dayCalendar = new SimpleDateFormat("dd");
        DateFormat monthCalendar = new SimpleDateFormat("MM");
        DateFormat yearCalendar = new SimpleDateFormat("yyyy");
        DateFormat hourCalendar = new SimpleDateFormat("HH");
        DateFormat minuteCalendar = new SimpleDateFormat("mm");

        String minuteNow = minuteCalendar.format(date);
        String hourNow = hourCalendar.format(date);
        String dayNow = dayCalendar.format(date);
        String monthNow = monthCalendar.format(date);
        String yearNow = yearCalendar.format(date);
        String formato = yearNow + monthNow + dayNow + "_" + hourNow + minuteNow;

        return formato;
    }

    //convert "string date" to "Date"
    public Date stringToDate(String dateTxt) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date dateNow = null;
        try {
            dateNow = dateFormat.parse(dateTxt);
        } catch (ParseException ex) {
            System.out.println("Error: " + ex);
            return dateNow;
        }
        return dateNow;
    }

    //convert "Date" to "string date"
    public String dateToString(Date date) {
        
        String dateNow = null;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        dateNow = dateFormat.format(date);
        return dateNow;
        
    }
    
    //get date today;
    public Date getDateToday() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Date dateNow = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String newDate = dateFormat.format(cal.getTime());

        try {
            dateNow = dateFormat.parse(newDate);
        } catch (ParseException ex) {
            System.out.println("Error formato fecha: " + ex);
            return dateNow;
        }
        //Desplegamos la fecha
        return dateNow;

    }
    
    
    public int differenceTime(String initDate, String finalDate){
    //pruebas 31-12-2008 09:45 && 09-04-2009 12:27 //hay 3 meses 9 dias 2 horas 41 minutos
    
        String fechaInicial = initDate;
        String fechaFinal = finalDate;
    
        
        java.util.GregorianCalendar jCal = new java.util.GregorianCalendar();
        java.util.GregorianCalendar jCal2 = new java.util.GregorianCalendar();

        jCal.set(Integer.parseInt(fechaInicial.substring(6,10)), Integer.parseInt(fechaInicial.substring(3,5))-1, Integer.parseInt(fechaInicial.substring(0,2)), Integer.parseInt(fechaInicial.substring(11,13)),Integer.parseInt(fechaInicial.substring(14,16)), Integer.parseInt(fechaInicial.substring(17,19)));
        jCal2.set(Integer.parseInt(fechaFinal.substring(6,10)), Integer.parseInt(fechaFinal.substring(3,5))-1, Integer.parseInt(fechaFinal.substring(0,2)), Integer.parseInt(fechaFinal.substring(11,13)),Integer.parseInt(fechaFinal.substring(14,16)), Integer.parseInt(fechaFinal.substring(17,19)));
      
        long diferencia = jCal2.getTime().getTime()-jCal.getTime().getTime();
        double minutos = diferencia / (1000 * 60);
        long horas = (long) (minutos / 60);
        long minuto = (long) (minutos%60);
        long segundos = (long) diferencia % 1000;
        long dias = horas/24;
        //Calcular meses...
        //Crear vector para almacenar los diferentes dias maximos segun correponda
        String[] mesesAnio = new String[12];
        mesesAnio[0] = "31";
        //validacion de los años bisiestos
        if (jCal.isLeapYear(jCal.YEAR)){mesesAnio[1] = "29";}else{mesesAnio[1] = "28";}
        mesesAnio[2] = "31";
        mesesAnio[3] = "30";
        mesesAnio[4] = "31";
        mesesAnio[5] = "30";
        mesesAnio[6] = "31";
        mesesAnio[7] = "31";
        mesesAnio[8] = "30";
        mesesAnio[9] = "31";
        mesesAnio[10] = "30";
        mesesAnio[11] = "31";
        int diasRestantes = (int) dias;
        //variable almacenará el total de meses que hay en esos dias
        int totalMeses = 0;
        int mesActual = jCal.MONTH;
        //Restar los dias de cada mes desde la fecha de ingreso hasta que ya no queden sufcientes dias para 
        // completar un mes.
        for (int i=0; i<=11; i++ ){
            //Validar año, si sumando 1 al mes actual supera el fin de año, 
            // setea la variable a principio de año 
            if ((mesActual+1)>=12){
                mesActual = i;
            }
            //Validar que el numero de dias resultantes de la resta de las 2 fechas, menos los dias
            //del mes correspondiente sea mayor a cero, de ser asi totalMeses aumenta,continuar hasta 
            //que ya nos se cumpla.
            if ((diasRestantes -Integer.parseInt(mesesAnio[mesActual]))>=0){
                totalMeses ++;
                diasRestantes = diasRestantes- Integer.parseInt(mesesAnio[mesActual]);
                mesActual ++;
            }else{
                break;
            }
        }
        //Resto de horas despues de sacar los dias
        horas = horas % 24;
        String salida ="";
        if (totalMeses > 0){
            if (totalMeses > 1)
                salida = salida+  String.valueOf(totalMeses)+" Meses,  ";
            else
                salida = salida+  String.valueOf(totalMeses)+" Mes, ";
        }
        if (diasRestantes > 0){
            if (diasRestantes > 1)
                salida = salida+  String.valueOf(diasRestantes)+" Dias, ";
            else
                salida = salida+  String.valueOf(diasRestantes)+" Dia, ";
        }
       
        int minutosMeses=((totalMeses*30)*24)*60; //dias promedio
        int minutosDias = ((diasRestantes * 24)*60);
        long minutosHoras = (horas*60);
        
        int totalMinutos = (int) (minutosDias+minutosHoras+minuto+minutosMeses);
        
         salida =String.valueOf(totalMinutos);
        return Integer.valueOf(salida);
        }
    
    
}
