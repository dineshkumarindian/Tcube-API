package com.tcube.api.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.model.block.LayoutBlock;
import com.github.seratch.jslack.api.model.block.SectionBlock;
import com.github.seratch.jslack.api.model.block.composition.MarkdownTextObject;
import com.github.seratch.jslack.api.webhook.Payload;
import com.github.seratch.jslack.api.webhook.WebhookResponse;
import com.tcube.api.controller.LeaveTrackerDetailsApiService;
import com.tcube.api.model.EmployeeDetails;
import com.tcube.api.model.LeaveTrackerDetails;
import com.tcube.api.model.ManageBranchDetails;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Service
public class TodayAttendanceWorkFromDetailsSendToSlack {


//	public static void main(String[] args) {
		private static Logger logger = LogManager.getLogger(LeaveTrackerDetailsApiService.class);
//		private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TodayLeaveUserListSendToSlack.class);
		private static final String NEW_LINE = "\n";
		
//		@Value("${slack.webhook}")
		private String urlSlackWebHook;
		
		public void sendMessageToSlack(String url) {
			logger.info("TodayLeaveUserListSendToSlack(sendMessageToSlack) >> Entry");
			ArrayList<LayoutBlock> payloadMessage = new ArrayList<LayoutBlock>();
//			StringBuilder messageBuilder = new StringBuilder();
			urlSlackWebHook = url;
			payloadMessage.add(SectionBlock
				    .builder()
				    .text(MarkdownTextObject
				      .builder()
				      .text("Employee List for Today :")
				      .build())
				    .build());
			payloadMessage.add(SectionBlock
					.builder()
					.text(MarkdownTextObject
					.builder()
					.text("> *"+"No one checkin *"+":slightly_smiling_face:"+"* !* ").build())
					.build());
			process(payloadMessage);
		}
		
