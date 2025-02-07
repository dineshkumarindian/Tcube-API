package com.tcube.api.dao;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.tcube.api.model.ActionLog;
import com.tcube.api.model.AttendanceCurrentStatus;
import com.tcube.api.model.AttendanceDateReport;
import com.tcube.api.model.AttendanceDetails;
import com.tcube.api.model.EmployeeAttendanceDatereport;
import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.ManageAttendance;
import com.tcube.api.model.UserAttendanceReport;
import com.tcube.api.utils.ImageProcessor;

@Component
public class AttendanceDetailsDaoImpl implements AttendanceDetailsDao {

	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = LogManager.getLogger(AttendanceDetailsDaoImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Create New Action Detail In Attendance Table with In out details
	 */
	@SuppressWarnings("unchecked")
	@Override
	public AttendanceDetails createAttendanceDetails(AttendanceDetails details, String timezone) {
		logger.info("AttendanceDetailsDaoImpl(createAttendanceDetailst) Entry>> ");
		final Session session = entityManager.unwrap(Session.class);
		try {

			/*
			 * Set new request details which need to be default while create
			 */
			
			Date date = new Date();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			// Use india time zone to format the date in
			df.setTimeZone(TimeZone.getTimeZone(timezone));
			
			details.setCreatedTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date)));
			details.setModifiedTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date)));
			details.setTimeOfAction(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date)));
//			details.setDateOfRequest(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
			details.setDateOfRequest(new SimpleDateFormat("dd-MM-yyyy").format(details.getTimeOfAction()));
			details.setDelete(false);
			details.setActive(true);

			// Initialize
			List<AttendanceDetails> lastactivedatalist = new ArrayList<AttendanceDetails>();
			boolean yeserdayactive = false;

			/*
			 * To Get Current Active state of the user
			 */
			final Query query = session.createQuery(
					"from AttendanceDetails where isActive=:i and isDelete=:j and dateOfRequest=:k and email=:l");
			query.setParameter("i", true);
			query.setParameter("j", false);
			query.setParameter("k", new SimpleDateFormat("dd-MM-yyyy").format(details.getTimeOfAction()));
			query.setParameter("l", details.getEmail());

			List<AttendanceDetails> todaylastactivedatalist = query.getResultList();
			// If today has no data
			if (todaylastactivedatalist.size() == 0) {
				final Query newquery = session
						.createQuery("from AttendanceDetails where isActive=:i and isDelete=:j and email=:l");
				newquery.setParameter("i", true);
				newquery.setParameter("j", false);
				newquery.setParameter("l", details.getEmail());
				List<AttendanceDetails> yesterdaylastactivedatalist = newquery.getResultList();

				// To check it has at least one detail
				if (yesterdaylastactivedatalist.size() >= 1) {
					// To get last action state details
					AttendanceDetails yesterdaylastdata = yesterdaylastactivedatalist.get(0);
					// If the last action is in type
					if (yesterdaylastdata.getActionType().equals("in")) {
						yeserdayactive = true;
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(yesterdaylastdata.getTimeOfAction());
						calendar.set(Calendar.HOUR_OF_DAY, 23);
						calendar.set(Calendar.MINUTE, 59);
						calendar.set(Calendar.SECOND, 59);
						calendar.set(Calendar.MILLISECOND, 999);
						// To calculate active hours of the last active day
						long difference = (calendar.getTime()).getTime()
								- (yesterdaylastdata.getTimeOfAction()).getTime();
						AttendanceDetails lastdatayesterday = new AttendanceDetails();

						lastdatayesterday.setCreatedTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date)));
						lastdatayesterday.setModifiedTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date)));
//						Date newdate = calendar.getTime();
						
						String date_s = yesterdaylastdata.getDateOfRequest()+" 23:59:59.999";

				        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
//				        Date ndate = dt.parse(date_s);
//				        System.out.println(date_s);
//				        Calendar cal = Calendar.getInstance();
//				        cal.setTime(new SimpleDateFormat("dd-mm-yyyy")
//		                        .parse(details.getDateOfRequest()));
//				        cal.set(Calendar.HOUR_OF_DAY, 23);
//						cal.set(Calendar.MINUTE, 59);
//						cal.set(Calendar.SECOND, 59);
//						cal.set(Calendar.MILLISECOND, 999);
				        
						lastdatayesterday.setTimeOfAction(dt.parse(date_s));
//				        lastdatayesterday.setTimeOfAction(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(ndate)));

//						lastdatayesterday.setTimeOfAction(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(newdate)));

