package eu.nimble.service.bp.config;

import eu.nimble.utility.config.BluemixDatabaseConfig;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Created by suat on 22-Nov-18.
 */
@Component
public class DataSourceCreator {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Environment environment;

    public DataSource createDatasource() {
        javax.sql.DataSource ds;

        if (Arrays.stream(environment.getActiveProfiles()).anyMatch(profile -> profile.contentEquals("kubernetes"))) {
            String camundaDbCredentialsJson = environment.getProperty("nimble.db-credentials-json");
            BluemixDatabaseConfig config = new BluemixDatabaseConfig(camundaDbCredentialsJson);
            ds = DataSourceBuilder.create()
                    .url(config.getUrl())
                    .username(config.getUsername())
                    .password(config.getPassword())
                    .driverClassName(config.getDriver())
                    .build();
        } else {
            logger.info("Creating datasource: url={}, user={}",
                    environment.getProperty("spring.datasource.url"),
                    environment.getProperty("spring.datasource.username"));

            ds = DataSourceBuilder.create()
                    .url(environment.getProperty("spring.datasource.url"))
                    .username(environment.getProperty("spring.datasource.username"))
                    .password(environment.getProperty("spring.datasource.password"))
                    .driverClassName(environment.getProperty("spring.datasource.driverClassName"))
                    .build();
        }
        // Assume we make use of Apache Tomcat connection pooling (default in Spring Boot)
        org.apache.tomcat.jdbc.pool.DataSource tds = (org.apache.tomcat.jdbc.pool.DataSource) ds;
        tds.setInitialSize(Integer.valueOf(environment.getProperty("spring.datasource.tomcat.initial-size")));
        tds.setTestWhileIdle(Boolean.valueOf(environment.getProperty("spring.datasource.tomcat.test-while-idle").toUpperCase()));
        tds.setTimeBetweenEvictionRunsMillis(Integer.valueOf(environment.getProperty("spring.datasource.tomcat.time-between-eviction-runs-millis")));
        tds.setMinEvictableIdleTimeMillis(Integer.valueOf(environment.getProperty("spring.datasource.tomcat.min-evictable-idle-time-millis")));
        tds.setMaxActive(Integer.valueOf(environment.getProperty("spring.datasource.maxActive")));
        tds.setMaxIdle(Integer.valueOf(environment.getProperty("spring.datasource.maxIdle")));
        tds.setMinEvictableIdleTimeMillis(Integer.valueOf(environment.getProperty("spring.datasource.minIdle")));
        return tds;
    }
}
