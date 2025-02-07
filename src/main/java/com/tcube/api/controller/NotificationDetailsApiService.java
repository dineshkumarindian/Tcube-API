package com.tcube.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.NotificationsDetails;
import com.tcube.api.service.EmployeeDetailsService;
import com.tcube.api.service.NotificationDetailsService;
import com.tcube.api.utils.ImageProcessor;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;

import io.sentry.Sentry;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = { "/api/notification" })
public class NotificationDetailsApiService {
	
	/**
	 * Logger is to log application messages.
	 */
	private static Logger logger = LogManager.getLogger(NotificationDetailsApiService.class);
	
	@Autowired
	NotificationDetailsService notificationDetailsService;
	
	@Autowired
	EmployeeDetailsService employeeDetailsService;
	//to add notifications
	@PostMapping(value = "/create", headers = "Accept=application/json")
	public String createNotification(@RequestBody final String request, final UriComponentsBuilder ucBuilder) {
		logger.info("NotificationDetailsApiService(addNotification) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			logger.debug("NotificationDetailsApiService(addNotification) >> Request");
			final JSONObject newJsonObject = new JSONObject(request);
			String zone= newJsonObject.getString("timezone");
			newJsonObject.remove("timezone");
			final NotificationsDetails details = MapperUtil.readAsObjectOf(NotificationsDetails.class, newJsonObject.toString());
			String empid = details.getNotifier();
			details.setIs_deleted(false);
			details.setIs_read(false);
			String name;
			if(!empid.equals("super_admin")){
				final EmployeeDetails empdetails = employeeDetailsService.getAllEmployeeDetailsByID(empid);
				name = empdetails.getFirstname()+" "+empdetails.getLastname();
				details.setTo_notifier_name(name);
//				details.setTo_notifier_prfl_img(empdetails.getProfile_image());
			}
			else {
			    name = "Super admin";
				details.setTo_notifier_name(name);
			}
			logger.debug("NotificationDetailsApiService(addNotification) >> Request Model Object"
					+ new Gson().toJson(details));
			/*
			 * Service file  create method call
			 */
			final NotificationsDetails data = notificationDetailsService.addNotification(details,zone);
			if (data != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Notification created successfully");
				response = new Gson().toJson(jsonObject);
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Failed to add notification Details");
				response = new Gson().toJson(jsonObject);
			}

		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in NotificationDetailsApiService(addNotification) and Exception details >> "+ e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Failed to add notification details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("NotificationDetailsApiService(addNotification) >> Exit");
		return response;
	}

	//to get notifications based on to_notify_id
	@GetMapping(value = "/getnotificationsbyempid/{empid}", headers = "Accept=application/json")
	@ResponseBody
	public String getNotificationByEmpid(@PathVariable("empid") final String empid) {
		logger.info("NotificationDetailsApiService(getNotificationByEmpid) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();

		try {
			logger.debug("NotificationDetailsApiService(getNotificationByEmpid) >> Request");
			List<NotificationsDetails> details = notificationDetailsService.getNotificationsEmpid(empid);
			final List<NotificationsDetails> updatedDetails =  new ArrayList<NotificationsDetails>();
			for(int i=0;i<details.size();i++) {
				NotificationsDetails data = new NotificationsDetails();
				data = details.get(i);
				updatedDetails.add(data);
//				if(data.getTo_notifier_prfl_img() != null) {
//					data.setTo_notifier_prfl_img(ImageProcessor.decompressBytes(details.get(i).getTo_notifier_prfl_img()));
//					updatedDetails.add(data);
//				}else updatedDetails.add(data);
			}
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "No data found for the given empID.");
			}
			logger.debug("NotificationDetailsApiService(getNotificationByEmpid) >> Response");
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			  logger.error("Exception occured in NotificationDetailsApiService(getNotificationByEmpid) and Exception details >> "
			  + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting Task details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("NotificationDetailsApiService(getNotificationByEmpid) >> Exit");
		return response;
	}
	
	
	@GetMapping(value = "/getnotificationsbyempidanddateandmodule", headers = "Accept=application/json")
	@ResponseBody
	public String getNotificationByEmpidAndDate(@RequestBody final String request) {
		logger.info("NotificationDetailsApiService(getNotificationByEmpidAndDate) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();

		try {
			logger.debug("NotificationDetailsApiService(getNotificationByEmpidAndDate) >> Request");
			final JSONObject newJsonObject = new JSONObject(request);
			String empId = newJsonObject.getString("emp_id");
			String dor = newJsonObject.getString("date_of_request");
			String subModule = newJsonObject.getString("sub_module");
			long tSheetID = newJsonObject.getLong("tSheetID");
			List<NotificationsDetails> details = notificationDetailsService.getNotificationsEmpidAndDateOfRequest(empId , dor ,subModule ,tSheetID);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "No data found for the given empID.");
			}
			logger.debug("NotificationDetailsApiService(getNotificationByEmpidAndDate) >> Response");
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
//			System.out.println(e);
			  logger.error("Exception occured in NotificationDetailsApiService(getNotificationByEmpidAndDate) and Exception details >> "
			  + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting Task details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("NotificationDetailsApiService(getNotificationByEmpidAndDate) >> Exit");
		return response;
	}
	
	// update 
	
	@PutMapping(value = "/updateStatus", headers = "Accept=application/json")
	public String updateNotification(@RequestBody final String details) {
		logger.info("NotificationDetailsApiService(updateNotification) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();	
		try {
			final JSONObject newJsonObject = new JSONObject(details);
			Long Id = newJsonObject.getLong("id");
			newJsonObject.remove("id");
			NotificationsDetails oldDetails = notificationDetailsService.getNotificationById(Id);
			logger.debug("NotificationDetailsApiService(updateNotification) >> Request");
			final NotificationsDetails notificationdetails = MapperUtil.readAsObjectOf(NotificationsDetails.class,
					newJsonObject.toString());
			oldDetails.setApproval_status(notificationdetails.getApproval_status());
			
			final NotificationsDetails details1 = notificationDetailsService.updateStatus(oldDetails);
			if (details1 != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "Notification updated successfully");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "Error in updating notification details");
			}
			response = new Gson().toJson(jsonObject);
			logger.debug("NotificationDetailsApiService(updateNotification) >> Response");
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
			logger.error("Exception occured in RoleDetailsApiService(updateNotification) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in updating notification details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("NotificationDetailsApiService(updateNotification) >> Exit");
		return response;
	}
	
	//to get unread notifications counts based on to_notify_id
	@GetMapping(value = "/getunreadnotificationcountsbyempid/{empid}", headers = "Accept=application/json")
	@ResponseBody
	public String getUnreadNotificationByEmpid(@PathVariable("empid") final String empid) {
		logger.info("NotificationDetailsApiService(getUnreadNotificationByEmpid) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();

		try {
			logger.debug("NotificationDetailsApiService(getUnreadNotificationByEmpid) >> Request");
			List<NotificationsDetails> details = notificationDetailsService.getUnreadNotificationsEmpid(empid);
			int Count = 0;
			if(details.size() == 0) {
				Count = 0;
			}else {
				Count =details.size() ;
			}
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(Count));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "No data found for the given empID.");
			}
			logger.debug("NotificationDetailsApiService(getUnreadNotificationByEmpid) >> Response");
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			  logger.error("Exception occured in NotificationDetailsApiService(getUnreadNotificationByEmpid) and Exception details >> "
			  + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting Task details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("NotificationDetailsApiService(getUnreadNotificationByEmpid) >> Exit");
		return response;
	}
	
	//to get notifications by id
	
	@PutMapping(value = "/getnotificationbyid", headers = "Accept=application/json")
	public String getNotificationById(@RequestBody final String request) {
		logger.info("NotificationDetailsApiService(getNotificationById) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(request);
			final NotificationsDetails newDetails = MapperUtil.readAsObjectOf(NotificationsDetails.class,
					newJsonObject.toString());
			Long Id = newDetails.getId();
			logger.debug("NotificationDetailsApiService(getNotificationById) >> Request");
			final NotificationsDetails details = notificationDetailsService.getNotificationById(Id);
			if (details != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "No data found for the given ID.");
			}
			logger.debug("NotificationDetailsApiService(getNotificationById) >> Response");
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			  logger.error("Exception occured in NotificationDetailsApiService(getNotificationById) and Exception details >> "
			  + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting notification details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("NotificationDetailsApiService(getNotificationById) >> Exit");
		return response;
	}
	
	//to update the mark_as_read for the notifications
	@SuppressWarnings("unused")
	@PutMapping(value = "/updatemarkasread", headers = "Accept=application/json")
	public String updateNotificationMarkAsRead(@RequestBody final String request) {
		logger.info("NotificationDetailsApiService(updateNotificationMarkAsRead) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
			final JSONObject newJsonObject = new JSONObject(request);
			Integer check = 0;
			JSONArray ids = newJsonObject.getJSONArray("Ids");
			for(int i = 0; i< ids.length(); i++) {
				check +=1;
				Long id = ids.getLong(i);
				logger.debug("NotificationDetailsApiService(updateNotificationMarkAsRead) >> Request");
				final NotificationsDetails details = notificationDetailsService.getNotificationById(id);
				final NotificationsDetails updatedetails = notificationDetailsService.updateNotificationMarkAsRead(details);
			}
			if (check == ids.length()) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, "updated the nofication by mark as read");
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
				jsonObject.put(RestConstants.DATA, "No data found for the given ID.");
			}
			logger.debug("NotificationDetailsApiService(updateNotificationMarkAsRead) >> Response");
			response = new Gson().toJson(jsonObject);
		} catch (Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			  logger.error("Exception occured in NotificationDetailsApiService(updateNotificationMarkAsRead) and Exception details >> "
			  + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in getting notification details");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("NotificationDetailsApiService(updateNotificationMarkAsRead) >> Exit");
		return response;
		
	}
	
	@PutMapping(value="/getNotificationByEmpIdAndDate",headers="Accept=application/json")
    public String getNotificationByEmpIdAndDate(@RequestBody final String details, final UriComponentsBuilder ucBuilder){
		logger.info("NotificationDetailsApiService(getNotificationByEmpIdAndDate) >> Entry");
		String response = "";
		final JSONObject jsonObject = new JSONObject();
		try {
        	logger.info("NotificationDetailsApiService(getNotificationByEmpIdAndDate)");
        	final JSONObject newJsonObject = new JSONObject(details);
        	Long orgId = newJsonObject.getLong("org_id");
        	newJsonObject.remove("org_id");
        	String zone= newJsonObject.getString("timezone");
			newJsonObject.remove("timezone");
        	final NotificationsDetails incomingDetails = MapperUtil.readAsObjectOf(NotificationsDetails.class,newJsonObject.toString());
        	final List<NotificationsDetails> newdetails = notificationDetailsService.getNotificationByEmpIdAndDate(incomingDetails , orgId,zone);
        	final List<NotificationsDetails> updatedDetails =  new ArrayList<NotificationsDetails>();
			for(int i=0;i<newdetails.size();i++) {
				NotificationsDetails data = new NotificationsDetails();
				data = newdetails.get(i);
				updatedDetails.add(data);
//				if(data.getTo_notifier_prfl_img() != null) {
//					data.setTo_notifier_prfl_img(ImageProcessor.decompressBytes(newdetails.get(i).getTo_notifier_prfl_img()));
//					updatedDetails.add(data);
//				}else updatedDetails.add(data);
			}
        	if (newdetails != null) {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
				jsonObject.put(RestConstants.DATA, new Gson().toJson(updatedDetails));
			} else {
				jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
				jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
				jsonObject.put(RestConstants.DATA, "Error in extracting notifications by emp id and dates");
			}
        	response = new Gson().toJson(jsonObject);
		}catch(Exception e) {
			Sentry.captureException(e);
//			System.out.println(e);
			logger.error("Exception occured in NotificationDetailsApiService(getNotificationByEmpIdAndDate) and Exception details >> " + e);
			jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
			jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
			jsonObject.put(RestConstants.DATA, "Error in extracting  notifications by emp id and dates");
			response = new Gson().toJson(jsonObject);
		}
		logger.info("NotificationDetailsApiService(getNotificationByEmpIdAndDate) >> Exit");
		return response;
    }
	
}
