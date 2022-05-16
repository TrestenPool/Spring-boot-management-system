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
public class LoginGatewaySpringBoot {

    /** login **/
    public static Session login(String userName, String password) throws UnauthorizedException {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        String URL = "http://127.0.0.1:8080/login";

        try {
            // create http client and http post
            httpclient = HttpClients.createDefault();
            HttpPost postRequest = new HttpPost(URL);

            // configure the json
            JSONObject formData = new JSONObject();
            formData.put("login", userName);
            formData.put("password", password);
            System.out.println("Login = " +userName);
            System.out.println("Password = " +password);

            // configure the json to be a string to be used in the post request
            StringEntity reqEntity = new StringEntity(formData.toString());
            postRequest.setEntity(reqEntity);

            // set header information
            postRequest.setHeader("Accept", "application/json");
            postRequest.setHeader("Content-type", "application/json");

            // send the post request, save in response
            response = httpclient.execute(postRequest);
            int responseStatusCode = response.getStatusLine().getStatusCode();

            // switch on the response code received from the server
            switch(responseStatusCode){
                case 200:
                    HttpEntity entity = response.getEntity();
                    String strResponse = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                    EntityUtils.consume(entity);

                    // parse the json that was returned
                    JSONObject jsonObject = new JSONObject( (String) strResponse);
                    String token = jsonObject.get("token").toString();

                    // create the session and return it
                    Session session = new Session();
                    session.setSessionID(token);
                    return session;

                    // incorrect username and/or password
                case 401:
                    throw new UnauthorizedException("Incorrect username and/or password");

                    // the username and/or password was not received in the json body
                case 400:
                    throw new UnauthorizedException("Json format of the username and/or password is wrong");

                    // unsure
                default:
                    System.exit(1);
            }// end of switch
        }//end of the try block
        catch (Exception e) {
            throw new UnauthorizedException("Issue sending request to 127.0.0.1:8080/login");
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