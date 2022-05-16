package mvc.gateway;

import mvc.exceptions.UnauthorizedException;
import mvc.exceptions.UnknownException;
import mvc.models.Person;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This class contains methods that communicate with the restful api that will manipulate the list of Person objects
 */
public class PersonGatewayWireMock {
    private static final String URL = "http://127.0.0.1:8080";

    /** Constructor **/
    public PersonGatewayWireMock() {
    }

    /** Fetches the people list **/
    public ArrayList<Person> fetchPeople(String token) throws UnauthorizedException, UnknownException {
        // rest route
        String restRoute = URL+"/people";

        // arraylist of people we will be returning
        ArrayList<Person> people = new ArrayList<>();

        // fetches data, converts json into person, adds to the "people" arraylist to return
        try{
            String response = executeGetRequestNoBody(restRoute, token);
            JSONArray personList = new JSONArray(response); // store the response into a json array
            for(Object person: personList){ // traverse json array
                people.add( Person.fromJSONObject( (JSONObject) person) );
            }
        }
        catch (RuntimeException e){
            System.out.println("ERROR in fetchPeople()");
            throw new UnknownException(e);
        }
        return people;
    }

    /** Update Person **/
    public void updatePerson(String token, int idNumber, String body){
        // rest route
        String restRoute = URL+"/people/" +idNumber;

        try{
            String response = executePutRequestWithBody(restRoute, token, body);
        }
        catch (RuntimeException e){
            System.out.println("ERROR in updatePerson()");
            throw new UnknownException(e);
        }
    }

    /** Add Person **/
    public int addPerson(String token, String body){
        // rest route
        String restRoute = URL+"/people";

        String response = "";
        int idOfNewPerson = -1;
        try{
          response  = executePostRequestWithBody(restRoute, token, body);
          JSONObject obj = new JSONObject(response);
          idOfNewPerson = Integer.parseInt((String) obj.get("id"));
          return idOfNewPerson;
        }
        catch (RuntimeException e){
            System.out.println("ERROR in addPerson()");
            throw new UnknownException(e);
        }

    }

    /** Delete Person **/
    public void deletePerson(String token, int id){
        // rest route
        String restRoute = URL+"/people/"+id;

        int responseCode = 0;
        try{
            responseCode = executeDeleteRequestNoBody(restRoute, token);
        }
        catch (RuntimeException e){
            System.out.println("ERROR in addPerson()");
            throw new UnknownException(e);
        }
    }

    /** execute delete request without body **/
    private int executeDeleteRequestNoBody(String url, String token){
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;

        try{
            httpClient = HttpClients.createDefault();
            HttpDelete httpDelete = new HttpDelete(url);

            // sets the header with the token
            if(token != null && token.length() > 0){
                httpDelete.setHeader("Authorization", token);
            }

            httpResponse = httpClient.execute(httpDelete);

            // switch based on the status code of the get request
            switch(httpResponse.getStatusLine().getStatusCode()){
                case 200:
                    return 200;
                case 401:
                    throw new UnauthorizedException(httpResponse.getStatusLine().getReasonPhrase());
                default:
                    throw new UnknownException(httpResponse.getStatusLine().getReasonPhrase());
            }
        }
        catch(IOException e){
            e.printStackTrace();
            throw new UnauthorizedException(e);
        }
        finally { // close our streams
            try{
                if(httpResponse != null)
                    httpResponse.close();
                if(httpClient != null)
                    httpClient.close();
            }
            catch(IOException e){
                e.printStackTrace();
                throw new UnauthorizedException(e);
            }
        }
    }

    /** execute post request with body **/
    private String executePostRequestWithBody(String url, String token, String body){
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;

        try{
            httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);

            // sets the header
            if(token != null && token.length() > 0){
                httpPost.setHeader("Authorization", token);
            }

            // sets the body
            httpPost.setEntity(new StringEntity(body));
            httpResponse = httpClient.execute(httpPost);

            switch(httpResponse.getStatusLine().getStatusCode()){
                case 200:
                    return getStringFromResponse(httpResponse);
                case 401:
                    throw new UnauthorizedException(httpResponse.getStatusLine().getReasonPhrase());
                default:
                    throw new UnknownException(httpResponse.getStatusLine().getReasonPhrase());
            }
        }
        catch(IOException e){
            e.printStackTrace();
            throw new UnauthorizedException(e);
        }
        finally {
            try{
                if(httpResponse != null)
                    httpResponse.close();
                if(httpClient != null)
                    httpClient.close();
            }
            catch(IOException e){
                e.printStackTrace();
                throw new UnauthorizedException(e);
            }
        }
    }

    /** executes the get request **/
    private String executeGetRequestNoBody(String url, String token) throws UnauthorizedException, UnknownException{
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;

        try{
            httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);

            if(token != null && token.length() > 0){
               httpGet.setHeader("Authorization", token);
            }

            httpResponse = httpClient.execute(httpGet);
            // switch based on the status code of the get request
            switch(httpResponse.getStatusLine().getStatusCode()){
                case 200:
                    return getStringFromResponse(httpResponse);
                case 401:
                    throw new UnauthorizedException(httpResponse.getStatusLine().getReasonPhrase());
                default:
                    throw new UnknownException(httpResponse.getStatusLine().getReasonPhrase());
            }
        }
        catch(IOException e){
           e.printStackTrace();
           throw new UnauthorizedException(e);
        }
        finally { // close our streams
            try{
               if(httpResponse != null)
                   httpResponse.close();
               if(httpClient != null)
                   httpClient.close();
            }
            catch(IOException e){
                e.printStackTrace();
               throw new UnauthorizedException(e);
            }
        }
    }

    /** executes the put request **/
    private String executePutRequestWithBody(String url, String token, String body) throws UnauthorizedException, UnknownException{
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;

        try{
            httpClient = HttpClients.createDefault();
            HttpPut httpPut = new HttpPut(url);

            // sets the header
            if(token != null && token.length() > 0){
                httpPut.setHeader("Authorization", token);
            }

            httpPut.setEntity(new StringEntity(body));
            httpResponse = httpClient.execute(httpPut);

            switch(httpResponse.getStatusLine().getStatusCode()){
                case 200:
                    return getStringFromResponse(httpResponse);
                case 401:
                    throw new UnauthorizedException(httpResponse.getStatusLine().getReasonPhrase());
                default:
                    throw new UnknownException(httpResponse.getStatusLine().getReasonPhrase());
            }
        }
        catch(IOException e){
            e.printStackTrace();
            throw new UnauthorizedException(e);
        }
        finally {
            try{
                if(httpResponse != null)
                    httpResponse.close();
                if(httpClient != null)
                    httpClient.close();
            }
            catch(IOException e){
                e.printStackTrace();
                throw new UnauthorizedException(e);
            }
        }
    }

    /** converts http response to a string and returns **/
    private String getStringFromResponse(CloseableHttpResponse httpResponse) throws  IOException{
        HttpEntity httpEntity = httpResponse.getEntity();
        String strResponse = EntityUtils.toString(httpEntity);
        EntityUtils.consume(httpEntity);
        return strResponse;
    }
}
