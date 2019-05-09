package twittersearch;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import twitter4j.*;

import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * This is an example class that shows how to exploit the Twitter4J java library to interact with Twitter
 * <p>
 * Twitter4j: http://twitter4j.org/en/index.html
 * Download (version 4.0.1):http://twitter4j.org/archive/twitter4j-4.0.1.zip
 * JavaDoc: http://twitter4j.org/javadoc/index.html
 * Example code of Twitter4j: http://twitter4j.org/en/code-examples.html
 *
 * @author Francesco Ronzano & Jeff Lee
 */
public class SearchAroundLocation {

    private static Logger logger = Logger.getLogger(SearchAroundLocation.class.getName());
    public static final String yourAccessToken = "246713978-vgUp3OIjQYCDD8qgim6n8V8o1Si74rKWynX3UVOb";
    public static final String yourAccessTokenSecret = "XfABcIOFGvwQxClffYC4UDMOsdujDqxSlesup8SJd5Bwt";
    public static final String yourConsumerKey = "eWmQkL42ZO9i42ZBQ9tC1AxEt";
    public static final String yourConsumerKeySecret = "y2QRGMLPBqERgpB1eMF4iGUTD1oIWvLx4rM9YB72igry4c6ggo";

    public static void main(String[] args) {

        // PrintWriter
        PrintWriter pw;
        // 1) Instantiate a Twitter Factory
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true).setJSONStoreEnabled(true);
        TwitterFactory tf = new TwitterFactory(cb.build());

        // 2) Instantiate a new Twitter client
        // Go to https://dev.twitter.com/ to register a new Twitter App and get credentials
        Twitter twitter = tf.getInstance();
        AccessToken accessToken = new AccessToken(yourAccessToken, yourAccessTokenSecret);
        twitter.setOAuthConsumer(yourConsumerKey, yourConsumerKeySecret);
        twitter.setOAuthAccessToken(accessToken);


        System.out.println("*********************************************");
        System.out.println("***** search for tweets within a 3 mile radius of Midtown, New York City ******");

        File outDir = new File("./data/NewYork");
        if (!outDir.exists()) outDir.mkdir();


        //Query query = new Query(queryString);
        Query query = new Query();
        query.geoCode(new GeoLocation(40.7549,-73.9840),3.0,"mi");
        query.count(100); // sets the number of tweets to return per page, up to a max of 100
        QueryResult result;

        try {
            result = twitter.search(query);
            Integer countTw = 1;

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM_dd_HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String date = dtf.format(now);

            System.out.println("Result for tweets around NYC: ");
            for (Status status : result.getTweets()) {
                pw = new PrintWriter(new FileWriter(outDir + File.separator + "tweet_" + date + "-" + countTw + ".json"));
                System.out.println("\n" + countTw++ + " > @" + status.getUser().getScreenName() + " (" + status.getCreatedAt().toString() + ") : " + status.getText() + "\n");
                String json = TwitterObjectFactory.getRawJSON(status);
                pw.print(json);
                pw.flush();
                pw.close();
                System.out.println(json);
            }
        } catch (TwitterException e) {
            logger.info("Exception while searching for tweets by a query string: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(SearchAroundLocation.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
