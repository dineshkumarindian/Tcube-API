package com.tcube.api.service;
import java.util.Date;
import java.util.List;

import com.tcube.api.model.NotificationsDetails;

public interface NotificationDetailsService {

	public NotificationsDetails addNotification(NotificationsDetails details,String zone);
	
	public List<NotificationsDetails> getNotificationsEmpid(String empid);
	
	public NotificationsDetails getNotificationById(Long id);
	
	public NotificationsDetails updateNotificationMarkAsRead(NotificationsDetails details);
	
	public NotificationsDetails updateNotifierPersonalDetails(String empid, byte[] img);

	public List<NotificationsDetails> getUnreadNotificationsEmpid(String empid);

	public List<NotificationsDetails> getNotificationsEmpidAndDateOfRequest(String empId, String dor, String subModule , long tSheetID);

	public NotificationsDetails updateStatus(NotificationsDetails oldDetails);

	public List<NotificationsDetails> getNotificationByEmpIdAndDate(NotificationsDetails incomingDetails, Long orgId,String zone);
	
	public boolean checkTimesheetNotSubmittedUserListNotificationCreated(Long orgid, String dateOfRequest);

}
