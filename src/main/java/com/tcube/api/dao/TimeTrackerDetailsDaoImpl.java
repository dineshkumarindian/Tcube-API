package com.tcube.api.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.tcube.api.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.tcube.api.service.EmployeeDetailsService;
import com.tcube.api.utils.ImageProcessor;

import net.bytebuddy.dynamic.loading.PackageDefinitionStrategy.Definition.Undefined;

@Component
public class TimeTrackerDetailsDaoImpl implements TimeTrackerDetailsDao {
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	EmployeeDetailsService employeeDetailsService;
	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = (Logger) LogManager.getLogger(TimeTrackerDetailsDaoImpl.class);

	@Override
	public TimeTrackerDetails createTask(TimeTrackerDetails timeTrackerDetails) {
		logger.info("TimeTrackerDetailsDaoImpl(createtasks) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		try {
			timeTrackerDetails.setCreated_time(new Date());
			timeTrackerDetails.setModified_time(new Date());
			logger.debug("TimeTrackerDetailsDaoImpl obj:" + new Gson().toJson(timeTrackerDetails));
			session.save(timeTrackerDetails);
			if (timeTrackerDetails.getId() != null) {
				entityManager.persist(timeTrackerDetails);
				return timeTrackerDetails;
			} else {
				entityManager.merge(timeTrackerDetails);
				return timeTrackerDetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("TimeTrackerDetailsDaoImpl(createtasks) >> Exit ");

		return timeTrackerDetails;
	}

	@Override
	public TimeTrackerDetails getTaskById(Long id) {
		logger.info("TimeTrackerDetailsDaoImpl(getTaskById) Entry>> Request -> " + id);
		final Session session = entityManager.unwrap(Session.class);
		final TimeTrackerDetails details = (TimeTrackerDetails) session.get(TimeTrackerDetails.class, id);
		logger.info("TimeTrackerDetailsDaoImpl(getTaskById) Exit>>-> ");
		return details;
	}

	@Override
	public TimeTrackerDetails updateEndTime(TimeTrackerDetails timeTrackerDetails) {
		logger.info("TimeTrackerDetailsDaoImpl(updateEndTime) Entry>>-> ");
		final Session session = entityManager.unwrap(Session.class);
		try {
			timeTrackerDetails.setModified_time(new Date());
			timeTrackerDetails.setIs_active(false);
			logger.debug("appInfo obj:" + new Gson().toJson(timeTrackerDetails));
			session.update(timeTrackerDetails);
			if (timeTrackerDetails.getId() == 0) {
				entityManager.persist(timeTrackerDetails);
				return timeTrackerDetails;
			} else {
				entityManager.merge(timeTrackerDetails);
				return timeTrackerDetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("TimeTrackerDetailsDaoImpl(updateEndTime) Exit>>-> ");
		return timeTrackerDetails;
	}

	@Override
	public TimeTrackerDetails updateTaskDetails(TimeTrackerDetails timeTrackerDetails) {
		logger.info("TimeTrackerDetailsDaoImpl(updateTaskDetails) Entry>>-> ");
		final Session session = entityManager.unwrap(Session.class);
		try {
			timeTrackerDetails.setModified_time(new Date());
			logger.debug("appInfo obj:" + new Gson().toJson(timeTrackerDetails));
			session.update(timeTrackerDetails);
			if (timeTrackerDetails.getId() == 0) {
				entityManager.persist(timeTrackerDetails);
				return timeTrackerDetails;
			} else {
				entityManager.merge(timeTrackerDetails);
				return timeTrackerDetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("TimeTrackerDetailsDaoImpl(updateTaskDetails) Exit>>-> ");
		return timeTrackerDetails;
	}

	@Override
	public TimeTrackerDetails deleteTaskDetails(TimeTrackerDetails details) {
		logger.info("TimeTrackerDetailsDaoImpl(deleteTaskDetails) Entry>>");
		final Session session = entityManager.unwrap(Session.class);
		try {
			details.setModified_time(new Date());
			session.update(details);
			if (details.getId() != null) {
				entityManager.persist(details);
				return details;
			} else {
				entityManager.merge(details);
				return details;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("TimeTrackerDetailsDaoImpl(deleteTaskDetails) Exit>>");
		return details;
	}

	@Override
	public JSONObject getTaskByEmpidAndDate(TimeTrackerDetails timeTrackerDetails) {
		logger.info("TimeTrackerDetailsDaoImpl (getTaskByEmpidAndDate) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked") final Query query = session
				.createQuery("from TimeTrackerDetails where is_deleted=:i and date_of_request=:j and emp_id=:k");
		query.setParameter("i", false);
		query.setParameter("j", timeTrackerDetails.getDate_of_request());
		query.setParameter("k", timeTrackerDetails.getEmp_id());
		List<TimeTrackerDetails> details = query.getResultList();
		if(details.size()>0) {
			String date = timeTrackerDetails.getDate_of_request();
			String empid = timeTrackerDetails.getEmp_id();
			BigDecimal total_time = ((BigDecimal) (session.createNativeQuery(
							"select sum(task_duration_ms) from time_tracker_details where is_deleted=false and date_of_request="
									+ "'" + date + "'" + " and emp_id=" + "'" + empid + "'")
					.getSingleResult()));

			BigDecimal billable_total_time = ((BigDecimal) (session.createNativeQuery(
							"select sum(task_duration_ms) from time_tracker_details where is_deleted=false and bill='Billable' and approval_status !='Not Submitted' and date_of_request="
									+ "'" + date + "'" + " and emp_id=" + "'" + empid + "'")
					.getSingleResult()));

			BigDecimal non_billable_total_time = ((BigDecimal) (session.createNativeQuery(
							"select sum(task_duration_ms) from time_tracker_details where is_deleted=false and bill='Non Billable' and approval_status !='Not Submitted' and date_of_request="
									+ "'" + date + "'" + " and emp_id=" + "'" + empid + "'")
					.getSingleResult()));
			TimeTrackerDetails tDetails = details.get(0);
			EmployeeDetails empDetails = employeeDetailsService.getAllEmployeeDetailsByID(tDetails.getEmp_id());
			JSONObject json = new JSONObject();
			json.put("details", new Gson().toJson(details));
			json.put("total_time", total_time);
			if(billable_total_time == null) {
				json.put("billable_total_time", 0);
			}else json.put("billable_total_time", billable_total_time);

			if(non_billable_total_time == null) {
				json.put("non_billable_total_time", 0);
			}else json.put("non_billable_total_time", non_billable_total_time);

			json.put("emp_name", empDetails.getFirstname()+" "+ empDetails.getLastname());
			json.put("emp_designation",empDetails.getDesignationDetails().getDesignation());
			if(empDetails.getProfile_image() != null) {
				json.put("profile_img", ImageProcessor.decompressBytes(empDetails.getProfile_image()));
			}
			JSONArray projects = new JSONArray();
			String projectCheck = "";
			for(TimeTrackerDetails i : details) {
				if(projectCheck.contains(i.getProject())) {
					continue;
				}else {
					projectCheck += i.getProject();
					projects.put(i.getProject());
				}
			}
			json.put("projects", projects);
			logger.info("TimeTrackerDetailsDaoImpl (getTaskByEmpidAndDate) >> Exit ");
			return json;
		} else {
			logger.info("TimeTrackerDetailsDaoImpl (getTaskByEmpidAndDate) >> Exit ");
			return null;
		}
	}

	// this api get Submitted , Approved and rejected tasks by emp id an date of request
	@Override
	public JSONObject getSubmittedTaskByEmpidAndDate(TimeTrackerDetails newDetails) {
		logger.info("TimeTrackerDetailsDaoImpl (getSubmittedTaskByEmpidAndDate) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked") final Query query = session
				.createQuery("from TimeTrackerDetails where is_deleted=:i and date_of_request=:j and emp_id=:k and timesheet_id =:z ");
		query.setParameter("i", false);
		query.setParameter("j", newDetails.getDate_of_request());
		query.setParameter("k", newDetails.getEmp_id());
		query.setParameter("z", newDetails.getTimesheet_id());
		List<TimeTrackerDetails> details = query.getResultList();
		if(details.size()>0) {
			String date = newDetails.getDate_of_request();
			String empid = newDetails.getEmp_id();
			long tSheetId = newDetails.getTimesheet_id() ;
			BigDecimal total_time = ((BigDecimal) (session.createNativeQuery(
							"select sum(task_duration_ms) from time_tracker_details where is_deleted=false and timesheet_id ="+tSheetId+ " and date_of_request="
									+ "'" + date + "'" + " and emp_id=" + "'" + empid + "'")
					.getSingleResult()));

			BigDecimal billable_total_time = ((BigDecimal) (session.createNativeQuery(
							"select sum(task_duration_ms) from time_tracker_details where is_deleted=false and bill='Billable' and timesheet_id ="+tSheetId+" and date_of_request="
									+ "'" + date + "'" + " and emp_id=" + "'" + empid + "'")
					.getSingleResult()));

			BigDecimal non_billable_total_time = ((BigDecimal) (session.createNativeQuery(
							"select sum(task_duration_ms) from time_tracker_details where is_deleted=false and bill='Non Billable' and timesheet_id ="+tSheetId+" and date_of_request="
									+ "'" + date + "'" + " and emp_id=" + "'" + empid + "'")
					.getSingleResult()));
			TimeTrackerDetails tDetails = details.get(0);
			EmployeeDetails empDetails = employeeDetailsService.getAllEmployeeDetailsByID(tDetails.getEmp_id());
			JSONObject json = new JSONObject();
			json.put("details", new Gson().toJson(details));
			json.put("total_time", total_time);
			if(billable_total_time == null) {
				json.put("billable_total_time", 0);
			}else json.put("billable_total_time", billable_total_time);

			if(non_billable_total_time == null) {
				json.put("non_billable_total_time", 0);
			}else json.put("non_billable_total_time", non_billable_total_time);

			json.put("emp_name", empDetails.getFirstname());
			json.put("emp_designation",empDetails.getDesignationDetails().getDesignation());
			if(empDetails.getProfile_image() != null) {
				json.put("profile_img", ImageProcessor.decompressBytes(empDetails.getProfile_image()));
			}
			JSONArray projects = new JSONArray();
			String projectCheck = "";
			for(TimeTrackerDetails i : details) {
				if(projectCheck.contains(i.getProject())) {
					continue;
				}else {
					projectCheck += i.getProject();
					projects.put(i.getProject());
				}
			}
			json.put("projects", projects);
			logger.info("TimeTrackerDetailsDaoImpl (getSubmittedTaskByEmpidAndDate) >> Exit ");
			return json;
		} else {
			logger.info("TimeTrackerDetailsDaoImpl (getSubmittedTaskByEmpidAndDate) >> Exit ");
			return null;
		}
	}

	@Override
	public List<TimeTrackerDetails> getSubmittedTaskByEmpidAndDate(String date , String emp_id) {
		logger.info("TimeTrackerDetailsDaoImpl (getSubmittedTaskByEmpidAndDate) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked") final Query query = session
				.createQuery("from TimeTrackerDetails where is_deleted=:i and date_of_request=:j and emp_id=:k and approval_status=:s ");
		query.setParameter("i", false);
		query.setParameter("s", "Submitted");
		query.setParameter("j",date);
		query.setParameter("k", emp_id);
		List<TimeTrackerDetails> details = query.getResultList();
		return details;
	}

	@Override
	public List<TimeTrackerDetails> getApprovedTaskByEmpidAndDate(String date , String emp_id) {
		logger.info("TimeTrackerDetailsDaoImpl (getSubmittedTaskByEmpidAndDate) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked") final Query query = session
				.createQuery("from TimeTrackerDetails where is_deleted=:i and date_of_request=:j and emp_id=:k and approval_status=:s ");
		query.setParameter("i", false);
		query.setParameter("s", "Approved");
		query.setParameter("j",date);
		query.setParameter("k", emp_id);
		List<TimeTrackerDetails> details = query.getResultList();
		return details;
	}


	@Override
	public TimeTrackerDetails getTaskDetailsByActive(String empid) {
		logger.info("TimeTrackerDetailsDaoImpl (getTaskDetailsByActive) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked") final Query query = session
				.createQuery("from TimeTrackerDetails where emp_id=:i and is_active=:j and is_deleted=: k");
		query.setParameter("i", empid);
		query.setParameter("j", true);
		query.setParameter("k", false);
		List<TimeTrackerDetails> details = query.getResultList();
		TimeTrackerDetails data = new TimeTrackerDetails();
		if(details.size()>0) {
			data = details.get(0);
			return data;
		}
		logger.info("JobDetailsDaoImpl (getAllJobDetails) >> Exit ");
		return null;
	}

	@Override
	public TimeTrackerDetails updateNewTimeInterval(TimeTrackerDetails timeTrackerDetails) {
		logger.info("TimeTrackerDetailsDaoImpl(updateNewTimeInterval) Entry>>-> ");
		final Session session = entityManager.unwrap(Session.class);
		try {
			timeTrackerDetails.setModified_time(new Date());
			timeTrackerDetails.setIs_active(true);
			logger.debug("appInfo obj:" + new Gson().toJson(timeTrackerDetails));
			session.update(timeTrackerDetails);
			if (timeTrackerDetails.getId() == 0) {
				entityManager.persist(timeTrackerDetails);
				return timeTrackerDetails;
			} else {
				entityManager.merge(timeTrackerDetails);
				return timeTrackerDetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("TimeTrackerDetailsDaoImpl(updateNewTimeInterval) Exit>>-> ");
		return timeTrackerDetails;
	}

	@Override
	public JSONObject gettotaltimebyprojectjob(String project, String job,Long orgId) {
		logger.info("TimeTrackerDetailsDaoImpl (gettotaltimebyprojectjob) >> Entry ");
		JSONObject durationObj = new JSONObject();
		final Session session = entityManager.unwrap(Session.class);
		BigDecimal total_time = ((BigDecimal) (session.createNativeQuery(
						"select sum(task_duration_ms) from time_tracker_details where is_deleted=false and org_id=" +"'" +orgId + "'" +" and approval_status='Approved' and project=" + "'"
								+ project + "'" + " and job=" + "'" + job + "'")
				.getSingleResult()));
		if (total_time == null) {
			total_time = new BigDecimal("0");
		}
		long remainingMillis = (total_time.longValue()); // Your value comes here.

		long hours = TimeUnit.MILLISECONDS.toHours(remainingMillis);
		long hoursMillis = TimeUnit.HOURS.toMillis(hours);

		long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMillis - hoursMillis);
		long minutesMillis = TimeUnit.MINUTES.toMillis(minutes);

		long seconds = TimeUnit.MILLISECONDS.toSeconds(remainingMillis - hoursMillis - minutesMillis);
		String resultString = "";
		if (hours > 0) {
			resultString = hours + " Hrs : " + minutes + " Mins ";
		} else {
			resultString = hours + " Hrs : " + minutes + " Mins ";
		}
		durationObj.put("duration_str", resultString);
		durationObj.put("duration_ms", remainingMillis);
		logger.info("TimeTrackerDetailsDaoImpl (gettotaltimebyprojectjob) >> Entry ");
		return durationObj;
	}

	@Override
	public JSONObject gettotaltimebyEmpId(String project, String job, String empid) {
		logger.info("TimeTrackerDetailsDaoImpl (gettotaltimebyEmpId) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		BigDecimal total_time = ((BigDecimal) (session.createNativeQuery(
						"select sum(task_duration_ms) from time_tracker_details where is_deleted=false and approval_status='Approved' and project=" + "'"
								+ project + "'" + " and job=" + "'" + job + "'" + " and emp_id=" + "'" + empid + "'")
				.getSingleResult()));
		if (total_time == null) {
			total_time = new BigDecimal("0");
		}
		long remainingMillis = (total_time.longValue()); // Your value comes here.

		long hours = TimeUnit.MILLISECONDS.toHours(remainingMillis);
		long hoursMillis = TimeUnit.HOURS.toMillis(hours);

		long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMillis - hoursMillis);
		long minutesMillis = TimeUnit.MINUTES.toMillis(minutes);

		long seconds = TimeUnit.MILLISECONDS.toSeconds(remainingMillis - hoursMillis - minutesMillis);
		String resultString = "";
		if (hours > 0) {
			resultString = hours + " Hrs : " + minutes + " Mins ";
		} else {
			resultString = hours + " Hrs : " + minutes + " Mins ";
		}
		JSONObject json = new JSONObject();
		json.put("duration", resultString);
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		double h = 0;
		if(minutes > 0) {
			double  min  = 0;
			min = ((double)  minutes / 60);
			h = (double) (hours + min);
		}
		json.put("hours", h);
		logger.info("TimeTrackerDetailsDaoImpl (gettotaltimebyEmpId) >> Exit ");
		return json;
	}

	@Override
	public JSONObject gettotaltimebyproject(String project, Long orgId) {
		logger.info("TimeTrackerDetailsDaoImpl (gettotaltimebyEmpId) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		JSONObject durationObj = new JSONObject();
		BigDecimal total_time = ((BigDecimal) (session.createNativeQuery(
						"select sum(task_duration_ms) from time_tracker_details as A join  job_details as B on A.project = B.project_name and A.org_id = B.org_id and A.job = B.job_name and A.project = B.project_name where A.is_deleted=false and A.approval_status ='Approved' and A.project = " + "'" + project + "'" +" and A.org_id=" +"'" + orgId +"'" +" and A.is_deleted=false and B.is_deleted=false")
				.getSingleResult()));
		if (total_time == null) {
			total_time = new BigDecimal("0");
		}
		long remainingMillis = (total_time.longValue()); // Your value comes here.

		long hours = TimeUnit.MILLISECONDS.toHours(remainingMillis);
		long hoursMillis = TimeUnit.HOURS.toMillis(hours);

		long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMillis - hoursMillis);
		long minutesMillis = TimeUnit.MINUTES.toMillis(minutes);

		long seconds = TimeUnit.MILLISECONDS.toSeconds(remainingMillis - hoursMillis - minutesMillis);
		String resultString = "";
		if (hours > 0) {
			resultString = hours + " Hrs : " + minutes + " Mins ";
		} else {
			resultString = hours + " Hrs : " + minutes + " Mins ";
		}
		durationObj.put("duration_str", resultString);
		if(resultString =="0 Hrs : 0 Mins ") {
			resultString="0";
		}
		durationObj.put("duration_ms", remainingMillis);
		logger.info("TimeTrackerDetailsDaoImpl (gettotaltimebyEmpId) >> Exit ");
		return durationObj;
	}

	@Override
	public JSONObject getFilterdata(String start_date, String end_date, JSONArray client_id, JSONArray project,
									JSONArray job, JSONArray bill, JSONArray status, TimeTrackerDetails details) {
		logger.info("TimeTrackerDetailsDaoImpl (getFilterdata) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);

		// client array to String concatenate process
		String client_list = new String();
		for (int i = 0; i < client_id.length(); i++) {
			client_list += client_id.get(i) + ",";
		}
		StringBuffer client_id_sb = new StringBuffer(client_list);
		client_id_sb.deleteCharAt(client_id_sb.length() - 1);

		// project array to string concatenate process
		String project_list = new String();
		for (int i = 0; i < project.length(); i++) {
			project_list += "'" + project.get(i) + "'" + ",";
		}
		StringBuffer project_sb = new StringBuffer(project_list);
		project_sb.deleteCharAt(project_sb.length() - 1);

		// job array to string concatenate process
		String job_list = new String();
		for (int i = 0; i < job.length(); i++) {
			job_list += "'" + job.get(i) + "'" + ",";
		}
		StringBuffer job_sb = new StringBuffer(job_list);
		job_sb.deleteCharAt(job_sb.length() - 1);

		// bill array to string concatenate process
		String bill_list = new String();
		for (int i = 0; i < bill.length(); i++) {
			bill_list += "'" + bill.get(i) + "'" + ",";
		}
		StringBuffer bill_sb = new StringBuffer(bill_list);
		bill_sb.deleteCharAt(bill_sb.length() - 1);

		// status array to string concatenate process
		String status_list = new String();
		for (int i = 0; i < status.length(); i++) {
			status_list += "'" + status.get(i) + "'" + ",";
		}
		StringBuffer status_sb = new StringBuffer(status_list);
		status_sb.deleteCharAt(status_sb.length() - 1);
		@SuppressWarnings("unchecked") final Query query = session
				.createQuery("from TimeTrackerDetails where is_deleted=:i and emp_id=:j and date_of_request>= " + "'"
						+ start_date + "'" + " and date_of_request<= " + "'" + end_date + "' and project IN ("
						+ project_sb + ") and client_id IN (" + client_id_sb + ") and job IN (" + job_sb
						+ ") and bill IN (" + bill_sb + ") and approval_status IN (" + status_sb + ")");
		query.setParameter("i", false);
		query.setParameter("j", details.getEmp_id());

		List<TimeTrackerDetails> details1 = query.getResultList();
		JSONObject json = new JSONObject();
		json.put("details", new Gson().toJson(details1));

		// to calculate the totaltime for the filtered time intervals
		String empid = details.getEmp_id();
		BigDecimal total_time = ((BigDecimal) (session.createNativeQuery(
						"select sum(task_duration_ms) from time_tracker_details where is_deleted=false and date_of_request>= "
								+ "'" + start_date + "'" + " and date_of_request<= " + "'" + end_date + "'  and emp_id=" + "'"
								+ empid + "' and project IN (" + project_sb + ") and client_id IN (" + client_id_sb
								+ ") and job IN (" + job_sb + ") and bill IN (" + bill_sb + ") and approval_status IN ("
								+ status_sb + ")")
				.getSingleResult()));
		json.put("details", new Gson().toJson(details1));
		json.put("total_time", total_time);
		logger.info("TimeTrackerDetailsDaoImpl (getFilterdata) >> Exit ");
		return json;
	}

	@Override
	public JSONObject getBilChartEmp(String empid, String date) {
		logger.info("TimeTrackerDetailsDaoImpl (getBilChartEmp) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		BigDecimal non_billable_time = ((BigDecimal) (session.createNativeQuery(
						"select sum(task_duration_ms) from time_tracker_details where is_deleted=false and bill='Non Billable' and emp_id="
								+ "'" + empid + "'" + " and date_of_request=" + "'" + date + "'")
				.getSingleResult()));
		if (non_billable_time == null) {
			non_billable_time = new BigDecimal("0");
		}
		long remainingMillisnonbils = (non_billable_time.longValue()); // Your value comes here.
		long non_billable_hours = remainingMillisnonbils / (1000 * 60 * 60);

		BigDecimal billable_time = ((BigDecimal) (session.createNativeQuery(
						"select sum(task_duration_ms) from time_tracker_details where is_deleted=false and bill='Billable' and emp_id="
								+ "'" + empid + "'" + " and date_of_request=" + "'" + date + "'")
				.getSingleResult()));
//		System.out.println(billable_time);

		if (billable_time == null) {
			billable_time = new BigDecimal("0");
		}
		long remainingMillisbills = (billable_time.longValue()); // Your value comes here.
		long billable_hours = remainingMillisbills / (1000 * 60 * 60);

//		System.out.println(billable_hours);
		JSONObject json = new JSONObject();
		json.put("non_billable_time", remainingMillisnonbils);
		json.put("billable_time", remainingMillisbills);
		logger.info("TimeTrackerDetailsDaoImpl (getBilChartEmp) >> Exit ");
		return json;
	}

	@Override
	public TimeTrackerDetails updateReporterdetails(TimeTrackerDetails details) {
		logger.info("TimeTrackerDetailsDaoImpl(updateReporterdetails) Entry>>-> ");
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
		logger.info("TimeTrackerDetailsDaoImpl(updateTaskDetails) Exit>>-> ");
		return details;
	}

	@Override
	public TimeTrackerDetails updateprojectname(String olddata, String newdata, Long id) {
		logger.info("TimeTrackerDetailsDaoImpl (updateprojectname) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery("from TimeTrackerDetails where project=:j  and org_id=:l");
		query.setParameter("j", olddata);
		query.setParameter("l", id);
		@SuppressWarnings("unchecked")
		List<TimeTrackerDetails> details = query.getResultList();
		for (int i = 0; i < details.size(); i++) {
			details.get(i).setProject(newdata);
			if (details.get(i).getId() == 0) {
				entityManager.persist(details.get(i));
			} else {
				entityManager.merge(details.get(i));
			}
		}
		logger.info("TimeTrackerDetailsDaoImpl(updateprojectname) >> Exit ");
		return null;
	}

	@Override
	public TimeTrackerDetails updateJobDetails(String olddata, String newdata, Long id, String project_name,
											   String bill) {
		logger.info("TimeTrackerDetailsDaoImpl (updateJobDetails) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery("from TimeTrackerDetails where job=:j and org_id=:l and project=:i");
		query.setParameter("i", project_name);
		query.setParameter("j", olddata);
		query.setParameter("l", id);
		@SuppressWarnings("unchecked")
		List<TimeTrackerDetails> details = query.getResultList();
		for (int i = 0; i < details.size(); i++) {
			details.get(i).setJob(newdata);
			if(bill != null) {
				details.get(i).setBill(bill);
			}
			if (details.get(i).getId() == 0) {
				entityManager.persist(details.get(i));
			} else {
				entityManager.merge(details.get(i));
			}
		}
		logger.info("TimeTrackerDetailsDaoImpl(updateJobDetails) >> Exit ");
		return null;
	}

	@Override
	public JSONObject getBilChartEmpMonth(String empid , String startdate , String enddate) {
		logger.info("TimeTrackerDetailsDaoImpl (getBilChartEmpMonth) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		long remainingMillisnonbils = 0;
		long remainingMillisbills = 0;
		try {
			List<Date> dates = new ArrayList<Date>();

			DateFormat formatter;

			formatter = new SimpleDateFormat("dd-MM-yyyy");
			Date startDate;
			startDate = (Date) formatter.parse(startdate);
			Date endDate = (Date) formatter.parse(enddate);
			long interval = 24 * 1000 * 60 * 60; // 1 hour in millis
			long endTime = endDate.getTime(); // create your endtime here, possibly using Calendar or Date
			long curTime = startDate.getTime();

			//To get All Dates between given two days
			while (curTime <= endTime) {
				dates.add(new Date(curTime));
				curTime += interval;
			}
			for (int k = 0; k < dates.size(); k++) {
				Date lDate = (Date) dates.get(k);
				String date = formatter.format(lDate);
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
				date = (sdf2.format(sdf.parse(date)));
				BigDecimal non_billable_time = ((BigDecimal) (session.createNativeQuery(
								"select sum(task_duration_ms) from time_tracker_details where is_deleted=false and bill='Non Billable' and emp_id="
										+ "'" + empid + "'" + " and date_of_request=" + "'" + date + "'")
						.getSingleResult()));
				if (non_billable_time == null) {
					non_billable_time = new BigDecimal("0");
				}
				remainingMillisnonbils += (non_billable_time.longValue()); // Your value comes here.

				BigDecimal billable_time = ((BigDecimal) (session.createNativeQuery(
								"select sum(task_duration_ms) from time_tracker_details where is_deleted=false and bill='Billable' and emp_id="
										+ "'" + empid + "'" + " and date_of_request=" + "'" + date + "'")
						.getSingleResult()));

				if (billable_time == null) {
					billable_time = new BigDecimal("0");
				}
				remainingMillisbills += (billable_time.longValue()); // Your value comes here.
			}
		} catch(ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		JSONObject json = new JSONObject();
		json.put("non_billable_time", remainingMillisnonbils);
		json.put("billable_time", remainingMillisbills);
		logger.info("TimeTrackerDetailsDaoImpl (getBilChartEmpMonth) >> Exit ");
		return json;
	}

	@Override
	public List<JSONObject> getProjectJobDropdownByOrgId(Long orgid,String empid) {
		logger.info("TimeTrackerDetailsDaoImpl (getProjectJobDropdownByOrgId) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		List<JSONObject> dropdown = new ArrayList<JSONObject>();
		@SuppressWarnings("unchecked")

		final Query query = session.createQuery("from ProjectDetails where orgDetails.org_id=:i and is_deleted=: k and is_activated=: a and project_status=: j");
		query.setParameter("i", orgid);
		query.setParameter("k", false);
		query.setParameter("a", true);
		query.setParameter("j", "Inprogress");
		List<ProjectDetails> pj_details = query.getResultList();
		List<JSONObject> project = new ArrayList<JSONObject>();
		if(pj_details.size()>0) {
			for(int i=0;i<pj_details.size();i++) {
				for(int j=0;j<pj_details.get(i).getResourceDetails().size();j++){
					JSONObject data = new JSONObject();
					if(empid.equals(pj_details.get(i).getResourceDetails().get(j).getEmployeeDetails().getId())) {
						data.put("name", pj_details.get(i).getProject_name());
						data.put("id", pj_details.get(i).getId());
						data.put("client_id", pj_details.get(i).getClientDetails().getId());
						project.add(data);
					}
				}
			}
		}

		final Query query2 = session.createQuery("from JobDetails where orgDetails.org_id=:i and is_deleted=: k and is_activated=: a and job_status=: j");
		query2.setParameter("i", orgid);
		query2.setParameter("k", false);
		query2.setParameter("a", true);
		query2.setParameter("j", "Inprogress");
		List<JobDetails> jb_details = query2.getResultList();
		List<JSONObject> job = new ArrayList<JSONObject>();
		if(jb_details.size()>0) {
			for(int i=0;i<jb_details.size();i++) {
				for(int j=0;j<jb_details.get(i).getJobAssigneeDetails().size();j++) {
					JSONObject data = new JSONObject();
					if(empid.equals(jb_details.get(i).getJobAssigneeDetails().get(j).getEmployeeDetails().getId()) && jb_details.get(i).getJobAssigneeDetails().get(j).getStatus().equals("Active")) {
						data.put("id",jb_details.get(i).getId());
						data.put("name",jb_details.get(i).getJob_name());
						data.put("bill",jb_details.get(i).getBill());
						data.put("project_id",jb_details.get(i).getProject_id());
						data.put("mail_sended", jb_details.get(i).getMail_sended());
						job.add(data);
					}
				}
			}
		}

		JSONObject projectdata = new JSONObject();
		projectdata.put("projectdetails", project);

		JSONObject jobdata = new JSONObject();
		jobdata.put("jobdetails", job);

		dropdown.add(projectdata);
		dropdown.add(jobdata);
		logger.info("TimeTrackerDetailsDaoImpl (getProjectJobDropdownByOrgId) >> Exit ");
		return dropdown;
	}

	@Override
	public JSONObject getHoursByOrgIdAndProject(long orgId, String sd_date, String ed_date) {

		// First need get the active projects based on org id
		logger.info("TimeTrackerDetailsDaoImpl (getHoursByOrgIdAndProject) Entry>> ");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createNativeQuery("SELECT DISTINCT project_name FROM project_details where org_id="+orgId+" and is_deleted=false and is_activated=true");
		List<String> projectDetails = query.getResultList();
		List<BigDecimal> billableTimeList = new ArrayList<BigDecimal>();
		List<BigDecimal> nonBillableTimeList = new ArrayList<BigDecimal>();
		for(int i=0 ; i<projectDetails.size();i++) {
			String pjtName = projectDetails.get(i);
			BigDecimal billable_time = ((BigDecimal) (session.createNativeQuery(
							"select sum(task_duration_ms) from time_tracker_details where org_id="+orgId+" and is_deleted=false and bill='Billable' and approval_status='Approved' and project="
									+ "'" + pjtName + "'" + " and date_of_request>=" + "'" + sd_date + "'" +" and date_of_request<=" + "'" + ed_date + "'")
					.getSingleResult()));
			if(billable_time == null) {
				billable_time = new BigDecimal("0");
			}
			billableTimeList.add(billable_time);

			BigDecimal non_billable_time = ((BigDecimal) (session.createNativeQuery(
							"select sum(task_duration_ms) from time_tracker_details where org_id="+orgId+" and is_deleted=false and bill='Non Billable' and approval_status='Approved' and project=" + "'" + pjtName + "'" + " and date_of_request>=" + "'" + sd_date + "'" +" and date_of_request<=" + "'" + ed_date + "'")
					.getSingleResult()));
			if(non_billable_time == null) {
				non_billable_time = new BigDecimal("0");
			}
			nonBillableTimeList.add(non_billable_time);
		}
		JSONObject json = new JSONObject();
		json.put("projects", projectDetails);
		json.put("billable", billableTimeList);
		json.put("non_billable", nonBillableTimeList);
		return json;
	}

	@Override
	public List<JSONObject> getProjectAndJobNames(Long orgid,String empid) {
		logger.info("TimeTrackerDetailsDaoImpl (getprojectandjobNames) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		List<JSONObject> dropdown = new ArrayList<JSONObject>();
		@SuppressWarnings("unchecked") final Query query_active = session.createQuery("from ProjectResourceDetails where emp_id=:i and status=:j and is_deleted=:k");
		query_active.setParameter("i",empid);
		query_active.setParameter("j", "Active");
//		query_active.setParameter("g", "Inactive");
		query_active.setParameter("k",false);
//		query_active.setParameter("l","team_members");
		List<ProjectResourceDetails>  details_active = query_active.getResultList();
		List<CustomProjectDetails> data = new ArrayList<CustomProjectDetails>();
		List<JSONObject> project = new ArrayList<JSONObject>();
		if(details_active != null) {
			for(int i=0;i< details_active.size();i++) {
				ProjectResourceDetails value = new ProjectResourceDetails();
				value.setId(details_active.get(i).getRef_projectid());
//				value.setEmployeeDetails(details_active.get(i).getEmployeeDetails());
				final Query query = session.createQuery
						("from ProjectDetails where id=:i and orgDetails.org_id=:j and project_status=: k and is_deleted=:l and is_activated=:h");
				query.setParameter("i",details_active.get(i).getRef_projectid());
				query.setParameter("j",orgid);
				query.setParameter("k","Inprogress");
				query.setParameter("l", false);
				query.setParameter("h",true);
				@SuppressWarnings("unchecked")
				List<ProjectDetails> details = query.getResultList();
				if(details != null) {
					for(int j=0; j<details.size();j++) {
						JSONObject data1 = new JSONObject();
						data1.put("name", details.get(j).getProject_name());
						data1.put("id", details.get(j).getId());
						data1.put("client_id", details.get(j).getClientDetails().getId());
						project.add(data1);
					}
				}
			}
		}

		final Query query2 = session.createQuery("from JobAssigneeDetails where emp_id=:i and status=:j and is_deleted=false");
		query2.setParameter("i", empid);
		query2.setParameter("j", "Active");
//		query2.setParameter("k", "Inactive");
		@SuppressWarnings("unchecked")
		List<JobAssigneeDetails>  details = query2.getResultList();
		List<JSONObject> job = new ArrayList<JSONObject>();
		if(details != null) {
			for(int i=0; i<details.size();i++) {
				JobAssigneeDetails value = new JobAssigneeDetails();
				value.setRef_jobid(details.get(i).getRef_jobid());
				final Query query = session.createQuery("from JobDetails where id=: i and orgDetails.org_id=:j and job_status=: k and is_deleted=:l");
				query.setParameter("i",details.get(i).getRef_jobid());
				query.setParameter("j", orgid);
				query.setParameter("k", "Inprogress");
				query.setParameter("l", false);
				@SuppressWarnings("unchecked")
				List<JobDetails>  details1 = query.getResultList();
				if(details1 != null) {
					for(int j=0; j<details1.size();j++) {
						JSONObject data2 = new JSONObject();
						data2.put("id",details1.get(j).getId());
						data2.put("name",details1.get(j).getJob_name());
						data2.put("bill",details1.get(j).getBill());
						data2.put("project_id",details1.get(j).getProject_id());
						data2.put("mail_sended", details1.get(j).getMail_sended());
						job.add(data2);
					}
				}
			}
		}

		JSONObject projectdata = new JSONObject();
		projectdata.put("projectdetails", project);

		JSONObject jobdata = new JSONObject();
		jobdata.put("jobdetails", job);

		dropdown.add(projectdata);
		dropdown.add(jobdata);
		logger.info("TimeTrackerDetailsDaoImpl (getProjectAndJobNames) >> Exit ");
		return dropdown;
	}

	@Override
	public boolean updateResubmittedStatus(Long timesheetid) {
		logger.info("TimeTrackerDetailsDaoImpl (updateResubmittedStatus) >> Entry ");
		final Session session = entityManager.unwrap(Session.class);
		@SuppressWarnings("unchecked") final Query query = session.createNativeQuery(
				"update time_tracker_details set approval_status = 'Updated' where timesheet_id="+"'" + timesheetid + "'");
		int count = query.executeUpdate();
		if(count>0) {
			return true;
		}
		logger.info("TimeTrackerDetailsDaoImpl (updateResubmittedStatus) >> Exit ");
		return false;
	}

	@Override
	public List<JSONObject> getTaskDetailsForPerformanceMetrics(String empid, String start_date, String end_date) {
		// TODO Auto-generated method stub
		logger.info("TimeTrackerDetailsDaoImpl(getTaskDetailsForPerformanceMetrics) >>Entry");
//		Date todayDate = new Date();
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from TimeTrackerDetails where emp_id =:idcccccccc and approval_status='Approved' and date_of_request>=:b and date_of_request<=:c");
//		query.setParameter("org", newDetails.getOrgDetails().getOrg_id());
		query.setParameter("id", empid);
		query.setParameter("b", start_date);
		query.setParameter("c", end_date);
//		@SuppressWarnings("unchecked")
		final List<JSONObject> details = query.getResultList();
		logger.info("TimeTrackerDetailsDaoImpl(getTaskDetailsForPerformanceMetrics) >>Exit");
		return details;
	}
	
	@Override
	public List<String> getNotSubmittedUserListByOrgid(BigInteger id) {
		logger.info("TimeTrackerDetailsDaoImpl(getNotSubmittedUserListByOrgid) >>Entry");
		final Session session = entityManager.unwrap(Session.class);
		//today date
		String pattern = "dd-MM-yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());		
		final Query query = session.createNativeQuery("select distinct B.id, CONCAT(B.firstname,' ',B.lastname), B.email  from attendance_details as A join employee_details as B "
				+ "on A.email = B.email and A.org_id = B.org_id where A.date_of_request =:d and A.org_id =:i and A.is_delete = false and A.action_type =:j" );
		query.setParameter("i", id);
		query.setParameter("d", date);
		query.setParameter("j", "in");
		List<Object[]> empDetails =  query.getResultList();
		
		if(empDetails.size() != 0) {
		//today date according to timesheet approval table
				String pattern1 = "yyyy-MM-dd";
				SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(pattern1);
				String dateTimeSheetApprovals = simpleDateFormat1.format(new Date());
				
			 String id_list = new String();
				for (int i = 0; i < empDetails.size(); i++) {
					id_list += "'" + empDetails.get(i)[0] + "'" + ",";
				}
				StringBuffer id_sb = new StringBuffer(id_list);
				id_sb.deleteCharAt(id_sb.length() - 1);
		
		final Query query1 = session.createQuery(" from TimesheetApprovalDetails where emp_id in (" + id_sb + ") and date_of_request=:d and approval_status=:a");
		query1.setParameter("d", dateTimeSheetApprovals);
		query1.setParameter("a", "Submitted");
		@SuppressWarnings("unchecked")
		List<TimesheetApprovalDetails> details = query1.getResultList();
		List<String> arraydata = new ArrayList<>();
		
			 for (int i=0; i<empDetails.size();i++) {
				 boolean isPresentInempDetails = false;
				 for (int j=0; j<details.size();j++) {
					 if (empDetails.get(i)[0].equals(details.get(j).getEmp_id())) {
		                    isPresentInempDetails = true;
		                    break;
		                }
				 }
				 if (!isPresentInempDetails) {
					 JSONObject iterate = new JSONObject();
	            	iterate.put("id", empDetails.get(i)[0]);
	            	iterate.put("name", empDetails.get(i)[1]);
	            	iterate.put("email", empDetails.get(i)[2]);
	            	arraydata.add(new Gson().toJson(iterate));	
		         }
			 }		 
			 logger.info("TimeTrackerDetailsDaoImpl(getNotSubmittedUserListByOrgid) >>Exit");
			return arraydata;
		}else {
			logger.info("TimeTrackerDetailsDaoImpl(getNotSubmittedUserListByOrgid) >>Exit");
			return null;
		}			
		
	}

	@Override
	public ArrayList<BigInteger> getActiveOrgIdsWithTimetrackerPlan() {
		logger.info("TimeTrackerDetailsDaoImpl(getActiveOrgIdsWithTimetrackerPlan) >>Entry");
		final Session session = entityManager.unwrap(Session.class);

		final Query query = session.createNativeQuery("select A.org_id from org_details as A join pricing_plan_details as B ON A.plan_id = B.id where A.is_deleted = false and A.is_activated = true and B.modules like concat('%','time-tracker','%')");
		@SuppressWarnings("unchecked")
		ArrayList<BigInteger> ids = new ArrayList<BigInteger>();
		ids = (ArrayList<BigInteger>) query.getResultList();
		if(ids != null && ids.size() > 0) {
			logger.info("TimeTrackerDetailsDaoImpl(getActiveOrgIdsWithTimetrackerPlan) >>Exit");
			System.out.println(ids);
			return ids;
		}else {
			logger.info("TimeTrackerDetailsDaoImpl(getActiveOrgIdsWithTimetrackerPlan) >>Exit");
			return null;
		}
	}

	@Override
	public JSONObject getProjectJobLoggedDetails(long orgId, long projectId, String projectName, String sd_date, String ed_date) {
		logger.info("TimeTrackerDetailsDaoImpl (getProjectJobLoggedDetails) Entry>> ");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from JobDetails where project_id=:id and is_deleted =:k order by timestamp(modified_time) desc");
		query.setParameter("id", projectId);
		query.setParameter("k", false);
		@SuppressWarnings("unchecked") final List<JobDetails> JobDetails = query.getResultList();
		List<JobAssigneeDetails> Users = new ArrayList<JobAssigneeDetails>();
		List<JobAssigneeDetails> usersPresent = new ArrayList<>();
		List<String> tempuser = new ArrayList<>();
		if(JobDetails.size()!=0) {
			for (int i = 0; i < JobDetails.size(); i++) {
				//query to get the active assignees
				final Query query_active = session.createQuery("from JobAssigneeDetails where  is_deleted=false and ref_jobid=:i and (status=:j or status=:k)");
				query_active.setParameter("i", JobDetails.get(i).getId());
				query_active.setParameter("j", "Active");
				query_active.setParameter("k", "Inactive");
				usersPresent = query_active.getResultList();
				for (JobAssigneeDetails emp :usersPresent) {
					if(!(tempuser.contains(emp.getEmployeeDetails().getId()))){
						tempuser.add(emp.getEmployeeDetails().getId());
						Users.add(emp);
					}
				}

			}
		}
		JSONObject result = new JSONObject();
		if(Users.size()!=0){

			List<JSONObject> arrayData = new ArrayList<>();
			long totalHours = 0;
			for(int i=0; i< Users.size(); i++){
				JSONObject userData = new JSONObject();
				long calc= 0;
				JSONObject data = new JSONObject();
				for (int j =0 ; j< JobDetails.size();j++ ){
					BigDecimal time_duration = ((BigDecimal) (session.createNativeQuery(
									"select sum(task_duration_ms) from time_tracker_details where org_id="+orgId+" and is_deleted=false and approval_status='Approved' and job="+"'"+JobDetails.get(j).getJob_name()+"'"+" and emp_id="+"'"+Users.get(i).getEmployeeDetails().getId()+"'"+" and project=" + "'" + projectName + "'" + " and date_of_request>=" + "'" + sd_date + "'" +" and date_of_request<=" + "'" + ed_date + "'")
							.getSingleResult()));
					long millis = 0;
					if(time_duration!=null){
						millis = time_duration.longValue();
					}
					long hoursl = TimeUnit.MILLISECONDS.toHours(millis);
					long hoursMillis = TimeUnit.HOURS.toMillis(hoursl);
					long minutes = TimeUnit.MILLISECONDS.toMinutes(millis - hoursMillis);
					String hours =  ((String.valueOf(hoursl).length()==1)?"0"+hoursl:hoursl)+":"+((String.valueOf(minutes).length()==1)?"0"+minutes:minutes);
					data.put(JobDetails.get(j).getJob_name(),millis);
					calc += millis;
				}
				userData.put("user", Users.get(i).getEmployeeDetails().getFirstname()+" "+Users.get(i).getEmployeeDetails().getLastname());
				long hoursl = TimeUnit.MILLISECONDS.toHours(calc);
				long hoursMillis = TimeUnit.HOURS.toMillis(hoursl);

				long minutes = TimeUnit.MILLISECONDS.toMinutes(calc - hoursMillis);
				String hours =  ((String.valueOf(hoursl).length()==1)?"0"+hoursl:hoursl)+":"+((String.valueOf(minutes).length()==1)?"0"+minutes:minutes);
				userData.put("hours", calc);
				userData.put("details",data);
				arrayData.add(userData);
				totalHours +=  calc;
			}
			long hoursl = TimeUnit.MILLISECONDS.toHours(totalHours);
			long hoursMillis = TimeUnit.HOURS.toMillis(hoursl);
			long minutes = TimeUnit.MILLISECONDS.toMinutes(totalHours - hoursMillis);
			String hours =  ((String.valueOf(hoursl).length()==1)?"0"+hoursl:hoursl)+":"+((String.valueOf(minutes).length()==1)?"0"+minutes:minutes);
			result.put("report",arrayData);
			result.put("total_duration",hours);
		}

		logger.info("TimeTrackerDetailsDaoImpl (getProjectJobLoggedDetails) Exit>> ");
		return result;
	}
		

}