//						details.setDateOfRequest(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
						lastdatayesterday.setDateOfRequest(yesterdaylastdata.getDateOfRequest());
						lastdatayesterday.setDelete(false);
						lastdatayesterday.setActionType("out");
						lastdatayesterday.setAction("Auto checkout");
						lastdatayesterday.setEmail(yesterdaylastdata.getEmail());
						lastdatayesterday.setOrgDetails(yesterdaylastdata.getOrgDetails());
						lastdatayesterday.setActiveHours(
								String.valueOf(difference + Long.parseLong(yesterdaylastdata.getActiveHours())));
						session.save(lastdatayesterday);
						if (lastdatayesterday.getId() == 0) {
							entityManager.persist(lastdatayesterday);
						} else {
							entityManager.merge(lastdatayesterday);
						}
						yesterdaylastactivedatalist = newquery.getResultList();
					}
				}
				lastactivedatalist = yesterdaylastactivedatalist;
			} else {
				lastactivedatalist = todaylastactivedatalist;
			}
			AttendanceDetails lastdata;
			if (lastactivedatalist.size() != 0) {
				lastdata = lastactivedatalist.get(0);

				if (lastdata != null) {
					// If last Action is in and current action is out and previous date has no
					// active state
					if (lastdata.getActionType().equals("in") && (details.getActionType().equals("out") || details.getActionType().equals("in"))
							&& yeserdayactive == false) {
						long difference_In_Time = (details.getTimeOfAction()).getTime()
								- (lastdata.getTimeOfAction()).getTime();
						if (lastdata.getActiveHours() != null) {
							details.setActiveHours(
									String.valueOf(difference_In_Time + Long.parseLong(lastdata.getActiveHours())));
						} else {
							details.setActiveHours(String.valueOf(difference_In_Time));
						}

					}
					// If last Action is in and current action is out and previous date has active
					// state
					else if (lastdata.getActionType().equals("in") && details.getActionType().equals("out")
							&& yeserdayactive == true) {
						Calendar calc = Calendar.getInstance();
						calc.add(Calendar.DATE, 0);
						calc.set(Calendar.HOUR_OF_DAY, 0);
						calc.set(Calendar.MINUTE, 0);
						calc.set(Calendar.SECOND, 0);
						calc.set(Calendar.MILLISECOND, 0);
						long diff = (details.getTimeOfAction()).getTime() - (calc.getTime()).getTime();
						
						AttendanceDetails todayfirst = new AttendanceDetails();

						todayfirst.setCreatedTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date)));
						todayfirst.setModifiedTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date)));
//						Long time = new Date().getTime();
//						Date newdate = calc.getTime();
						
						String date_s = details.getDateOfRequest()+" 00:00:000";
                        
				        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
//				        Date ndate = dt.parse(date_s);
//				        
//				        Calendar cal = Calendar.getInstance();
//				        cal.setTime(new SimpleDateFormat("dd-mm-yyyy")
//		                        .parse(details.getDateOfRequest()));
//						cal.set(Calendar.HOUR_OF_DAY, 0);
//						cal.set(Calendar.MINUTE, 0);
//						cal.set(Calendar.SECOND, 0);
//						cal.set(Calendar.MILLISECOND, 0);

						todayfirst.setTimeOfAction(dt.parse(date_s));
