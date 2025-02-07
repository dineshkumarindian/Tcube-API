package com.tcube.api.dao;

import java.text.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;

import java.math.BigInteger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.tcube.api.model.BusinessLetterDetails;
import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.JobAssigneeDetails;
import com.tcube.api.model.LeaveTrackerDetails;
import com.tcube.api.model.NotificationsDetails;
import com.tcube.api.model.TimesheetApprovalDetails;

@Component
public class LeaveTrackerDetailsDaoImpl implements LeaveTrackerDetailsDao {

	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = LogManager.getLogger(LeaveTrackerDetailsDaoImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public LeaveTrackerDetails createLeave(LeaveTrackerDetails leaveDetails, String zone) {
		logger.info("LeaveTrackerDetailsDaoImpl(createLeave)>> Entry");
		final Session session = entityManager.unwrap(Session.class);
		try {
			leaveDetails.setCreated_time(new Date());
			leaveDetails.setModified_time(new Date());
			Date date = leaveDetails.getStart_date();
			Date date2 = leaveDetails.getEnd_date();
//			Date date3 = details.getLeave_date();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			// Use india time zone to format the date in
			df.setTimeZone(TimeZone.getTimeZone(zone));

			leaveDetails.setStart_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date)));
			leaveDetails.setEnd_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date2)));
