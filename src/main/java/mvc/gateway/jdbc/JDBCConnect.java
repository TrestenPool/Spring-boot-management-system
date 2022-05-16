package mvc.gateway.jdbc;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCConnect {

    // connects to the db, returns a connection instance
    public static Connection connectToDB() throws SQLException, IOException{
        // connect to the data source
        Properties props = getConfig("/config.properties");

        // create the datasource
        MysqlDataSource ds = new MysqlDataSource();
        ds.setURL(props.getProperty("MYSQL_DB_URL"));
        ds.setUser(props.getProperty("MYSQL_DB_USERNAME"));
        ds.setPassword(props.getProperty("MYSQL_DB_PASSWORD"));

        // create the connection
        return ds.getConnection();
    }


    // helper function for the resource file with the credentials
    public static Properties getConfig(String propsFileName) throws IOException{
        Properties props = new Properties();
        BufferedInputStream propsFile = (BufferedInputStream) JDBCConnect.class.getResourceAsStream(propsFileName);
        props.load(propsFile);
        propsFile.close();

        return props;
    }
}
