package com.company;


import java.io.*;
import java.util.*;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.methods.HttpPost;
import javax.mail.*;

public class Main {

    public static void main(String[] args) {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            /*output = new FileOutputStream("config.properties");

            // set the properties value
            prop.setProperty("email_pass", "watch1234");
            prop.setProperty("email_address", "ashbournwatch2@gmail.com");
            prop.setProperty("num_notifications", "50");

            // save properties to project root folder
            prop.store(output, null);*/

            input = new FileInputStream("config.properties");

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            System.out.println(prop.getProperty("email_pass"));
            System.out.println(prop.getProperty("email_address"));
            System.out.println(prop.getProperty("num_notifications"));

            String epass = prop.getProperty("email_pass");
            String eaddress = prop.getProperty("email_address");
            int nume =  Integer.parseInt(prop.getProperty("num_notifications"));

            check_last_loc(eaddress, epass, nume);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }




    public static void check_last_loc(String email, String pass, int num_updates){
        try{

            ////////////////////////////////////////////////////////////////////////////
            // Connecting to email in order to receive the watch notification
            Properties props = System.getProperties();
            props.setProperty("mail.store.protocol", "imaps");
            Session session = Session.getDefaultInstance(props, null);
            Store store = null;
            store = session.getStore("imaps");
            store.connect("imap.gmail.com", email, pass);//email, pass);
            Folder SafeTracks = store.getFolder("Inbox");
            SafeTracks.open(Folder.READ_ONLY);
            Message[] messages = SafeTracks.getMessages();          // all of the messages in this folder

            // Finding the last report in the notifications
            int final_report_ind = messages.length;
            System.out.println(final_report_ind);
            for (int i=num_updates; i>0; i--) {
                Message message = messages[messages.length - 4 - i];
                ArrayList words1 = extract(message.getContent().toString());
                List<NameValuePair> urlP = new ArrayList<NameValuePair>();
                urlP.add(new BasicNameValuePair("category", "Location"));
                urlP.add(new BasicNameValuePair("watch_id", words1.get(2).toString()));
                urlP.add(new BasicNameValuePair("time", words1.get(6) + " " + words1.get(7)));
                urlP.add(new BasicNameValuePair("activity_type", String.format("watch location %2d", i)));
                urlP.add(new BasicNameValuePair("locLat", words1.get(8).toString()));
                urlP.add(new BasicNameValuePair("locLon", words1.get(9).toString()));


                // Send to server
                CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                try {
                    HttpPost request = new HttpPost("http://127.0.0.1:5000/add_record/");
                    request.addHeader("content-type", "application/x-www-form-urlencoded");
                    request.setEntity(new UrlEncodedFormEntity(urlP));
                    HttpResponse response = httpClient.execute(request);
                    System.out.println("------------");
                    System.out.println(response);
                    System.out.println("------------");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        httpClient.close();
                    } catch (IOException e) {

                    }
                }
                for(Object s: words1) {
                    System.out.println(s);
                }
            }
            SafeTracks.close(false);
            store.close();



        }
        catch (NoSuchProviderException e){
            e.printStackTrace();
        }
        catch (MessagingException e){
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }




    private static ArrayList<String> extract(String content){
        ArrayList<String> words = new ArrayList<String>();
        String p1,p2,p3,p4,p5,p6,p7,p8,p9,p10;
        int[] camas = new int[10];
        int j=0;
        for (int i=0; i<9; i++) {
            while(content.charAt(j)!=','){
                j++;
            }
            camas[i] = j;
            j++;
        }
        while(content.charAt(j)!='\n'){
            j++;
        }
        camas[9] = j;

        words.add(content.substring(0,camas[0]));
        words.add(content.substring(camas[0]+1,camas[1]));
        words.add(content.substring(camas[1]+1,camas[2]));
        words.add(content.substring(camas[2]+1,camas[3]));
        words.add(content.substring(camas[3]+1,camas[4]));
        words.add(content.substring(camas[4]+1,camas[5]));
        words.add(content.substring(camas[5]+1,camas[6]));
        words.add(content.substring(camas[6]+1,camas[7]));
        words.add(content.substring(camas[7]+1,camas[8]));
        words.add(content.substring(camas[8]+1,camas[9]));
        return words;
    }


}
