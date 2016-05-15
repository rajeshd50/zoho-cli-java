/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zoho;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import zohoobjects.Log;
import zohoobjects.Project;
import zohoobjects.Task;

/**
 *
 * @author rajesh
 */
public class ZohoApp {
    private ArrayList<String> states = new ArrayList<>();
    private int currentState;
    private Scanner input = new Scanner(System.in);
    
    private class ZohoConfig {
        public String auth_token = "";
        public String portal_id = "";
        public String log_id = "";
        public String currentPortal = "";
        public Project currentProject;
        public Task currentTask;
        public Log currentLog;
        
        public ArrayList<String> portals = new ArrayList<>();
        public ArrayList<Project> projects = new ArrayList<>();
        public ArrayList<Task> tasks = new ArrayList<>();
        public ArrayList<Log> logs = new ArrayList<>();
    }
    
    private String rajeshPass = "ec23cef03131da4f8bc0caa14b0e4719b614adc7bcc1f82e3c19a3f7577a733e";
    
    private ZohoConfig currentConfig;
    
    public ZohoApp() {
        states.add("user");
        states.add("login");
        states.add("portals");
        states.add("projects");
        states.add("tasks");
        states.add("logs");
        
        this.currentState = 0;
        
        this.currentConfig = new ZohoConfig();
        
        this.startZoho();
    }
    
