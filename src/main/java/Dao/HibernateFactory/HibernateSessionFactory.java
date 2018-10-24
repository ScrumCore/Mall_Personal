package Dao.HibernateFactory;

import com.xia.configuration.SystemConfiguration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.setup.Environment;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.jvnet.hk2.annotations.Service;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.Entity;
import javax.sql.DataSource;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Service
@Singleton
public class HibernateSessionFactory {
    static final Logger logger = LoggerFactory.getLogger(HibernateSessionFactory.class);

    @PostConstruct
    public void initHibernateSessionFactory(SystemConfiguration systemConfiguration,Environment environment){
        DataSourceFactory dataSourceFactory = systemConfiguration.getDataSourceFactory();
        Map<String,String> properities = dataSourceFactory.getProperties();
        ManagedDataSource dataSource = dataSourceFactory.build(environment.metrics(),"hibernate");
        ConnectionProvider connectionProvider =this.buildConnectionProvider(dataSource,properities);
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.current_session_context_class","manager");
        configuration.setProperty("hibernate.use_sql_comments",Boolean.toString(dataSourceFactory.isAutoCommentsEnabled()));
        configuration.setProperty("hibernate.jdbc.use_get_generated_keys","true");
        configuration.setProperty("hibernate.generate_statistics","true");
        configuration.setProperty("hibernate.bytecode.use_reflection_optimizer","true");
        configuration.setProperty("hibernate.order_updates","true");
        configuration.setProperty("hibernate.order_inserts","true");
        configuration.setProperty("hibernate.id.new_generator_mappings","true");
        configuration.setProperty("jadira.usertype.autoRegisterUserTypes","true");
        Iterator registry = properities.entrySet().iterator();
        while(registry.hasNext()){
            logger.info(registry.next().toString());
        }
        String packagename= "Entity";
        Set<Class<?>> entities = getClassOfPackage(packagename);
        for(Class kclass:entities){
            configuration.addAnnotatedClass(kclass);
            logger.info(kclass.getName());
        }
        StandardServiceRegistry standardServiceRegistry = (new StandardServiceRegistryBuilder()).addService(ConnectionProvider.class,connectionProvider).applySettings(
                properities).build();
        SessionFactory sessionFactory = configuration.buildSessionFactory(standardServiceRegistry);
        SessionFactoryHolder.setSessionFactory(sessionFactory);
        logger.info("build sucess!");
    }


    private ConnectionProvider buildConnectionProvider(DataSource dataSource, Map<String,String> properities){
        DatasourceConnectionProviderImpl connectionProvider = new DatasourceConnectionProviderImpl();
        connectionProvider.setDataSource(dataSource);
        connectionProvider.configure(properities);
        return connectionProvider;

    }

    private Set<Class<?>> getClassOfPackage(String packageName){
        FilterBuilder filterBuilder = new FilterBuilder();
        filterBuilder.includePackage(packageName);
        ConfigurationBuilder reflectionCfg = new ConfigurationBuilder();
        reflectionCfg.addUrls(ClasspathHelper.forPackage(packageName));
        reflectionCfg.filterInputsBy(filterBuilder).setScanners(new SubTypesScanner(false),new TypeAnnotationsScanner());
        Reflections reflections = new Reflections(reflectionCfg);
        Set<Class<?>> entities = reflections.getTypesAnnotatedWith(Entity.class,true);
        return entities;
    }


}