//			details.setLeave_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date3)));
			logger.debug("HolidayDetailsDaoImpl obj:" + new Gson().toJson(leaveDetails));
			session.save(leaveDetails);
			if (leaveDetails.getId() == 0) {
				entityManager.persist(leaveDetails);
				return leaveDetails;
			} else {
				entityManager.merge(leaveDetails);
				return leaveDetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("LeaveTrackerDetailsDaoImpl(createLeave) >> Exit ");
		return leaveDetails;
	}

	@Override
	public LeaveTrackerDetails updateLeaveStatus(LeaveTrackerDetails details) {
		logger.info("LeaveTrackerDetailsDaoImpl(updateLeaveStatus) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		try {
			details.setModified_time(new Date());
			logger.debug("appInfo obj:" + new Gson().toJson(details));
			session.update(details);
			if (details.getId() == 0) {
				entityManager.persist(details);
				return details;
			} else {
				entityManager.merge(details);
				return details;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("LeaveTrackerDetailsDaoImpl(updateLeaveStatus) >> Exit");
		return details;
	}

	@Override
	public LeaveTrackerDetails getById(Long id) {
		logger.info("LeaveTrackerDetailsDaoImpl(getById) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final LeaveTrackerDetails details = (LeaveTrackerDetails) session.get(LeaveTrackerDetails.class, id);
		logger.info("LeaveTrackerDetailsDaoImpl(getById) >> Exit");
		return details;
	}

	@Override
	public List<LeaveTrackerDetails> getActiveLeaveByEmpId(String id) {
		logger.info("LeaveTrackerDetailsDaoImpl(getActiveLeaveByEmpId)>> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from LeaveTrackerDetails where emp_id=:id and is_deleted=:d order by timestamp(modified_time) desc");
		query.setParameter("id", id);
		query.setParameter("d", false);
		@SuppressWarnings("unchecked")
		final List<LeaveTrackerDetails> details = query.getResultList();
		logger.info("LeaveTrackerDetailsDaoImpl(getActiveLeaveByEmpId) >> Exit");
		return details;
	}

	@Override
	public List<LeaveTrackerDetails> getLeaveByEmpIdAndYear(LeaveTrackerDetails newDetails) {
		logger.info("LeaveTrackerDetailsDaoImpl(getLeaveByEmpIdAndYear)>> Entry");
		Date todayDate = new Date();
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from LeaveTrackerDetails where orgDetails.org_id=:org and emp_id=:id and start_date>=:b and end_date<=:c order by timestamp(modified_time) desc");
		query.setParameter("org", newDetails.getOrgDetails().getOrg_id());
		query.setParameter("id", newDetails.getEmp_id());
		query.setParameter("b", newDetails.getStart_date());
		query.setParameter("c", newDetails.getEnd_date());
		@SuppressWarnings("unchecked")
		final List<LeaveTrackerDetails> details = query.getResultList();
		logger.info("LeaveTrackerDetailsDaoImpl(getLeaveByEmpIdAndYear) >> Exit");
		return details;
	}
	
	@Override
	public List<LeaveTrackerDetails> getLeaveByEmpIdAndYearforappliedAndApprovedLeave(LeaveTrackerDetails newDetails) {
		logger.info("LeaveTrackerDetailsDaoImpl(getLeaveByEmpIdAndYearforappliedAndApprovedLeave)>> Entry");
		Date todayDate = new Date();
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from LeaveTrackerDetails where orgDetails.org_id=:org and emp_id=:id and start_date>=:b and end_date<=:c and (approval_status =:x or approval_status =:y) order by timestamp(modified_time) desc");
		query.setParameter("org", newDetails.getOrgDetails().getOrg_id());
		query.setParameter("id", newDetails.getEmp_id());
		query.setParameter("b", newDetails.getStart_date());
		query.setParameter("c", newDetails.getEnd_date());
		query.setParameter("x", "Pending");
		query.setParameter("y","Approved");
		@SuppressWarnings("unchecked")
		final List<LeaveTrackerDetails> details = query.getResultList();
		logger.info("LeaveTrackerDetailsDaoImpl(getLeaveByEmpIdAndYearforappliedAndApprovedLeave) >> Exit");
		return details;
	}

	@Override
	public List<LeaveTrackerDetails> getLeaveByReporterIdAndYear(LeaveTrackerDetails newDetails) {
		logger.info("LeaveTrackerDetailsDaoImpl(getLeaveByEmpIdAndYear)>> Entry");
		final Session session = entityManager.unwrap(Session.class);
//		final Query query = session.createQuery(
//				"from LeaveTrackerDetails where orgDetails.org_id=:org and reporter=:id and is_deleted=:a and start_date>=:b and end_date<=:c order by timestamp(modified_time) desc");
		final Query query = session.createQuery(
				"from LeaveTrackerDetails where orgDetails.org_id=:org and reporter=:id and is_deleted=:a and start_date>=:b and end_date<=:c and approval_status=:z order by timestamp(modified_time) desc");
		query.setParameter("org", newDetails.getOrgDetails().getOrg_id());
		query.setParameter("id", newDetails.getReporter());
		query.setParameter("a", false);
		query.setParameter("b", newDetails.getStart_date());
		query.setParameter("c", newDetails.getEnd_date());
		query.setParameter("z", "Pending");
//		query.setFirstResult(0);
//		query.setMaxResults(0);
		@SuppressWarnings("unchecked")
		final Query query1 = session.createQuery(
				"from LeaveTrackerDetails where orgDetails.org_id=:org and reporter=:id and is_deleted=:a and start_date>=:b and end_date<=:c and approval_status != :z order by timestamp(modified_time) desc");
		query1.setParameter("org", newDetails.getOrgDetails().getOrg_id());
		query1.setParameter("id", newDetails.getReporter());
		query1.setParameter("a", false);
		query1.setParameter("b", newDetails.getStart_date());
		query1.setParameter("c", newDetails.getEnd_date());
		query1.setParameter("z", "Pending");
//		query1.setFirstResult(0);
//		query1.setMaxResults(0);
		final List<LeaveTrackerDetails> details = query.getResultList();
		final List<LeaveTrackerDetails> details1 = query1.getResultList();
		final ArrayList<LeaveTrackerDetails> leaveRequestData = new ArrayList<LeaveTrackerDetails>();
		leaveRequestData.addAll(details);
		leaveRequestData.addAll(details1);
		logger.info("LeaveTrackerDetailsDaoImpl(getLeaveByEmpIdAndYear) >> Exit");
		return leaveRequestData;
	}

	@Override
	public LeaveTrackerDetails updateLeave(LeaveTrackerDetails details) {
		logger.info("LeaveTrackerDetailsDaoImpl(updateLeave) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		try {
			details.setModified_time(new Date());
			logger.debug("appInfo obj:" + new Gson().toJson(details));
			session.update(details);
			if (details.getId() == 0) {
				entityManager.persist(details);
				return details;
			} else {
				entityManager.merge(details);
				return details;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("LeaveTrackerDetailsDaoImpl(updateLeave) >> Exit");
		return details;
	}

	@Override
	public List<LeaveTrackerDetails> getAllLeaveType(LeaveTrackerDetails details) {
//		System.out.println(id);
		// TODO Auto-generated method stub
		logger.info("LeaveTrackerDetailsDaoimpl(getAllLeaveType)>>Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from LeaveTrackerDetails where orgDetails.org_id=:org and emp_id=:id and start_date>=:b and end_date<=:c order by timestamp(modified_time) desc");
//		final Query query = session.createQuery("from LeaveTrackerDetails where emp_id=:id order by timestamp(modified_time) desc");
//		query.setParameter("org", details.getOrgDetails().getOrg_id());
		query.setParameter("org", details.getOrgDetails().getOrg_id());
		query.setParameter("id", details.getEmp_id());
		query.setParameter("b", details.getStart_date());
		query.setParameter("c", details.getEnd_date());

		@SuppressWarnings("unchecked")
		List<LeaveTrackerDetails> details1 = query.getResultList();
		logger.info("LeaveTrackerDetailsDaoimpl(getAllLeaveType)>>Exit");
		return details1;
//		return null;
	}


	@Override
	public List<LeaveTrackerDetails> getTodayLeavesbyOrgId(LeaveTrackerDetails newDetails, String zone) {
		logger.info("LeaveTrackerDetailsDaoImpl(getTodayLeavesbyOrgId) >> Entry");
//		try {
		Date date = newDetails.getStart_date();
//		Date date2 = newDetails.getEnd_date();
//		Date date3 = details.getLeave_date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		// Use india time zone to format the date in
		df.setTimeZone(TimeZone.getTimeZone(zone));

		try {
			newDetails.setStart_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date)));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		leaveDetails.setEnd_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date2)));
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from LeaveTrackerDetails where orgDetails.org_id=:org and is_deleted=:a and approval_status =:s and start_date <=:b and end_date >=:b order by timestamp(modified_time) desc");
		query.setParameter("org", newDetails.getOrgDetails().getOrg_id());
		query.setParameter("a", false);
		query.setParameter("s", "Approved");
		query.setParameter("b", newDetails.getStart_date());
//		query.setParameter("c", newDetails.getEnd_date());
		final List<LeaveTrackerDetails> details = query.getResultList();
		logger.info("LeaveTrackerDetailsDaoImpl(getTodayLeavesbyOrgId)>> Exit");
		return details;
//		} catch(Exception e) {
//			logger.info("LeaveTrackerDetailsDaoImpl(getTodayLeavesbyOrgId) exit>>");
//		}
//		return details;
	}