//						todayfirst.setTimeOfAction(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(ndate)));
//						todayfirst.setTimeOfAction(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(newdate)));
//						details.setDateOfRequest(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
						todayfirst.setDateOfRequest(details.getDateOfRequest());
						todayfirst.setDelete(false);
						todayfirst.setActionType("in");
						todayfirst.setAction("Auto checkin");
						todayfirst.setActiveHours("0");
						todayfirst.setEmail(details.getEmail());
						todayfirst.setOrgDetails(details.getOrgDetails());
						session.save(todayfirst);
						if (todayfirst.getId() == 0) {
							entityManager.persist(todayfirst);
						} else {
							entityManager.merge(todayfirst);
						}
						details.setActiveHours(String.valueOf(diff));
					} else {

						if (lastdata.getActiveHours() != null && lastdata.getDateOfRequest()
								.equals(new SimpleDateFormat("dd-MM-yyyy").format(new Date()))) {
							details.setActiveHours(lastdata.getActiveHours());
						} else {
							details.setActiveHours("0");
						}
					}
					lastdata.setActive(false);
					session.save(lastdata);
					if (lastdata.getId() == 0) {
						entityManager.persist(lastdata);
					} else {
						entityManager.merge(lastdata);
					}
				} else {
					details.setActiveHours("0");
				}
			} else {
				details.setActiveHours("0");
			}
			session.save(details);
			if (details.getId() == 0) {
				entityManager.persist(details);
				return details;
			} else {
				entityManager.merge(details);
				return details;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(
					"Exception occured in AttendanceDetailsDaoImpl(createAttendanceDetails) and Exception details >> "
							+ e);
		}
		logger.info("AttendanceDetailsDaoImpl(createAttendanceDetailst) Exit>> ");

		return details;
	}

	/**
	 * Get All Attendance details Dao
	 */

	@Override
	public List<AttendanceDetails> getAllAttendanceDetails() {
		logger.info("AttendanceDetailsDaoImpl(getAllDetails) Entry>> ");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery("from AttendanceDetails where isDelete=:i");
		query.setParameter("i", false);
		@SuppressWarnings("unchecked")
		List<AttendanceDetails> details = query.getResultList();
		logger.info("AttendanceDetailsDaoImpl(getAllDetails) Exit>> ");
		return details;
	}

	/**
	 * Get All Attendance details Dao By org_id
	 */

	@Override
	public List<AttendanceDetails> getAllAttendanceDetailsByOrgId(Long org_id) {
		logger.info("AttendanceDetailsDaoImpl(getAllAttendanceDetailsByClientId) Entry>> ");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session
				.createQuery("from AttendanceDetails where isDelete=:i and OrgDetails.org_id=:org_id");
		query.setParameter("i", false);
		query.setParameter("org_id", org_id);
		@SuppressWarnings("unchecked")
		List<AttendanceDetails> details = query.getResultList();
		logger.info("AttendanceDetailsDaoImpl(getAllAttendanceDetailsByClientId) Exit>> ");
		return details;
	}

	/**
	 * Get All Attendance details Dao By email_id
	 */

	@Override
	public List<AttendanceDetails> getAllAttendanceDetailsByemail(String email) {
		logger.info("AttendanceDetailsDaoImpl(getAllAttendanceDetailsByemail) Entry>> ");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session
				.createQuery("from AttendanceDetails where email=:i and isDelete=:j ORDER BY timeOfAction ASC");
		query.setParameter("i", email);
		query.setParameter("j", false);
		@SuppressWarnings("unchecked")
		List<AttendanceDetails> details = query.getResultList();
		logger.info("AttendanceDetailsDaoImpl(getAllAttendanceDetailsByemail) Exit>> ");	
		return details;
	}

	/**
	 * Get All Attendance details Dao By date and email
	 */
	@SuppressWarnings("unchecked")
	@Override
	public AttendanceDateReport getAttendanceReportsByDate(String date, String email) {
		logger.info("AttendanceDetailsDaoImpl(getAttendanceReportsByDate) Entry>> ");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session
				.createQuery("from AttendanceDetails where email=:i and isDelete=:j and dateOfRequest=:k");
		query.setParameter("i", email);
		query.setParameter("j", false);
		query.setParameter("k", date);
		List<AttendanceDetails> details = query.getResultList();
		AttendanceDetails[] arraydata = new AttendanceDetails[details.size()];
		for (int i = 0; i < details.size(); i++) {
			arraydata[i] = details.get(i);
		}
		AttendanceDateReport Reportdata = new AttendanceDateReport();
		if (arraydata != null) {
			Reportdata.setDate(arraydata[0].getDateOfRequest());
			Reportdata.setLastout(
					new SimpleDateFormat("hh.mm aa").format(arraydata[arraydata.length - 1].getTimeOfAction()));
			long active = new Long(arraydata[arraydata.length - 1].getActiveHours());
			Reportdata.setActiveHours(
					TimeUnit.MILLISECONDS.toHours(active) % 24 + ":" + TimeUnit.MILLISECONDS.toMinutes(active) % 60
							+ ":" + TimeUnit.MILLISECONDS.toSeconds(active) % 60);

			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

			Date startTime;
			Date now;
			try {
				startTime = sdf.parse("08:00:00");
				now = sdf.parse(Reportdata.getActiveHours());
				long duration = now.getTime() - startTime.getTime();
				long hours = TimeUnit.MILLISECONDS.toHours(duration);
				long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60;
				long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60;
//				long milliseconds = duration % 1000;
				if (duration > 0) {
					// Overtime setter
					Reportdata.setOvertime(String.format("%02d:%02d:%02d", hours, minutes, seconds));
				} else {
					// Diviation setter
					Reportdata.setDeviation(
							String.format("%02d:%02d:%02d", Math.abs(hours), Math.abs(minutes), Math.abs(seconds)));
				}
//				System.out.println(String.format("%02d:%02d:%02d", hours, minutes, seconds));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error(
						"Exception occured in AttendanceDetailsDaoImpl(getAttendanceReportsByDate) and Exception details >> "
								+ e);
			}

			long lunchtakenmilliseconds = 0;
			long breaktakenmilliseconds = 0;
			boolean first = false;
			for (int i = 0; i < arraydata.length; i++) {
				if (first == false && arraydata[i].getActionType().equals("in")) {
					Reportdata.setFirstIn(new SimpleDateFormat("hh.mm aa").format(arraydata[i].getTimeOfAction()));
					first = true;
				}
				if (arraydata[i].getAction().equals("out for lunch")) {
					Date lunchstart = new Date();
					Date lunchend = new Date();
					lunchstart = arraydata[i].getTimeOfAction();
					for (int j = i + 1; j < arraydata.length; j++) {
						if (arraydata[j].getActionType().equals("in")) {
							lunchend = arraydata[j].getTimeOfAction();
							lunchtakenmilliseconds += (lunchend.getTime() - lunchstart.getTime());
							break;
						}
					}
				}
				if (arraydata[i].getAction().equals("out for break")) {
					Date breakstart = new Date();
					Date breakend = new Date();
					breakstart = arraydata[i].getTimeOfAction();
					for (int j = i + 1; j < arraydata.length; j++) {
						if (arraydata[j].getActionType().equals("in")) {
							breakend = arraydata[j].getTimeOfAction();
							breaktakenmilliseconds += (breakend.getTime() - breakstart.getTime());
							break;
						}
					}
				}
			}
			Reportdata.setOutForLunch(TimeUnit.MILLISECONDS.toHours(lunchtakenmilliseconds) % 24 + ":"
					+ TimeUnit.MILLISECONDS.toMinutes(lunchtakenmilliseconds) % 60 + ":"
					+ TimeUnit.MILLISECONDS.toSeconds(lunchtakenmilliseconds) % 60);
			Reportdata.setOutForBreak(TimeUnit.MILLISECONDS.toHours(breaktakenmilliseconds) % 24 + ":"
					+ TimeUnit.MILLISECONDS.toMinutes(breaktakenmilliseconds) % 60 + ":"
					+ TimeUnit.MILLISECONDS.toSeconds(breaktakenmilliseconds) % 60);
		}
		logger.info("AttendanceDetailsDaoImpl(getAttendanceReportsByDate) Exit>> ");
		
		return Reportdata;
	}

	@SuppressWarnings("deprecation")
	@Override
	public AttendanceCurrentStatus getcurrentstatusByemail(String email, String timezone) {
		logger.info("AttendanceDetailsDaoImpl(getcurrentstatusByemail) Entry>> ");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session.createQuery(
				"from AttendanceDetails where email=:i and isDelete=:j and isActive=:k ORDER BY timeOfAction DESC");
		query.setParameter("i", email);
		query.setParameter("j", false);
		query.setParameter("k", true);
		@SuppressWarnings("unchecked")
		List<AttendanceDetails> details = query.getResultList();
		AttendanceDetails detail = details.get(0);
		AttendanceCurrentStatus data = new AttendanceCurrentStatus();
		data.setEmail(detail.getEmail());
		data.setActionType(detail.getActionType());
		data.setAction(detail.getAction());
		data.setNext_section(detail.getNext_action_section());
		data.setDateOfRequest(detail.getDateOfRequest());
		if (detail.getActionType().equals("in")) {
			Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			c.setTimeZone(TimeZone.getTimeZone(timezone));
			Date indate = detail.getTimeOfAction();
			Date date = new Date();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			// Use india time zone to format the date in
			df.setTimeZone(TimeZone.getTimeZone(timezone));

			try {
				Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(df.format(date));
//				if (indate.getDate() == c.getTime().getDate()) {
				if (indate.getDate() == d.getDate()) {
//					long difference = (c.getTime()).getTime() - (detail.getTimeOfAction()).getTime();
					long difference = d.getTime() - (detail.getTimeOfAction()).getTime();
					data.setActiveHours(String.valueOf(difference + Long.parseLong(detail.getActiveHours())));
				} else {
					Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
					calendar.setTimeZone(TimeZone.getTimeZone(timezone));
					calendar.set(Calendar.HOUR_OF_DAY, 0);
					calendar.set(Calendar.MINUTE, 0);
					calendar.set(Calendar.SECOND, 0);
					calendar.set(Calendar.MILLISECOND, 0);
					long difference = (c.getTime()).getTime() - (calendar.getTime()).getTime();
					data.setActiveHours(String.valueOf(difference));
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error(
						"Exception occured in AttendanceDetailsDaoImpl(getcurrentstatusByemail) and Exception details >> "
								+ e);
			}

		} else {
			if (detail.getDateOfRequest().equals(new SimpleDateFormat("dd-MM-yyyy").format(new Date()))) {
				data.setActiveHours(detail.getActiveHours());
			} else {
				data.setActiveHours("0");
			}
		}
		logger.info("AttendanceDetailsDaoImpl(getcurrentstatusByemail) Exit>> ");
		return data;

	}

	/*
	 * Attendance Monthly report
	 */
	@Override
	public JSONObject getAttendanceReportsByMonth(String startdate, String enddate, String email) {

		logger.info("AttendanceDetailsDaoImpl(getAttendanceReportsByMonth) Entry>> ");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session
				.createQuery("from AttendanceDetails where email=:i and isDelete=:j and dateOfRequest=:k");
		
		final Query actionquery = session.createQuery("from ManageAttendance where isDelete=:i and action=:j and org_id=:org_id");
		actionquery.setParameter("i", false);
        
		List<AttendanceDateReport> monthlyreport = new ArrayList<AttendanceDateReport>();
		List<List<ActionLog>> monthlyreportDetails = new ArrayList<List<ActionLog>>();
		final JSONObject jsonObject = new JSONObject();
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
			
			// To get date reports with details and push into list
			for (int k = 0; k < dates.size(); k++) {
				Date lDate = (Date) dates.get(k);
				String ds = formatter.format(lDate);
				
				//set value for the query parameters
				query.setParameter("i", email);
				query.setParameter("j", false);
				query.setParameter("k", ds);
				@SuppressWarnings("unchecked")
				List<AttendanceDetails> details = query.getResultList();
				if (details.size() > 0) {
					AttendanceDetails[] arraydata = new AttendanceDetails[details.size()];
					List<ActionLog> detailslist = new ArrayList<>();
					for (int i = 0; i < details.size(); i++) {
						ActionLog addvalue = new ActionLog();
						arraydata[i] = details.get(i);
						addvalue.setAction(details.get(i).getAction());
						addvalue.setActionType(details.get(i).getActionType());
						addvalue.setTimeOfAction(details.get(i).getTimeOfAction());
						actionquery.setParameter("org_id", details.get(i).getOrgDetails().getOrg_id());
						actionquery.setParameter("j", details.get(i).getAction());
						@SuppressWarnings("unchecked")
						List<ManageAttendance> result = actionquery.getResultList();
						if(result.size()>0) {
							if(result.get(0).getAction_image()!=null) {
								addvalue.setImage(ImageProcessor.decompressBytes(result.get(0).getAction_image()));	
							}
													
						}
						detailslist.add(addvalue);
					}
					AttendanceDateReport Reportdata = new AttendanceDateReport();
					if (arraydata != null) {
						Reportdata.setDate(arraydata[0].getDateOfRequest());
//						Reportdata.setLastout(new SimpleDateFormat("hh.mm aa")
//								.format(arraydata[arraydata.length - 1].getTimeOfAction()));
						for (int i = details.size()-1; i > 0; i--) {
							if(arraydata[i].getActionType().equals("out")) {
								Reportdata.setLastout(new SimpleDateFormat("hh.mm aa")
										.format(arraydata[i].getTimeOfAction()));
								break;
							}
						}
						long active = new Long(arraydata[arraydata.length - 1].getActiveHours());
						Reportdata.setActiveHours(TimeUnit.MILLISECONDS.toHours(active) % 24 + ":"
								+ TimeUnit.MILLISECONDS.toMinutes(active) % 60 + ":"
								+ TimeUnit.MILLISECONDS.toSeconds(active) % 60);

						SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

						Date startTime;
						Date now;
						try {
							startTime = sdf.parse("08:00:00");
							now = sdf.parse(Reportdata.getActiveHours());
							long duration = now.getTime() - startTime.getTime();
							long hours = TimeUnit.MILLISECONDS.toHours(duration);
							long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60;
							long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60;
//							long milliseconds = duration % 1000;
							if (duration > 0) {
								// Overtime setter
								Reportdata.setOvertime(String.format("%02d:%02d:%02d", Math.abs(hours),
										Math.abs(minutes), Math.abs(seconds)));
							} else {
								// Diviation setter
								Reportdata.setDeviation(String.format("%02d:%02d:%02d", Math.abs(hours),
										Math.abs(minutes), Math.abs(seconds)));
							}
//							System.out.println(String.format("%02d:%02d:%02d", hours, minutes, seconds));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							logger.error(
									"Exception occured in AttendanceDetailsDaoImpl(getAttendanceReportsByMonth) and Exception details >> "
											+ e);
							
						}

//						long lunchtakenmilliseconds = 0;
//						long breaktakenmilliseconds = 0;
						long outmilliseconds = 0;
						boolean first = false;
						for (int i = 0; i < arraydata.length; i++) {
							if (first == false && arraydata[i].getActionType().equals("in") && !(arraydata[i].getAction().equals("Auto checkin"))) {
								Reportdata.setFirstIn(
										new SimpleDateFormat("hh.mm aa").format(arraydata[i].getTimeOfAction()));
								first = true;
							}
//							if (arraydata[i].getAction().equals("out for lunch")) {
//								Date lunchstart = new Date();
//								Date lunchend = new Date();
//								lunchstart = arraydata[i].getTimeOfAction();
//								for (int j = i + 1; j < arraydata.length; j++) {
//									if (arraydata[j].getActionType().equals("in")) {
//										lunchend = arraydata[j].getTimeOfAction();
//										lunchtakenmilliseconds += (lunchend.getTime() - lunchstart.getTime());
//										break;
//									}
//								}
//							}
							if (arraydata[i].getActionType().equals("out")) {
								Date start = new Date();
								Date end = new Date();
								start = arraydata[i].getTimeOfAction();
								for (int j = i + 1; j < arraydata.length; j++) {
									if (arraydata[j].getActionType().equals("in")) {
										end = arraydata[j].getTimeOfAction();
										outmilliseconds += (end.getTime() - start.getTime());
										break;
									}
								}
							}
						}
						Reportdata.setInactiveHours(TimeUnit.MILLISECONDS.toHours(outmilliseconds) % 24 + ":"
								+ TimeUnit.MILLISECONDS.toMinutes(outmilliseconds) % 60 + ":"
								+ TimeUnit.MILLISECONDS.toSeconds(outmilliseconds) % 60);
						
//						Reportdata.setOutForLunch(TimeUnit.MILLISECONDS.toHours(lunchtakenmilliseconds) % 24 + ":"
//								+ TimeUnit.MILLISECONDS.toMinutes(lunchtakenmilliseconds) % 60 + ":"
//								+ TimeUnit.MILLISECONDS.toSeconds(lunchtakenmilliseconds) % 60);
//						Reportdata.setOutForBreak(TimeUnit.MILLISECONDS.toHours(breaktakenmilliseconds) % 24 + ":"
//								+ TimeUnit.MILLISECONDS.toMinutes(breaktakenmilliseconds) % 60 + ":"
//								+ TimeUnit.MILLISECONDS.toSeconds(breaktakenmilliseconds) % 60);
						
					}
					monthlyreport.add(Reportdata);
					monthlyreportDetails.add(detailslist);

				}

			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(
					"Exception occured in AttendanceDetailsDaoImpl(getAttendanceReportsByMonth) and Exception details >> "
							+ e);
		}
		jsonObject.put("report", new Gson().toJson(monthlyreport));
		jsonObject.put("details", new Gson().toJson(monthlyreportDetails));
		logger.info("AttendanceDetailsDaoImpl(getAttendanceReportsByMonth) Exit>> ");
		return jsonObject;
	}

	@Override
	public List<String> getActiveAttendanceDetailsByOrgIdwithDate(Long org_id, String date) {
		logger.info("AttendanceDetailsDaoImpl(getActiveAttendanceDetailsByOrgIdwithDate) Entry>> ");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session
				.createQuery("from AttendanceDetails where isDelete=:i and org_id=:org_id and dateOfRequest=:k and actionType='in'");
		query.setParameter("i", false);
		query.setParameter("org_id", org_id);
		query.setParameter("k", date);
		@SuppressWarnings("unchecked")
		List<AttendanceDetails> details = query.getResultList();
		List<String> emails= new ArrayList<>();
		List<String> listemail= new ArrayList<>();
		if(details.size()>0) {
			AttendanceDetails[] arraydata = new AttendanceDetails[details.size()];
			for (int i = 0; i < details.size(); i++) {
				arraydata[i] = details.get(i);
			}
			for(int j=0; j<arraydata.length;j++) {
				
				emails.add(arraydata[j].getEmail());
			}
			// Get list without duplicates
			List<String> emaillist = emails.stream().distinct().collect(Collectors.toList());
			listemail = emaillist;
		}
		logger.info("AttendanceDetailsDaoImpl(getActiveAttendanceDetailsByOrgIdwithDate) Exit>> ");
		return listemail;
	}

	@Override
	public JSONObject getAttendanceDateReport(String date, Long orgId, List<EmployeeDetails> users) {
		logger.info("AttendanceDetailsDaoImpl(getAttendanceReportsByMonth) Entry>> ");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session
				.createQuery("from AttendanceDetails where email=:i and org_id=:org_id and isDelete=:j and dateOfRequest=:k");
		List<EmployeeAttendanceDatereport> allreport = new ArrayList<EmployeeAttendanceDatereport>();
		int date_active = 0;
		int date_inactive = 0;
		int date_activeout = 0;
		int date_activein = 0;
		final JSONObject jsonObject = new JSONObject();
		query.setParameter("j",false);
		query.setParameter("org_id", orgId);
		query.setParameter("k", date);
		if(users.size()>0) {
			EmployeeDetails[] arraydata = new EmployeeDetails[users.size()];
			for (int bk = 0; bk < users.size(); bk++) {
				arraydata[bk] = users.get(bk);
			}
			for(int i=0;i<arraydata.length;i++) {
				
				query.setParameter("i",arraydata[i].getEmail());
				@SuppressWarnings("unchecked")
				List<AttendanceDetails> details = query.getResultList();
				EmployeeAttendanceDatereport report = new EmployeeAttendanceDatereport();
				report.setId(arraydata[i].getId());
				report.setEmail(arraydata[i].getEmail());
				report.setFirstname(arraydata[i].getFirstname());
				report.setLastaction("");
				report.setRole(arraydata[i].getRoleDetails().getRole());
				report.setDesignation(arraydata[i].getDesignationDetails().getDesignation());
				report.setDate(date);
				if(details.size()>0) {
					AttendanceDetails data = details.get(details.size()-1);
			        long active = new Long(data.getActiveHours());
			        report.setActivehrs(TimeUnit.MILLISECONDS.toHours(active) % 24 + ":"
							+ TimeUnit.MILLISECONDS.toMinutes(active) % 60 + ":"
							+ TimeUnit.MILLISECONDS.toSeconds(active) % 60);
			        report.setLastaction(data.getAction());
			        boolean present = false;
			        for(int b=0; b<details.size();b++) {
			        	if(details.get(b).getActionType().equals("in")) {
			        		present = true;
			        		date_active += 1;
			        		break;
			        	}
			        }
			        if(present==true) {
			        	if(data.getActionType().equals("in")) {
			        		date_activein += 1;
			        	}
			        	else {
			        		date_activeout += 1;
			        	}
			        }
			        report.setPresent(present);
			        
				}
				else {
					report.setActivehrs("00:00:00");
					date_inactive += 1;
					report.setPresent(false);
				}
				allreport.add(report);
			}
			
			
		}
		jsonObject.put("report", new Gson().toJson(allreport));
		jsonObject.put("day_presents", date_active);
		jsonObject.put("day_absents", date_inactive);
		jsonObject.put("active_In", date_activein);
		jsonObject.put("active_Out", date_activeout);
		logger.info("AttendanceDetailsDaoImpl(getAttendanceReportsByMonth) Exit>> ");
		return jsonObject;
	}

	@Override
	public List<AttendanceDetails> getAttendanceActiveStatusByOrgId_emailwithDate(Long org_id, String email, String date) {
		logger.info("AttendanceDetailsDaoImpl(getAttendanceActiveStatusByOrgId_emailwithDate) Entry>> ");
		final Session session = entityManager.unwrap(Session.class);
//		final Query query = session.createQuery(
//				"from AttendanceDetails where email=:i and org_id=:org_id and isDelete=:j and isActive=:k and dateOfRequest=:date ORDER BY timeOfAction DESC");
		final Query query = session.createQuery(
				"from AttendanceDetails where email=:i and org_id=:org_id and isDelete=:j and isActive=:k ORDER BY timeOfAction DESC");
		query.setParameter("i", email);
		query.setParameter("org_id", org_id);
//		query.setParameter("date", date);
		query.setParameter("j", false);
		query.setParameter("k", true);
		@SuppressWarnings("unchecked")
		List<AttendanceDetails> details = query.getResultList();
		boolean status;
		if(details.size()!=0) {
			AttendanceDetails detail = details.get(0);
			if(detail.getActionType().equals("in")) {
				status = true;
			}
			else {
				status = false;
			}
		}
		else {
			status = false;
		}
		logger.info("AttendanceDetailsDaoImpl(getAttendanceActiveStatusByOrgId_emailwithDate) Exit>> ");
//		return status;
		return details;
	}
	
	
	/**
	 * Get All Attendance details action report Dao By date and email
	 */
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getAttendanceactionReportsByDate(String date, String email) {
		logger.info("AttendanceDetailsDaoImpl(getAttendanceReportsByDate) Entry>> ");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session
				.createQuery("from AttendanceDetails where email=:i and isDelete=:j and dateOfRequest=:k");
		query.setParameter("i", email);
		query.setParameter("j", false);
		query.setParameter("k", date);
		JSONObject DateReport = new JSONObject();
		List<JSONObject> actionReportlist = new ArrayList<JSONObject>();
		List<AttendanceDetails> details = query.getResultList();
		AttendanceDetails[] arraydata = new AttendanceDetails[details.size()];
		for (int i = 0; i < details.size(); i++) {
			arraydata[i] = details.get(i);
		}
//		AttendanceDateReport Reportdata = new AttendanceDateReport();
		if (arraydata != null) {
			DateReport.put("Date",arraydata[0].getDateOfRequest());
//			DateReport.put("LastOut",new SimpleDateFormat("hh.mm aa").format(arraydata[arraydata.length - 1].getTimeOfAction()));
			for (int i = details.size()-1; i > 0; i--) {
				if(arraydata[i].getActionType().equals("out")) {
					DateReport.put("LastOut",new SimpleDateFormat("hh.mm aa").format(arraydata[i].getTimeOfAction()));
					break;
				}
			}
			long active = new Long(arraydata[arraydata.length - 1].getActiveHours());
			String activehrs = (TimeUnit.MILLISECONDS.toHours(active) % 24 + ":" + TimeUnit.MILLISECONDS.toMinutes(active) % 60
					+ ":" + TimeUnit.MILLISECONDS.toSeconds(active) % 60 );
			DateReport.put("ActiveHours",activehrs);

			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

			Date startTime;
			Date now;
			try {
				startTime = sdf.parse("08:00:00");
				now = sdf.parse(activehrs);
				long duration = now.getTime() - startTime.getTime();
				long hours = TimeUnit.MILLISECONDS.toHours(duration);
				long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60;
				long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60;
				
//				long milliseconds = duration % 1000;
				if (duration > 0 ) {
					// Overtime setter
					DateReport.put("Overtime",String.format("%02d:%02d:%02d", Math.abs(hours), Math.abs(minutes), Math.abs(seconds)));
				} else {
					// Deviation setter
					DateReport.put("Deviation",String.format("%02d:%02d:%02d", Math.abs(hours), Math.abs(minutes), Math.abs(seconds)));
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error(
						"Exception occured in AttendanceDetailsDaoImpl(getAttendanceReportsByDate) and Exception details >> "
								+ e);
				
			}

			boolean first = false;
			for (int i = 0; i < arraydata.length; i++) {
				if (first == false && arraydata[i].getActionType().equals("in") && !(arraydata[i].getAction().equals("Auto checkin"))) {
					DateReport.put("FirstIn",new SimpleDateFormat("hh.mm aa").format(arraydata[i].getTimeOfAction()));
					first = true;
				}
				if(arraydata[i].getActionType().equals("out")&& i>=1 && i!=arraydata.length-1) {
					Date start = new Date();
					Date end = new Date();
					start = arraydata[i].getTimeOfAction();
					end = arraydata[i+1].getTimeOfAction();
					long takenmilliseconds = 0;
					takenmilliseconds += (end.getTime() - start.getTime());
					long hours = TimeUnit.MILLISECONDS.toHours(takenmilliseconds);
					long hoursMillis = TimeUnit.HOURS.toMillis(hours);

					long minutes = TimeUnit.MILLISECONDS.toMinutes(takenmilliseconds - hoursMillis);
					long minutesMillis = TimeUnit.MINUTES.toMillis(minutes);

					long seconds = TimeUnit.MILLISECONDS.toSeconds(takenmilliseconds - hoursMillis - minutesMillis);
					JSONObject data = new JSONObject();
					String resultString = "";
					resultString = hours + " : " + minutes + " : " + seconds +" HMS";
					data.put("action", arraydata[i].getAction());
					data.put("duration", resultString);
					actionReportlist.add(data);
				}
			}
			DateReport.put("outActionReport",actionReportlist);
			
		}
		logger.info("AttendanceDetailsDaoImpl(getAttendanceReportsByDate) Exit>> ");
        return DateReport;
	}
	
	/*
	 * Attendance Monthly report
	 */
	@Override
	public UserAttendanceReport getUserAttendanceBarchartData(String startdate, String enddate, String email) {

		logger.info("AttendanceDetailsDaoImpl(getUserAttendanceBarchartData) Entry>> ");
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session
				.createQuery("from AttendanceDetails where email=:i and isDelete=:j and dateOfRequest=:k");
		
		final UserAttendanceReport result = new UserAttendanceReport();
		
		
		// Create a List for chart x axis data
		List<String> xAxit = new ArrayList<String>();
	    
	  // Create a list of list String object for chart data chart
		List<ArrayList<Object>> chart_data = new ArrayList<ArrayList<Object>>();
		
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
			
			// To get date reports with details and push into list
			for (int k = 0; k < dates.size(); k++) {
				

				// Create a ArrayList object for chart data			    
			    ArrayList<Object> chartDataArraystring = new ArrayList<Object>();
				
				Date lDate = (Date) dates.get(k);
				String ds = formatter.format(lDate);	
				//set value for the query parameters
				query.setParameter("i", email);
				query.setParameter("j", false);
				query.setParameter("k", ds);
				@SuppressWarnings("unchecked")
				List<AttendanceDetails> details = query.getResultList();
				if (details.size() > 0) {
					xAxit.add(ds);
					AttendanceDetails[] arraydata = new AttendanceDetails[details.size()];
					
					for (int i = 0; i < details.size(); i++) {
						arraydata[i] = details.get(i);
					}
					
					if (arraydata != null) {
						Long active = new Long(arraydata[arraydata.length - 1].getActiveHours());
						String activeHrsStr = TimeUnit.MILLISECONDS.toHours(active) % 24 + ":"
								+ TimeUnit.MILLISECONDS.toMinutes(active) % 60 + ":"
								+ TimeUnit.MILLISECONDS.toSeconds(active) % 60;						
						

						SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

						Date startTime;
						Date now;
						try {
							startTime = sdf.parse("08:00:00");
							now = sdf.parse(activeHrsStr);
							long duration = now.getTime() - startTime.getTime();
							long hours = TimeUnit.MILLISECONDS.toHours(duration);
							long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60;
							long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60;
//							long milliseconds = duration % 1000;
							if (duration > 0) {
								// Overtime setter
								String ActiveHrscomment = "Over Time : "+String.format("%02d:%02d:%02d", Math.abs(hours),Math.abs(minutes), Math.abs(seconds));
								chartDataArraystring.add(ActiveHrscomment);
								double  val = (double)  active / 3600000;
								chartDataArraystring.add(val);
							} else {
								// Diviation setter
								String ActiveHrscomment = "Deviation Time : "+String.format("%02d:%02d:%02d", Math.abs(hours),Math.abs(minutes), Math.abs(seconds));
								chartDataArraystring.add(ActiveHrscomment);
								double  val = (double)  active / 3600000;
								chartDataArraystring.add(val);
							}
							chart_data.add(chartDataArraystring);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							logger.error(
									"Exception occured in AttendanceDetailsDaoImpl(getUserAttendanceBarchartData) and Exception details >> "
											+ e);
						}
						
					}

				}
				

			}
			
			result.setChart_data(chart_data);
			result.setDates_x_axis(xAxit);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(
					"Exception occured in AttendanceDetailsDaoImpl(getUserAttendanceBarchartData) and Exception details >> "
							+ e);
		}
		logger.info("AttendanceDetailsDaoImpl(getUserAttendanceBarchartData) Exit>> ");
		return result;
	}

	@Override
	public List<AttendanceDetails> getActiveEmployeesTodayAttendanceList(Long orgId,String email) {
		// TODO Auto-generated method stub
		logger.info("AttendanceDetailsDaoImpl(getActiveEmployeesTodayAttendanceList) Entry>> ");
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		String strDate = df.format(date);
//		System.out.println("strDate.."+strDate);
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session
				.createQuery("from AttendanceDetails where org_id=:org_id and isDelete=:j and dateOfRequest=:k and email=:y");
		query.setParameter("org_id", orgId);
		query.setParameter("j", false);
		query.setParameter("k", strDate);
		query.setParameter("y", email);
		@SuppressWarnings("unchecked")
		List<AttendanceDetails> details = query.getResultList();
		logger.info("AttendanceDetailsDaoImpl(getActiveEmployeesTodayAttendanceList) Exit>> ");	
		return details;
		
	}

	@Override
	public ArrayList<BigInteger> getActiveOrgIdsWithAttendance() {
		// TODO Auto-generated method stub
		logger.info("AttendanceDetailsDaoImpl(getActiveOrgIdsWithAttendance) >>Entry");
		final Session session = entityManager.unwrap(Session.class);

		final Query query = session.createNativeQuery("select A.org_id from org_details as A join pricing_plan_details as B ON A.plan_id = B.id where A.is_deleted = false and A.is_activated = true and B.modules like concat('%','attendance','%')");
		@SuppressWarnings("unchecked")
		ArrayList<BigInteger> ids = new ArrayList<BigInteger>();
		ids = (ArrayList<BigInteger>) query.getResultList();
		if(ids != null && ids.size() > 0) {
			logger.info("AttendanceDetailsDaoImpl(getActiveOrgIdsWithAttendance) >>Exit");
			//System.out.println(ids);
			return ids;
		}else {
			logger.info("AttendanceDetailsDaoImpl(getActiveOrgIdsWithAttendance) >>Exit");
			return null;
		}
//		return null;
	}

	@Override
	public List<String> getEmailForCheckInAttendanceUserList(Long org_id) {
		// TODO Auto-generated method stub
		logger.info("AttendanceDetailsDaoImpl(getEmailForCheckInAttendanceUserList) Entry>> ");
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		String strDate = df.format(date);
//		System.out.println("strDate.."+strDate);
		final Session session = entityManager.unwrap(Session.class);
		final Query query = session
				.createNativeQuery("select distinct email from attendance_details where date_of_request=:k and org_id=:org and is_delete=:j");
		query.setParameter("org", org_id);
		query.setParameter("j", false);
		query.setParameter("k", strDate);
		@SuppressWarnings("unchecked")
		List<String> details = query.getResultList();
		logger.info("AttendanceDetailsDaoImpl(getActiveEmployeesTodayAttendanceList) Exit>> ");	
		return details;
		}


}
