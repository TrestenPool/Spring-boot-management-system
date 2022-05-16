package mvc.gateway.wirmockAPI;

import mvc.exceptions.UnauthorizedException;
import mvc.gateway.Session;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/*
The LoginController will call a method inside of it and call this class LoginGateway
 */
public class LoginGateway {

    /** login **/
    public static Session login(String userName, String password) throws UnauthorizedException {
        // checks to see if valid by wiremock mapping
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        String URL = "http://127.0.0.1:8080/login";

        try {
            // create http client and http post
            httpclient = HttpClients.createDefault();
            HttpPost postRequest = new HttpPost(URL);

            // configure the json
            JSONObject formData = new JSONObject();
            formData.put("username", userName);
            formData.put("password", password);

            // configure json to be a string
            StringEntity reqEntity = new StringEntity(formData.toString());
            // setup the post request to have the json body
            postRequest.setEntity(reqEntity);

            // sent the post request and store the response
            response = httpclient.execute(postRequest); // EXCEPTION
            int responseStatusCode = response.getStatusLine().getStatusCode();

            // switch on the response code received from the server
            switch(responseStatusCode){
                case 200:
                    HttpEntity entity = response.getEntity();
                    String strResponse = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                    EntityUtils.consume(entity);
                    // create the session and return it
                    Session session = new Session(strResponse);
                    return session;
                case 401:
                case 404:
                    throw new UnauthorizedException("login failed");
                default:
                    System.exit(1);
            }// end of switch
        }//end of the try block
        catch (Exception e) {
            throw new UnauthorizedException("login failed");
        }
        finally {
            // close our streams
            try {
                if(response != null){
                    response.close();
                }
                if(httpclient != null){
                    httpclient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }//end of finally
        return null;
    }//end of login function
}// end of LoginGateway