package Dao.HibernateFactory;

import com.xia.configuration.SystemConfiguration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.setup.Environment;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.util.Map;

@Singleton
public class HibernateSessionFactory {

    @Inject
    private SystemConfiguration systemConfiguration;

    @Inject
    private Environment environment;

    @PostConstruct
    private void initHibernateSessionFactory(){
        DataSourceFactory dataBase = systemConfiguration.getDataSourceFactory();
        Map<String,String> properities = dataBase.getProperties();
        ManagedDataSource dataSource = dataBase.build(environment.metrics(),"hibernate");
        ConnectionProvider connectionProvider =this.buildConnectionProvider(dataSource,properities);
        Configuration configuration = new Configuration();
        configuration.setProperty("","");


    }


    private ConnectionProvider buildConnectionProvider(DataSource dataSource, Map<String,String> properities){
        DatasourceConnectionProviderImpl connectionProvider = new DatasourceConnectionProviderImpl();
        connectionProvider.setDataSource(dataSource);
        connectionProvider.configure(properities);
        return connectionProvider;

    }


}
