package com.tcube.api.cron;

import com.google.gson.Gson;

import com.tcube.api.controller.AttendanceDetailsApiService;
import com.tcube.api.model.*;
import com.tcube.api.service.*;
import com.tcube.api.utils.TodayAttendanceWorkFromDetailsSendToSlack;
import com.tcube.api.utils.TodayLeaveUserListSendToSlack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/cron" })
public class CronTask {

    private static Logger logger = LogManager.getLogger(AttendanceDetailsApiService.class);

    @Autowired
    AttendanceDetailsService attendaceDetailsService;

    @Autowired
    OrgDetailsService orgDetailsService;

    @Autowired
    HolidayDetailsService holidayDetailsService;

    @Autowired
    EmployeeDetailsService employeeDetailsService;

    @Autowired
    NotificationDetailsService notificationDetailsService;

    @Autowired
    AppsIntegrationDetailsService appsIntegrationDetailsService;

    @Autowired
    TodayAttendanceWorkFromDetailsSendToSlack todayAttendanceWorkFromDetailsSendToSlack;

    @Autowired
    ManageIntegrationService manageIntegrationService;

    @Autowired
    MailConfigDetailsService mailConfigDetailsService;

    @Autowired
    ReminderDetailsService remainderDetailsService;

    @Autowired
    PropertiesService propertiesService;

    @Autowired
    LeaveTrackerDetailsService leaveTrackerDetailsService;

    @Autowired
    TodayLeaveUserListSendToSlack todayLeaveUserListSendToSlackService;
    
    @Autowired
    ManageBranchDetailsService manageBranchService;
    
    @Autowired
    TimeTrackerDetailsService timeTrackerDetailsService;
    


