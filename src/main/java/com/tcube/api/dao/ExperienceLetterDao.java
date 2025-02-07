package com.tcube.api.dao;

import java.util.List;
import com.tcube.api.model.ExperienceLetterDetails;
import com.tcube.api.model.BusinessLetterDetails;

public interface ExperienceLetterDao {
	
	public ExperienceLetterDetails createExperienceLetter(ExperienceLetterDetails experienceLetterDetails);
	
	public List<ExperienceLetterDetails> getActiveOrgIdInternsipDetail(Long id);
	
	public ExperienceLetterDetails getById(Long id);
	
	public ExperienceLetterDetails deleteExperienceDetails(ExperienceLetterDetails oldExperienceDetails);
	
	public ExperienceLetterDetails updateExperienceDetails(ExperienceLetterDetails oldExperienceDeatils);
	 
	public ExperienceLetterDetails  deleteAllExperienceDetails(ExperienceLetterDetails experienceLetterDetails);
	
}
