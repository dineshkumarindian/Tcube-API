package com.tcube.api.dao;

public interface EmailSenderDao {

	public Boolean sendEmail(String toAddress, String subject, String template, boolean isHtml);
}