	@SuppressWarnings("unchecked")
	@Scheduled(cron = "0 30 10 ? * MON-FRI", zone = "Asia/Calcutta")
//	@GetMapping(value = "/getActiveEmployeesTodayAttendanceList", headers = "Accept=application/json")
//    @Scheduled(cron = "*/10 * * * * ?", zone = "Asia/Calcutta")
	public void getActiveEmployeesTodayAttendanceList() {
		try {
			Appproperties postUniqueData = new Appproperties();
			postUniqueData.setKey("attendance-summary");
			Date newdate = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String datevalue = formatter.format(newdate);
			postUniqueData.setValue("attendance-summary" + datevalue);
			propertiesService.createproperties(postUniqueData);
			logger.info("AttendanceDetailsApiService(getActiveEmployeesTodayAttendanceList) >> Entry");
//            System.out.println("inside getActiveEmployeesTodayAttendanceList");
			String response = "";
			final JSONObject jsonObject = new JSONObject();

			try {
				ArrayList<BigInteger> orgList = attendaceDetailsService.getActiveOrgIdsWithAttendance();
				String holidayPattern = "yyyy-MM-dd";
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(holidayPattern);
				String date = simpleDateFormat.format(new Date());

				TimeZone timezone = TimeZone.getTimeZone("Asia/Kolkata");

				for (int j = 0; j < orgList.size(); j++) {
					final long orgId = orgList.get(j).longValue();
						ManageIntegration manageIntegration = manageIntegrationService.getOrgAMdetails(orgId, "slack",
								"all");
						boolean isKeyPrimaryModule = false;
//						boolean isAppPrimaryModule = false;
						String slackUrl = "";
						JSONParser jsonParser = new JSONParser();
						JSONArray jsonArray1 = new JSONArray();
						if (manageIntegration != null) {
							if (manageIntegration.getisActive() == true) {
								slackUrl = appsIntegrationDetailsService.getSlackUrlAttendance(orgId);
							}
						}

						final List<HolidayDetails> Holiday_data = holidayDetailsService.getTodayHolidayDetails(orgId,
								date);
						if (Holiday_data.size() == 0) {
							
							List<ReminderDetails> remainderTodayLeaveUpdate = remainderDetailsService
									.TodayUserListAccessUpdate(orgId);
							for (int r = 0; r < remainderTodayLeaveUpdate.size(); r++) {
//                            System.out.println("r value:" + r);
								String keyValue = remainderTodayLeaveUpdate.get(r).getKey_primary();
								if (keyValue.equals("slack-today-Active-Employee")) {
									isKeyPrimaryModule = true;
								} else {
									if(keyValue.equals("app-active-users")){
										if (remainderTodayLeaveUpdate.get(r).getActive_users() != null) {
											String s = remainderTodayLeaveUpdate.get(r).getActive_users();
//											System.out.println("today-leaves" + s);
											jsonArray1 = (JSONArray) jsonParser.parse(s);
										}
									}
								}

							}

							@SuppressWarnings("rawtypes")
							List<EmployeeDetails> allActiveEmployee = new ArrayList<EmployeeDetails>();

							final List<EmployeeDetails> employeeDetails = employeeDetailsService
									.getEmployeeDetailsByOrgID(orgId);
							allActiveEmployee.addAll(employeeDetails); 

//							System.out.println("before..."+allActiveEmployee.size());

							final List<ManageBranchDetails> branchDetails = manageBranchService
									.getBranchesByOrgId(orgId);

							List<LeaveTrackerDetails> todayLeaveUserList = leaveTrackerDetailsService
									.getTodayLeaveUserList(orgId);

							final List<String> attendanceCheckInPersonEmail = attendaceDetailsService
									.getEmailForCheckInAttendanceUserList(orgId);
//							System.out
//									.println("attendanceCheckInPersonEmail...." + attendanceCheckInPersonEmail.size());

							for (int y = 0; y < attendanceCheckInPersonEmail.size(); y++) {
								for (int z = 0; z < allActiveEmployee.size(); z++) {
									if (attendanceCheckInPersonEmail.get(y)
											.equals(allActiveEmployee.get(z).getEmail())) {
										allActiveEmployee.remove(z);
										break;
									}
								}
							}
							
							 List<LeaveTrackerDetails> todayLeaveUserData = leaveTrackerDetailsService.getTodayLeaveUserList(orgId);
							 for (int y = 0; y < todayLeaveUserData.size(); y++) {
									for (int z = 0; z < allActiveEmployee.size(); z++) {
										if (todayLeaveUserData.get(y).getEmp_id()
												.equals(allActiveEmployee.get(z).getId())) {
											allActiveEmployee.remove(z);
											break;
										}
									}
								}
//							 System.out.println("after..."+allActiveEmployee.size());

							int count_of_I_am_from_home = 0;
							int count_of_I_am_from_office = 0;

							ArrayList<String> arrayOfIAmFromHomeEmployee = new ArrayList<String>();

							ArrayList<EmployeeDetails> arrayOfIAmFromOfficeEmployee = new ArrayList<EmployeeDetails>();

							for (int z = 0; z < employeeDetails.size(); z++) {
								String email = employeeDetails.get(z).getEmail();
								String userName = employeeDetails.get(z).getFirstname();
//                            System.out.println("email...." + email);
								final List<AttendanceDetails> details = attendaceDetailsService
										.getActiveEmployeesTodayAttendanceList(orgId, email);
								String action = "";
								for (int k = 0; k < details.size(); k++) {
//                                System.out.println("k value:" + k);
									if (details.get(k).getAction().equals("I am in from office")) {
										action = details.get(k).getAction();
										arrayOfIAmFromOfficeEmployee.add(employeeDetails.get(z));
									} else if (details.get(k).getAction().equals("I am in from home")) {
										action = details.get(k).getAction();
									}

								}

								if (action != "" && action.equals("I am in from office")) {
								} else {
									if (action != "" && action.equals("I am in from home")) {
										arrayOfIAmFromHomeEmployee.add(count_of_I_am_from_home, userName);
										count_of_I_am_from_home = count_of_I_am_from_home + 1;

									}
								}
							}

							JSONArray jsonArray = new JSONArray();
							jsonArray.add(arrayOfIAmFromOfficeEmployee);
							jsonArray.add(arrayOfIAmFromHomeEmployee);
							jsonArray.add(branchDetails);
							jsonArray.add(todayLeaveUserList);
							jsonArray.add(allActiveEmployee);
							
							if (arrayOfIAmFromOfficeEmployee.size() != 0 || arrayOfIAmFromHomeEmployee.size() != 0) {
								if (slackUrl != "" && isKeyPrimaryModule == true) {
									todayAttendanceWorkFromDetailsSendToSlack.sendMessageActiveEmployeeUserList(
											slackUrl, arrayOfIAmFromOfficeEmployee, arrayOfIAmFromHomeEmployee,
											branchDetails, todayLeaveUserList,allActiveEmployee);
								}
//								final OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
								logger.info("attendance today work from home/work from office userlist >> Entry");
								for (int k = 0; k < jsonArray1.size(); k++) {
									final JSONObject newJsonObject = new JSONObject((Map) jsonArray1.get(k));
								NotificationsDetails notification = new NotificationsDetails();
								notification.setOrg_id(orgId);
								notification.setMessage("Staff attendance summary");
								notification.setTo_notify_id(newJsonObject.getString("emp_Id"));
								notification.setNotifier(newJsonObject.getString("emp_Id"));
								notification.setModule_name("");
								notification.setSub_module_name("");
								notification.setDate_of_request(date);
								notification.setIs_deleted(false);
								notification.setIs_read(false);
								notification.setApproval_comments(new Gson().toJson(jsonArray));
								notification.setKeyword("attendance-summary");
								final NotificationsDetails notification_data = notificationDetailsService
										.addNotification(notification, timezone.toString());
								logger.info("attendance today work from home/work from office userlist >> Exit");
								}
							}
						}
					}
				

			} catch (Exception e) {
				e.printStackTrace();
			}
			logger.info("AttendanceDetailsApiService(getActiveEmployeesTodayAttendanceList) >> Exit");

			// Your cron task logic goes here
		} catch (Exception e) {
			// Handle exceptions here
		}
	}

