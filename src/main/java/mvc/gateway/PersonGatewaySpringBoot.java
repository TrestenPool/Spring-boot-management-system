package mvc.gateway;

import mvc.exceptions.UnauthorizedException;
import mvc.exceptions.UnknownException;
import mvc.models.AuditTrailRecord;
import mvc.models.PagingResults;
import mvc.models.Person;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class contains methods that communicate with the restful api that will manipulate the list of Person objects
 */
public class PersonGatewaySpringBoot {
    private static final String URL = "http://127.0.0.1:8080";

    /** Constructor **/
    public PersonGatewaySpringBoot() {
    }

    /** Fetches all the audit trail record of a particular person id **/
    public ArrayList<AuditTrailRecord> fetchAuditTrails(String token, String personId){
        String restRoute = URL + "/people/" +personId +"/audittrail";
        ArrayList<AuditTrailRecord> auditTrailRecordArrayList = new ArrayList<>();

        try{
            System.out.println("\n\n in fetch audit trails \n\n");
            // store response in json array
            String response = executeGetRequestNoBody(restRoute, token);

            // empty list
            if(response.isEmpty() == true)
                return auditTrailRecordArrayList;

            JSONArray auditTrailJSONArray = new JSONArray(response);

            // store all the records into auditTrailRecordArrayList
            for(int i = 0; i < auditTrailJSONArray .length(); i++){
                String whenOccured = auditTrailJSONArray.getJSONObject(i).get("whenOccured").toString();
                String changeBy = auditTrailJSONArray.getJSONObject(i).getJSONObject("changedBy").get("login").toString();
                String changeMsg = auditTrailJSONArray.getJSONObject(i).get("changeMsg").toString();

                AuditTrailRecord auditTrailRecord = new AuditTrailRecord(whenOccured, changeBy, changeMsg);
                auditTrailRecordArrayList.add(auditTrailRecord);
            }
        }
        catch (RuntimeException e){
            System.out.println("Error in fetchAuditTrails()...");
            throw new RuntimeException();
        }

        return auditTrailRecordArrayList;
    }

    /** Fetches the people list **/
    public PagingResults fetchPeople(String token, int pageNum) throws UnauthorizedException, UnknownException {
        // rest route
        String restRoute = URL+"/people?pageNum="+pageNum;
        PagingResults results = new PagingResults();
        // fetches data, converts json into person, adds to the "people" arraylist to return
        try{
            // store the response body into personList json
            String response = executeGetRequestNoBody(restRoute, token);
            JSONObject responseJSON = new JSONObject(response.trim());
            ArrayList<Person> people = new ArrayList<>();
            JSONArray personList = new JSONArray(responseJSON.get("content").toString());
            for(int i = 0; i < personList.length(); i++){
                // get the variables
               String idStr = personList.getJSONObject(i).get("id").toString();
               String dobStr = personList.getJSONObject(i).get("dob").toString();
               String first_nameStr =  personList.getJSONObject(i).get("firstName").toString();
               String last_nameStr =  personList.getJSONObject(i).get("lastName").toString();
               String ageStr =  personList.getJSONObject(i).get("age").toString();

                LocalDate localDate = LocalDate.parse(dobStr);

               // create the person
                Person newPerson = new Person(first_nameStr, last_nameStr, Integer.parseInt(idStr), localDate);

                // add to the people array list
                people.add(newPerson);
            }
            results.setValues(people);
            results.setLast(responseJSON.getBoolean("last"));
            results.setNumberOfElements(responseJSON.getInt("numberOfElements"));
            try{
                results.setTotalPages(responseJSON.getInt("totalPages"));
            }
            catch (JSONException e){
                results.setTotalPages(1);
            }
            results.setSize(responseJSON.getInt("size"));
            try{
                results.setTotalElements(responseJSON.getInt("totalElements"));
            }
            catch (JSONException e){
                results.setTotalElements(1);
            }
            JSONObject pageableObj = responseJSON.getJSONObject("pageable");
            results.setOffset(pageableObj.getInt("offset"));
            results.setPageNumber(pageableObj.getInt("pageNumber"));
            results.setPageSize(pageableObj.getInt("pageSize"));
        }
        catch (RuntimeException e){
            System.out.println("ERROR in fetchPeople()");
            throw new UnknownException(e);
        }
        return results;
    }

    /** Update Person **/
    public void updatePerson(String token, int idNumber, String body){
        // rest route
        String restRoute = URL+"/people/" +idNumber;

        try{
            // attempt to update the record
           executePutRequestWithBody(restRoute, token, body);
        }
        catch (RuntimeException e){
            System.out.println("ERROR in updatePerson()");
            throw new UnknownException(e);
        }
    }

    /** Add Person **/
    public void addPerson(String token, String body){
        // rest route
        String restRoute = URL+"/people";

        String response = "";
        int idOfNewPerson = -1;
        try{
          // saves into the db, returns the person object that was created
          response  = executePostRequestWithBody(restRoute, token, body);
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
                httpDelete.setHeader("authorization", token);
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
                httpPost.setHeader("authorization", token);
            }

            // set header information
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

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

            // set the Authorization header to the session token
            if(token != null && token.length() > 0){
                httpGet.setHeader("authorization", token);
            }

            // set header information
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Content-type", "application/json");

            // execute the get request
            httpResponse = httpClient.execute(httpGet);

            // log the status code
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            System.out.println("\n\n status code = " +statusCode +" \n\n");

            // switch based on the status code of the get request
            switch(httpResponse.getStatusLine().getStatusCode()){
                case 200:
                    // returns a json list of the users
                    return getStringFromResponse(httpResponse);

                    // invalid session token
                case 401:
                    throw new UnauthorizedException(httpResponse.getStatusLine().getReasonPhrase());

                    // the id is not in the list, just return empty list
                case 404:
                    return "";

                    // unknown
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
                httpPut.setHeader("authorization", token);
            }

            // set header information
            httpPut.setHeader("Accept", "application/json");
            httpPut.setHeader("Content-type", "application/json");

            httpPut.setEntity(new StringEntity(body));
            httpResponse = httpClient.execute(httpPut);

            switch(httpResponse.getStatusLine().getStatusCode()){
                // success
                case 200:
                    return getStringFromResponse(httpResponse);
                // invalid token
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
