package controllers;

import play.mvc.Result;

import static play.mvc.Results.ok;

/**
 * Created by Victor on 17/03/2015.
 */
public class StashController {


    public static Result step2(){
        return ok(views.html.complete_user_profile.complete_user_profile_2.render());
    }

    public static Result step3(){
        return ok(views.html.complete_user_profile.complete_user_profile_3.render());
    }

    public static Result cv1(){return ok(views.html.complete_cv.complete_cv_1.render());}

    public static Result cv2(){return ok(views.html.complete_cv.complete_cv_2.render());}

    public static Result cv3(){return ok(views.html.complete_cv.complete_cv_3.render());}
}