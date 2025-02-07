package com.tcube.api.controller;

import java.math.BigInteger;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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
import com.tcube.api.model.AttendanceCurrentStatus;
import com.tcube.api.model.AttendanceDateReport;
import com.tcube.api.model.AttendanceDetails;
import com.tcube.api.model.DateEmployeeStatus;
import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.HolidayDetails;
import com.tcube.api.model.ManageIntegration;
import com.tcube.api.model.NotificationsDetails;
import com.tcube.api.model.OrgDetails;
import com.tcube.api.model.ReminderDetails;
import com.tcube.api.model.UserAttendanceReport;
import com.tcube.api.service.AttendanceDetailsService;
import com.tcube.api.service.EmployeeDetailsService;
import com.tcube.api.service.HolidayDetailsService;
import com.tcube.api.service.MailConfigDetailsService;
import com.tcube.api.service.ManageIntegrationService;
import com.tcube.api.service.OrgDetailsService;
import com.tcube.api.service.ReminderDetailsService;
import com.tcube.api.utils.TodayAttendanceWorkFromDetailsSendToSlack;
import com.tcube.api.utils.ImageProcessor;
import com.tcube.api.utils.MapperUtil;
import com.tcube.api.utils.RestConstants;
import com.tcube.api.utils.TodayLeaveUserListSendToSlack;
import com.tcube.api.service.NotificationDetailsService;
import com.tcube.api.service.AppsIntegrationDetailsService;

import java.util.TimeZone;

import io.sentry.Sentry;

@Component
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = {"/api/AttendanceDetails"})
public class AttendanceDetailsApiService {
    /**
     * Logger is to log application messages.
     */
    private static Logger logger = LogManager.getLogger(AttendanceDetailsApiService.class);

    @Autowired
    AttendanceDetailsService attendaceDetailsService;

    @Autowired
    OrgDetailsService orgDetailsService;

    @Autowired
    HolidayDetailsService holidayDetailsService;

    @Autowired
    EmployeeDetailsService employeeDetailsService;

    @Autowired
    NotificationDetailsService notificationDetailsService;

    @Autowired
    AppsIntegrationDetailsService appsIntegrationDetailsService;

    @Autowired
    TodayAttendanceWorkFromDetailsSendToSlack todayAttendanceWorkFromDetailsSendToSlack;

    @Autowired
    ManageIntegrationService manageIntegrationService;

    @Autowired
    MailConfigDetailsService mailConfigDetailsService;

    @Autowired
    ReminderDetailsService remainderDetailsService;

    /**
     * This service is to Create Employee details.
     *
     * @param request
     * @param ucBuilder
     * @return
     */

