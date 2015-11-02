import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterAPI {
  private static final String CONSUMER_KEY = "<KEY>";
  private static final String CONSUMER_SECRET_KEY = "<SECRET>";
  private static final String ACCESS_TOKEN = "<TOKEN>";
  private static final String ACCESS_TOKEN_SECRET = "<TOKEN_SECRET>";
  
  public static void main(String args[]) throws Exception{    
    
    ConfigurationBuilder builder = new ConfigurationBuilder();
    builder.setOAuthConsumerKey(CONSUMER_KEY);
    builder.setOAuthConsumerSecret(CONSUMER_SECRET_KEY);
    builder.setOAuthAccessToken(ACCESS_TOKEN);
    builder.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
    Configuration configuration = builder.build();
    
    TwitterFactory factory = new TwitterFactory(configuration);
    Twitter twitter = factory.getInstance();

    User user = twitter.verifyCredentials();
    
    System.out.println("Fetching @" + user.getScreenName() + "'s home timeline....");
    
    //Get recent tweets
    List<Status> statuses = twitter.getHomeTimeline();
    StringBuffer sb = new StringBuffer();
    for (Status status : statuses) {
        sb.append("@" + status.getUser().getScreenName() + " - " + status.getText() + "\n");
    }
    
    String inputFile = "recent_tweets";
    PrintWriter out = null;
    System.out.println("Writing the collected tweets to the file");
    try {
      out = new PrintWriter(inputFile);
      out.write(sb.toString());        
    } catch (FileNotFoundException e) {
      System.out.println("Problem in generating output file");
    } finally {      
      out.close();
    }
    
    System.out.println("Done!");
    System.exit(0);
  }
}

