/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zoho;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import zohoobjects.Log;
import zohoobjects.Project;
import zohoobjects.Task;

/**
 *
 * @author rajesh
 */
public class ZohoService {
    private String baseUrl = "https://projectsapi.zoho.com/restapi";
    
    public ArrayList<String> getPortals(String token) {
        ArrayList<String> portals = new ArrayList<>();
        try {
            URL obj = new URL(this.baseUrl+"/portals/?authtoken="+token);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/40.0");
            int responseCode = con.getResponseCode();
            System.out.println("Response code "+responseCode);
            BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONObject res = new JSONObject(response.toString());
            JSONArray arr = res.getJSONArray("portals");
            for(int i=0;i<arr.length();i++) {
                portals.add(arr.getJSONObject(i).getString("name")+"::"
                        +arr.getJSONObject(i).getString("id_string"));
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(ZohoService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ZohoService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return portals;
    }
    
    public ArrayList<Project> getProjects(String token,String portal) {
        ArrayList<Project> projects = new ArrayList<>();
        try {
            URL obj = new URL(this.baseUrl+"/portal/"+portal+"/projects/?authtoken="+token);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/40.0");
            int responseCode = con.getResponseCode();
            System.out.println("Response code "+responseCode);
            BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONObject res = new JSONObject(response.toString());
            JSONArray arr = res.getJSONArray("projects");
            for(int i=0;i<arr.length();i++) {
                Project p = new Project();
                p.name = arr.getJSONObject(i).getString("name");
                p.id = arr.getJSONObject(i).getString("id_string");
                p.taskLink = arr.getJSONObject(i).getJSONObject("link").getJSONObject("task").getString("url");
                projects.add(p);
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(ZohoService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ZohoService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return projects;
    }
    
    public ArrayList<Task> getTasks(String token,String portal,String project) {
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            URL obj = new URL(this.baseUrl+"/portal/"+portal+"/projects/"+project+"/tasks/?authtoken="+token);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/40.0");
            int responseCode = con.getResponseCode();
            System.out.println("Response code "+responseCode);
            BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject res = new JSONObject(response.toString());
            JSONArray arr = res.getJSONArray("tasks");
            for(int i=0;i<arr.length();i++) {
                Task p = new Task();
                p.name = arr.getJSONObject(i).getString("name");
                p.id = arr.getJSONObject(i).getString("id_string");
                p.work = arr.getJSONObject(i).getString("work");
                p.completed = arr.getJSONObject(i).getBoolean("completed");
                p.timesheetLink = (arr.getJSONObject(i)).getJSONObject("link").getJSONObject("timesheet").getString("url");
                tasks.add(p);
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(ZohoService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ZohoService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tasks;
    }
    
    public ArrayList<Log> getLogs(String token,String url) {
        ArrayList<Log> logs = new ArrayList<>();
        try {
            URL obj = new URL(url+"?authtoken="+token);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/40.0");
            int responseCode = con.getResponseCode();
            System.out.println("Response code "+responseCode);
            BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONObject res = new JSONObject(response.toString());
            JSONArray arr = res.getJSONObject("timelogs").getJSONArray("tasklogs");
            for(int i=0;i<arr.length();i++) {
                Log p = new Log();
                p.approval_status = arr.getJSONObject(i).getString("approval_status");
                p.id = Long.toString(arr.getJSONObject(i).getLong("id"));
                p.bill_status = arr.getJSONObject(i).getString("bill_status");
                p.hourDisplay = arr.getJSONObject(i).getString("hours_display");
                p.log_date = arr.getJSONObject(i).getString("log_date");
                p.min = arr.getJSONObject(i).getInt("total_minutes");
                p.notes = arr.getJSONObject(i).getString("notes");
                p.selfUrl = arr.getJSONObject(i).getJSONObject("link").getJSONObject("self").getString("url");
                logs.add(p);
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(ZohoService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ZohoService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return logs;
    }
    
    /**
     * Add a new log
     * @param token User token
     * @param url url of the log
     * @param note Notes
     * @param date Date
     * @param bill Bill status
     * @param hour Total hour
     * @return response code or -1 if error
     */
    public int addLog(String token, String url, String note, String date,
            String bill, String hour) {
        int res = -1;
        try {
            String urlParam = "date="+date+"&bill_status="+bill+"&hours="+hour;
            urlParam += "&notes="+note+"&authtoken="+token;
            byte[] postData = urlParam.getBytes( StandardCharsets.UTF_8 );
            int postDataLength = postData.length;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setDoOutput( true );
            con.setInstanceFollowRedirects( false );
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Mozilla/40.0");
            con.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
            con.setRequestProperty( "charset", "utf-8");
            con.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
            con.setUseCaches( false );
            
            try( DataOutputStream wr = new DataOutputStream( con.getOutputStream())) {
                wr.write( postData );
             }
            int responseCode = con.getResponseCode();
            System.out.println("Response code "+responseCode);
            BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            res = responseCode;
        } catch (MalformedURLException ex) {
            Logger.getLogger(ZohoService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ZohoService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }
}
