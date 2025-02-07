package com.tcube.api.dao;


import com.tcube.api.model.Appproperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

@Component
public class PropertiesDaoImpl  implements PropertiesDao{
    /**
     * Logger is to log application messages.
     */
    private static Logger logger = LogManager.getLogger(ProjectResourceDaoImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * @param key
     * @return
     */
    @Override
    public Appproperties getproperties(String key) {
        logger.info("PropertiesDaoImpl(getproperties) >> Entry");
        final Session session = entityManager.unwrap(Session.class);
        final Query query = session.createQuery(
                "from Appproperties where  key=:k ");
        query.setParameter("k", key);
        Appproperties details = new Appproperties();
        try {
            details = (Appproperties) query.getSingleResult();
            logger.info("PropertiesDaoImpl(getproperties) >> Exit");
            return details;
        }
        catch(Exception e) {
            logger.info("PropertiesDaoImpl(getproperties) >> Exit");
            return null;
        }

    }

    /**
     * @param data
     * @return
     */
    @Override
    public Appproperties createproperties(Appproperties data) {
        logger.info("PropertiesDaoImpl(createproperties) >> Entry");
        final Session session= entityManager.unwrap(Session.class);
        try {

            session.save(data);
            if (data.getId() == 0) {
                entityManager.persist(data);
                logger.info("PropertiesDaoImpl(createproperties) >> Exit");
                return data;
            } else {
                entityManager.merge(data);
                logger.info("PropertiesDaoImpl(createproperties) >> Exit");
                return data;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("PropertiesDaoImpl(createproperties) >> Exit");
            return null;
        }
    }

//    /**
//     * @param data
//     * @return
//     */
//    @Override
//    public Appproperties updateproperties(Appproperties data) {
//        logger.info("PropertiesDaoImpl(updateproperties) >> Entry");
//        final Session session= entityManager.unwrap(Session.class);
//        try {
//            session.update(data);
//            if (data.getId() == 0) {
//                entityManager.persist(data);
//                logger.info("PropertiesDaoImpl(updateproperties) >> Exit");
//                return data;
//            } else {
//                entityManager.merge(data);
//                logger.info("PropertiesDaoImpl(updateproperties) >> Exit");
//                return data;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        logger.info("PropertiesDaoImpl(updateproperties) >> Exit");
//        return null;
//    }
}