		@SuppressWarnings("unlikely-arg-type")
		public void sendMessageActiveEmployeeUserList(String url,
				ArrayList<EmployeeDetails> arrayOfIAmFromOfficeEmployee, ArrayList<String> arrayOfIAmFromHomeEmployee,
				List<ManageBranchDetails> branchDetails, List<LeaveTrackerDetails> todayLeaveUser,List<EmployeeDetails> noUpdateUserList) {
			logger.info("TodayLeaveUserListSendToSlack(sendMessageToSlack) >> Entry");
			ArrayList<LayoutBlock> payloadMessage = new ArrayList<LayoutBlock>();
			urlSlackWebHook = url;
			ArrayList<EmployeeDetails> workFromOfficeEmployeeList = arrayOfIAmFromOfficeEmployee;
			ArrayList<String> workFromHomeEmployeeList = arrayOfIAmFromHomeEmployee;
			final List<ManageBranchDetails> branchDetailsData = branchDetails;
			final List<EmployeeDetails> withOutUpdateUserList = noUpdateUserList;
//			ArrayList<?> branchOfAArray = new ArrayList<>();
//			System.out.println("workFromOfficeEmployeeList..." + workFromOfficeEmployeeList.size());
//			System.out.println("workFromHomeEmployeeList..." + workFromHomeEmployeeList.size());
//			System.out.println("branch name"+branchDetailsData.size());
			String strWFOEmployeeList = "";
			String strWFHEmployeeList = "";
			String todayLeaveUserList = "";
			String noUpdateUserStaffList = "";
//			for (int i = 0; i < workFromHomeEmployeeList.size(); i++) {
//				strWFHEmployeeList = strWFHEmployeeList.concat(workFromHomeEmployeeList.get(i));
//				if (i != workFromHomeEmployeeList.size() - 1) {
//					strWFHEmployeeList = strWFHEmployeeList.concat(" - ");
//				}
//			}
			payloadMessage.add(SectionBlock.builder()
					.text(MarkdownTextObject.builder().text("*Attendance Summary :*").build()).build());

			if (workFromHomeEmployeeList.size() != 0) {
				payloadMessage.add(SectionBlock.builder().text(MarkdownTextObject.builder()
						.text("> *" + "Staff in from home : " + workFromHomeEmployeeList.size() + " * ").build())
						.build());
				for (int i = 0; i < workFromHomeEmployeeList.size(); i++) {
					strWFHEmployeeList = strWFHEmployeeList.concat(workFromHomeEmployeeList.get(i));
					if (i != workFromHomeEmployeeList.size() - 1) {
						strWFHEmployeeList = strWFHEmployeeList.concat(" | ");
					}
				}
				payloadMessage.add(SectionBlock.builder()
						.text(MarkdownTextObject.builder().text("> " + "`" + strWFHEmployeeList + "`").build())
						.build());
			}
//			ArrayList<LayoutBlock> branchOfArrayList = new ArrayList<LayoutBlock>();
			if (branchDetailsData != null) {
				for (int i = 0; i < branchDetailsData.size(); i++) {

					int count = 0;
					String strofficeDataWithBranch = "";
					ArrayList<String> a1 = new ArrayList<String>();
					for (int j = 0; j < workFromOfficeEmployeeList.size(); j++) {
						if (workFromOfficeEmployeeList.get(j).getBranchDetails() != null) {
							if (branchDetailsData.get(i).getId().equals(workFromOfficeEmployeeList.get(j).getBranchDetails().getId())) {
								count = count + 1;
								if (count > 1) {
									strofficeDataWithBranch = strofficeDataWithBranch.concat(" | ");
								}
								strofficeDataWithBranch = strofficeDataWithBranch
										.concat(workFromOfficeEmployeeList.get(j).getFirstname());

							}
						}

					}
					if (count != 0) {
						payloadMessage.add(SectionBlock.builder()
								.text(MarkdownTextObject.builder()
										.text("> *" + "Staff in from "
												+ branchDetailsData.get(i).getBranch().toLowerCase() + " office : "
												+ count + " * ")
										.build())
								.build());
						payloadMessage.add(SectionBlock.builder().text(
								MarkdownTextObject.builder().text("> " + "`" + strofficeDataWithBranch + "`").build())
								.build());
					}

//					strWFOEmployeeList = strWFOEmployeeList.concat(workFromOfficeEmployeeList.get(i));
//					if (i != workFromOfficeEmployeeList.size() - 1) {
//						strWFOEmployeeList = strWFOEmployeeList.concat(" - ");
//					}
				}
			}
			if (todayLeaveUser.size() != 0) {
				payloadMessage.add(SectionBlock.builder()
						.text(MarkdownTextObject.builder()
								.text("> *" + "Staff on leave today : " + todayLeaveUser.size() + " * ").build())
						.build());
				for (int i = 0; i < todayLeaveUser.size(); i++) {
					todayLeaveUserList = todayLeaveUserList.concat(todayLeaveUser.get(i).getEmp_name());
					if (i !=  todayLeaveUser.size() - 1) {
						todayLeaveUserList = todayLeaveUserList.concat(" | ");
					}
				}
				payloadMessage.add(SectionBlock.builder()
						.text(MarkdownTextObject.builder().text("> " + "`" + todayLeaveUserList + "`").build())
						.build());

			}
			if(withOutUpdateUserList.size() != 0 ) {
				payloadMessage.add(SectionBlock.builder()
						.text(MarkdownTextObject.builder()
								.text("> *" + "Staff with no updates : " + withOutUpdateUserList.size() + " * ").build())
						.build());
				for (int i = 0; i < withOutUpdateUserList.size(); i++) {
					noUpdateUserStaffList = noUpdateUserStaffList.concat(withOutUpdateUserList.get(i).getFirstname());
					if (i !=  withOutUpdateUserList.size() - 1) {
						noUpdateUserStaffList = noUpdateUserStaffList.concat(" | ");
					}
				}
				payloadMessage.add(SectionBlock.builder()
						.text(MarkdownTextObject.builder().text("> " + "`" + noUpdateUserStaffList + "`").build())
						.build());
			}

//				
//				if(workFromOfficeEmployeeList.size() != 0) {
//					
//				payloadMessage.add(SectionBlock.builder().text(MarkdownTextObject.builder()
//						.text("> *" + "Staff in from office - (" + workFromOfficeEmployeeList.size() + ") * ").build())
//						.build());
//				
//				payloadMessage.add(SectionBlock.builder()
//						.text(MarkdownTextObject.builder().text("> " + "`" + strWFOEmployeeList + "`").build())
//						.build());
//				}
//				if(workFromHomeEmployeeList.size() != 0) {
//					
//				
//				payloadMessage.add(SectionBlock.builder()
//						.text(MarkdownTextObject.builder()
//								.text("> *" + "Staff in from home - (" + workFromHomeEmployeeList.size() + ") * ").build())
//						.build());
//
//				payloadMessage.add(SectionBlock.builder()
//						.text(MarkdownTextObject.builder().text("> " + "`" + strWFHEmployeeList + "`").build())
//						.build());
//				}
			process(payloadMessage);

		}
		private void process(ArrayList<LayoutBlock> payloadMessage) {
//			ArrayList<LayoutBlock> payloadMessage = new ArrayList<LayoutBlock>();
			logger.info("TodayLeaveUserListSendToSlack(processSlacksendMessage) >> Entry");
			@SuppressWarnings("unchecked")
			Payload payload = Payload.builder().blocks(payloadMessage).build();
			try {
				WebhookResponse webhookResponse = Slack.getInstance().send(urlSlackWebHook, payload);
				logger.info("code ->" + webhookResponse.getCode());
				logger.info("Body -> " + webhookResponse.getBody());
				logger.info("TodayLeaveUserListSendToSlack(sendMessageToSlack) >> Exit");

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		// TODO Auto-generated method stub

//	}

}
