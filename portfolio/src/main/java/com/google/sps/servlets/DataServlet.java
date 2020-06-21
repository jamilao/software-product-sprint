// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  ArrayList<String> messages = new ArrayList<>();
  ArrayList<CommentData> comments = new ArrayList<>(); 

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    UserService userService = UserServiceFactory.getUserService();
        for (Entity entity : results.asIterable()){
            String name = (String) entity.getProperty("name");
            String email = (String) entity.getProperty("email");
            String message = (String) entity.getProperty("message");
            CommentData comment = new CommentData(name,email,message);
            comments.add(comment);
        }
        String json_messages = convertToJson(comments);
        response.setContentType("application/json;");
        response.getWriter().println(json_messages);
  }
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String name = request.getParameter("full-name");
      String email = request.getParameter("email");
      String message = request.getParameter("message");

      Entity commentEntity = new Entity("Comment");
      commentEntity.setProperty("name",name);
      commentEntity.setProperty("email",email);
      commentEntity.setProperty("message",message);

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(commentEntity);

      response.sendRedirect("/index.html");
  }
  private String convertToJson(ArrayList items){
      Gson gson = new Gson();
      String json = gson.toJson(items);
      return json;
  }
  private class CommentData {
      String name;
      String email;
      String message;
      CommentData(String n, String e, String m){
        name = n;
        email = e;
        message = m;
      }
  }
}