    /*
     *  This service for create a attendance action request
     */
    @PostMapping(value = "/create", headers = "Accept=application/json")
    public String createAttendanceRequest(@RequestBody final String request, final UriComponentsBuilder ucBuilder) {
        logger.info("AttendanceDetailsApiService(createAttendanceRequest) >> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
        try {
//			logger.debug("AttendanceDetailsApiService(createAttendanceDetails) >> Request :" + request);
            final JSONObject newJsonObject = new JSONObject(request);
            long orgId = newJsonObject.getLong("org_id");
            newJsonObject.remove("org_id");
            String zone = newJsonObject.getString("timezone");
            newJsonObject.remove("timezone");
            //To get and Set org details
            final OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
            final AttendanceDetails details = MapperUtil.readAsObjectOf(AttendanceDetails.class, newJsonObject.toString());
            details.setOrgDetails(orgDetails);
//			logger.debug("AttendanceDetailsApiService(createAttendanceDetails) >> Request Model Object :"
//					+ new Gson().toJson(details));
            /*
             * Service file  create method call
             */
            final AttendanceDetails data = attendaceDetailsService.createAttendanceDetails(details, zone);
            if (data != null) {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                jsonObject.put(RestConstants.DATA, data);
                response = new Gson().toJson(jsonObject);
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "Failed to add Employee Details");
                response = new Gson().toJson(jsonObject);
            }

        } catch (Exception e) {
            Sentry.captureException(e);
            e.printStackTrace();
            logger.error(
                    "Exception occured in AttendanceDetailsApiService(createAttendanceDetails) and Exception details >> "
                            + e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Failed to add Employee details");
            response = new Gson().toJson(jsonObject);
        }
        logger.info("AttendanceDetailsApiService(createAttendanceDetails) >> Exit");
        return response;
    }

    // This service for get all attendance details
    @GetMapping(value = "/getAllAttendanceDetails", headers = "Accept=application/json")
    public String getAllAttendanceDetails() {
        logger.info("AttendanceDetailsApiService(getAllAttendanceDetails) >> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
        try {
//			logger.info("AttendanceDetailsApiService(getAllAttendanceDetails)");

            /*
             * Get All details service method call
             */
            final List<AttendanceDetails> details = attendaceDetailsService.getAllAttendanceDetails();
            if (details != null && details.size() > 0) {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
//				jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
                jsonObject.put(RestConstants.DATA, details);
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "Error in extracting Attendance getall details");
            }
            response = new Gson().toJson(jsonObject);
        } catch (Exception e) {
//			System.out.println(e);
            Sentry.captureException(e);
            logger.error(
                    "Exception occured in AttendanceDetailsApiService(getAllAttendanceDetails) and Exception details >> "
                            + e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in extracting Attendance get all details");
            response = new Gson().toJson(jsonObject);
        }
        logger.info("AttendanceDetailsApiService(getAllAttendanceDetails) >> Exit");
        return response;
    }

    // This service for get all attendance details By email
    @GetMapping(value = "/getAllByEmail/{email}", headers = "Accept=application/json")
    public String getAllAttendanceDetailsByEmail(@PathVariable(value = "email") final String email) {
        logger.info("AttendanceDetailsApiService(getAllAttendanceDetailsByEmail) >> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
        try {
//			logger.info("AttendanceDetailsApiService(getAllAttendanceDetailsByEmail)");
            final List<AttendanceDetails> details = attendaceDetailsService.getAllAttendanceDetailsByemail(email);
            if (details != null && details.size() > 0) {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "Error in extracting Attendance getall details By Email");
            }
            response = new Gson().toJson(jsonObject);
        } catch (Exception e) {
//			System.out.println(e);
            Sentry.captureException(e);
            logger.error(
                    "Exception occured in AttendanceDetailsApiService(getAllAttendanceDetailsByEmail) and Exception details >> "
                            + e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in extracting Attendance get all details By Email");
            response = new Gson().toJson(jsonObject);
        }
        logger.info("AttendanceDetailsApiService(getAllAttendanceDetailsByEmail) >> Exit");
        return response;
    }

    // This service for get all attendance details report By date
    @GetMapping(value = "/getAttendanceDateReport/{date}/{email}", headers = "Accept=application/json")
    public String getAttendanceDateReport(@PathVariable(value = "date") final String date,
                                          @PathVariable(value = "email") final String email) {
        logger.info("AttendanceDetailsApiService(getAttendanceDateReport) >> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
        try {
//			logger.info("AttendanceDetailsApiService(getAttendanceDateReport)");
            AttendanceDateReport details = attendaceDetailsService.getAttendanceReportsByDate(date, email);
            if (details != null) {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "Error in extracting Attendance details Date Report");
            }
            response = new Gson().toJson(jsonObject);
        } catch (Exception e) {
//			System.out.println(e);
            Sentry.captureException(e);
            logger.error(
                    "Exception occured in AttendanceDetailsApiService(getAttendanceDateReport) and Exception details >> "
                            + e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in extracting Attendance details Date Report");
            response = new Gson().toJson(jsonObject);
        }
        logger.info("AttendanceDetailsApiService(getAttendanceDateReport) >> Exit");
        return response;
    }

    // This service for get current status details By email
    @PutMapping(value = "/getCurrentStatusByEmail/{email}", headers = "Accept=application/json")
    public String getCurrentStatusByEmail(@PathVariable(value = "email") final String email, @RequestBody final String request) {
        logger.info("AttendanceDetailsApiService(getCurrentStatusByEmail) >> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
        final JSONObject newJsonObject = new JSONObject(request);
        String zone = newJsonObject.getString("timezone");
        newJsonObject.remove("timezone");
        try {
//				logger.info("AttendanceDetailsApiService(getCurrentStatusByEmail)");
            final AttendanceCurrentStatus details = attendaceDetailsService.getcurrentstatusByemail(email, zone);
            if (details != null) {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
//					jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
                jsonObject.put(RestConstants.DATA, details);
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "Error in extracting Attendance current Status By Email");
            }
            response = new Gson().toJson(jsonObject);
        } catch (Exception e) {
            Sentry.captureException(e);
//				System.out.println(e);
            logger.error(
                    "Exception occured in AttendanceDetailsApiService(getCurrentStatusByEmail) and Exception details >> "
                            + e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in extracting Attendance current Status By Email");
            response = new Gson().toJson(jsonObject);
        }
        logger.info("AttendanceDetailsApiService(getCurrentStatusByEmail) >> Exit");
        return response;
    }

    // This service for get all attendance details by org_id
    @GetMapping(value = "/getAllAttendanceDetailsByOrgId/{org_id}", headers = "Accept=application/json")
    public String getAllAttendanceDetailsByOrgId(@PathVariable(value = "org_id") final Long org_id) {
        logger.info("AttendanceDetailsApiService(getAllAttendanceDetailsByOrgId) >> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
        try {
//				logger.info("AttendanceDetailsApiService(getAllAttendanceDetailsByOrgId)");

            /*
             * Get All details service method call
             */
            final List<AttendanceDetails> details = attendaceDetailsService.getAllAttendanceDetailsByOrgId(org_id);
            if (details != null && details.size() > 0) {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                jsonObject.put(RestConstants.DATA, new Gson().toJson(details));
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "Error in extracting Attendance getall details by client Id");
            }
            response = new Gson().toJson(jsonObject);
        } catch (Exception e) {
            Sentry.captureException(e);
//				System.out.println(e);
            logger.error(
                    "Exception occured in AttendanceDetailsApiService(getAllAttendanceDetailsByOrgId) and Exception details >> "
                            + e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in extracting Attendance get all details by client Id");
            response = new Gson().toJson(jsonObject);
        }
        logger.info("AttendanceDetailsApiService(getAllAttendanceDetailsByOrgId) >> Exit");
        return response;
    }

    // This service for monthly report with details
    @PutMapping(value = "/getAttendanceMonthReport", headers = "Accept=application/json")
    public String getAttendanceMonthReport(@RequestBody final String request) {
        logger.info("AttendanceDetailsApiService(getAttendanceMonthReport) >> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
        final JSONObject newJsonObject = new JSONObject(request);
        String startdate = newJsonObject.getString("startdate");
        String enddate = newJsonObject.getString("enddate");
        String email = newJsonObject.getString("email");
        newJsonObject.remove("timezone");
        try {
//				logger.info("AttendanceDetailsApiService(getAttendanceMonthReport)");
            JSONObject details = attendaceDetailsService.getAttendanceReportsByMonth(startdate, enddate, email);
            if (details.length() != 0) {
                details.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                details.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                response = new Gson().toJson(details);
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "Error in extracting Attendance details Month Report");
                response = new Gson().toJson(jsonObject);
            }

        } catch (Exception e) {
            Sentry.captureException(e);
//				System.out.println(e);
            logger.error(
                    "Exception occured in AttendanceDetailsApiService(getAttendanceMonthReport) and Exception details >> "
                            + e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in extracting Attendance details Month Report");
            response = new Gson().toJson(jsonObject);
        }
        logger.info("AttendanceDetailsApiService(getAttendanceDateReport) >> Exit");
        return response;
    }

    // This service for get active employees by date
    @PutMapping(value = "/getactiveemployeebydate", headers = "Accept=application/json")
    public String getactiveEmpByDate(@RequestBody final String request) {
        logger.info("AttendanceDetailsApiService(getactiveemployeebydate) >> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
        final JSONObject newJsonObject = new JSONObject(request);
        Long org_id = newJsonObject.getLong("org_id");
        String date = newJsonObject.getString("date");
        try {
//						logger.info("AttendanceDetailsApiService(getactiveemployeebydate)");
            List<String> details = attendaceDetailsService.getActiveAttendanceDetailsByOrgIdwithDate(org_id, date);
            List<EmployeeDetails> data = new ArrayList<>();
            if (details.size() > 0) {
                for (int bk = 0; bk < details.size(); bk++) {
                    EmployeeDetails value = employeeDetailsService.getEmployeeDetailsByEmail(details.get(bk));
                    if (value != null) {
                        data.add(value);
                    }
                }
            }
            if (data.size() != 0) {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                jsonObject.put(RestConstants.DATA, new Gson().toJson(data));
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "Error in extracting getactiveemployeebydate ");
            }
            response = new Gson().toJson(jsonObject);
        } catch (Exception e) {
            Sentry.captureException(e);
//						System.out.println(e);
            logger.error(
                    "Exception occured in AttendanceDetailsApiService(getactiveemployeebydate) and Exception details >> "
                            + e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in extracting getactiveemployeebydate");
            response = new Gson().toJson(jsonObject);
        }
        logger.info("AttendanceDetailsApiService(getactiveemployeebydate) >> Exit");
        return response;
    }

    // This service for date report with details
    @PutMapping(value = "/getDateAttendanceReport", headers = "Accept=application/json")
    public String getAttendanceDateReport(@RequestBody final String request) {
        logger.info("AttendanceDetailsApiService(getDateAttendanceReport) >> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
        final JSONObject newJsonObject = new JSONObject(request);
        String date = newJsonObject.getString("date");
        Long org_id = newJsonObject.getLong("org_id");
        List<EmployeeDetails> users = new ArrayList<>();
        try {
//						logger.info("AttendanceDetailsApiService(getDateAttendanceReport)");
            users = employeeDetailsService.getAllActiveEmployeeReportsByOrgId(org_id);
            JSONObject details = attendaceDetailsService.getAttendanceDateReport(date, org_id, users);
            if (details.length() != 0) {
                details.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                details.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                response = new Gson().toJson(details);
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "Error in extracting Attendance details Date Report");
                response = new Gson().toJson(jsonObject);
            }

        } catch (Exception e) {
            Sentry.captureException(e);
//						System.out.println(e);
            logger.error(
                    "Exception occured in AttendanceDetailsApiService(getDateAttendanceReport) and Exception details >> "
                            + e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in extracting Attendance details Date Report");
            response = new Gson().toJson(jsonObject);
        }
        logger.info("AttendanceDetailsApiService(getDateAttendanceReport) >> Exit");
        return response;
    }

    // This service for get employees date status
    @PutMapping(value = "/getallemployeebydatestatus", headers = "Accept=application/json")
    public String getallemployeebydatestatus(@RequestBody final String request) {
        logger.info("AttendanceDetailsApiService(getallemployeebydatestatus) >> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
        final JSONObject newJsonObject = new JSONObject(request);
        Long org_id = newJsonObject.getLong("org_id");
        String date = newJsonObject.getString("date");
        try {
            List<EmployeeDetails> employees = employeeDetailsService.getAllActiveEmployeeReportsByOrgId(org_id);
            List<DateEmployeeStatus> data = new ArrayList<DateEmployeeStatus>();
            if (employees.size() > 0) {
                for (int bk = 0; bk < employees.size(); bk++) {
                    DateEmployeeStatus value = new DateEmployeeStatus();
                    value.setId(employees.get(bk).getId());
                    value.setFirstname(employees.get(bk).getFirstname());
                    value.setLastname(employees.get(bk).getLastname());
                    value.setEmail(employees.get(bk).getEmail());
                    value.setDesignation(employees.get(bk).getDesignationDetails().getDesignation());
                    if (employees.get(bk).getProfile_image() != null) {
                        value.setImage(ImageProcessor.decompressBytes(employees.get(bk).getProfile_image()));
                    }
                    List<AttendanceDetails> result = attendaceDetailsService.getAttendanceActiveStatusByOrgId_emailwithDate(org_id, employees.get(bk).getEmail(), date);
                    boolean status;
                    if (result.size() != 0) {
                        AttendanceDetails detail = result.get(0);
                        if (detail.getActionType().equals("in")) {
                            status = true;
                        } else {
                            status = false;
                        }
                        value.setTimeOfAction(detail.getTimeOfAction());
                    } else {
                        status = false;
                    }
                    value.setPresent(status);
                    data.add(value);
                }
            }
            if (data.size() != 0) {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                jsonObject.put(RestConstants.DATA, new Gson().toJson(data));
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "Error in extracting getallemployeebydatestatus");
            }
            response = new Gson().toJson(jsonObject);
        } catch (Exception e) {
            Sentry.captureException(e);
//						System.out.println(e);
            logger.error(
                    "Exception occured in AttendanceDetailsApiService(getallemployeebydatestatus) and Exception details >> "
                            + e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in extracting getallemployeebydatestatus");
            response = new Gson().toJson(jsonObject);
        }
        logger.info("AttendanceDetailsApiService(getallemployeebydatestatus) >> Exit");
        return response;
    }

    // This service for get all attendance details report By date
    @GetMapping(value = "/getAttendanceactionReportsByDate/{date}/{email}", headers = "Accept=application/json")
    public String getAttendanceactionReportsByDate(@PathVariable(value = "date") final String date,
                                                   @PathVariable(value = "email") final String email) {
        logger.info("AttendanceDetailsApiService(getAttendanceactionReportsByDate) >> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
        try {
//						logger.info("AttendanceDetailsApiService(getAttendanceactionReportsByDate)");
            JSONObject details = attendaceDetailsService.getAttendanceactionReportsByDate(date, email);
            if (details != null) {
                details.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                details.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                response = new Gson().toJson(details);
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "Error in extracting Attendance action report details Date Report");
                response = new Gson().toJson(jsonObject);
            }

        } catch (Exception e) {
            Sentry.captureException(e);
//						System.out.println(e);
            logger.error(
                    "Exception occured in AttendanceDetailsApiService(getAttendanceactionReportsByDate) and Exception details >> "
                            + e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in extracting Attendance action report details Date Report");
            response = new Gson().toJson(jsonObject);
        }
        logger.info("AttendanceDetailsApiService(getAttendanceactionReportsByDate) >> Exit");
        return response;
    }

    // This service for date range attendance active hours chart for an employee
    @PutMapping(value = "/getUserAttendanceBarchartData", headers = "Accept=application/json")
    public String getUserAttendanceBarchartData(@RequestBody final String request) {
        logger.info("AttendanceDetailsApiService(getUserAttendanceBarchartData) >> Entry");
        String response = "";
        final JSONObject jsonObject = new JSONObject();
        final JSONObject newJsonObject = new JSONObject(request);
        String startdate = newJsonObject.getString("startdate");
        String enddate = newJsonObject.getString("enddate");
        String email = newJsonObject.getString("email");
        newJsonObject.remove("timezone");
        try {
//						logger.info("AttendanceDetailsApiService(getUserAttendanceBarchartData)");
            UserAttendanceReport details = attendaceDetailsService.getUserAttendanceBarchartData(startdate, enddate, email);
            if (details != null) {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.SUCCESS_STRING);
                jsonObject.put(RestConstants.DATA, details);
                response = new Gson().toJson(jsonObject);
            } else {
                jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_200);
                jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
                jsonObject.put(RestConstants.DATA, "Error in extracting User Attendance Bar chart data");
                response = new Gson().toJson(jsonObject);
            }

        } catch (Exception e) {
            Sentry.captureException(e);
//						System.out.println(e);
            logger.error(
                    "Exception occured in AttendanceDetailsApiService(getUserAttendanceBarchartData) and Exception details >> "
                            + e);
            jsonObject.put(RestConstants.STATUS_CODE, RestConstants.STATUS_CODE_500);
            jsonObject.put(RestConstants.STATUS_MESSAGE, RestConstants.ERROR_STRING);
            jsonObject.put(RestConstants.DATA, "Error in extracting User Attendance Bar chart data");
            response = new Gson().toJson(jsonObject);
        }
        logger.info("AttendanceDetailsApiService(getUserAttendanceBarchartData) >> Exit");
        return response;
    }


//    @SuppressWarnings("unchecked")
//    @Scheduled(cron = "0 0 11 ? * MON-FRI", zone ="Asia/Calcutta")
//    @GetMapping(value = "/getActiveEmployeesTodayAttendanceList", headers = "Accept=application/json")
////    @Scheduled(cron = "*/10 * * * * ?", zone = "Asia/Calcutta")
//    public void getActiveEmployeesTodayAttendanceList() {
//        logger.info("AttendanceDetailsApiService(getActiveEmployeesTodayAttendanceList) >> Entry");
//        System.out.println("inside getActiveEmployeesTodayAttendanceList");
//        String response = "";
//        final JSONObject jsonObject = new JSONObject();
//
//        try {
//            ArrayList<BigInteger> orgList = attendaceDetailsService.getActiveOrgIdsWithAttendance();
//            String pattern = "yyyy-MM-dd";
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
//            String date = simpleDateFormat.format(new Date());
//            TimeZone timezone = TimeZone.getTimeZone("Asia/Kolkata");
//
//            for (int j = 0; j < orgList.size(); j++) {
//                final long orgId = orgList.get(j).longValue();
//                ManageIntegration manageIntegration = manageIntegrationService.getOrgAMdetails(orgId,
//                        "slack", "all");
//                boolean isKeyPrimaryModule = false;
//                String slackUrl = "";
//                if (manageIntegration != null) {
//                    if (manageIntegration.getisActive() == true) {
//                        isKeyPrimaryModule = false;
//                        slackUrl = appsIntegrationDetailsService.getSlackUrlAttendance(orgId);
//                        List<ReminderDetails> remainderTodayLeaveUpdate = remainderDetailsService
//                                .getAllRemindersByOrgId(orgId);
//                        for (int r = 0; r < remainderTodayLeaveUpdate.size(); r++) {
//                            System.out.println("r value:" + r);
//                            String keyValue = remainderTodayLeaveUpdate.get(r).getKey_primary();
//                            boolean is_Active = remainderTodayLeaveUpdate.get(r).getIs_active();
//                            if (keyValue.equals("slack-today-Active-Employee") && is_Active) {
//                                isKeyPrimaryModule = true;
//                                System.out.println("true..." + isKeyPrimaryModule);
//                                break;
//                            }
//
//                        }
//                    }
//                }
//
//                final List<HolidayDetails> Holiday_data = holidayDetailsService
//                        .getTodayHolidayDetails(orgId, date);
//                if (Holiday_data.size() == 0) {
//                    final List<EmployeeDetails> employeeDetails = employeeDetailsService
//                            .getEmployeeDetailsByOrgID(orgId);
//                    int count_of_I_am_from_home = 0;
//                    int count_of_I_am_from_office = 0;
//
//                    ArrayList<String> arrayOfIAmFromHomeEmployee = new ArrayList<String>();
//
//                    ArrayList<String> arrayOfIAmFromOfficeEmployee = new ArrayList<String>();
//
//                    for (int z = 0; z < employeeDetails.size(); z++) {
//                        String email = employeeDetails.get(z).getEmail();
//                        String userName = employeeDetails.get(z).getFirstname();
//                        System.out.println("email...." + email);
//                        final List<AttendanceDetails> details = attendaceDetailsService
//                                .getActiveEmployeesTodayAttendanceList(orgId, email);
//                        String action = "";
//                        for (int k = 0; k < details.size(); k++) {
//                            System.out.println("k value:" + k);
//                            if (details.get(k).getAction().equals("I am in from office")) {
//                                action = details.get(k).getAction();
//                            } else if (details.get(k).getAction().equals("I am in from home")) {
//                                action = details.get(k).getAction();
//                            }
//
//                        }
//
//                        if (action != "" && action.equals("I am in from office")) {
//                            arrayOfIAmFromOfficeEmployee.add(count_of_I_am_from_office, userName);
//                            count_of_I_am_from_office = count_of_I_am_from_office + 1;
//
//                        } else {
//                            if (action != "" && action.equals("I am in from home")) {
//                                arrayOfIAmFromHomeEmployee.add(count_of_I_am_from_home, userName);
//                                count_of_I_am_from_home = count_of_I_am_from_home + 1;
//
//                            }
//                        }
//                    }
//
//                    JSONArray jsonArray = new JSONArray();
//                    jsonArray.add(arrayOfIAmFromOfficeEmployee);
//                    jsonArray.add(arrayOfIAmFromHomeEmployee);
//                    if (arrayOfIAmFromOfficeEmployee.size() != 0
//                            || arrayOfIAmFromHomeEmployee.size() != 0) {
//                        if (slackUrl != "" && isKeyPrimaryModule == true) {
//                            todayAttendanceWorkFromDetailsSendToSlack.sendMessageActiveEmployeeUserList(
//                                    slackUrl, arrayOfIAmFromOfficeEmployee, arrayOfIAmFromHomeEmployee);
//                        }
//                        final OrgDetails orgDetails = orgDetailsService.getOrgDetailsById(orgId);
//                        logger.info("attendance today work from home/work from office userlist >> Entry");
//                        NotificationsDetails notification = new NotificationsDetails();
//                        notification.setOrg_id(orgId);
//                        notification.setMessage("Staff attendance summary");
//                        notification.setTo_notify_id(orgDetails.getEmp_id());
//                        notification.setNotifier(orgDetails.getEmp_id());
//                        notification.setModule_name("");
//                        notification.setSub_module_name("");
//                        notification.setDate_of_request(date);
//                        notification.setIs_deleted(false);
//                        notification.setIs_read(false);
//                        notification.setApproval_comments(new Gson().toJson(jsonArray));
//                        final NotificationsDetails notification_data = notificationDetailsService
//                                .addNotification(notification, timezone.toString());
//                        logger.info("attendance today work from home/work from office userlist >> Exit");
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        logger.info("AttendanceDetailsApiService(getActiveEmployeesTodayAttendanceList) >> Exit");
//    }


}
