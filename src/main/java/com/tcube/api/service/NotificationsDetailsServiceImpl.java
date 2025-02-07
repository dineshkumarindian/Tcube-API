package com.tcube.api.service;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.NotificationDetailsDao;
import com.tcube.api.model.NotificationsDetails;

@Service
@Transactional
public class NotificationsDetailsServiceImpl implements NotificationDetailsService {

	@Autowired
	NotificationDetailsDao notificationDetailsDao;
	
	@Override
	public NotificationsDetails addNotification(NotificationsDetails details,String zone) {
		// TODO Auto-generated method stub
		return notificationDetailsDao.addNotification(details,zone);
	}

	@Override
	public List<NotificationsDetails> getNotificationsEmpid(String empid) {
		// TODO Auto-generated method stub
		return notificationDetailsDao.getNotificationsEmpid(empid);
	}

	@Override
	public NotificationsDetails getNotificationById(Long id) {
		// TODO Auto-generated method stub
		return notificationDetailsDao.getNotificationById(id);
	}
	
	@Override
	public NotificationsDetails updateNotificationMarkAsRead(NotificationsDetails details) {
		// TODO Auto-generated method stub
		return notificationDetailsDao.updateNotificationMarkAsRead(details);
	}

	@Override
	public NotificationsDetails updateNotifierPersonalDetails(String empid, byte[] img) {
		// TODO Auto-generated method stub
		return notificationDetailsDao.updateNotifierPersonalDetails(empid,img);
	}

	@Override
	public List<NotificationsDetails> getUnreadNotificationsEmpid(String empid) {
		// TODO Auto-generated method stub
		return notificationDetailsDao.getUnreadNotificationsEmpid(empid);
	}

	@Override
	public List<NotificationsDetails> getNotificationsEmpidAndDateOfRequest(String empId, String dor , String sunmodule , long tSheetID) {
		// TODO Auto-generated method stub
		return notificationDetailsDao.getNotificationsEmpidAndDateOfRequest(empId , dor , sunmodule , tSheetID);
	}

	@Override
	public NotificationsDetails updateStatus(NotificationsDetails oldDetails) {
		// TODO Auto-generated method stub
		return notificationDetailsDao.updateStatus(oldDetails);
	}

	@Override
	public List<NotificationsDetails> getNotificationByEmpIdAndDate(NotificationsDetails incomingDetails, Long orgId,String zone) {
		// TODO Auto-generated method stub
		return notificationDetailsDao.getNotificationByEmpIdAndDate(incomingDetails,orgId,zone);
	}

	@Override
	public boolean checkTimesheetNotSubmittedUserListNotificationCreated(Long orgid, String dateOfRequest) {
		// TODO Auto-generated method stub
		return notificationDetailsDao.checkTimesheetNotSubmittedUserListNotificationCreated(orgid,dateOfRequest);
	}

}
