package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.itextpdf.text.DocumentException;
import models.datasource.ConfDataSource;
import models.datasource.SingletonDataSource;
import models.entities.User;
import models.entities.orientation.*;
import play.libs.Json;
import play.mvc.Result;
import utils.Files;
import utils.pdf.PresentationLetter;
import utils.pdf.cv_templates.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import play.Logger;

import static play.mvc.Controller.session;
import static play.mvc.Http.Context.Implicit.request;
import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;
import utils.Utils;


/**
 * Created by:
 * Victor Garcia Zarco - victor.gzarco@gmail.com
 * Mikel Garcia Najera - mikel.garcia.najera@gmail.com
 * Carlos Fernandez-Lancha Moreta - carlos.fernandez.lancha@gmail.com
 * Victor Rodriguez Latorre - viypam@gmail.com
 * Stalin Yajamin Quisilema - rimid22021991@gmail.com
 */
public class GenerateDocumentsController {


    public static Result step2(){
        return ok(views.html.complete_user_profile.complete_user_profile_2.render());
    }

    public static Result step3(){
        return ok(views.html.complete_user_profile.complete_user_profile_3.render());
    }

    public static Result cv1(){
        User user = SingletonDataSource.getInstance().getUserByEmail(session().get("email"));
        return ok(views.html.complete_cv.complete_cv_1.render(user));
    }

    public static Result submitCv1(){
        JsonNode request = request().body().asJson();
        User user = SingletonDataSource.getInstance().getUserByEmail(session().get("email"));

        if(user != null){
            String[] personalInformation = new Gson().fromJson(request.toString(), new TypeToken<String[]>(){}.getType());

            //user.name = personalInformation[0];
            //user.surnames = personalInformation[1];
            user.birthDate = Utils.changeFormatDate(personalInformation[2]);
            user.residenceCity = personalInformation[3];
            user.residenceAddress = personalInformation[4];
            user.residenceNumber = personalInformation[5];
            user.residenceZipCode = personalInformation[6];
            //user.email = personalInformation[7];
            user.phoneNumber = personalInformation[8];

            if(!personalInformation[9].equals("")){
                //Add driving license
                user.drivingLicense = personalInformation[9];
            }else{
                user.drivingLicense = null;
            }

            if(!personalInformation[10].equals("")){
                user.certificateOfDisability = personalInformation[10];
            }else{
                user.certificateOfDisability = null;
            }

            SingletonDataSource.getInstance().updateAllUserData(user);
        }
        return redirect("/orientation/gettools/cv2");
    }

    public static Result cv2(){
        User user = SingletonDataSource.getUserByEmail(session().get("email"));
        return ok(views.html.complete_cv.complete_cv_2.render(user));
    }

    public static Result submitCv2(){
        JsonNode request = request().body().asJson();
        User user = SingletonDataSource.getInstance().getUserByEmail(session().get("email"));

        if(user != null){
            String[] result = new Gson().fromJson(request.toString(), new TypeToken<String[]>(){}.getType());

            user.educationLevel = result[0];

            if(result[1].equals("EmptyExperience")){
                user.currentSituation.professionalExperienceList = new ArrayList<>();
            }else {
                String[][] professionalExperience = new Gson().fromJson(result[1], new TypeToken<String[][]>() {
                }.getType());
                List<ProfessionalExperience> auxProfessionalExperience = new ArrayList<>();

                for (int i = 0; i < professionalExperience.length; i++) {
                    String expID =  UUID.randomUUID().toString();
                    auxProfessionalExperience.add(new ProfessionalExperience(professionalExperience[i][0], professionalExperience[i][1], professionalExperience[i][2], professionalExperience[i][3],expID));
                }

                user.currentSituation.professionalExperienceList = auxProfessionalExperience;
            }
            List<String> interests = new Gson().fromJson(result[2].toString(), new TypeToken<List<String>>() {
            }.getType());

            user.interests = interests;

            SingletonDataSource.getInstance().updateAllUserData(user);
        }
        return redirect("/orientation/gettools/cv3");
    }

    public static Result cv3(){
        User user = SingletonDataSource.getInstance().getUserByEmail(session().get("email"));
        return ok(views.html.complete_cv.complete_cv_3.render(user));
    }

