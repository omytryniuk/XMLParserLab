package net.learn2develop.NetworkingText;

import android.app.Activity;
import android.os.Bundle;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.util.Log;
import android.widget.TextView;
import android.os.AsyncTask;
import android.widget.Toast;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.learn2develop.Teacher.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class NetworkingActivity extends Activity {
String TAG = "OLEG";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // ---access a Web Service using GET---
        new AccessWebServiceTask().execute("apple");
        Log.d(TAG, "AFTER EXECUTE");
    }

    private InputStream OpenHttpConnection(String urlString)
            throws IOException {
        InputStream in = null;
        int response = -1;

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");
        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = new BufferedInputStream(httpConn.getInputStream());
            }
        } catch (Exception ex) {
            Log.d("Networking", ex.getLocalizedMessage());
            throw new IOException("Error connecting");
        }
        return in;
    }


// PARSER CODE WAS USED FORM http://www.vogella.com/tutorials/AndroidXML/article.html


    private String WordDefinition(String word) throws XmlPullParserException {

        InputStream in = null;
        String strDefinition = "O";
        try {
            in = OpenHttpConnection("http://services.aonaware.com/DictService/DictService.asmx/Define?word="
                    + word);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            // WAS PRACTICING WITH CODE FROM http://www.xmlpull.org/v1/download/unpacked/doc/quick_intro.html

            /*
            String SAMPLE_XML =
                    "<?xml version=\"1.0\"?>\n"+
                            "\n"+
                            "<poem xmlns=\"http://www.megginson.com/ns/exp/poetry\">\n"+
                            "<title>Roses are Red</title>\n"+
                            "<l>Roses are red,</l>\n"+
                            "<l>Violets are blue;</l>\n"+
                            "<l>Sugar is sweet,</l>\n"+
                            "<l>And I love you.</l>\n"+
                            "</poem>";
*/


            // INPUT STREAM READER FROM http://www.tutorialforandroid.com/2009/05/how-to-use-xmlpullparser-in-android.html

             xpp.setInput(new InputStreamReader(in));
            int eventType = xpp.getEventType();


            while (eventType != XmlPullParser.END_DOCUMENT) {

                String tag = xpp.getName();

                if(eventType == XmlPullParser.START_DOCUMENT) {
                    System.out.println("Start document");

                } else if(eventType == XmlPullParser.END_DOCUMENT) {
                    System.out.println("End document");

                } else if(eventType == XmlPullParser.START_TAG) {
                    Log.d("Ole", "START TAG");
                    System.out.println("Start tag "+xpp.getName());
                } else if(eventType == XmlPullParser.END_TAG) {
                    System.out.println("End tag "+xpp.getName());
                    Log.d("Ole", "END TAG");
                } else if(eventType == XmlPullParser.TEXT) {

                  if (xpp.getText().trim().length() != 0)
                       strDefinition = xpp.getText();

                }
                eventType = xpp.next();

            }




        }catch(IOException e){}



      //  Log.d("Oles", "We are sending " + strDefinition);
   return strDefinition;


    }

    private class AccessWebServiceTask extends
            AsyncTask<String, Void, String> {
            String res=" ";
        protected String doInBackground(String... urls) {

            try {
               res = WordDefinition(urls[0]);
            } catch (XmlPullParserException e) {
                e.printStackTrace();

            }

            return res;
        }

        protected void onPostExecute(String result) {
          TextView tv = (TextView) findViewById(R.id.textView2);
            tv.setText(result);
        }
    }

}