package test.xia;

import Dao.PersonDao;
import Entity.Person;
import com.xia.configuration.SwitchableSwaggerBundle;
import com.xia.configuration.SystemConfiguration;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import resource.HelloWorldResource;
import resource.PersonResource;

/**
 * Hello world!
 *
 */
public class App extends Application<SystemConfiguration>
{
    private final HibernateBundle<SystemConfiguration> hibernate = new HibernateBundle<SystemConfiguration>(Person.class) {
        public DataSourceFactory getDataSourceFactory(SystemConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    public static void main( String[] args ) throws Exception {
        new App().run(args);
        System.out.println( "Hello World!" );
    }

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<SystemConfiguration> bootstrap) {
        // nothing to do yet
        bootstrap.addBundle(hibernate);
        bootstrap.addBundle(new SwitchableSwaggerBundle());
    }

    @Override
    public void run(SystemConfiguration configuration,
                    Environment environment) {
        // nothing to do yet
        final HelloWorldResource resource = new HelloWorldResource(configuration.getTemplate(), configuration.getDefaultName() );
        final PersonDao dao = new PersonDao(hibernate.getSessionFactory());

        environment.jersey().register(new PersonResource(dao));

        environment.jersey().register(resource);//environment环境类添加到Jersey服务器中
    }
}
