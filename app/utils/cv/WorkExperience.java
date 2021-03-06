package utils.cv;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by:
 * Victor Garcia Zarco - victor.gzarco@gmail.com
 * Mikel Garcia Najera - mikel.garcia.najera@gmail.com
 * Carlos Fernandez-Lancha Moreta - carlos.fernandez.lancha@gmail.com
 * Victor Rodriguez Latorre - viypam@gmail.com
 * Stalin Yajamin Quisilema - rimid22021991@gmail.com
 */
public class WorkExperience {

    private String title;
    private String company;
    private String startDate;
    private String endDate;
    private String description;

    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM-yyyy");

    public WorkExperience(String title, String company,
                          int startDateMonth, int startDateYear, int endDateMonth, int endDateYear,
                          String description){

        this.title = title;
        this.company = company;
        this.startDate = dateFormatter.format(new Date(startDateYear-1900, startDateMonth, 0));
        if(endDateMonth == 0 && endDateYear == 0){
            this.endDate = "Actualmente";
        }else{
            this.endDate = dateFormatter.format(new Date(endDateYear-1900, endDateMonth, 0));
        }
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getCompany() {
        return company;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getDescription() {
        return description;
    }
}
