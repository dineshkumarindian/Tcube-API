package com.tcube.api.service;


import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tcube.api.model.ExperienceLetterDetails;
import com.tcube.api.dao.ExperienceLetterDao;

@Service
@Transactional
public class ExperienceLetterServiceImpl implements ExperienceLetterService{
	
	@Autowired
	ExperienceLetterDao experienceLetterDao;

	@Override
	public ExperienceLetterDetails createExperienceLetter(ExperienceLetterDetails experienceDetails) {
		// TODO Auto-generated method stub
		return experienceLetterDao.createExperienceLetter(experienceDetails);
	}

	@Override
	public List<ExperienceLetterDetails> getActiveOrgIdInternsipDetail(Long id) {
		// TODO Auto-generated method stub
		return experienceLetterDao.getActiveOrgIdInternsipDetail(id);
	}

	@Override
	public ExperienceLetterDetails getById(Long id) {
		// TODO Auto-generated method stub
		return experienceLetterDao.getById(id);
	}

	@Override
	public ExperienceLetterDetails deleteExperienceLetter(ExperienceLetterDetails experienceDetails) {
		// TODO Auto-generated method stub
		return experienceLetterDao.deleteExperienceDetails(experienceDetails);
	}

	@Override
	public ExperienceLetterDetails updateExperienceLetter(ExperienceLetterDetails experienceDetails) {
		// TODO Auto-generated method stub
		return experienceLetterDao.updateExperienceDetails(experienceDetails);
	}

	@Override
	public ExperienceLetterDetails DeleteAllSelectExperienceLetter(ExperienceLetterDetails experienceDetails) {
		// TODO Auto-generated method stub
		return experienceLetterDao.deleteAllExperienceDetails(experienceDetails);
	}

}
