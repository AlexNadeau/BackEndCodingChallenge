import java.io.*;
import java.net.*;
import java.util.ArrayList;
import org.json.*;
import java.lang.reflect.Method;
import java.util.*;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Main{

public static ArrayList<Streamer> streamerData;
public static String ResultJson;
  public static void main(String[] args) throws Exception
  {
    for (int i = 1; i <= 6; i++){
      System.out.println("TEST CASE " + i);
      System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
      streamerData = new ArrayList<Streamer>();
      GetRequest("https://interview.outstem.io/tests?test_case=" + i);
      PostRequest("https://interview.outstem.io/tests?test_case=" + i);
    }
  }
  public static void PostRequest(String urlToRead) throws Exception {

        System.out.println(ResultJson);
      //"{ \"name\":\"tammy133\", \"salary\":\"5000\", \"age\":\"20\" }";
        var request = HttpRequest.newBuilder()
            .uri(URI.create(urlToRead))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(ResultJson))
            .build();

        var client = HttpClient.newHttpClient();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.statusCode());
        System.out.println(response.body());
  }
   public static void GetRequest(String urlToRead) throws Exception {
      StringBuilder result = new StringBuilder();
      URL url = new URL(urlToRead);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
        String json = reader.readLine();
        ComputeData(json);
      }
      catch (Exception e){
        System.out.println(e);
      }
  }
  public static void ComputeData(String json){
    JSONObject obj = new JSONObject(json);
    //String data = obj.getJSONObject("data");
    JSONArray array = obj.getJSONArray("data");
    System.out.println("Start: " + array);
    System.out.println("Start query: " + obj.getString("query"));
    System.out.println("**********************************************");
    String[] query = obj.getString("query").split(",");
    ResultJson = "{ \"results\":[";
    Streamer streamer;
    int n = array.length();
    for (int i = 0; i < n; i = i + 3){
      streamer = new Streamer(array.get(i).toString(), array.get(i + 1).toString(), array.get(i + 2).toString());
      streamerData.add(streamer);
    }
    String functionName;
    int numberOfParams;
    String[] args;
    String first;
    int numOfResults = 0;
    int j = 0;
    while (j < query.length){
      functionName = query[j];
      functionName = functionName.replaceAll("\\s+","");
      if (numOfResults == 0){
        first = "";
      } else{
        first = ",";
      }
      System.out.println(functionName);
        if (functionName.equals("EducatorOnline")){
          numberOfParams = 3;
          args = new String[] {query[j+1].substring(1), query[j+2].substring(1), query[j+3].substring(1)};
          EducatorOnline(args);
          System.out.println("New educator online: " + printStreamerData());
        }
        else if (functionName.equals("UpdateViews")){
          numberOfParams = 3;
          args = new String[] {query[j+1].substring(1), query[j+2].substring(1), query[j+3].substring(1)};
          UpdateViews(args);
          System.out.println("Views Updated: " + printStreamerData());
        }
        else if (functionName.equals("UpdateSubject")){
          numberOfParams = 3;
          args = new String[] {query[j+1].substring(1), query[j+2].substring(1), query[j+3].substring(1)};
          UpdateSubject(args);
          System.out.println("Subject Updated: " + printStreamerData());
        }

        else if (functionName.equals("EducatorOffline")){
          numberOfParams = 2;
          args = new String[] {query[j+1].substring(1), query[j+2].substring(1)};
          EducatorOffline(args);
          System.out.println("Educator Offline: " + printStreamerData());
        }

        else if (functionName.equals("ViewsInSubject")){
          numberOfParams = 1;
          args = new String[] {query[j+1].substring(1)};
          int viewsInSubj = ViewsInSubject(args);
          System.out.println("Total views in "+ query[j+1] + " is: " + viewsInSubj);
          ResultJson = ResultJson + first + "\"" + viewsInSubj + "\"";
          numOfResults++;
        }

        else if (functionName.equals( "TopEducatorOfSubject")){
          numberOfParams = 1;
          args = new String[] {query[j+1].substring(1)};
          String topEducator = TopEducatorOfSubject(args);
          System.out.println("Most viewers in "+ query[j+1] + " is: " + topEducator);
          if (topEducator == null){
            ResultJson = ResultJson + first  + "\"" + null + "\"";
          }
          else{
              ResultJson = ResultJson + first + "\"" + topEducator + "\"";
          }
          numOfResults++;
        }

        else {
          numberOfParams = 0;
          args = new String[] {};

          String topEducator = TopEducator(args);
          System.out.println("Most viewers are watching: " + topEducator);
          if (topEducator == null){
            ResultJson = ResultJson + first  + "\"" + null + "\"";
          }
          else{
              ResultJson = ResultJson + first + "\"" + topEducator + "\"";
          }
          numOfResults++;
        }
      j = j + numberOfParams + 1;
    }
    ResultJson = ResultJson + "]}";
  }

  public static void EducatorOnline(String[] args){
    Streamer newStreamer = new Streamer(args[0], args[1], args[2]);
    streamerData.add(newStreamer);
  }
  public static void UpdateViews(String[] args){
    String name;
    String oldSubj;
    for (int i = 0; i < streamerData.size(); i++){
      name = streamerData.get(i).nameOfStreamer;
      oldSubj = streamerData.get(i).subjectOfStream;
      if (name.equals(args[0]) && oldSubj.equals(args[2])){
        streamerData.set(i, new Streamer(name, args[1], oldSubj));
      }
    }
  }
  public static void UpdateSubject(String[] args){
    String name;
    String oldSubj;
    for (int i = 0; i < streamerData.size(); i++){
      name = streamerData.get(i).nameOfStreamer;
      oldSubj = streamerData.get(i).subjectOfStream;
      if (name.equals(args[0]) && oldSubj.equals(args[1])){
        streamerData.set(i, new Streamer(name, streamerData.get(i).viewsOfStream, args[2]));
      }
    }
  }
  public static void EducatorOffline(String[] args){
    String name;
    String subj;
    for (int i = 0; i < streamerData.size(); i++){
      name = streamerData.get(i).nameOfStreamer;
      subj = streamerData.get(i).subjectOfStream;
      if (name.equals(args[0]) && subj.equals(args[1])){
        streamerData.remove(i);
      }
    }
  }
  public static int ViewsInSubject(String[] args){
    int sum = 0;
    String subj;
    for (int i = 0; i < streamerData.size(); i++){
      subj = streamerData.get(i).subjectOfStream;
      if (subj.equals(args[0])){
        sum = sum + Integer.parseInt(streamerData.get(i).viewsOfStream);
      }
    }
    return sum;
  }
  public static String TopEducatorOfSubject(String[] args){
    String topEducator = null;
    int highestView = 0;
    int views;
    String subj;
    for (int i = 0; i < streamerData.size(); i++){
      subj = streamerData.get(i).subjectOfStream;
      if (subj.equals(args[0])){
        views = Integer.parseInt(streamerData.get(i).viewsOfStream);
        if (views > highestView){
          highestView = views;
          topEducator = streamerData.get(i).nameOfStreamer;
        }
      }
    }
    return topEducator;
  }
  public static String TopEducator(String[] args){
    HashMap<String, Integer> viewsMap = new HashMap<String, Integer>();
    HashMap<Integer, String> nameMap = new HashMap<Integer, String>();
    String topSubj;
    int mostViews = 0;
    int views;
    String currTopEducator = null;
    String currTopSubj = "";
    String subj;
    String name;
    for (int i = 0; i < streamerData.size(); i++){
      name = streamerData.get(i).nameOfStreamer;
      subj = streamerData.get(i).subjectOfStream;
      views = Integer.parseInt(streamerData.get(i).viewsOfStream);
      if (nameMap.containsKey(views)){
        nameMap.replace(views, nameMap.get(views) + "," + name);
      }
      else{
        nameMap.put(views,name);
      }
      if (viewsMap.containsKey(subj)){
        viewsMap.replace(subj, viewsMap.get(subj) + views);
      }
      else{
        viewsMap.put(subj,views);
      }
      if (views > mostViews){
        mostViews = views;
        currTopEducator = name;
        currTopSubj = subj;
      }
    }
    if (nameMap.get(mostViews) == null){
      return null;
    }
    else{
      String[] namesWithHighestViews = nameMap.get(mostViews).split(",");
      if (namesWithHighestViews.length == 1){
        return namesWithHighestViews[0];
      } else{
        int highestSubj = 0;
        for (int j = 0; j < namesWithHighestViews.length; j++){
          subj = getSubject(namesWithHighestViews[j]);
          if (viewsMap.get(subj) > highestSubj){
            highestSubj = viewsMap.get(subj);
            currTopEducator = namesWithHighestViews[j];
          }
        }
        return currTopEducator;
      }
    }
  }
  private static String getSubject(String name){
    int n = streamerData.size();
    Streamer streamer;
    for (int i = 0; i < n; i++){
      streamer = streamerData.get(i);
      if (streamer.nameOfStreamer.equals(name)){
        return streamer.subjectOfStream;
      }
    }
    return null;
  }
  private static String printStreamerData(){
    String s = "[";
    Streamer streamer;
    for (int i = 0; i < streamerData.size(); i++){
      streamer = streamerData.get(i);
      s = s + "[" + streamer.nameOfStreamer + "," + streamer.viewsOfStream + "," + streamer.subjectOfStream + "]";
    }
    return s + "]";
  }
}
