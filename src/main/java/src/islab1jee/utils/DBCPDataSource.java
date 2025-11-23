package src.islab1jee.utils;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;

public class DBCPDataSource {

    private static BasicDataSource ds = new BasicDataSource();

    static {
        ds.setUrl("jdbc:postgresql://localhost:5432/studs");
        ds.setUsername("s408145");
        ds.setPassword("JLzD%6772");
        ds.setDriverClassName("org.postgresql.Driver");

        ds.setMinIdle(5);
        ds.setMaxIdle(10);
        ds.setMaxTotal(20);
        ds.setMaxWaitMillis(10000);

        ds.setValidationQuery("SELECT 1");
        ds.setTestOnBorrow(true);
    }

    private DBCPDataSource() { }

    public static DataSource getDataSource() {
        return ds;
    }
}