    public static Result submitCv3(){
        JsonNode request = request().body().asJson();
        User user = SingletonDataSource.getInstance().getUserByEmail(session().get("email"));

        if(user != null){
            String[] result = new Gson().fromJson(request.toString(), new TypeToken<String[]>(){}.getType());

            String[][] courses = new Gson().fromJson(result[0], new TypeToken<String[][]>(){}.getType());
            if(courses.length > 0){
                List<Course> auxCourses = new ArrayList<>();
                for(int i=0; i<courses.length; i++){
                    auxCourses.add(new Course(courses[i][0], courses[i][1], courses[i][2]));
                }
                user.courses = auxCourses;
            }

            String[][] languages = new Gson().fromJson(result[1], new TypeToken<String[][]>(){}.getType());
            if(languages.length>0){
                List<Language> auxLanguages = new ArrayList<>();
                for(int i=0; i<languages.length; i++){
                    auxLanguages.add(new Language(languages[i][0], languages[i][1]));
                }
                user.languages = auxLanguages;
            }

            String[][] software = new Gson().fromJson(result[2], new TypeToken<String[][]>(){}.getType());
            if(software.length>0){
                List<Software> auxSoftware = new ArrayList<>();
                for(int i=0; i<software.length; i++){
                    auxSoftware.add(new Software(software[i][0], software[i][1]));
                }
                user.softwareList = auxSoftware;
            }

            SingletonDataSource.getInstance().updateAllUserData(user);
        }

        return redirect("/orientation/gettools/cv4");
    }

    public static Result cv4(){
        User user = SingletonDataSource.getInstance().getUserByEmail(session().get("email"));
        return ok(views.html.complete_cv.complete_cv_4.render(user));
    }

    public static Result submitCv4(){
        User user = SingletonDataSource.getInstance().getUserByEmail(session().get("email"));

        if(user != null){
            SingletonDataSource.getInstance().updateAllUserData(user);
        } else {
            return badRequest("No se ha podido generar el CV porque no existe el usuario");
        }

        return redirect("/");
    }