    private int askQuestions() throws NoSuchAlgorithmException {
        switch(this.currentState) {
            case 0:
                System.out.println("1. If you are rajesh");
                System.out.print("2. If not\n\t==> ");
                int option = getChoice();
                if(option==1) {
                    System.out.println("Enter password");
                    String pw = input.nextLine();
                    if(this.getHash(pw) == null ? this.rajeshPass == null : this.getHash(pw).equals(this.rajeshPass)) {
                        System.out.println("Welcome Rajesh");
                        this.currentConfig.auth_token = "23a08e39dfb160df790ab8068ec59665";
                        return 2;
                    }else {
                        System.out.println("Wrong, select uer again");
                        return 0;
                    }
                } else {
                    return 1;
                }
            case 1:
                System.out.println("Enter your zoho auth_token");
                System.out.println("If you dont have one, visit this link and get one");
                System.out.println("https://accounts.zoho.com/apiauthtoken/create?SCOPE=ZohoProjects/projectsapi");
                System.out.print("Your token:: \n\t");
                String tok = this.input.nextLine();
                if(!tok.isEmpty()) {
                    this.currentConfig.auth_token = tok;
                    return 2;
                }else {
                    System.out.println("Why don't you select user again...");
                    return 0;
                }
            case 2:
                if(this.currentConfig.portals.isEmpty()) {
                    ArrayList<String> res = new ZohoService().getPortals(this.currentConfig.auth_token);
                    this.currentConfig.portals = res;
                }
                System.out.println("Select a portal");
                int index = 1;
                for(String p:this.currentConfig.portals) {
                    System.out.println(""+index+"--> "+p.substring(0,p.indexOf("::")));
                    index++;
                }
                System.out.println(""+index+"--> Go back to preious menu");
                System.out.print("\t==> ");
                int ch;
                ch = getChoice();
                
                if(ch==index) {
                    return 0;
                }
                if(ch>0 && ch<index) {
                    String tmp = this.currentConfig.portals.get(ch-1);
                    this.currentConfig.portal_id = tmp.substring(tmp.indexOf("::")+2);
                    this.currentConfig.currentPortal = tmp.substring(0,tmp.indexOf("::"));
                    return 3;
                } else {
                    return 2;
                }
            case 3: //Select project
                if(this.currentConfig.projects.isEmpty()) {
                    this.currentConfig.projects = new ZohoService().getProjects(this.currentConfig.auth_token, this.currentConfig.portal_id);
                }
                System.out.println("Select a project, Current portal: "+this.currentConfig.currentPortal);
                System.out.println("------------------");
                index = 1;
                for(Project p:this.currentConfig.projects) {
                    System.out.println(""+index+"--> "+p.name);
                    index++;
                }
                System.out.println(""+index+"--> Go back to preious menu");
                System.out.print("\t==> ");
                ch = getChoice();

                if(ch==index) {
                    return 2;
                }
                if(ch>0 && ch<index) {
                    this.currentConfig.currentProject = this.currentConfig.projects.get(ch-1);
                    return 4; //Select task
                }else {
                    return 3; //Select Project
                }
            case 4: // Select task
                System.out.println("----------------------------");
                System.out.println("Currect project: "+this.currentConfig.currentProject.name+"\n");
                this.currentConfig.tasks = new ZohoService().getTasks(this.currentConfig.auth_token,
                        this.currentConfig.portal_id, this.currentConfig.currentProject.id);
                System.out.println();
                index = 1;
                for(Task t:this.currentConfig.tasks) {
                    System.out.println(""+index+"--> "+t.name+" (completed: "+t.completed+")");
                    index++;
                }
                int createTaskIndex = index,goBack = index+1;
                System.out.println(""+createTaskIndex+"--> Create new task");
                System.out.println(""+goBack+"--> Go back");
                System.out.print("\t==> ");
                
                ch = getChoice();
                
                if(ch==goBack) {
                    return 3;//Select project
                }
                if(ch==createTaskIndex) {
                    return 5;//Create task
                }
                if(ch>0&&ch<=this.currentConfig.tasks.size()) {
                    this.currentConfig.currentTask = this.currentConfig.tasks.get(ch-1);
                    return 6;//List log
                } else {
                    System.out.println("Wrong choice, Try again");
                    return 4; //Select task
                }
            case 5: // Create task
                break;
            case 6: //List log
                this.currentConfig.logs = new ZohoService().getLogs(this.currentConfig.auth_token, 
                        this.currentConfig.currentTask.timesheetLink);
                System.out.println("----------------------");
                System.out.println("Current task "+this.currentConfig.currentTask.name
                        +", Current project "+this.currentConfig.currentProject.name+"\n");
                System.out.println("Select log to edit (if not approved)");
                index = 1;
                
                for(Log l:this.currentConfig.logs) {
                    String top = l.notes.isEmpty()?"No notes available":l.notes;
                    top += " || "+l.log_date + " || "+ l.hourDisplay;
                    top += " || "+l.bill_status + " || "+l.approval_status;
                    System.out.println(""+index+"--> "+top);
                    index++;
                }
                int newLog = index;
                goBack = index+1;
                System.out.println(""+newLog+"--> Create new Log");
                System.out.println(""+goBack+"--> Go back to previous menu");
                System.out.print("\t==> ");
                ch = getChoice();
                if(ch==newLog) {
                    return 7; // New log
                }
                if(ch == goBack) {
                    return 4; //Task list
                }
                if(ch>0 && ch<=this.currentConfig.logs.size()) {
                    Log lt = this.currentConfig.logs.get(ch-1);
                    if(lt.approval_status.equalsIgnoreCase("approved")) {
                        System.out.println("Log already approved, Select another one");
                        System.out.println("--------------------------");
                        return 6; // Log list
                    }
                    this.currentConfig.currentLog = lt;
                    return 8; //Edit log
                } else {
                    System.out.println("Wrong choice, Try again");
                    return 6; // Log list
                }
            case 7: // Create log
                System.out.println("----------------------");
                System.out.println("Current task "+this.currentConfig.currentTask.name
                        +", Current project "+this.currentConfig.currentProject.name+"\n");
                System.out.println("Answer few question for the log\n\n");
                System.out.print("Enter date(MM-DD-YYYY): (press enter for today) :: ");
                String date = this.input.nextLine();
                System.out.print("Enter hour (HH:MM) :: ");
                String hour = this.input.nextLine();
                System.out.print("Billable?? (y/n) :: ");
                String billable = this.input.nextLine();
                System.out.print("Enter notes? if any...\n");
                String notes = this.input.nextLine();
                if(date.isEmpty()) {
                    date = ""+ new SimpleDateFormat("MM-dd-yyyy").format(new Date());
                }
                if(billable.equalsIgnoreCase("y")) {
                    billable = "Billable";
                } else {
                    billable = "Non Billable";
                }
                System.out.println("----------");
                System.out.println("Your log details");
                System.out.println(notes+" || "+date+" || "+hour+" || "+billable);
                System.out.println("----------");
                
                System.out.print("Add this log? (press enter for add or any other for cancel) :: ");
                String resp = this.input.nextLine();

                if(resp!=null && resp.isEmpty()) {
                    int rcode = new ZohoService().addLog(this.currentConfig.auth_token,
                            this.currentConfig.currentTask.timesheetLink, notes, date, billable, hour);
                    System.out.println(rcode);
                    if(rcode==201) {
                        System.out.println("Log created successfully!! :D");
                    }
                    return 6; // Get back to log list
                }else {
                    return 6; // Get back to log list
                }
                
        }       
        
        return -1;
    }
    
    private int getChoice() {
        int ch = 0;
        try {
            ch = this.input.nextInt();
            this.input.nextLine();
        } catch(Exception e) {
            System.out.println("Wrong answer!!");
            this.input.next();
            ch = 0;
        }
        return ch;
    }
    
    private String getHash(String password) throws NoSuchAlgorithmException {
    	
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes());
        
        byte byteData[] = md.digest();
 
        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
     
    	return sb.toString();
    } 
    
    private void startZoho() {
        try {
            while(true) {
                this.currentState = this.askQuestions();
                if(this.currentState<0) {
                    break;
                }
            }
            
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ZohoApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (java.util.InputMismatchException ex1) {
            System.out.println("Don't do this!!");
        }
    }
}
