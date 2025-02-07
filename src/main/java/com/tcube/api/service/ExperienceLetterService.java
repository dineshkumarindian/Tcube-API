package com.tcube.api.service;

import java.util.*;
import com.tcube.api.model.ExperienceLetterDetails;


public interface ExperienceLetterService {

	public ExperienceLetterDetails createExperienceLetter(ExperienceLetterDetails experienceDetails);
	
	public List<ExperienceLetterDetails> getActiveOrgIdInternsipDetail(Long id);
	
	public ExperienceLetterDetails getById(Long id);
	
	public ExperienceLetterDetails deleteExperienceLetter(ExperienceLetterDetails experienceDetails);
	
	public ExperienceLetterDetails updateExperienceLetter(ExperienceLetterDetails experienceDetails);
	
	public ExperienceLetterDetails  DeleteAllSelectExperienceLetter(ExperienceLetterDetails experienceDetails);
}