    // Modificar el Template que sea, hay que crearlos
    public static Result previewCV1()throws DocumentException, IOException{
        User user = SingletonDataSource.getInstance().getUserByEmail(session().get("email"));

        if(user == null){
            return badRequest("No se ha podido generar el CV porque no existe el usuario");
        }
        String route = Files.newPathForNewFile("public/pdf", "pdf");

        Template1 template = new Template1();
        try {
            template.createPdf(route, user, user.personalCharacteristics, user.skill);
            ConfDataSource.addNewGeneratedDoc();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return redirect(routes.Assets.at(route.substring(7)));
    }

    public static Result previewCV2()throws DocumentException, IOException{
        User user = SingletonDataSource.getInstance().getUserByEmail(session().get("email"));

        if(user == null){
            return badRequest("No se ha podido generar el CV porque no existe el usuario");
        }
        String route = Files.newPathForNewFile("public/pdf", "pdf");

        Template2 template = new Template2();
        try {
            template.createPdf(route, user,user.personalCharacteristics, user.skill);
            ConfDataSource.addNewGeneratedDoc();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return redirect(routes.Assets.at(route.substring(7)));
    }

    public static Result previewCV3() throws DocumentException, IOException {
        User user = SingletonDataSource.getInstance().getUserByEmail(session().get("email"));

        if(user == null){
            return badRequest("No se ha podido generar el CV porque no existe el usuario");
        }
        String route = Files.newPathForNewFile("public/pdf", "pdf");

        Template3 template = new Template3();
        try {
            template.createPdf(route, user, user.personalCharacteristics, user.skill);
            ConfDataSource.addNewGeneratedDoc();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return redirect(routes.Assets.at(route.substring(7)));
    }

    public static Result previewCV4() throws DocumentException, IOException{
        User user = SingletonDataSource.getInstance().getUserByEmail(session().get("email"));

        if(user == null){
            return badRequest("No se ha podido generar el CV porque no existe el usuario");
        }
        String route = Files.newPathForNewFile("public/pdf", "pdf");

        Template4 template = new Template4();
        try {
            template.createPdf(route, user, user.personalCharacteristics, user.skill);
            ConfDataSource.addNewGeneratedDoc();    //Counter of all generated docs
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return redirect(routes.Assets.at(route.substring(7)));
    }

    public static Result lp1(){
        User user = SingletonDataSource.getInstance().getUserByEmail(session().get("email"));
        return ok(views.html.complete_letter_presentation.letter_presentation_1.render(user));
    }

    public static Result submitLp1(){
        JsonNode request = request().body().asJson();
        User user = SingletonDataSource.getInstance().getUserByEmail(session().get("email"));

        if(user != null){
            String[] personalInformation = new Gson().fromJson(request.toString(), new TypeToken<String[]>(){}.getType());

            //user.name = personalInformation[0];
            //user.surnames = personalInformation[1];
            user.birthDate = personalInformation[2];
            user.residenceCity = personalInformation[3];
            user.residenceAddress = personalInformation[4];
            user.residenceNumber = personalInformation[5];
            user.residenceZipCode = personalInformation[6];
            //user.email = personalInformation[7];
            user.phoneNumber = personalInformation[8];

            if(!personalInformation[9].equals("")){
                //Add academic experience
                String[] academicExp = new Gson().fromJson(personalInformation[9].toString(), new TypeToken<String[]>(){}.getType());
                user.studyTitle = academicExp[0];
                user.studyLocation = academicExp[1];
            }else{
                user.studyTitle = null;
                user.studyLocation = null;
            }

            List<String> personalCharacteristics = new Gson().fromJson(personalInformation[10].toString(), new TypeToken<List<String>>() {
            }.getType());

            user.personalCharacteristics = personalCharacteristics;

            SingletonDataSource.getInstance().updateAllUserData(user);
        }
        return redirect("/orientation/gettools/lp2");
    }

    public static Result lp2(){
        return ok(views.html.complete_letter_presentation.letter_presentation_2.render());
    }

    public static Result submitLp2(){
        JsonNode request = request().body().asJson();

        String[] result = new Gson().fromJson(request.toString(), new TypeToken<String[]>() {}.getType());

        session("lp2_company_name", result[0].toString());
        session("lp2_job_name", result[1].toString());
        session("lp2_attach_cv", result[2].toString());
        session("lp2_attach_portfolio", result[3]);
        session("lp2_attach_lr", result[4]);
        session("lp2_attach_certificates", result[5]);

        return ok();
    }

    public static Result lp3(){
        User user = SingletonDataSource.getInstance().getUserByEmail(session().get("email"));
        return ok(views.html.complete_letter_presentation.letter_presentation_3.render(user, null));
    }

    public static Result submitLp3(){
        User user = SingletonDataSource.getInstance().getUserByEmail(session().get("email"));

        JsonNode request = request().body().asJson();

        if(user != null) {
            String[][] skills = new Gson().fromJson(request.toString(), new TypeToken<String[][]>() {
            }.getType());
            if(user.skill.isEmpty()){
                for (int i = 0; i < skills.length; i++) {
                    user.skill.add(i, new Skill(skills[i][0], skills[i][1]));
                }
            }else{
                for (int i=0; i<skills.length; i++){
                    user.skill.get(i).level = skills[i][1];
                }
            }
            user.completedOrientationSteps.skills = String.valueOf(true);
            SingletonDataSource.getInstance().updateAllUserData(user);
        }
        return redirect("/orientation");
    }

    public static Result previewLP(){
        String error = "Por favor, complete el paso 2 antes de continuar.";
        User user = SingletonDataSource.getInstance().getUserByEmail(session().get("email"));

        if(session().get("lp2_attach_cv") == null || session().get("lp2_attach_portfolio") == null ||
                session().get("lp2_attach_lr") == null || session().get("lp2_attach_certificates") == null ||
                session().get("lp2_company_name") == null || session().get("lp2_job_name") == null){
            return badRequest(views.html.complete_letter_presentation.letter_presentation_3.render(user, error));
        }

        String route = Files.newPathForNewFile("public/pdf", "pdf");
        PresentationLetter template = new PresentationLetter();
        List<String> attachments = new ArrayList<>();

        if(session().get("lp2_attach_cv").equals("true")){
            attachments.add("Curriculum Vitae");
        }
        if(session().get("lp2_attach_portfolio").equals("true")){
            attachments.add("Portfolio");
        }
        if(session().get("lp2_attach_lr").equals("true")){
            attachments.add("Carta de recomendación");
        }
        if(session().get("lp2_attach_certificates").equals("true")){
            attachments.add("Certificados");
        }

        try {
            template.createPdf(route, user.name, user.surnames, user.studyTitle, user.studyLocation, session().get("lp2_company_name"), session().get("lp2_job_name"), attachments, user.personalCharacteristics, user.skill, user.email, user.phoneNumber);
            ConfDataSource.addNewGeneratedDoc();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        session().remove("lp2_company_name");
        session().remove("lp2_job_name");
        session().remove("lp2_attach_cv");
        session().remove("lp2_attach_portfolio");
        session().remove("lp2_attach_lr");
        session().remove("lp2_attach_certificates");

        return redirect(routes.Assets.at(route.substring(7)));
    }
    public static Result addExperienceGenerate(){
        JsonNode request = request().body().asJson();
        User user = SingletonDataSource.getInstance().getUserByEmail(session().get("email"));

        if(user == null){
            return redirect("/");
        }


        String[] experience = new Gson().fromJson(request.toString(), new TypeToken<String[]>() {}.getType());
        List<ProfessionalExperience> currentExperienceCopy = new ArrayList<ProfessionalExperience>();

        //for(int j=0; j<user.currentSituation.professionalExperienceList.size();j++){
        if (user.currentSituation.professionalExperienceList.size() == 0){
            String experienceID;



                String expID =  UUID.randomUUID().toString();
                user.currentSituation.addProfessionalExperience(experience[0], experience[1], experience[2],experience[3],expID );
            
        }else{
            // Copiamos el array de experiencia profesional
            for (ProfessionalExperience professionalExperience : user.currentSituation.professionalExperienceList) {
                currentExperienceCopy.add(professionalExperience);
            }

                int addNewElement = 1;
                for (ProfessionalExperience professionalExperience : currentExperienceCopy) {
                    if (professionalExperience.company.toLowerCase().equals(experience[0].toLowerCase())
                            && professionalExperience.job.toLowerCase().equals(experience[1].toLowerCase())
                            /*&& professionalExperience.experienceYears.equals(experience[i][2])*/) {
                        //Logger.debug("Salgo del for porque ya existe");
                        addNewElement = 0;
                        break;
                    }

                }
                if (addNewElement == 1){

                    for(ProfessionalExperience professionalExperience : currentExperienceCopy){
                        if (professionalExperience.company.equals("No tengo experiencia")){
                            //  Logger.debug("Borro");
                            user.currentSituation.clearProfessionalExperience();

                        }
                    }
                    String expID =  UUID.randomUUID().toString();
                    // Logger.debug("Añado la nueva experiencia");
                    user.currentSituation.addProfessionalExperience(experience[0], experience[1], experience[2], experience[3], expID);
                }


        }
        user.completedOrientationSteps.currentSituation = String.valueOf(true);
        SingletonDataSource.getInstance().updateAllUserData(user);

        return ok(Json.toJson(experience));

    }
    public static Result addCheckExperienceGenerate(){
        JsonNode request = request().body().asJson();
        User user = SingletonDataSource.getInstance().getUserByEmail(session().get("email"));

        if(user == null){
            return redirect("/orientation/gettools/cv2");
        }

        String checkExperience = new Gson().fromJson(request.toString(), new TypeToken<String>(){}.getType());

        if(user.currentSituation.professionalExperienceList.size() == 0){
            String expID =  UUID.randomUUID().toString();
            user.currentSituation.addProfessionalExperience(checkExperience, "", "", "",expID);
        }else {
            for (int i = 0; i < user.currentSituation.professionalExperienceList.size(); i++) {

                if (user.currentSituation.professionalExperienceList.get(i).company.equals("No tengo experiencia")) {
                    //Logger.info("La Base de datos  contiene el check de no tengo experiencia");

                } else {
                    //Logger.info("La Base de datos  no contiene el check de no tengo experiencia");
                    user.currentSituation.clearProfessionalExperience();
                    String expID =  UUID.randomUUID().toString();
                    user.currentSituation.addProfessionalExperience(checkExperience, "", "", "",expID);
                }
            }
        }

        user.completedOrientationSteps.currentSituation = String.valueOf(true);
        SingletonDataSource.getInstance().updateAllUserData(user);

        return ok(Json.toJson(checkExperience));

    }

    public static Result deleteExperienceCurrentSituation(){
        JsonNode request = request().body().asJson();
        User user = SingletonDataSource.getInstance().getUserByEmail(session().get("email"));

        String idExperience = new Gson().fromJson(request.toString(), new TypeToken<String>() {}.getType());
        for( int i=0; i<user.currentSituation.professionalExperienceList.size(); i++){
            if(user.currentSituation.professionalExperienceList.get(i).ID.contains(idExperience)){
                user.currentSituation.professionalExperienceList.remove(i);
                break;
            }
        }

        user.completedOrientationSteps.currentSituation = String.valueOf(true);
        SingletonDataSource.getInstance().updateAllUserData(user);

        return ok(Json.toJson(idExperience));

    }
    public static Result addNoStudiesGenerate(){
        JsonNode request = request().body().asJson();
        User user = SingletonDataSource.getInstance().getUserByEmail(session().get("email"));

        String[] studies = new Gson().fromJson(request.toString(), new TypeToken<String[]>() {}.getType());

        user.studyTitle=studies[0];
        user.studyLocation="";


        user.completedOrientationSteps.currentSituation = String.valueOf(true);
        SingletonDataSource.getInstance().updateAllUserData(user);

        return ok(Json.toJson(studies));

    }
    public static Result addStudiesGenerate(){
        JsonNode request = request().body().asJson();
        User user = SingletonDataSource.getInstance().getUserByEmail(session().get("email"));

        String[] studies = new Gson().fromJson(request.toString(), new TypeToken<String[]>() {}.getType());

        user.studyTitle=studies[0];
        user.studyLocation=studies[1];


        user.completedOrientationSteps.currentSituation = String.valueOf(true);
        SingletonDataSource.getInstance().updateAllUserData(user);

        return ok(Json.toJson(studies));

    }
}
