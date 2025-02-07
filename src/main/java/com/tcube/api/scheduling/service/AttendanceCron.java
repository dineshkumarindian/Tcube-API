//package com.tcube.api.scheduling.service;
//
//import java.math.BigInteger;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.TimeZone;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.json.JSONObject;
//import org.json.simple.JSONArray;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.web.bind.annotation.GetMapping;
//
//import com.google.gson.Gson;
//import com.tcube.api.model.AttendanceDetails;
//import com.tcube.api.model.EmployeeDetails;
//import com.tcube.api.model.HolidayDetails;
//import com.tcube.api.model.ManageIntegration;
//import com.tcube.api.model.NotificationsDetails;
//import com.tcube.api.model.OrgDetails;
//import com.tcube.api.model.ReminderDetails;
//import com.tcube.api.service.AppsIntegrationDetailsService;
//import com.tcube.api.service.AttendanceDetailsService;
//import com.tcube.api.service.EmployeeDetailsService;
//import com.tcube.api.service.HolidayDetailsService;
//import com.tcube.api.service.MailConfigDetailsService;
//import com.tcube.api.service.ManageIntegrationService;
//import com.tcube.api.service.NotificationDetailsService;
//import com.tcube.api.service.OrgDetailsService;
//import com.tcube.api.service.ReminderDetailsService;
//import com.tcube.api.utils.TodayAttendanceWorkFromDetailsSendToSlack;
//
//
//@Service
//@EnableScheduling
//public class AttendanceCron {
//
//	/**
//	 * Logger is to log application messages.
//	 */
//	private static Logger logger = (Logger) LogManager.getLogger(AttendanceCron.class);
//
//	@Autowired
//	AttendanceDetailsService attendaceDetailsService;
//
//	@Autowired
//	OrgDetailsService orgDetailsService;
//
//	@Autowired
//	HolidayDetailsService holidayDetailsService;
//
//	@Autowired
//	EmployeeDetailsService employeeDetailsService;
//
//	@Autowired
//	NotificationDetailsService notificationDetailsService;
//
//	@Autowired
//	AppsIntegrationDetailsService appsIntegrationDetailsService;
//
//	@Autowired
//	TodayAttendanceWorkFromDetailsSendToSlack todayAttendanceWorkFromDetailsSendToSlack;
//
//	@Autowired
//	ManageIntegrationService manageIntegrationService;
//
//	@Autowired
//	MailConfigDetailsService mailConfigDetailsService;
//
//	@Autowired
//	ReminderDetailsService remainderDetailsService;
//
//
//
//	@SuppressWarnings("unchecked")
//	//@Scheduled(cron = "0 0 11 ? * MON-FRI", zone ="Asia/Calcutta")
//	@Scheduled(cron = "*/10 * * * * ?", zone ="Asia/Calcutta")
//	public void getActiveEmployeesTodayAttendanceList() {
//		logger.info("AttendanceDetailsApiService(getActiveEmployeesTodayAttendanceList) >> Entry");
//		System.out.println("inside getActiveEmployeesTodayAttendanceList");
//		String response = "";
//		final JSONObject jsonObject = new JSONObject();
//
//		try {
//			ArrayList<BigInteger> orgList = attendaceDetailsService.getActiveOrgIdsWithAttendance();
//			String pattern = "yyyy-MM-dd";
//			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
//			String date = simpleDateFormat.format(new Date());
//			TimeZone timezone = TimeZone.getTimeZone("Asia/Kolkata");
//
//			for (int j = 0; j < orgList.size(); j++) {
//				final long orgId = orgList.get(j).longValue();
//				ManageIntegration manageIntegration = manageIntegrationService.getOrgAMdetails(orgId,
//						"slack", "all");
//				boolean isKeyPrimaryModule = false;
//				String slackUrl = "";
//				if (manageIntegration != null) {
//					if (manageIntegration.getisActive() == true) {
//						isKeyPrimaryModule = false;
//						slackUrl = appsIntegrationDetailsService.getSlackUrlAttendance(orgId);
//						List<ReminderDetails> remainderTodayLeaveUpdate = remainderDetailsService
//								.getAllRemindersByOrgId(orgId);
//						for (int r = 0; r < remainderTodayLeaveUpdate.size(); r++) {
//							System.out.println("r value:"+r);
//							String keyValue = remainderTodayLeaveUpdate.get(r).getKey_primary();
//							boolean is_Active = remainderTodayLeaveUpdate.get(r).getIs_active();
//							if (keyValue.equals("slack-today-Active-Employee") && is_Active) {
//								isKeyPrimaryModule = true;
//								System.out.println("true..." + isKeyPrimaryModule);
//								break;
//							}
//
//						}
//					}
//				}
//
//				final List<HolidayDetails> Holiday_data = holidayDetailsService
//						.getTodayHolidayDetails(orgId, date);
//				if (Holiday_data.size() == 0) {
//					final List<EmployeeDetails> employeeDetails = employeeDetailsService
//							.getEmployeeDetailsByOrgID(orgId);
//					int count_of_I_am_from_home = 0;
//					int count_of_I_am_from_office = 0;
//
//					ArrayList<String> arrayOfIAmFromHomeEmployee = new ArrayList<String>();
//
//					ArrayList<String> arrayOfIAmFromOfficeEmployee = new ArrayList<String>();
//
//					for (int z = 0; z < employeeDetails.size(); z++) {
//						String email = employeeDetails.get(z).getEmail();
//						String userName = employeeDetails.get(z).getFirstname();
//						System.out.println("email...." + email);
//						final List<AttendanceDetails> details = attendaceDetailsService
//								.getActiveEmployeesTodayAttendanceList(orgId, email);
//						String action = "";
//						for (int k = 0; k < details.size(); k++) {
//							System.out.println("k value:"+k);
//							if (details.get(k).getAction().equals("I am in from office")) {
//								action = details.get(k).getAction();
//							} else if (details.get(k).getAction().equals("I am in from home")) {
//								action = details.get(k).getAction();
//							}
//
//						}
//
//						if (action != "" && action.equals("I am in from office")) {
//							arrayOfIAmFromOfficeEmployee.add(count_of_I_am_from_office, userName);
//							count_of_I_am_from_office = count_of_I_am_from_office + 1;
//
//						} else {
//							if (action != "" && action.equals("I am in from home")) {
//								arrayOfIAmFromHomeEmployee.add(count_of_I_am_from_home, userName);
//								count_of_I_am_from_home = count_of_I_am_from_home + 1;
//
//							}
//						}
//					}
//
//					JSONArray jsonArray = new JSONArray();
//					jsonArray.add(arrayOfIAmFromOfficeEmployee);
//					jsonArray.add(arrayOfIAmFromHomeEmployee);
//					if (arrayOfIAmFromOfficeEmployee.size() != 0
//							|| arrayOfIAmFromHomeEmployee.size() != 0) {
//						if (slackUrl != "" && isKeyPrimaryModule == true) {
//							todayAttendanceWorkFromDetailsSendToSlack.sendMessageActiveEmployeeUserList(
//									slackUrl, arrayOfIAmFromOfficeEmployee, arrayOfIAmFromHomeEmployee);
//						}
//						final OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
//						logger.info("attendance today work from home/work from office userlist >> Entry");
//						NotificationsDetails notification = new NotificationsDetails();
//						notification.setOrg_id(orgId);
//						notification.setMessage("Employee List for Today - Work from Home And Office.");
//						notification.setTo_notify_id(orgDetails.getEmp_id());
//						notification.setNotifier(orgDetails.getEmp_id());
//						notification.setModule_name("");
//						notification.setSub_module_name("");
//						notification.setDate_of_request(date);
//						notification.setIs_deleted(false);
//						notification.setIs_read(false);
//						notification.setApproval_comments(new Gson().toJson(jsonArray));
//						final NotificationsDetails notification_data = notificationDetailsService
//								.addNotification(notification, timezone.toString());
//						logger.info("attendance today work from home/work from office userlist >> Exit");
//					}
//				}
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		logger.info("AttendanceDetailsApiService(getActiveEmployeesTodayAttendanceList) >> Exit");
//	}
//
//}
