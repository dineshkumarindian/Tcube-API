package com.tcube.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.tcube.api.model.DesignationDetails;
import com.tcube.api.model.OrgDetails;
import com.tcube.api.model.RoleDetails;
import com.tcube.api.service.DesignationDetailsService;
import com.tcube.api.service.OrgDetailsService;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;

import io.sentry.Sentry;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = {"/api/DesignationDetails"})
public class DesignationDetailsApiService {

    private static Logger logger = LogManager.getLogger(DesignationDetailsApiService.class);

    @Autowired
    DesignationDetailsService designationDetailsService;

    @Autowired
    OrgDetailsService orgDetailsService;

    @PostMapping(value = "/create", headers = "Accept=application/json")
    public String createDesignationDetail(@RequestBody String details) {
        logger.info("DesignationDetailsApiService(createDesignationDetail) >> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
        try {
            final JSONObject newJsonObject = new JSONObject(details);
            Long orgId = newJsonObject.getLong("org_id");
            newJsonObject.remove("org_id");

            logger.debug("DesignationDetailsApiService(createDesignationDetail) >> Request");
            final DesignationDetails designationDetails = MapperUtil.readAsObjectOf(DesignationDetails.class,
                    newJsonObject.toString());
            OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
            designationDetails.setIs_deleted(false);
            designationDetails.setOrgDetails(orgDetails);
            final DesignationDetails newdetails = designationDetailsService.createDesignation(designationDetails);
            if (newdetails != null) {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                jsonObject.put(RestConstants.DATA, "designation details created successfully");
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "Error in creating designation details");
            }
            response = new Gson().toJson(jsonObject);
            logger.debug("DesignationDetailsApiService(createDesignationDetail) >> Response");
        } catch (Exception e) {
            Sentry.captureException(e);
            e.printStackTrace();
            logger.error("Exception occured in DesignationDetailsApiService(createDesignationDetail) and Exception details >> " + e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in creating designation details");
            response = new Gson().toJson(jsonObject);
        }
        logger.info("DesignationDetailsApiService(createDesignationDetail) >> Exit");
        return response;
    }

    @PutMapping(value = "/update", headers = "Accept=application/json")
    public String updateDesignationDetail(@RequestBody final String details) {
        logger.info("DesignationDetailsApiService(updateDesignationDetail) >> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
        try {
            final JSONObject newJsonObject = new JSONObject(details);
            Long orgId = newJsonObject.getLong("org_id");
            Long Id = newJsonObject.getLong("id");
            newJsonObject.remove("org_id");
            newJsonObject.remove("id");
            OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
            logger.debug("DesignationDetailsApiService(updateDesignationDetail) >> Request");
            final DesignationDetails Roledetails = MapperUtil.readAsObjectOf(DesignationDetails.class,
                    newJsonObject.toString());
            DesignationDetails newDetails = designationDetailsService.getDesignationById(Id);
            newDetails.setOrgDetails(orgDetails);
            newDetails.setDesignation(Roledetails.getDesignation());
            newDetails.setDesignation_responsibilities(Roledetails.getDesignation_responsibilities());

            final DesignationDetails details1 = designationDetailsService.updateDesignation(newDetails);
            if (details1 != null) {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                jsonObject.put(RestConstants.DATA, "Designation details updated successfully");
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "Error in updating designation details");
            }
            response = new Gson().toJson(jsonObject);
            logger.debug("DesignationDetailsApiService(updateDesignationDetail) >> Response");
        } catch (Exception e) {
            Sentry.captureException(e);
            e.printStackTrace();
            logger.error("Exception occured in DesignationDetailsApiService(updateDesignationDetail) and Exception details >> " + e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in updating designation details");
            response = new Gson().toJson(jsonObject);
        }
        logger.info("DesignationDetailsApiService(updateDesignationDetail) >> Exit");
        return response;
    }

    @PutMapping(value = "/delete/{id}", headers = "Accept=application/json")
    public String deleteDesignationDetail(@PathVariable(value = "id") Long id) {
        logger.info("DesignationDetailsApiService(deleteDesignationDetail) >> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
        try {
            final DesignationDetails newDetails = designationDetailsService.getDesignationById(id);
            newDetails.setIs_deleted(true);
            final DesignationDetails details = designationDetailsService.deleteDesignation(newDetails);
            if (details != null) {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                jsonObject.put(RestConstants.DATA, "Designation details deleted successfully");
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "Error in deleting designation details");
            }
            response = new Gson().toJson(jsonObject);
        } catch (Exception e) {
            Sentry.captureException(e);
            e.printStackTrace();
            logger.error("Exception occured in DesignationDetailsApiService(deleteDesignationDetail) and Exception details >> " + e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in deleting designation details");
            response = new Gson().toJson(jsonObject);
        }
        logger.info("DesignationDetailsApiService(deleteDesignationDetail) >> Exit");
        return response;
    }

    @GetMapping(value = "/getAllDesignationDetails", headers = "Accept=application/json")
    public String getAllDesignationDetails() {
        logger.info("DesignationDetailsApiService(getAllDesignation) >> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
        try {
            logger.debug("DesignationDetailsApiService(getAllDesignation) >> Request");
            List<DesignationDetails> details = designationDetailsService.getAllDesignation();

            if (details != null) {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "No data found in designation details");
            }
            logger.debug("DesignationDetailsApiService(getAllDesignation) >> Response");
            response = new Gson().toJson(jsonObject);
        } catch (Exception e) {
            Sentry.captureException(e);
//			System.out.println(e);

            logger.error("Exception occured in DesignationDetailsApiService(getAllDesignation) and Exception details >> " + e);

            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in getting  designation details");
            response = new Gson().toJson(jsonObject);
        }
        logger.info("DesignationDetailsApiService(getAllDesignation) >> Exit");
        return response;
    }

    @GetMapping(value = "/getAllDesignationByOrgId/{id}", headers = "Accept=application/json")
    public String getAllDesignationByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
        logger.info("DesignationDetailsApiService(getAllDesignationByOrgId) >> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
        try {
            logger.info("DesignationDetailsApiService(getAllDesignationByOrgId)");
            List<DesignationDetails> details = designationDetailsService.getAllDesignationByOrgId(id);
            if (details != null) {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "Failed to get designation details by org Id");
            }
            response = new Gson().toJson(jsonObject);
        } catch (Exception e) {
            Sentry.captureException(e);
//			System.out.println(e);
            logger.error("Exception occured in DesignationDetailsApiService(getAllDesignationByOrgId) and Exception details >> " + e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in extracting  designation details by org Id");
            response = new Gson().toJson(jsonObject);
        }
        logger.info("DesignationDetailsApiService(getAllDesignationByOrgId) >> Exit");
        return response;
    }


    @GetMapping(value = "/getDesignationByName/{details}", headers =
            "Accept=application/json")
    public String getDesignationByName(@PathVariable
                                       String details, Long id, final UriComponentsBuilder ucBuilder) {
        logger.info("DesignationDetailsApiService(getDesignationByName) >> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
        try {
            logger.info("DesignationDetailsApiService(getDesignationByName)");
            DesignationDetails details1 =
                    designationDetailsService.getDesignationByName(details, id);
            if (details1 !=
                    null) {
                jsonObject.put(RestConstants.STATUS_CODE,
                        RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE,
                        RestConstants.SUCCESS_STRING);
                jsonObject.put(RestConstants.DATA, new
                        Gson().toJson(details1));
            } else {
                jsonObject.put(RestConstants.STATUS_CODE,
                        RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE,
                        RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA,
                        "Failed to get designation details by name");
            }
            response = new
                    Gson().toJson(jsonObject);
        } catch (Exception e) {
            Sentry.captureException(e);
            logger.
                    error("Exception occured in DesignationDetailsApiService(getDesignationByName) and Exception details >> "
                            + e);
            jsonObject.put(RestConstants.STATUS_CODE,
                    RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE,
                    RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA,
                    "Error in extracting  designation details by name");
            response = new
                    Gson().toJson(jsonObject);
        }
        logger.info("DesignationDetailsApiService(getDesignationByName) >> Exit");
        return response;
    }


    @GetMapping(value = "/getActiveDesignationByOrgId/{id}", headers = "Accept=application/json")
    public String getActiveDesignationByOrgId(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
        logger.info("DesignationDetailsApiService(getActiveDesignationByOrgId) >> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
        try {
            logger.info("DesignationDetailsApiService(getActiveDesignationByOrgId)");
            final List<DesignationDetails> details = designationDetailsService.getAllDesignationByOrgId(id);
            final List<DesignationDetails> newDetails = new ArrayList<>();
            for (DesignationDetails i : details) {
                if (i.getIs_deleted() == false)
                    newDetails.add(i);
            }
            if (newDetails != null && newDetails.size() > 0) {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                jsonObject.put(RestConstants.DATA, new Gson().toJson(newDetails));
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "Failed to get designation details by org Id");
            }
            response = new Gson().toJson(jsonObject);
        } catch (Exception e) {
            Sentry.captureException(e);
//			System.out.println(e);
            logger.error("Exception occured in DesignationDetailsApiService(getActiveDesignationByOrgId) and Exception details >> " + e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in extracting  designation details by org Id");
            response = new Gson().toJson(jsonObject);
        }
        logger.info("DesignationDetailsApiService(DesignationDetailsApiService) >> Exit");
        return response;
    }


    @GetMapping(value = "/getDesignationById/{id}", headers = "Accept=application/json")
    public String getDesignationById(@PathVariable final Long id, final UriComponentsBuilder ucBuilder) {
        logger.info("DesignationDetailsApiService(getDesignationById) >> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
        try {
            logger.info("DesignationDetailsApiService(getDesignationById)");
            final DesignationDetails details = designationDetailsService.getDesignationById(id);
            if (details != null) {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "Error in extracting designation details by id");
            }
            response = new Gson().toJson(jsonObject);
        } catch (Exception e) {
            Sentry.captureException(e);
//			System.out.println(e);
            logger.error("Exception occured in DesignationDetailsApiService(getDesignationById) and Exception details >> " + e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in extracting  designation details by id");
            response = new Gson().toJson(jsonObject);
        }
        logger.info("DesignationDetailsApiService(getDesignationById) >> Exit");
        return response;
    }

    //bulk delete for designation
    @PutMapping(value = "/designationbulkdelete", headers = "Accept=application/json")
    public String bulkDeletePlanDetail(@RequestBody final String request) {
        logger.info("DesignationDetailsApiService(designationBulkDelete) >> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
        try {
            final JSONObject newJsonObject = new JSONObject(request);
            JSONArray deleteIds = newJsonObject.getJSONArray("deleteIds");
            final int details = designationDetailsService.bulkDelete(deleteIds);
            if (details == deleteIds.length()) {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                jsonObject.put(RestConstants.DATA, "designation bulk deleted successfully");
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.FAIL_STRING);
                jsonObject.put(RestConstants.DATA, "Failed to bulk delete designation");
            }
            response = new Gson().toJson(jsonObject);
            logger.debug("DesignationDetailsApiService(designationBulkDelete) >> Response");
        } catch (Exception e) {
            Sentry.captureException(e);
//			System.out.println(e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in bulk delete designation");
            response = new Gson().toJson(jsonObject);
        }
        logger.info("DesignationDetailsApiService(designationBulkDelete) >> Exit");
        return response;
    }

}
