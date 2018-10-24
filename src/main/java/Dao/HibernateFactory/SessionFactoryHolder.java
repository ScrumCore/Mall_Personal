package Dao.HibernateFactory;

import org.hibernate.SessionFactory;

public class SessionFactoryHolder {
    public static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory(){
        return  sessionFactory;
    }

    public  static void setSessionFactory(SessionFactory sessionFactory){
        SessionFactoryHolder.sessionFactory = sessionFactory;
    }
}
