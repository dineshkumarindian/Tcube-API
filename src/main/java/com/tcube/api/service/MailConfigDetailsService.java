package com.tcube.api.service;

import com.tcube.api.model.MailConfigDetails;

public interface MailConfigDetailsService {

	public MailConfigDetails createMailConfig(MailConfigDetails MailConfigDetails);

	public MailConfigDetails getMailConfigById(Long id);

	public MailConfigDetails updateMailConfig(MailConfigDetails newDetails);

	public MailConfigDetails deleteMailConfig(MailConfigDetails newDetails);

	public MailConfigDetails getMailConfigByOrgId(Long id);

}
