package mvc.gateway.jdbc;

import mvc.exceptions.DBException;
import mvc.exceptions.UnauthorizedException;
import mvc.exceptions.UnknownException;
import mvc.models.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class PersonGatewaySQL {
    public static final Logger LOGGER = LogManager.getLogger();

    private Connection connection;

    // constructor connects to the db
    public PersonGatewaySQL() {
        // connects to the db
        try {
            connection = JDBCConnect.connectToDB();
        } catch (SQLException | IOException e) {
            throw new DBException(e);
        }
    }

    public ArrayList<Person> fetchPeople() throws UnauthorizedException, UnknownException {
        PreparedStatement st = null;
        ResultSet rs = null;
        ArrayList<Person> people = new ArrayList<>();
        try{
            st = connection.prepareStatement("select * from people");
            rs = st.executeQuery();
            while(rs.next()){
                people.add(Person.fromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try{
                rs.close();
                st.close();
            } catch (SQLException e){
                e.printStackTrace();
            }
        }

        return people;
    }

    public void close(){
        try{
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


}
