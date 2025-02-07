package com.tcube.api.service;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tcube.api.dao.ReleaseNotesDao;
import com.tcube.api.model.CustomReleaseNotes;
import com.tcube.api.model.CustomReleaseNotesGetAllTable;
import com.tcube.api.model.DashboardReleaseNotes;
import com.tcube.api.model.ReleaseNotesDetails;

@Service
@Transactional
public class ReleaseNotesServiceImpl implements ReleaseNotesService{

    @Autowired
    ReleaseNotesDao releaseNotesDao;
    
    @Override
    public ReleaseNotesDetails createReleaseNotes(ReleaseNotesDetails releaseNotesDetails,String zone) {
        return releaseNotesDao.createReleaseNotes(releaseNotesDetails,zone);
    }
    
    @Override
    public List<ReleaseNotesDetails> getAllReleaseNotesDetails() {
        return releaseNotesDao.getAllReleaseNotesDetails();
    }
    
   
    @Override
    public List<CustomReleaseNotes> getEmpWithReleaseById(Long id) {
        return releaseNotesDao.getEmpWithReleaseById(id);
    }
    
    @Override
    public List<ReleaseNotesDetails>  getReleaseNotesCount(Long id) {
        return releaseNotesDao.getReleaseNotesCount(id);
    }
    
    @Override
    public List<DashboardReleaseNotes>  getAddedReleaseNotes(Long id) {
        return releaseNotesDao.getAddedReleaseNotes(id);
    }
    
    @Override
    public ReleaseNotesDetails getById(Long id) {
        return releaseNotesDao.getById(id);
    }

    @Override
    public ReleaseNotesDetails getDeleteReleaseNotesDetails(ReleaseNotesDetails oldDetails) {
        return releaseNotesDao.deleteReleaseNotesDetails(oldDetails);
    }

    @Override
    public ReleaseNotesDetails updateReleaseNotesDetails(ReleaseNotesDetails ReleaseIdDetails,String zone) {
        return releaseNotesDao.updateReleaseNotesDetails(ReleaseIdDetails,zone);
    }

    @Override 
    public ReleaseNotesDetails deleteAllReleaseNotesDetails(ReleaseNotesDetails oldReleaseNotesDetails) {
        return releaseNotesDao.deleteAllReleaseNotesDetails(oldReleaseNotesDetails);
    }

	@Override
	public List<CustomReleaseNotes> getAllReleaseNoteNew(String type) {
		// TODO Auto-generated method stub
		return releaseNotesDao.getAllReleaseNoteNew(type);
	}
//
	@Override
	public List<ReleaseNotesDetails> getPdfViewDetails(Long id) {
		// TODO Auto-generated method stub
		return releaseNotesDao.getPdfView(id);
	}

	@Override
	public ReleaseNotesDetails updatePublishReleaseNotesDetails(ReleaseNotesDetails oldDetails) {
		// TODO Auto-generated method stub
		return releaseNotesDao.updatePublishReleaseNotesDetails(oldDetails);
	}

	@Override
	public List<CustomReleaseNotesGetAllTable> getAllReleaseNotesTable() {
		// TODO Auto-generated method stub
		return releaseNotesDao.getAllReleaeNotesTable();
	}

	@Override
	public int updatenew_release() {
		return releaseNotesDao.updatenew_release();
	}

	@Override
	public int updatenew_release_byEmpId(String id) {
		return releaseNotesDao.updatenew_release_byEmpId(id);
	}
	
	
}
