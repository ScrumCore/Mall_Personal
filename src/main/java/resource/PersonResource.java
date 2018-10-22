package resource;


import Dao.PersonDao;
import Entity.Person;
import com.codahale.metrics.annotation.Timed;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.Api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Api("/view")
@Path(value = "/view")
@Produces(MediaType.APPLICATION_JSON)
public class PersonResource {
    private PersonDao dao;
    public PersonResource(PersonDao dao){
        this.dao = dao;
    }

    @GET
    @Timed
    @Path("/person")
    @UnitOfWork
    public Person sayHello(@QueryParam("id") Long id) {
        Person person = dao.findById(id);
        return person;
    }
}

