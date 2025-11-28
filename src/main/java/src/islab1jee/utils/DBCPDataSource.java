package src.islab1jee.utils;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DBCPDataSource {

    private static final BasicDataSource ds = new BasicDataSource();

    static {
        Properties properties = new Properties();

        try (InputStream input = DBCPDataSource.class.getClassLoader()
                .getResourceAsStream("db.properties")) {

            if (input == null) {
                throw new RuntimeException("Не найден файл конфигурации db.properties");
            }

            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки db.properties", e);
        }

        ds.setUrl(properties.getProperty("db.url"));
        ds.setUsername(properties.getProperty("db.username"));
        ds.setPassword(properties.getProperty("db.password"));
        ds.setDriverClassName(properties.getProperty("db.driver"));

        ds.setMinIdle(Integer.parseInt(properties.getProperty("db.minIdle")));
        ds.setMaxIdle(Integer.parseInt(properties.getProperty("db.maxIdle")));
        ds.setMaxTotal(Integer.parseInt(properties.getProperty("db.maxTotal")));
        ds.setMaxWaitMillis(Long.parseLong(properties.getProperty("db.maxWaitMillis")));

        ds.setValidationQuery(properties.getProperty("db.validationQuery"));
        ds.setTestOnBorrow(Boolean.parseBoolean(properties.getProperty("db.testOnBorrow")));
    }

    private DBCPDataSource() {}

    public static DataSource getDataSource() {
        return ds;
    }
}