    @Scheduled(cron = "0 0 10 ? * MON-FRI", zone = "Asia/Calcutta")
//	@GetMapping(value = "/gettodayleaveuserlistbyslack", headers = "Accept=application/json")
	public void getTodayLeaveUserListBySlack() {
		try {
            Appproperties postUniqueData = new Appproperties();
            postUniqueData.setKey("pto-timeoff");
            Date newdate = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String datevalue = formatter.format(newdate);
            postUniqueData.setValue("pto-timeoff" + datevalue);
            propertiesService.createproperties(postUniqueData);

            logger.info("LeaveTrackerDetailsApiService(getTodayLeaveUserListBySlack) >> Entry");
            logger.info("(---------- scheduleTodayLeaveAsync(getTodayLeaveUserListBySlack) >> Entry ----------");
            String response = "";
            final JSONObject jsonObject = new JSONObject();
			try {
				logger.info(
						"(---------- scheduleTodayLeaveAsync(getTodayLeaveUserListBySlack) >> Entry request----------");
				ArrayList<BigInteger> orgList = leaveTrackerDetailsService.getActiveOrgIdsWithLeaveTrackerPlan();
				// System.out.println(orgList);
				String holidayPattern = "yyyy-MM-dd";
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(holidayPattern);
				String date = simpleDateFormat.format(new Date());
				TimeZone timezone = TimeZone.getTimeZone("Asia/Kolkata");

				for (int i = 0; i < orgList.size(); i++) {
					final long orgId = orgList.get(i).longValue();
//							System.out.println("<----------------->"+orgList.get(i).longValue());
					List<LeaveTrackerDetails> todayLeaveUserList = leaveTrackerDetailsService
							.getTodayLeaveUserList(orgList.get(i).longValue());
					JSONParser jsonParser = new JSONParser();
					JSONArray jsonArray = new JSONArray();
					boolean isKeyPrimaryModule = false;
					List<ReminderDetails> remainderTodayLeaveUpdate = remainderDetailsService
							.TodayUserListAccessUpdate(orgList.get(i).longValue());
					for (int r = 0; r < remainderTodayLeaveUpdate.size(); r++) {
						String keyValue = remainderTodayLeaveUpdate.get(r).getKey_primary();
						if (keyValue.equals("slack-today-leave-userlist")) {
							isKeyPrimaryModule = true;
						} else {
							if (keyValue.equals("today-leaves")) {
								if (remainderTodayLeaveUpdate.get(r).getActive_users() != null) {
									String s = remainderTodayLeaveUpdate.get(r).getActive_users();
//									System.out.println("today-leaves" + s);
									jsonArray = (JSONArray) jsonParser.parse(s);
								}
							}
						}

					}
					ManageIntegration manageIntegration = manageIntegrationService.getOrgAMdetails(orgId, "slack",
							"all");
						if (manageIntegration != null) {
						if (manageIntegration.getisActive() == true) {
							String	slackUrl = appsIntegrationDetailsService.getSlackUrlLeaveTracker(orgList.get(i).longValue());
//									System.out.println("slackUrl.." + slackUrl);
							if (slackUrl != "" && isKeyPrimaryModule == true) {
								if (todayLeaveUserList.size() != 0) {
									todayLeaveUserListSendToSlackService.sendMessageToSlackLeaveUserList(slackUrl,
											todayLeaveUserList);
									logger.info(
											"(---------- scheduleTodayLeaveAsync(getTodayLeaveUserListBySlack) >> response----------");
								} else {
									if (todayLeaveUserList.size() == 0) {
										todayLeaveUserListSendToSlackService.sendMessageToSlack(slackUrl);
										logger.info(
												"(---------- scheduleTodayLeaveAsync(getTodayLeaveUserListBySlack) >> no data response----------");
									}
								}
							}
						}
					}
							for (int k = 0; k < jsonArray.size(); k++) {
								final JSONObject newJsonObject = new JSONObject((Map) jsonArray.get(k));
								NotificationsDetails notification = new NotificationsDetails();
								notification.setOrg_id(orgId);
								notification.setMessage("Staff on PTO Today");
								notification.setTo_notify_id(newJsonObject.getString("emp_Id"));
								notification.setNotifier(newJsonObject.getString("emp_Id"));
								notification.setModule_name("");
								notification.setSub_module_name("");
								notification.setDate_of_request(date);
								notification.setIs_deleted(false);
								notification.setIs_read(false);
								notification.setApproval_comments(new Gson().toJson(todayLeaveUserList));
								notification.setKeyword("Staff-leave-today");
								final NotificationsDetails notification_data = notificationDetailsService
										.addNotification(notification, timezone.toString());

							}
						
				}

			} catch (Exception e) {
				logger.info("exception occuerd------>" + e);
			}
			logger.info("---------- scheduleTodayLeaveAsync(getTodayLeaveUserListBySlack) >> Exit ----------");

			// Your cron task logic goes here
		} catch (Exception e) {
			// Handle exceptions here
		}
	}
    