//	@Override
//	public List<LeaveTrackerDetails> getAllLeaveType(Long id) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public List<LeaveTrackerDetails> getLeaveByEmpIdAndYearForLeavetype(LeaveTrackerDetails newDetails) {
		logger.info("LeaveTrackerDetailsDaoImpl(getLeaveByEmpIdAndYearForLeavetype) >> Entry");
//		System.out.println(todayDate);
//	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 00:00:00.0");  
//	    String strDate = formatter.format(todayDate);  
//	    System.out.println(strDate); 
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"select start_date,end_date,approval_status,half_full_day from LeaveTrackerDetails where orgDetails.org_id=:org and emp_id=:id and is_deleted=:a and start_date>=:b and end_date<=:c order by timestamp(modified_time) desc");
		query.setParameter("org", newDetails.getOrgDetails().getOrg_id());
		query.setParameter("id", newDetails.getEmp_id());
		query.setParameter("a", false);
//		query.setParameter("p","Approved");
//		query.setParameter("q","Pending");
		query.setParameter("b", newDetails.getStart_date());
		query.setParameter("c", newDetails.getEnd_date());
//		query.setParameter("h", "Full Day");
		@SuppressWarnings("unchecked")
		final List<LeaveTrackerDetails> details = query.getResultList();
		logger.info("LeaveTrackerDetailsDaoImpl(getLeaveByEmpIdAndYearForLeavetype) >> Exit");
		return details;
	}

	// While user update the profilr image --> update the image in leave tracker
	// details for that user
	@Override
	public LeaveTrackerDetails updateEmpDetails(String empid, byte[] img) {
		logger.info("LeaveTrackerDetailsDaoImpl(updateEmpDetails) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session.createQuery("from LeaveTrackerDetails where emp_id=:i");
		query.setParameter("i", empid);
		List<LeaveTrackerDetails> details = query.getResultList();
		for (int i = 0; i < details.size(); i++) {
//			details.get(i).setEmp_img(img);
			if (details.get(i).getId() == 0) {
				entityManager.persist(details.get(i));
			} else {
				entityManager.merge(details.get(i));
			}
		}
		logger.info("LeaveTrackerDetailsDaoImpl(updateEmpDetails) >> Exit");
		return null;
	}

	@Override
	public List<LeaveTrackerDetails> getEmpDateRangeApprovedLeaves(String startdate, String enddate, String emp_id) {
		logger.info("LeaveTrackerDetailsDaoimpl(getEmpDateRangeApprovedLeaves)>>Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from LeaveTrackerDetails where emp_id=:id and approval_status =:status and ((start_date>=:startdate and end_date<=:enddate) or (end_date>=:startdate and start_date<=:enddate)) order by timestamp(modified_time) desc");
		query.setParameter("id", emp_id);
		query.setParameter("status", "Approved");
		Date start;
		Date end;

		try {
			start = new SimpleDateFormat("dd-MM-yyyy").parse(startdate);
			end = new SimpleDateFormat("dd-MM-yyyy").parse(enddate);
			query.setParameter("startdate", start);
			query.setParameter("enddate", end);

			@SuppressWarnings("unchecked")
			List<LeaveTrackerDetails> details = query.getResultList();
			return details;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("LeaveTrackerDetailsDaoimpl(getEmpDateRangeApprovedLeaves)>>Exit");
		return null;
	}

//	leave tracker reports for all employee's
	@SuppressWarnings("unchecked")
	@Override
	public List getLeaveByEmpIdAndYearForReports(LeaveTrackerDetails newDetails, JSONArray ids) {
		logger.info("LeaveTrackerDetailsDaoImpl(getLeaveByEmpIdAndYearForReports) >>Entry");
//		Date todayDate = new Date();

		List leavetype = new ArrayList();
		final Session session = entityManager.unwrap(Session.class);
		String id_list = new String();
		for (int i = 0; i < ids.length(); i++) {
			id_list += "'" + ids.get(i) + "'" + ",";
		}

		StringBuffer id_sb = new StringBuffer(id_list);
		id_sb.deleteCharAt(id_sb.length() - 1);
		final Query query = session.createQuery("from LeaveTrackerDetails where orgDetails.org_id=:org and emp_id in ("
				+ id_sb + ") and start_date>=:b and end_date<=:c order by timestamp(modified_time) desc");
		query.setParameter("org", newDetails.getOrgDetails().getOrg_id());
//		query.setParameter("id", newDetails.getEmp_id());
		query.setParameter("b", newDetails.getStart_date());
		query.setParameter("c", newDetails.getEnd_date());
		final List<LeaveTrackerDetails> details = query.getResultList();
		leavetype.addAll(0, details);
		String id_list1 = new String();
		for (int i = 0; i < details.size(); i++) {
			id_list1 += "'" + details.get(i).getLeave_type_id() + "'" + ",";
		}

		StringBuffer leaveType_Id = new StringBuffer(id_list1);
		leaveType_Id.deleteCharAt(leaveType_Id.length() - 1);
		final Query query1 = session
				.createQuery("Select id, leave_type,available_days from ManageLeaveTypes where id in (" + leaveType_Id
						+ ") order by timestamp(modified_time) desc");
		@SuppressWarnings("unchecked")
		final List leaveTypedetails = query1.getResultList();
		JSONArray jArray = new JSONArray();
		for (int i = 0; i < leaveTypedetails.size(); i++) {
			JSONObject studentJSON = new JSONObject();
			studentJSON.put("leavetypeId", ((Object[]) leaveTypedetails.get(i))[0]);
			studentJSON.put("leaveTypeName", ((Object[]) leaveTypedetails.get(i))[1]);
			studentJSON.put("leaveTypeAvailDays", ((Object[]) leaveTypedetails.get(i))[2]);
			jArray.put(studentJSON);
		}
		leavetype.add(0, jArray);
		logger.info("LeaveTrackerDetailsDaoImpl(getLeaveByEmpIdAndYearForReports) >>Exit");
		return leavetype;
	}

	@Override
	public JSONObject getEmpCausalAndSickLeaveCount(String org_id, String emp_id) {
		logger.info("LeaveTrackerDetailsDaoimpl(getEmpCausalAndSickLeaveCountForCurrentYear)>>Entry");
		final Session session = entityManager.unwrap(Session.class);
		LocalDate now = LocalDate.now();
		LocalDate firstDay = now.with(firstDayOfYear());
		LocalDate lastDay = now.with(lastDayOfYear());
		String currentyear_firstday = firstDay + " 00:00:00";
		String currentyear_lastday = lastDay + " 23:59:59";
		final Query query = session
				.createQuery("select available_days from ManageLeaveTypes where org_id=:org_id and start_date >='"
						+ currentyear_firstday + "' and end_date <='" + currentyear_lastday
						+ "' and is_activated=true and is_deleted=false and leave_type='casual leave'");
		query.setParameter("org_id", org_id);
		List<LeaveTrackerDetails> leaves = (List<LeaveTrackerDetails>) query.getResultList();
		Object causal_leaves = new Object();
		if (leaves.isEmpty()) {
			causal_leaves = 0;
		} else {
			causal_leaves = leaves.get(0);
		}
		final Query query1 = session
				.createQuery("select available_days from ManageLeaveTypes where org_id=:org_id and start_date >='"
						+ currentyear_firstday + "' and end_date <='" + currentyear_lastday
						+ "' and is_activated=true and is_deleted=false and leave_type='Sick leave'");
		query1.setParameter("org_id", org_id);
		List<LeaveTrackerDetails> sleaves = (List<LeaveTrackerDetails>) query1.getResultList();
		Object sick_leaves = new Object();
		if (sleaves.isEmpty()) {
			sick_leaves = 0;
		} else {
			sick_leaves = sleaves.get(0);
		}
		final Query query2 = session
				.createQuery("select sum(total_days) from LeaveTrackerDetails where emp_id=:emp_id and start_date >='"
						+ currentyear_firstday + "' and end_date <='" + currentyear_lastday
						+ "' and approval_status='Approved' and is_active=true and is_deleted=false and leave_type='casual leave'");
		query2.setParameter("emp_id", emp_id);
		List<LeaveTrackerDetails> leaves_count = (List<LeaveTrackerDetails>) query2.getResultList();
		Object causalleave_taken = new Object();
		if (leaves_count.isEmpty()) {
			causalleave_taken = 0;
		} else {
			causalleave_taken = leaves_count.get(0);
		}
		final Query query3 = session
				.createQuery("select sum(total_days) from LeaveTrackerDetails where emp_id=:emp_id and start_date >='"
						+ currentyear_firstday + "' and end_date <='" + currentyear_lastday
						+ "' and approval_status='Approved' and is_active=true and is_deleted=false and leave_type='Sick leave'");
		query3.setParameter("emp_id", emp_id);
		List<LeaveTrackerDetails> sleaves_count = (List<LeaveTrackerDetails>) query3.getResultList();
		Object sickleave_taken = new Object();
		if (sleaves_count.isEmpty()) {
			sickleave_taken = 0;
		} else {
			sickleave_taken = sleaves_count.get(0);
		}
		if (causal_leaves == null) {
			causal_leaves = 0;
		}
		if (sick_leaves == null) {
			sick_leaves = 0;
		}
		if (causalleave_taken == null) {
			causalleave_taken = 0;
		}
		if (sickleave_taken == null) {
			sickleave_taken = 0;
		}
		JSONObject obj = new JSONObject();
		obj.put("Causal_leave_available", causal_leaves);
		obj.put("Sick_leave_available", sick_leaves);
		obj.put("Causal_leave_taken", causalleave_taken);
		obj.put("Sick_leave_taken", sickleave_taken);
		int Causal_leave_available = obj.getInt("Causal_leave_available") - obj.getInt("Causal_leave_taken");
		int Sick_leave_available = obj.getInt("Sick_leave_available") - obj.getInt("Sick_leave_taken");
		JSONObject objResult = new JSONObject();
		objResult.put("Causal_leave_available", Causal_leave_available);
		objResult.put("Sick_leave_available", Sick_leave_available);
		objResult.put("Causal_leave_taken", causalleave_taken);
		objResult.put("Sick_leave_taken", sickleave_taken);
		logger.info("LeaveTrackerDetailsDaoimpl(getEmpCausalAndSickLeaveCountForCurrentYear)>>Exit");
		return objResult;
	}

//Update reporting manager with pending leaves when assigned manager got deactivated 
	@Override
	public int updateReportingManagerAfterDeactivateUser(String id, Long orgId) {
		logger.info("LeaveTrackerDetailsDaoimpl(updateReportingManagerAfterDeactivateUser)>>Entry");
		final Session session = entityManager.unwrap(Session.class);
		int details2 = 0;
		String id_list = new String();
		for (int k = 0; k < id.length(); k++) {
//			id_list += "'" + id.get(i) + "'" + ",";
			id_list += "'" + id + "'" + ",";
		}
		StringBuffer id_sb = new StringBuffer(id_list);
		id_sb.deleteCharAt(id_sb.length() - 1);
		final Query query = session
				.createQuery("from LeaveTrackerDetails where reporter in (" + id_sb + ") and approval_status=:l");
//		query.setParameter("i", id);
		query.setParameter("l", "Pending");
		@SuppressWarnings("unchecked")
		final List<LeaveTrackerDetails> details = query.getResultList();
		if (details.size() != 0) {
			final Query query1 = session.createQuery(
					"from EmployeeDetails where orgDetails.id=:orgId and user_login_type = 'OrgAdmin' and is_deleted=:i and is_activated=:j");
			query1.setParameter("orgId", orgId);
			query1.setParameter("i", false);
			query1.setParameter("j", true);
			@SuppressWarnings("unchecked")
			EmployeeDetails details1 = (EmployeeDetails) query1.getSingleResult();
			for (int i = 0; i < details.size(); i++) {
				final Query query2 = session.createNativeQuery(
						"update leave_tracker_details set reporter=:i, reporter_name=:j where reporter=:k ");
				query2.setParameter("i", details1.getId());
				query2.setParameter("j", details1.getFirstname());
				query2.setParameter("k", details.get(i).getReporter());
				details2 = query2.executeUpdate();
			}
		}
		logger.info("LeaveTrackerDetailsDaoimpl(updateReportingManagerAfterDeactivateUser)>>Exit");
		return details2;
	}

	// Update reporting managers with pending leaves when assigned managers got bulk
	// deactivated
	@Override
	public int updateReporterAfterBulkDeactivateUser(JSONArray id, Long orgId) {
		logger.info("LeaveTrackerDetailsDaoimpl(updateReporterAfterBulkDeactivateUser)>>Entry");
		final Session session = entityManager.unwrap(Session.class);
		int details2 = 0;
		int reporterDetails = 0;
		logger.info("EmployeeDetailsDaoImpl(bulkDeactiveEmp) >> Entry");
		// project array to string concatenate process
		String id_list = new String();
		for (int i = 0; i < id.length(); i++) {
			id_list += "'" + id.get(i) + "'" + ",";
		}
		StringBuffer id_sb = new StringBuffer(id_list);
		id_sb.deleteCharAt(id_sb.length() - 1);
		final Query query = session
				.createQuery("from LeaveTrackerDetails where reporter in (" + id_sb + ") and approval_status=:l");
		query.setParameter("l", "Pending");
		@SuppressWarnings("unchecked")
		final List<LeaveTrackerDetails> details = query.getResultList();
		if (details.size() != 0) {

			final Query query2 = session.createQuery(
					"from EmployeeDetails where orgDetails.id=:orgId and user_login_type = 'OrgAdmin'  and is_deleted=:i and is_activated=: j");
			query2.setParameter("orgId", orgId);
			query2.setParameter("i", false);
			query2.setParameter("j", true);
			EmployeeDetails details1 = (EmployeeDetails) query2.getSingleResult();
			for (int i = 0; i < details.size(); i++) {
				final Query query1 = session.createNativeQuery(
						"update leave_tracker_details set reporter=:i, reporter_name=:j where reporter=:k");
				query1.setParameter("i", details1.getId());
				query1.setParameter("j", details1.getFirstname());
				query1.setParameter("k", details.get(i).getReporter());
				details2 = query1.executeUpdate();
//		 return details2;
			}
		}
//		else {
//			return 0;
//		}
		logger.info("LeaveTrackerDetailsDaoimpl(updateReporterAfterBulkDeactivateUser)>>Exit");
		return details2;
	}

	@Override
	public List<LeaveTrackerDetails> getLeaveTrackerDetailsByLeaveType(Long id) {
		// TODO Auto-generated method stub
		logger.info("LeaveTrackerDetailsDaoImpl(getLeaveTrackerDetailsByLeaveType) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked")
		final Query query = session.createQuery("from LeaveTrackerDetails where leave_type_id=:a");
		query.setParameter("a", id);
		final List<LeaveTrackerDetails> details = query.getResultList();
		logger.info("LeaveTrackerDetailsDaoImpl(getLeaveTrackerDetailsByLeaveType) >>Exit");
		return details;

	}

	@Override
	public int updateLeaveTrackerDetailksByLeaveType(Long id, String newLeaveType) {
		// TODO Auto-generated method stub
		logger.info("LeaveTrackerDetailsDaoImpl(updateLeaveTrackerDetailksByLeaveType) >> Entry");
		final Session session = entityManager.unwrap(Session.class);

		final Query query = session.createNativeQuery("update leave_tracker_details set leave_type=:j where id=:i");
		query.setParameter("i", id);
		query.setParameter("j", newLeaveType);
		@SuppressWarnings("unchecked")
		int details = query.executeUpdate();
		logger.info("LeaveTrackerDetailsDaoImpl(updateLeaveTrackerDetailksByLeaveType) >>Exit");
		return details;

	}

	@Override
	public int updateSlackNotificationStatus(int notificationId, Boolean status) {
		logger.info("LeaveTrackerDetailsDaoImpl(updateSlackNotificationStatus) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createNativeQuery(
				"update leave_tracker_details set is_notified_toslack=:j,slack_notify =:k where id =:i");
		query.setParameter("i", notificationId);
		query.setParameter("j", status);
		query.setParameter("k", true);
		@SuppressWarnings("unchecked")
		int details = query.executeUpdate();
		logger.info("LeaveTrackerDetailsDaoImpl(updateSlackNotificationStatus) >>Exit");
		return details;
	}

	@Override
	public List<LeaveTrackerDetails> getRequestLeaveCount(LeaveTrackerDetails newDetails) {
		// TODO Auto-generated method stub
		logger.info("LeaveTrackerDetailsDaoImpl(getRequestLeaveCount) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
//		final Query query = session.createQuery(
//				"from LeaveTrackerDetails where orgDetails.org_id=:org and reporter=:id and is_deleted=:a and start_date>=:b and end_date<=:c order by timestamp(modified_time) desc");
		final Query query = session.createQuery(
				"from LeaveTrackerDetails where orgDetails.org_id=:org and reporter=:id and is_deleted=:a and start_date>=:b and end_date<=:c and approval_status=:z order by timestamp(modified_time) desc");
		query.setParameter("org", newDetails.getOrgDetails().getOrg_id());
		query.setParameter("id", newDetails.getReporter());
		query.setParameter("a", false);
		query.setParameter("b", newDetails.getStart_date());
		query.setParameter("c", newDetails.getEnd_date());
		query.setParameter("z", "Pending");
		@SuppressWarnings("unchecked")
		List<LeaveTrackerDetails> details = query.getResultList();
		logger.info("LeaveTrackerDetailsDaoImpl(getRequestLeaveCount) >>Exit");
		return details;

	}

	@Override
	public List<LeaveTrackerDetails> getRequestLeaveDetailsCount(LeaveTrackerDetails newDetails) {
		logger.info("LeaveTrackerDetailsDaoImpl(getRequestLeaveCount) >> Entry");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from LeaveTrackerDetails where orgDetails.org_id=:org and reporter=:id and is_deleted=:a and start_date>=:b and end_date<=:c order by timestamp(modified_time) desc");
//		final Query query = session.createQuery(
//				"select count(id) from LeaveTrackerDetails where orgDetails.org_id=:org and reporter=:id and is_deleted=:a and start_date>=:b and end_date<=:c order by timestamp(modified_time) desc");
		query.setParameter("org", newDetails.getOrgDetails().getOrg_id());
		query.setParameter("id", newDetails.getReporter());
		query.setParameter("a", false);
		query.setParameter("b", newDetails.getStart_date());
		query.setParameter("c", newDetails.getEnd_date());
		@SuppressWarnings("unchecked")
		List<LeaveTrackerDetails> details = query.getResultList();
		logger.info("LeaveTrackerDetailsDaoImpl(getRequestLeaveCount) >>Exit");
		return details;
		// TODO Auto-generated method stub

	}

	@Override
	public int getLeaveRequestByEmpIdByStatusPending(String emp_id, String activeAction) {
		// TODO Auto-generated method stub
		final Session session = entityManager.unwrap(Session.class);
		logger.info("LeaveTrackerDetailsDaoImpl(getLeaveRequestByEmpIdByStatusPending) >>entry");
		boolean action = false;
		if (activeAction.equals("deactivate")) {
			action = true;
		} else {
			action = false;
		}
		final Query query = session.createNativeQuery(
				"update leave_tracker_details set is_deleted=:a where emp_id=:i and approval_status=:j");
		query.setParameter("i", emp_id);
		query.setParameter("j", "Pending");
		query.setParameter("a", action);
		@SuppressWarnings("unchecked")
		int details = query.executeUpdate();
		logger.info("LeaveTrackerDetailsDaoImpl(getLeaveRequestByEmpIdByStatusPending) >>Exit");
		return details;
	}

	@Override
	public int bulkGetLeaveRequestByEmpIdByStatusPending(JSONArray ids, String activeAction) {
		// TODO Auto-generated method stub
		final Session session = entityManager.unwrap(Session.class);
		logger.info("LeaveTrackerDetailsDaoImpl(bulkGetLeaveRequestByEmpIdByStatusPending) >>entry");
		boolean action = false;
		if (activeAction.equals("deactivate")) {
			action = true;
		} else {
			action = false;
		}
		String id_list = new String();
		for (int i = 0; i < ids.length(); i++) {
			id_list += "'" + ids.get(i) + "'" + ",";
		}

		StringBuffer id_sb = new StringBuffer(id_list);
		id_sb.deleteCharAt(id_sb.length() - 1);
		final Query query = session.createNativeQuery(
				"update leave_tracker_details set is_deleted=:i where approval_status=:j and emp_id in (" + id_sb
						+ ")");
		query.setParameter("j", "Pending");
		query.setParameter("i", action);
		int details = query.executeUpdate();
		logger.info("LeaveTrackerDetailsDaoImpl(bulkGetLeaveRequestByEmpIdByStatusPending) Exit>>");
		return details;
//		final Query query = session.createNativeQuery("update leave_tracker_details set is_deleted=:a where emp_id=:i and approval_status=:j");
//		query.setParameter("i", emp_id);
//		query.setParameter("j", "Pending");
//		query.setParameter("a", action);
//		@SuppressWarnings("unchecked")
//	    int details = query.executeUpdate();
//		logger.info("LeaveTrackerDetailsDaoImpl(getLeaveRequestByEmpIdByStatusPending) >>Exit");
//		return details;
	}

	@Override
	public List<LeaveTrackerDetails> getTodayLeaveUserList(Long id) {
		// TODO Auto-generated method stub
		logger.info("LeaveTrackerDetailsDaoImpl(getTodayLeaveUserList)>> Entry");
		final Session session = entityManager.unwrap(Session.class);
		List<LeaveTrackerDetails> details = new ArrayList<LeaveTrackerDetails>();
		LocalDate now = LocalDate.now();
//		LocalDate firstDay = now.with(firstDayOfYear());
//		LocalDate lastDay = now.with(lastDayOfYear());
		String currentyear_firstday = now + " 00:00:00";
		String currentyear_lastday = now + " 23:59:59";

//		Date date123 = null;

//		String newDate = formatter.format(date);

//			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  

//				date123 = df.format(date);
//				System.out.println("date123"+date123);
//				LocalDateTime datetime = LocalDateTime.parse(date123, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//				System.out.println("datetime"+datetime);
		final Query query = session.createQuery(
				"from LeaveTrackerDetails where orgDetails.org_id=:org and is_deleted=:a and approval_status =:s and start_date <='"
						+ currentyear_firstday + "'and end_date >='" + currentyear_firstday
						+ "' order by timestamp(modified_time) desc");
		query.setParameter("org", id);
		query.setParameter("a", false);
		query.setParameter("s", "Approved");

//				query.setParameter("c", newDetails.getEnd_date());
		@SuppressWarnings("unchecked")
		List<LeaveTrackerDetails> leaveData = query.getResultList();
		System.out.println("leaveData size" + leaveData.size());
		details.addAll(leaveData);

//			try {
//				date123 = dt.format(newstring);
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} 
//			System.out.print("date123"+date123);
//			System.out.println(date1+"id.."+id);

		logger.info("LeaveTrackerDetailsDaoImpl(getTodayLeaveUserList)>> Exit");

		return details;
	}

	@Override
	public ArrayList<BigInteger> getActiveOrgIdsWithLeaveTrackerPlan() {
		logger.info("LeaveTrackerDetailsDaoImpl(getActiveOrgIdsWithLeaveTrackerPlan) >>Entry");
		final Session session = entityManager.unwrap(Session.class);

		final Query query = session.createNativeQuery(
				"select A.org_id from org_details as A join pricing_plan_details as B ON A.plan_id = B.id where A.is_deleted = false and A.is_activated = true and B.modules like concat('%','leave-tracker','%')");
		@SuppressWarnings("unchecked")
		ArrayList<BigInteger> ids = new ArrayList<BigInteger>();
		ids = (ArrayList<BigInteger>) query.getResultList();
		if (ids != null && ids.size() > 0) {
			logger.info("LeaveTrackerDetailsDaoImpl(getActiveOrgIdsWithLeaveTrackerPlan) >>Exit");
			System.out.println(ids);
			return ids;
		} else {
			logger.info("LeaveTrackerDetailsDaoImpl(getActiveOrgIdsWithLeaveTrackerPlan) >>Exit");
			return null;
		}
		// TODO Auto-generated method stub

	}

// 	@Override
// 	public List<LeaveTrackerDetails> getRequestLeaveDetailsPaginationCountData(LeaveTrackerDetails newDetails, int pageSize, int pageIndexSize) {
// 		// TODO Auto-generated method stub
// 		logger.info("LeaveTrackerDetailsDaoImpl(getLeaveByEmpIdAndYear)>> Entry");
// 		final Session session = entityManager.unwrap(Session.class);
// 		int fromData = pageIndexSize;
// 		int pageSizeValue = pageSize;
// //		final Query query = session.createQuery(
// //				"from LeaveTrackerDetails where orgDetails.org_id=:org and reporter=:id and is_deleted=:a and start_date>=:b and end_date<=:c order by timestamp(modified_time) desc");
// 		final Query query = session.createQuery(
// 				"from LeaveTrackerDetails where orgDetails.org_id=:org and reporter=:id and is_deleted=:a and start_date>=:b and end_date<=:c and approval_status=:z order by timestamp(modified_time) desc");
// 		query.setParameter("org", newDetails.getOrgDetails().getOrg_id());
// 		query.setParameter("id", newDetails.getReporter());
// 		query.setParameter("a", false);
// 		query.setParameter("b", newDetails.getStart_date());
// 		query.setParameter("c", newDetails.getEnd_date());
// 		query.setParameter("z", "Pending");
// 		query.setFirstResult(fromData);
// 		query.setMaxResults(pageSizeValue);
// //		query.setFirstResult(0);
// //		query.setMaxResults(0);
// 		@SuppressWarnings("unchecked")
// 		final Query query1 = session.createQuery(
// 				"from LeaveTrackerDetails where orgDetails.org_id=:org and reporter=:id and is_deleted=:a and start_date>=:b and end_date<=:c and approval_status != :z order by timestamp(modified_time) desc");
// 		query1.setParameter("org", newDetails.getOrgDetails().getOrg_id());
// 		query1.setParameter("id", newDetails.getReporter());
// 		query1.setParameter("a", false);
// 		query1.setParameter("b", newDetails.getStart_date());
// 		query1.setParameter("c", newDetails.getEnd_date());
// 		query1.setParameter("z", "Pending");
// 		query.setFirstResult(fromData);
// 		query.setMaxResults(pageSizeValue);
// //		query1.setFirstResult(0);
// //		query1.setMaxResults(0);
// 		final List<LeaveTrackerDetails> details = query.getResultList();
// 		final List<LeaveTrackerDetails> details1 = query1.getResultList();
// 		final ArrayList<LeaveTrackerDetails> leaveRequestData = new ArrayList<LeaveTrackerDetails>();
// 		leaveRequestData.addAll(details);
// 		leaveRequestData.addAll(details1);
// 		logger.info("LeaveTrackerDetailsDaoImpl(getLeaveByEmpIdAndYear) >> Exit");
// 		return leaveRequestData;

// 	}

}
