package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.gson.Gson;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

@WebServlet("/translate")
public class TranslationServlet extends HttpServlet {
  ArrayList<String> translations = new ArrayList<>();
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the request parameters.
    String originalText = request.getParameter("message");
    String languageCode = request.getParameter("language");
    
    // Do the translation.
    Translate translate = TranslateOptions.getDefaultInstance().getService();
    Translation translation = translate.translate(originalText, Translate.TranslateOption.targetLanguage(languageCode));
    String translatedText = translation.getTranslatedText();

    //Delete previous translations from database
    Query query = new Query("Translation");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    UserService userService = UserServiceFactory.getUserService();
    for (Entity entity : results.asIterable()){
        datastore.delete(entity.getKey());
    }

    //Save translation to database
    Entity translationEntity = new Entity("Translation");
    translationEntity.setProperty("translation",translatedText); 
    datastore.put(translationEntity);

    response.sendRedirect("/");
  }
 @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Translation");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    UserService userService = UserServiceFactory.getUserService();
        for (Entity entity : results.asIterable()){
            String translationString = (String) entity.getProperty("translation");
            translations.add(translationString);
            datastore.delete(entity.getKey());
        }
    String json_translations = convertToJson(translations);
    translations.clear();
    response.setContentType("application/json; charset=UTF-8");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(json_translations);
  }
  private String convertToJson(ArrayList items){
      Gson gson = new Gson();
      String json = gson.toJson(items);
      return json;
  }
}