	/**
	 * To send the notification for the all active orgadmin that timesheet not
	 *  submitted user list
//	 * @throws ParseException 
	 */
//    @SuppressWarnings("unused")
	//		@Scheduled(cron = "0 30 18 ? * *")
	@Scheduled(cron = "0 30 21 ? * MON-SAT", zone = "Asia/Calcutta")
//	@GetMapping(value = "/timesheetnotsubmitetduserlist", headers = "Accept=application/json")
	public String getTimesheetNotSubmitetdUserListForActiveOrg() throws ParseException {
		logger.info("TimeTrackerDetailsApiService(getTimesheetNotSubmitetdUserListForActiveOrg) >> Entry");
		final JSONObject jsonObject = new JSONObject();
		//to get the active orgids which there pricing plan includes the time-tracker module
		logger.info("TimeTrackerDetailsApiService(getActiveOrgIdsWithTimetrackerPlan) >> Entry");
		ArrayList<BigInteger> activeOrgIds = timeTrackerDetailsService.getActiveOrgIdsWithTimetrackerPlan();
		logger.info("TimeTrackerDetailsApiService(getActiveOrgIdsWithTimetrackerPlan) >> Exit");
		//today date
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		TimeZone timezone = TimeZone.getTimeZone("Asia/Kolkata");       
		//to get the list of employees details where they are not submitted the timesheets
		if(activeOrgIds != null) {
			for (int j=0;j<activeOrgIds.size();j++) {
				JSONParser jsonParser = new JSONParser();
				JSONArray jsonArray = new JSONArray();
				List<ReminderDetails> remainderTodayLeaveUpdate = remainderDetailsService
						.TodayUserListAccessUpdate(activeOrgIds.get(j).longValue());
				
//				System.out.println("remainderTodayLeaveUpdate.."+remainderTodayLeaveUpdate.size());
				
				for (int r = 0; r < remainderTodayLeaveUpdate.size(); r++) {
					String keyValue = remainderTodayLeaveUpdate.get(r).getKey_primary();
//					System.out.println("keyValue..."+ keyValue);
//					System.out.println(remainderTodayLeaveUpdate.get(r).getActive_users());
					if(keyValue.equals("not-submitted-user")) {
						if (remainderTodayLeaveUpdate.get(r).getActive_users() != null) {
							String s = remainderTodayLeaveUpdate.get(r).getActive_users();
//							System.out.println("today-leaves" + s);
							jsonArray = (JSONArray) jsonParser.parse(s);
						}
					}
				}
//				System.out.println("not-submitted-user size ..."+jsonArray.size());
//				if(!notificationDetailsService.checkTimesheetNotSubmittedUserListNotificationCreated(activeOrgIds.get(j).longValue(), date)) {
				for(int k = 0; k < jsonArray.size(); k++) {
					
					final JSONObject newJsonObject = new JSONObject((Map) jsonArray.get(k));
//					System.out.println(newJsonObject.getString("emp_Id"));
					List<String> empDetails = timeTrackerDetailsService.getNotSubmittedUserListByOrgid(activeOrgIds.get(j));
					if(empDetails != null && empDetails.size() != 0) {
//						final OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(activeOrgIds.get(j).longValue());
						logger.info("TimeTrackerDetailsApiService(send notification to orgadmin) >> Entry");
						NotificationsDetails notification = new NotificationsDetails();
						notification.setOrg_id(activeOrgIds.get(j).longValue());
						notification.setMessage("Timesheets not submitted staff");
						notification.setTo_notify_id(newJsonObject.getString("emp_Id"));
						notification.setNotifier(newJsonObject.getString("emp_Id"));
						notification.setModule_name("");
						notification.setSub_module_name("");
						notification.setDate_of_request(date);
						notification.setIs_deleted(false);
						notification.setIs_read(false);
						notification.setKeyword("timesheet-not-submitted");
						notification.setApproval_comments(new Gson().toJson(empDetails));
						final NotificationsDetails data = notificationDetailsService.addNotification(notification,timezone.toString());
					} else if(empDetails !=null && empDetails.size() == 0){
//						final OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(activeOrgIds.get(j).longValue());
						logger.info("TimeTrackerDetailsApiService(send notification to orgadmin) >> Entry");
						NotificationsDetails notification = new NotificationsDetails();
						notification.setOrg_id(activeOrgIds.get(j).longValue());
						notification.setMessage("Everyone has submitted their timesheets");
						notification.setTo_notify_id(newJsonObject.getString("emp_Id"));
						notification.setNotifier(newJsonObject.getString("emp_Id"));
						notification.setModule_name("");
						notification.setSub_module_name("");
						notification.setDate_of_request(date);
						notification.setIs_deleted(false);
						notification.setIs_read(false);
						notification.setKeyword("all-submitted");
						final NotificationsDetails data = notificationDetailsService.addNotification(notification,timezone.toString());
					}
				}
//				}else {
//					jsonObject.put("orgid",activeOrgIds.get(j));
//				}
			}
		}
		return new Gson().toJson(jsonObject);

	}
}

