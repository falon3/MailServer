package com.company;

import java.io.IOException;
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
	// write your code here
        //System.out.println("Hi");
        check_last_loc();
    }




    public static void check_last_loc(){
        try{

            ////////////////////////////////////////////////////////////////////////////
            // Connecting to email in order to receive the watch notification
            Properties props = System.getProperties();
            props.setProperty("mail.store.protocol", "imaps");
            Session session = Session.getDefaultInstance(props, null);
            Store store = null;
            store = session.getStore("imaps");
            store.connect("imap.gmail.com", "watch.notifications@gmail.com", "q1w2e3r4t");
            Folder SafeTracks = store.getFolder("Inbox");
            SafeTracks.open(Folder.READ_ONLY);
            Message[] messages = SafeTracks.getMessages();          // all of the messages in this folder

            // Finding the last report in the notifications
            int final_report_ind = messages.length;
            System.out.println(final_report_ind);
            for (int i=100; i>=0; i--) {
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
