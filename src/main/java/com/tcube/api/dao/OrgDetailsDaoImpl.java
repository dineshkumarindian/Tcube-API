package com.tcube.api.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.tcube.api.PropertiesConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.tcube.api.model.LeaveTrackerDetails;
import com.tcube.api.model.OrgDetails;
import com.tcube.api.model.RoleDetails;
import com.tcube.api.model.SuperAdminDashboard;
import com.tcube.api.utils.EncryptorUtil;

@Component
public class OrgDetailsDaoImpl implements OrgDetailsDao {

    /**
     * Logger is to log application messages.
     */
    private static Logger logger = LogManager.getLogger(OrgDetailsDaoImpl.class);

    static PropertiesConfig config = null;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public OrgDetails createOrgDetails(OrgDetails admindetails) {
        logger.info("OrgDetailsDaoImpl(createOrgDetails) Entry>> ");
        final Session session = entityManager.unwrap(Session.class);
        try {
            admindetails.setCreated_time(new Date());
            admindetails.setModified_time(new Date());
            admindetails.setDate_of_joining(new Date());
            admindetails.setPassword(EncryptorUtil.encryptPropertyValue(admindetails.getPassword()));
            session.save(admindetails);
            if (admindetails.getOrg_id() == 0) {
                entityManager.persist(admindetails);
                return admindetails;
            } else {
                entityManager.merge(admindetails);
                return admindetails;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("OrgDetailsDaoImpl(createOrgDetails) Entry>> ");
        return admindetails;
    }

    @Override
    public OrgDetails getOrgDetailsById(Long id) {
        logger.info("OrgDetailsDaoImpl(getOrgtDetailsById) Entry>> Request -> " + id);
        final Session session = entityManager.unwrap(Session.class);
        final OrgDetails details = (OrgDetails) session.get(OrgDetails.class, id);
        logger.info("OrgDetailsDaoImpl(getOrgtDetailsById) Exit>>-> ");
        return details;
    }

	@Override
	public OrgDetails updateOrgDetails(OrgDetails oldDetails) {
		logger.info("OrgDetailsDaoImpl(updateOrgDetails) Entry>>-> ");
		final Session session= entityManager.unwrap(Session.class);
		try {
			oldDetails.setModified_time(new Date());
//			oldDetails.setPassword(EncryptorUtil.encryptPropertyValue(oldDetails.getPassword()));
			logger.debug("appInfo obj:" + new Gson().toJson(oldDetails));
			session.update(oldDetails);
			if (oldDetails.getOrg_id() == 0) {
				entityManager.persist(oldDetails);
				return oldDetails;
			} else {
				entityManager.merge(oldDetails);
				return oldDetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("OrgDetailsDaoImpl(updateOrgDetails) Exit>>-> ");
		return oldDetails;
	}

    @Override
    public OrgDetails deleteOrgDetails(OrgDetails oldDetails) {
        logger.info("OrgDetailsDaoImpl(deleteorgDetails) Entry>>-> ");
        final Session session = entityManager.unwrap(Session.class);
        try {

            oldDetails.setModified_time(new Date());
            session.update(oldDetails);
            if (oldDetails.getOrg_id() == 0) {
                entityManager.persist(oldDetails);
                return oldDetails;
            } else {
                entityManager.merge(oldDetails);
                return oldDetails;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("OrgDetailsDaoImpl(deleteorgDetails) Exit>>-> ");
        return oldDetails;

    }

    @Override
    public List<OrgDetails> getAllOrgDetails() {
        logger.info("OrgDetailsDaoImpl(getAllOrgDetails) Entry>>-> ");
        final Session session = entityManager.unwrap(Session.class);
        final List<OrgDetails> details = session.createCriteria(OrgDetails.class).list();

        for (int i = 0; i < details.size(); i++) {
            boolean verified = VerifyAccount(details.get(i).getOrg_id());
            if (!verified) {
                details.get(i).setStatus("Expired");
            }
        }
        logger.info("OrgDetailsDaoImpl(getAllOrgDetails) Exit>>-> ");
        return details;

    }

    @Override
    public OrgDetails authenticateOrg(OrgDetails orgDetails) {
        logger.info("OrgDetailsDaoImpl(authenticateOrg) Entry>>-> ");
        final Session session = entityManager.unwrap(Session.class);
        final Query query = session.createQuery("from OrgDetails where email=:e and is_deleted=:d");
        query.setParameter("e", orgDetails.getEmail());
        query.setParameter("d", false);
        try {
            final OrgDetails orgDetails1 = (OrgDetails) query.getSingleResult();
            logger.info("OrgDetailsDaoImpl(authenticateOrg) Exit>>-> ");
            return orgDetails1;
        } catch (NoResultException nre) {
            logger.info("OrgDetailsDaoImpl(authenticateOrg) Exit>>-> ");
            return null;
        }


    }

    @Override
    public List<OrgDetails> getActiveOrgDetails() {
        logger.info("OrgDetailsDaoImpl(getActiveOrgDetails) Entry>>");
        final Session session = entityManager.unwrap(Session.class);
        final Query query = session.createQuery(
                "from OrgDetails where is_deleted=false and is_activated=true order by timestamp(modified_time) asc");
        @SuppressWarnings("unchecked") final List<OrgDetails> details = query.getResultList();
        logger.info("OrgDetailsDaoImpl(getActiveOrgDetails) Exit>>");
        return details;
    }

    @Override
    public List<OrgDetails> getInactiveOrgDetails() {
        logger.info("OrgDetailsDaoImpl(getInactiveOrgDetails) Entry>>");
        final Session session = entityManager.unwrap(Session.class);
        final Query query = session.createQuery(
                "from OrgDetails where is_deleted=false and is_activated=false or status = 'Expired' order by timestamp(modified_time) desc");
        @SuppressWarnings("unchecked") final List<OrgDetails> details = query.getResultList();
        logger.info("OrgDetailsDaoImpl(getInactiveOrgDetails) Exit>>");
        return details;
    }

    @Override
    public OrgDetails updateEmpid(String email, String empid) {
        logger.info("OrgDetailsDaoImpl(updateEmpid) Entry>>-> ");
        final Session session = entityManager.unwrap(Session.class);
        final Query query = session.createQuery("from OrgDetails where email=:e");
        query.setParameter("e", email);
        final OrgDetails orgDetails1 = (OrgDetails) query.getSingleResult();
        if (orgDetails1 != null) {
            orgDetails1.setEmp_id(empid);
            if (orgDetails1.getOrg_id() == 0) {
                entityManager.persist(orgDetails1);
                return orgDetails1;
            } else {
                entityManager.merge(orgDetails1);
                return orgDetails1;
            }
        }
        logger.info("OrgDetailsDaoImpl(updateEmpid) Exit>>-> ");
        return null;
    }

    @Override
    public OrgDetails updatePricingplanDetails(OrgDetails oldDetails) {
        logger.info("OrgDetailsDaoImpl(updateOrgDetails) Entry>>-> ");
        final Session session = entityManager.unwrap(Session.class);
        try {
            oldDetails.setModified_time(new Date());
            oldDetails.setPassword(EncryptorUtil.encryptPropertyValue(oldDetails.getPassword()));
            logger.debug("appInfo obj:" + new Gson().toJson(oldDetails));
            session.update(oldDetails);
            if (oldDetails.getOrg_id() == 0) {
                entityManager.persist(oldDetails);
                return oldDetails;
            } else {
                entityManager.merge(oldDetails);
                return oldDetails;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("OrgDetailsDaoImpl(updateOrgDetails) Exit>>-> ");
        return oldDetails;
    }

    @Override
    public List<SuperAdminDashboard> getTotalOrgCount() {
        logger.info("OrgDetailsDaoImpl(getTotalOrgCount) Entry>>");
        final Session session = entityManager.unwrap(Session.class);
        final Query query = session.createQuery(
                "from OrgDetails where is_deleted =:k and status is not null and is_activated is not null");
        query.setParameter("k", false);
        @SuppressWarnings("unchecked")
        List<OrgDetails> details = query.getResultList();
        List<SuperAdminDashboard> data = new ArrayList<>();
        int activeCount = 0;
        int inActiveCount = 0;
        int rejectCount = 0;
        int pendingCount = 0;
        SuperAdminDashboard temp = new SuperAdminDashboard();
        for (int i = 0; i < details.size(); i++) {
            if (details.get(i).getStatus().equals("Approved") && details.get(i).getIs_activated()) {
                activeCount++;
            }
            if (!details.get(i).getIs_activated()) {
                inActiveCount++;
            }
            if (details.get(i).getStatus().equals("Rejected")) {
                rejectCount++;
            }
            if (details.get(i).getStatus().equals("Pending")) {
                pendingCount++;
            }
        }
        temp.setActiveCountInfo(activeCount);
        temp.setInActiveCountInfo(inActiveCount);
        temp.setRejectCountInfo(rejectCount);
        temp.setPendingCountInfo(pendingCount);
        temp.setTotalCountInfo(details.size());
        data.add(temp);
        logger.info("OrgDetailsDaoImpl(getTotalOrgCount) Exit>>");
        return data;
    }

    @Override
    public boolean getRejectedOrgDetailsById(Long id) {
        boolean isDel;
        logger.info("OrgDetailsDaoImpl(getRejectedOrgDetailsById) Entry>>-> ");
        final Session session = entityManager.unwrap(Session.class);
        final Query query2 = session.createQuery("delete from RoleDetails where orgDetails.org_id=:org_id");
        final Query query1 = session.createQuery("delete from EmployeeDetails where orgDetails.org_id=:org_id");
        final Query query3 = session.createQuery("delete from DesignationDetails where orgDetails.org_id=:org_id");
        final Query query = session.createQuery("delete from OrgDetails where org_id=:org_id");
        query1.setParameter("org_id", id);
        query2.setParameter("org_id", id);
        query3.setParameter("org_id", id);
        query.setParameter("org_id", id);
        query1.executeUpdate();
        query2.executeUpdate();
        query3.executeUpdate();
        int result = query.executeUpdate();
        if (result != 1) {
            isDel = false;
        } else {
            isDel = true;
        }
        logger.info("OrgDetailsDaoImpl(getRejectedOrgDetailsById) exit>>-> ");
        return isDel;
    }

    @Override
    public boolean bulkDeleteRejectedOrgs(Long id) {
        boolean isBulkDel;
        logger.info("OrgDetailsDaoImpl(bulkDeleteRejectedOrgs) Entry>>-> ");
        final Session session = entityManager.unwrap(Session.class);
        final Query query2 = session.createQuery("delete from RoleDetails where orgDetails.org_id=:org_id");
        final Query query1 = session.createQuery("delete from EmployeeDetails where orgDetails.org_id=:org_id");
        final Query query3 = session.createQuery("delete from DesignationDetails where orgDetails.org_id=:org_id");
        final Query query = session.createQuery("delete from OrgDetails where org_id=:org_id");
        query1.setParameter("org_id", id);
        query2.setParameter("org_id", id);
        query3.setParameter("org_id", id);
        query.setParameter("org_id", id);
        query1.executeUpdate();
        query2.executeUpdate();
        query3.executeUpdate();
        int result = query.executeUpdate();
        if (result != 1) {
            isBulkDel = false;
        } else {
            isBulkDel = true;
        }
        logger.info("OrgDetailsDaoImpl(bulkDeleteRejectedOrgs) exit>>-> ");

        // TODO Auto-generated method stub
        return isBulkDel;
    }

    @Override
    public List<OrgDetails> getAllPendingDetails() {
        logger.info("OrgDetailsDaoImpl(getAllPendingDetails) Entry>>");
        final Session session = entityManager.unwrap(Session.class);
        final Query query = session.createQuery(
                "from OrgDetails where status =:d order by timestamp(modified_time) desc");
        query.setParameter("d", "Pending");
        @SuppressWarnings("unchecked") final List<OrgDetails> details = query.getResultList();
        logger.info("OrgDetailsDaoImpl(getAllPendingDetails) Exit>>");
        return details;
        // TODO Auto-generated method stub
//	return null;
    }

    @Override
    public List<OrgDetails> getAllRejectDetails() {
        logger.info("OrgDetailsDaoImpl(getAllRejectDetails) Entry>>");
        final Session session = entityManager.unwrap(Session.class);
        final Query query = session.createQuery(
                "from OrgDetails where status =:d order by timestamp(modified_time) desc");
        query.setParameter("d", "Rejected");
        @SuppressWarnings("unchecked") final List<OrgDetails> details = query.getResultList();
        logger.info("OrgDetailsDaoImpl(getAllRejectDetails) Exit>>");
        return details;
        // TODO Auto-generated method stub
//	return null;
    }

    /**
     * @return true or false based on trail verification
     */
    @Override
    public boolean VerifyAccount(Long id) {
        logger.info("OrgDetailsDaoImpl(VerifyAccount) Entry>> Request -> " + id);

        try {
            final Session session = entityManager.unwrap(Session.class);
            final OrgDetails details = (OrgDetails) session.get(OrgDetails.class, id);
            Date date1 = new Date();
            if (details.getExpiry_date() == null) {
                logger.info("OrgDetailsDaoImpl(VerifyAccount) Exit>>-> ");
                return true;
            }
            if (details.getExpiry_date().after(date1)) {
                logger.info("OrgDetailsDaoImpl(VerifyAccount) Exit>>-> ");
                return true;
            } else {
                details.setStatus("Expired");
                session.update(details);
                if (details.getOrg_id() == 0) {
                    entityManager.persist(details);
                    logger.info("OrgDetailsDaoImpl(VerifyAccount) Exit>>-> ");
                    return false;
                } else {
                    entityManager.merge(details);
                    logger.info("OrgDetailsDaoImpl(VerifyAccount) Exit>>-> ");
                    return false;
                }
            }

        } catch (Exception e) {
//			throw new RuntimeException(e);
            logger.info("OrgDetailsDaoImpl(VerifyAccount) Exit>>-> ");
            return false;
        }
    }

    /**
     * @return
     */
    @Override
    public int[] TrialDetails() {
        logger.info("OrgDetailsDaoImpl(TrialDetails) Entry>>");
        try {
            config = PropertiesConfig.getInstance();
            int TrialDays = Integer.parseInt(config.getTrialDays());
            int TrialUserLimit = Integer.parseInt(config.getTrialUserLimit());
            int[] output = {TrialDays, TrialUserLimit};
            logger.info("OrgDetailsDaoImpl(TrialDetails) Exit>>");
            return output;
        } catch (Exception e) {
//			throw new RuntimeException(e);
            logger.info("OrgDetailsDaoImpl(TrialDetails) Exit>>");
            return null;
        }
    }

}
