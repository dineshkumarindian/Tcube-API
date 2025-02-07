package com.tcube.api.service;

import java.util.List;
import com.tcube.api.model.CustomReleaseNotes;
import com.tcube.api.model.CustomReleaseNotesGetAllTable;
import com.tcube.api.model.DashboardReleaseNotes;
import com.tcube.api.model.ReleaseNotesDetails;

public interface ReleaseNotesService {

    public ReleaseNotesDetails createReleaseNotes(ReleaseNotesDetails releaseNotesDetails,String zone);
    
    public List<ReleaseNotesDetails> getAllReleaseNotesDetails();
    
    public List<CustomReleaseNotes> getEmpWithReleaseById(Long id);
    
    public List<ReleaseNotesDetails>  getReleaseNotesCount(Long id);
    
    public List<CustomReleaseNotesGetAllTable> getAllReleaseNotesTable();

    public List<DashboardReleaseNotes>  getAddedReleaseNotes(Long id);
    
    public ReleaseNotesDetails getById(Long id);

    public ReleaseNotesDetails getDeleteReleaseNotesDetails(ReleaseNotesDetails oldDetails);
    
    public ReleaseNotesDetails updatePublishReleaseNotesDetails(ReleaseNotesDetails oldDetails);
    
    public ReleaseNotesDetails updateReleaseNotesDetails(ReleaseNotesDetails ReleaseIdDetails,String zone);
    
    public ReleaseNotesDetails deleteAllReleaseNotesDetails(ReleaseNotesDetails oldReleaseNotesDetails);
    
    public List<CustomReleaseNotes> getAllReleaseNoteNew(String type);    
//    
    public List<ReleaseNotesDetails> getPdfViewDetails(Long id);

	public int updatenew_release();

	public int updatenew_release_byEmpId(String id);
}
