package mvc.gateway.jdbc;

import mvc.models.Person;

import java.util.ArrayList;

public class SQLDemo1 {
    public static void main(String[] args) {
        // connects to the db
        PersonGatewaySQL gatewaySQL = new PersonGatewaySQL();

        ArrayList<Person> people = gatewaySQL.fetchPeople();
        for(Person p: people){
            System.out.println(p);
        }
        // close the necessary streams that were opened
        gatewaySQL.close();
    }
}
