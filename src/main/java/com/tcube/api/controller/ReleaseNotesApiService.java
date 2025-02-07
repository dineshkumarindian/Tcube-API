package com.tcube.api.controller;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.google.gson.Gson;
//import com.tcube.api.model.CustomInternshipDetails;
import com.tcube.api.model.CustomReleaseNotes;
import com.tcube.api.model.CustomReleaseNotesGetAllTable;
import com.tcube.api.model.DashboardReleaseNotes;
import com.tcube.api.model.OrgDetails;
import com.tcube.api.model.ReleaseNotesDetails;
import com.tcube.api.service.OrgDetailsService;
import com.tcube.api.service.ReleaseNotesService;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;

import io.sentry.Sentry;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = {"/api/ReleaseNotesDetails"})
public class ReleaseNotesApiService {
    
    private static Logger logger = LogManager.getLogger(ReleaseNotesService.class);
    
    @Autowired
    ReleaseNotesService releaseNotesService;
    
    @Autowired
    OrgDetailsService orgDetailService;
    
    //create release notes api 
    @PostMapping(value = "/add-Release", headers = "Accept=application/json")
    public String createReleaseNotesDetails(@RequestBody String releaseNotesDetails) {
        logger.info("ReleaseNotesApiService(createReleaseNotesDetails)>> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
//        System.out.println("51" + releaseNotesDetails);
        try {
            final JSONObject newJsonObject = new JSONObject(releaseNotesDetails);
            String zone = newJsonObject.getString("timezone");
			newJsonObject.remove("timezone");
            logger.debug("ReleaseNotesApiService(createReleaseNotesDetails) >> Request");
            final ReleaseNotesDetails releaseDetails = MapperUtil.readAsObjectOf(ReleaseNotesDetails.class,newJsonObject.toString());
//            releaseDetails.setOrgDetails(orgDetails);
//            releaseDetails.setIs_deleted(false);
            final ReleaseNotesDetails details = releaseNotesService.createReleaseNotes(releaseDetails,zone);
           details.setIs_deleted(false);
          
            if (details!= null) {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                jsonObject.put(RestConstants.DATA, details);
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "Error in creating new details");
            }
            response = new Gson().toJson(jsonObject);
            logger.debug("ReleaseNotesApiService(createReleaseNotesDetails)>> Response");

        } catch (Exception e) {
        	Sentry.captureException(e);
            e.printStackTrace();
            logger.error("Exception occured in releaseNotesService(createReleaseNotes) and Exception details >>" + e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error ocurs while creating the release Notes details");
            response = new Gson().toJson(jsonObject);
        }
        logger.info("ReleaseNotesApiService(createReleaseNotesDetails) >> Exit");
        return response;
    }
    
    //getAllreleasenoteswithpdf 
    @GetMapping(value="/getAllReleaseNotes",headers="Accept=application/json")
    public String getAllReleaseNotesDetails(){
        logger.info("ReleaseNotesApiService(getAllReleaseNotesDetails) >> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
        try {
            logger.debug("ReleaseNotesApiService(getAllReleaseNotesDetails)");
            final List<ReleaseNotesDetails> details = releaseNotesService.getAllReleaseNotesDetails();
//            final List<ReleaseNotesDetails> newDetails = new ArrayList<>();
//            for(ReleaseNotesDetails i : details) {
//                if(i.getIs_deleted() == false) {
//                    newDetails.add(i);
//                }
//            }
            if (details != null) {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "No data found for the given ID.");
            }
            logger.debug("ReleaseNotesApiService(getAllReleaseNotesDetails) >> Request");
            response = new Gson().toJson(jsonObject);
        }catch(Exception e) {
        	Sentry.captureException(e);
            logger.error("Exception occured in ReleaseNotesApiService(getAllReleaseNotesDetails) and Exception details >> " + e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in getting  release notes details");
            response = new Gson().toJson(jsonObject);
        }
        logger.info("ReleaseNotesApiService(getAllReleaseNotesDetails) >> Exit");
        return response;
    }
    @GetMapping(value="/getAllCustomReleaseNotes",headers="Accept=application/json")
    public String getAllCustomReleaseNotesDetails(){
        logger.info("ReleaseNotesApiService(getAllCustomReleaseNotes) >> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
        try {
            logger.debug("ReleaseNotesApiService(getAllReleaseNotesDetails)");
            final List<CustomReleaseNotesGetAllTable> details = releaseNotesService.getAllReleaseNotesTable();
//            final List<ReleaseNotesDetails> newDetails = new ArrayList<>();
//            for(ReleaseNotesDetails i : details) {
//                if(i.getIs_deleted() == false) {
//                    newDetails.add(i);
//                }
//            }
            if (details != null) {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "No data found for the given ID.");
            }
            logger.debug("ReleaseNotesApiService(getAllCustomReleaseNotes) >> Request");
            response = new Gson().toJson(jsonObject);
        }catch(Exception e) {
        	Sentry.captureException(e);
            logger.error("Exception occured in ReleaseNotesApiService(getAllReleaseNotesDetails) and Exception details >> " + e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in getting  release notes details");
            response = new Gson().toJson(jsonObject);
        }
        logger.info("ReleaseNotesApiService(getAllCustomReleaseNotes) >> Exit");
        return response;
    }
    
    //get perticular release id
    @GetMapping(value = "/getEmpWithReleaseById/{id}", headers = "Accept=application/json")
    public String getActiveEmpWithReleaseByOrgId(@PathVariable final Long id,final UriComponentsBuilder ucBuilder) {
        logger.info("ReleaseNotesApiService(getActiveEmpWithReleaseByOrgId)>> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
        try {
        	logger.debug("ReleaseNotesApiService(getActiveEmpWithReleaseByOrgId) >> Request");
//            logger.info("get the releaseNotesApiService<<Entry");
            List<CustomReleaseNotes> details = releaseNotesService.getEmpWithReleaseById(id);
            if (details != null) {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "Failed to get releasenotes details by Org Id");
            }
            response = new Gson().toJson(jsonObject);
        } catch(Exception e) {
        	Sentry.captureException(e);
            logger.error(
                    "Exception occured in ReleaseNotesApiService(getActiveEmpWithReleaseByOrgId) and Exception details >> "
                            + e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in extracting  releasenotes details by Org Id");
            response = new Gson().toJson(jsonObject);
        }
        logger.info("ReleaseNotesApiService(getActiveEmpWithReleaseByOrgId) >> Exit");
        return response;
    }
    
    //get the perticular id release notes 
    @GetMapping(value="/getById/{id}",headers ="Accept=application/json")
    public String getReleaseNotesbyId(@PathVariable final Long id) {
        logger.info("ReleaseNotesApiService(getReleaseNotesbyId)>> Entry");
        String response ="";
        final JSONObject jsonObject = new JSONObject();
        try {
            logger.debug("ReleaseNotesApiService(getReleaseNotesbyId)");
            final ReleaseNotesDetails details = releaseNotesService.getById(id);
            if(details != null) {
                jsonObject.put(RestConstants.STATUS_CODE,RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE,RestConstants.SUCCESS_STRING);
                jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "Error in extracting release notes details by id");
            }
            response = new Gson().toJson(jsonObject);
        } catch(Exception e) {
        	Sentry.captureException(e);
            logger.error("Exception occured in (getReleaseNotesbyId) and Exception details >> " + e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in extracting  release notes details by id");
            response = new Gson().toJson(jsonObject);
        }
        logger.info("ReleaseNotesApiService(getReleaseNotesbyId)<< Exit");
        return response;
        
    }
    
    //delete release notes id
    @PutMapping(value="/deleteReleaseNotes/{id}", headers = "Accept=appllication/json")
    public String deleteReleaseNotes(@PathVariable(value ="id")Long id) {
        logger.info("ReleaseNotesApiService(deleteReleaseNotes)>> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
        try {
            final ReleaseNotesDetails oldDetails = releaseNotesService.getById(id);
            oldDetails.setIs_deleted(true);
            logger.debug("ReleaseNotesApiService(deleteReleaseNotes)>> Request");
            final ReleaseNotesDetails details = releaseNotesService.getDeleteReleaseNotesDetails(oldDetails);
            if (details != null) {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                jsonObject.put(RestConstants.DATA, "Release notes details deleted successfully");
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "Error in deleting release notes details");
            }
            response = new Gson().toJson(jsonObject);
        } catch(Exception e) {
        	Sentry.captureException(e);
            logger.error("Exception occured in releaseNotesApiService(deleteReleaseLetter)>> "+e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in deleting release notes details");
            response = new Gson().toJson(jsonObject);
        }
        logger.info("ReleaseNotesApiService(deleteReleaseNotes)>> Exit");
        return response;
    }
    
    //publish the release notes
    
    @PutMapping(value="/publishReleaseNotes/{id}", headers = "Accept=appllication/json")
    public String publishReleaseNotes(@PathVariable(value ="id")Long id) {
    	logger.info("ReleaseNotesApiService(publishReleaseNotes)>> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
        try {
            final ReleaseNotesDetails oldDetails = releaseNotesService.getById(id);
            oldDetails.setIs_publish(true);
            logger.debug("ReleaseNotesApiService(publishReleaseNotes)>> Request");
            final ReleaseNotesDetails details = releaseNotesService.updatePublishReleaseNotesDetails(oldDetails);
            if (details != null) {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                jsonObject.put(RestConstants.DATA, "Publish release notes details successfully");
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "Error in publish release notes details");
            }
            response = new Gson().toJson(jsonObject);
        }
        catch(Exception e) {
        	Sentry.captureException(e);
        	logger.error("Exception occured in releaseNotesApiService(deleteReleaseLetter)>> "+e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in publish release notes details");
            response = new Gson().toJson(jsonObject);
        	
        }
        logger.info("ReleaseNotesApiService(publishReleaseNotes)>> Exit");
		return response;
    }
    
    //republish the release notes
    
    @PutMapping(value="/rePublishReleaseNotes/{id}", headers = "Accept=appllication/json")
    public String rePublishReleaseNotes(@PathVariable(value ="id")Long id) {
    	logger.info("ReleaseNotesApiService(rePublishReleaseNotes)>> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
        try {
            final ReleaseNotesDetails oldDetails = releaseNotesService.getById(id);
            oldDetails.setIs_republish(false);
            logger.debug("ReleaseNotesApiService(rePublishReleaseNotes)>> Request");
            final ReleaseNotesDetails details = releaseNotesService.updatePublishReleaseNotesDetails(oldDetails);
            if (details != null) {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                jsonObject.put(RestConstants.DATA, "Republish release notes details deleted successfully");
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "Error in republish release notes details");
            }
            response = new Gson().toJson(jsonObject);
        }
        catch(Exception e) {
        	Sentry.captureException(e);
        	logger.error("Exception occured in releaseNotesApiService(deleteReleaseLetter)>> "+e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in republish release notes details");
            response = new Gson().toJson(jsonObject);
        	
        }
        logger.info("ReleaseNotesApiService(rePublishReleaseNotes)>> Exit");
		return response;
    }
    
    //update frelease notes
    @SuppressWarnings("unused")
    @PutMapping(value="/updateReleaseNotes/{id}",headers="Accept=application/json")
    public String updateReleaseNotesDetails(@PathVariable(value="id")Long id,@RequestBody String body)
    {
        logger.info("ReleaseNotesApiService(updateReleaseNotesDetails)>> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
        try {
            logger.info("ReleaseNotesApiService(updateReleaseNotesDetails)>> Request");
            final JSONObject newJsonObject = new JSONObject(body);
            String zone = newJsonObject.getString("timezone");
			newJsonObject.remove("timezone");
            
            final ReleaseNotesDetails releaseIdDetails = releaseNotesService.getById(id);
            final ReleaseNotesDetails newReleaseDetails = MapperUtil.readAsObjectOf(ReleaseNotesDetails.class, newJsonObject.toString());
            releaseIdDetails.setKeyNote(newReleaseDetails.getKeyNote());
            releaseIdDetails.setReleaseNotesTitle(newReleaseDetails.getReleaseNotesTitle());
            releaseIdDetails.setProductName(newReleaseDetails.getProductName());
            releaseIdDetails.setVersion(newReleaseDetails.getVersion());
            releaseIdDetails.setDor(newReleaseDetails.getDor());
            releaseIdDetails.setCompanyLogo(newReleaseDetails.getCompanyLogo());
            releaseIdDetails.setLogoFileName(newReleaseDetails.getLogoFileName());
            releaseIdDetails.setWhatsNew(newReleaseDetails.getWhatsNew());
            releaseIdDetails.setImprovement(newReleaseDetails.getImprovement());
            releaseIdDetails.setBugFixes(newReleaseDetails.getBugFixes());
            releaseIdDetails.setComingsoon(newReleaseDetails.getComingsoon());
            releaseIdDetails.setGeneral(newReleaseDetails.getGeneral());
            releaseIdDetails.setNotes_pdfFormat(newReleaseDetails.getNotes_pdfFormat());
            if(!newReleaseDetails.getIs_publish()) {
            	releaseIdDetails.setIs_publish(false);
            	releaseIdDetails.setIs_republish(false);
            } else {
            	releaseIdDetails.setIs_publish(true);
            	releaseIdDetails.setIs_republish(true);
            	
            }
//            	releaseIdDetails.setIs_publish(newReleaseDetails.getIs_publish());
            
            final ReleaseNotesDetails details = releaseNotesService.updateReleaseNotesDetails(releaseIdDetails,zone);
            
         
//            releaseIdDetails.setOrgDetails(orgDetails);
           
            if (details != null) {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                jsonObject.put(RestConstants.DATA, details);
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "Failed to update release notes");
            }
            response = new Gson().toJson(jsonObject);
            logger.debug("ReleaseNotesApiService(updateReleaseNotesStatus) >> Response");
        } catch (Exception e) 
        {
        	Sentry.captureException(e);
            logger.error("Exception occured in ReleaseNotesApiService(updateReleaseNotesDetails)<<"+e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in updating release notes");
            response = new Gson().toJson(jsonObject);
        }
        logger.info("ReleaseNotesApiService(updateReleaseNotesStatus) >> Exit");
        return response;
        
    }
    
    
    //bulk delete api for rlease notes
    @PutMapping(value ="/bulkDelete" , headers = "Accept=application/json")
    public String bulkReleaseNotesDelete(@RequestBody final String details) {
        logger.info("ReleaseNotesApiService(bulkDelete) >> Entry");
        String response ="";
        final JSONObject jsonObject = new JSONObject();
        try {
            Integer check = 0;
            final JSONObject newJsonObject = new JSONObject(details);
            JSONArray deleteIds = newJsonObject.getJSONArray("deleteIds");
            for(int i = 0; i< deleteIds.length(); i++) {
                Long id = deleteIds.getLong(i);
                final ReleaseNotesDetails oldDetails = releaseNotesService.getById(id);
                oldDetails.setIs_deleted(true);
                final ReleaseNotesDetails newDetails = releaseNotesService.deleteAllReleaseNotesDetails(oldDetails);
                if(newDetails != null) {
                    check +=1;
                }
            }
            if ( check == deleteIds.length()) {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                jsonObject.put(RestConstants.DATA, "Release Notes bulk deleted successfully");
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "Error in deleting  release notes details");
            }
            response = new Gson().toJson(jsonObject);
        }catch(Exception e) {
        	Sentry.captureException(e);
            e.printStackTrace();
            logger.error("Exception occured in ReleaseNotesApiService(bulkDelete) and Exception details >> " + e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in deleting release notes details");
            response = new Gson().toJson(jsonObject);
            
        }
        logger.info("ReleaseNotesApiService(bulkDelete) >> Exit");
        return response;
    }
    
    //get custom to show the release notes details
    
     @PutMapping(value="/getAllReleaseNotesNew",headers="Accept=application/json")
     public String getAllReleaseNotesDetailsNew(@RequestBody final String details){
         logger.info("ReleaseNotesApiService(getAllReleaseNotesDetailsNew) >> Entry");
         String response = "";
         final JSONObject jsonObject = new JSONObject();
         try {
        	 final JSONObject newJsonObject = new JSONObject(details);
        	 String type = newJsonObject.getString("type");
             logger.debug("ReleaseNotesApiService(getAllReleaseNotesDetailsNew)");
             final List<CustomReleaseNotes> detailsData = releaseNotesService.getAllReleaseNoteNew(type);
 //            final List<ReleaseNotesDetails> newDetails = new ArrayList<>();
 //            for(ReleaseNotesDetails i : details) {
 //                if(i.getIs_deleted() == false) {
 //                    newDetails.add(i);
 //                }
 //            }
             if (detailsData != null) {
                 jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                 jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                 jsonObject.put(RestConstants.DATA, new Gson().toJson(detailsData));
             } else {
                 jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                 jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                 jsonObject.put(RestConstants.DATA, "No data found for the given ID.");
             }
             logger.debug("ReleaseNotesApiService(getAllReleaseNotesDetailsNew) >> Request");
             response = new Gson().toJson(jsonObject);
         }catch(Exception e) {
        	 Sentry.captureException(e);
             logger.error("Exception occured in ReleaseNotesApiService(getAllReleaseNotesDetails) and Exception details >> " + e);
             jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
             jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
             jsonObject.put(RestConstants.DATA, "Error in getting  release notes details");
             response = new Gson().toJson(jsonObject);
         }
         logger.info("ReleaseNotesApiService(getAllReleaseNotesDetailsNew) >> Exit");
         return response;
     }
     
//     @GetMapping(value="/getReleaseNotes/{id}",headers="Accept=application/json")
//     public String getAddedReleaseNotes(@PathVariable final  Long id){
//         logger.info("ReleaseNotesApiService(getAddedReleaseNotes) >> Entry");
//         String response = "";
//         final JSONObject jsonObject = new JSONObject();
//         try {
//             logger.debug("ReleaseNotesApiService(getAddedReleaseNotes)>> Request");
//             final List<DashboardReleaseNotes> details = releaseNotesService.getAddedReleaseNotes(id);
//                 jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
//                 jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
//                 jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
//             response = new Gson().toJson(jsonObject);
//             
//         }catch(Exception e) {
//             logger.error("Exception occured in ReleaseNotesApiService(getAddedReleaseNotes) and Exception details >> " + e);
//             jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
//             jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
//             jsonObject.put(RestConstants.DATA, "Error in getting  release notes details");
//             response = new Gson().toJson(jsonObject);
//         }
//         logger.info("ReleaseNotesApiService(getAddedReleaseNotes) >> Exit");
//         return response;
//     }
//     
//     @GetMapping(value="/getActiveReleaseLetterLength/{id}",headers="Accept=application/json")
//     public String getReleaseNotesCount(@PathVariable final  Long id){
//         logger.info("ReleaseNotesApiService(getReleaseNotesCount)>> Entry");
//         String response = "";
//         final JSONObject jsonObject = new JSONObject();
//         try {
//             logger.info("ReleaseNotesApiService(ReleaseNotesApiService)");
//             final List<ReleaseNotesDetails> details = releaseNotesService.getReleaseNotesCount(id);
//                 jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
//                 jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
//                 jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
//             logger.debug("ReleaseNotesApiService(getReleaseNotesCount) >> Request");
//             response = new Gson().toJson(jsonObject);
//         }catch(Exception e) {
//             logger.error("Exception occured in ReleaseNotesApiService(getAllReleaseNotesDetails) and Exception details >> " + e);
//             jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
//             jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
//             jsonObject.put(RestConstants.DATA, "Error in getting  release notes length");
//             response = new Gson().toJson(jsonObject);
//         }
//         logger.info("ReleaseNotesApiService(getReleaseNotesCount) >> Exit");
//         return response;
//     }
    
     @GetMapping(value="/getReleaseNotesPdf/{id}",headers="Accept=application/json")
     public String getAllReleaseNotesDetailsPDf(@PathVariable(value="id")Long id){
         logger.info("ReleaseNotesApiService(getAllReleaseNotesDetails) >> Entry");
         String response = "";
         final JSONObject jsonObject = new JSONObject();
         try {
             logger.debug("ReleaseNotesApiService(getAllReleaseNotesDetails)");
             final List<ReleaseNotesDetails> details = releaseNotesService.getPdfViewDetails(id);
             if (details != null) {
                 jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                 jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                 jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
             } else {
                 jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                 jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                 jsonObject.put(RestConstants.DATA, "No data found for the given ID.");
             }
             logger.debug("ReleaseNotesApiService(getAllReleaseNotesDetails) >> Request");
             response = new Gson().toJson(jsonObject);
         }catch(Exception e) {
             logger.error("Exception occured in ReleaseNotesApiService(getAllReleaseNotesDetails) and Exception details >> " + e);
             jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
             jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
             jsonObject.put(RestConstants.DATA, "Error in getting  release notes details");
             response = new Gson().toJson(jsonObject);
         }
         logger.info("ReleaseNotesApiService(getAllReleaseNotesDetails) >> Exit");
         return response;
     }
     
     @PutMapping(value="/updatenew_release/{id}",headers="Accept=application/json")
     public String updatenew_release(@PathVariable(value="id")Long id){
         logger.info("ReleaseNotesApiService(updatenew_release) >> Entry");
         String response = "";
         final JSONObject jsonObject = new JSONObject();
         try {
             logger.debug("ReleaseNotesApiService(updatenew_release)");
             final int details = releaseNotesService.updatenew_release();
             if (details != 0) {
                 jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                 jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                 jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
             } else {
                 jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                 jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                 jsonObject.put(RestConstants.DATA, "No data found.");
             }
             logger.debug("ReleaseNotesApiService(updatenew_release) >> Request");
             response = new Gson().toJson(jsonObject);
         }catch(Exception e) {
             logger.error("Exception occured in ReleaseNotesApiService(updatenew_release) and Exception details >> " + e);
             jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
             jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
             jsonObject.put(RestConstants.DATA, "Error in updating new_release");
             response = new Gson().toJson(jsonObject);
         }
         logger.info("ReleaseNotesApiService(updatenew_release) >> Exit");
         return response;
     }
     
     @PutMapping(value="/updatenew_release_byEmpId/{id}",headers="Accept=application/json")
     public String updatenew_release_byEmpId(@PathVariable(value = "id") String id){
         logger.info("ReleaseNotesApiService(updatenew_release) >> Entry");
         String response = "";
         final JSONObject jsonObject = new JSONObject();
         try {
             logger.debug("ReleaseNotesApiService(updatenew_release_byEmpId)");
             final int Details = releaseNotesService.updatenew_release_byEmpId(id);
             if (Details != 0) {
                 jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                 jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                 jsonObject.put(RestConstants.DATA, new Gson().toJson(Details));
             } else {
                 jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                 jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                 jsonObject.put(RestConstants.DATA, "No data found.");
             }
             logger.debug("ReleaseNotesApiService(updatenew_release_byEmpId) >> Request");
             response = new Gson().toJson(jsonObject);
         }catch(Exception e) {
             logger.error("Exception occured in ReleaseNotesApiService(updatenew_release_byEmpId) and Exception details >> " + e);
             jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
             jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
             jsonObject.put(RestConstants.DATA, "Error in updating new_release");
             response = new Gson().toJson(jsonObject);
         }
         logger.info("ReleaseNotesApiService(updatenew_release_byEmpId) >> Exit");
         return response;
     }
    
    
}
