package com.tcube.api.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;
import org.json.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.model.block.LayoutBlock;
import com.github.seratch.jslack.api.model.block.SectionBlock;
import com.github.seratch.jslack.api.model.block.composition.MarkdownTextObject;
import com.github.seratch.jslack.api.webhook.Payload;
import com.tcube.api.controller.LeaveTrackerDetailsApiService;
import com.tcube.api.model.LeaveTrackerDetails;
import com.github.seratch.jslack.api.webhook.WebhookResponse;
import com.google.gson.JsonObject;
import com.slack.api.model.block.DividerBlock;
import com.slack.api.model.block.ImageBlock;
import com.slack.api.rtm.message.Message;
//import org.json.JSONObject;  
//import org.json.JSONArray;  

@Service
public class TodayLeaveUserListSendToSlack {
	private static Logger logger = LogManager.getLogger(LeaveTrackerDetailsApiService.class);
//	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TodayLeaveUserListSendToSlack.class);
	private static final String NEW_LINE = "\n";
	
//	@Value("${slack.webhook}")
	private String urlSlackWebHook;
	
//	ArrayList<LayoutBlock> payloadMessage = new ArrayList<LayoutBlock>();
	
	public void sendMessageToSlack(String url) {
		logger.info("TodayLeaveUserListSendToSlack(sendMessageToSlack) >> Entry");
		ArrayList<LayoutBlock> payloadMessage = new ArrayList<LayoutBlock>();
//		StringBuilder messageBuilder = new StringBuilder();
		urlSlackWebHook = url;
//		JSONParser parser = new JSONParser();
//		JSONArray array = new JSONArray();
		
//		String s ="{\"blocks\":[{\"type\":\"header\",\"text\":{\"type\":\"plain_text\",\"text\":\"This is a header block\",\"emoji\":true}},{\"type\":\"section\",\"text\":{\"type\":\"mrkdwn\",\"text\":\"*No one take leave Today!*\"},\"accessory\":{\"type\":\"image\",\"image_url\":\"https://uat.tcube.io/assets/images/App_slack_logo1.png\",\"alt_text\":\"Tcube logo\"}}]}";
//		 try{
//			 JsonObject object = Json.parse(s).asObject(); ;
//	         System.out.println("obj..."+obj2);
////	         JSONArray array = (JSONArray)obj2;
////	          System.out.println("array..."+array);
////	          messageBuilder.append(obj); 
//	          process(messageBuilder.toString());
//		 } catch(Exception e) {
//			 e.printStackTrace();
//		 }
		payloadMessage.add(SectionBlock
			    .builder()
			    .text(MarkdownTextObject
			      .builder()
			      .text("*Staff on PTO Today:*")
			      .build())
			    .build());
		payloadMessage.add(SectionBlock
				.builder()
				.text(MarkdownTextObject
				.builder()
				.text("> *"+"All are working today *"+":slightly_smiling_face:"+"* !* ").build())
				.build());
//		payloadMessage.add((LayoutBlock) DividerBlock
//			    .builder()
//			    .build());
//		payloadMessage.add((LayoutBlock) ImageBlock
//				.builder()
//				.imageUrl("https://uat.tcube.io/assets/images/final-logo-T.png")
//				.imageHeight(50)
//				.imageWidth(50)
//				.altText("Tcbe logo").build());
				
//		payloadMessage.addAll((Collection<? extends LayoutBlock>) DividerBlock.builder().build());
		
		process(payloadMessage);
	
//		payloadMessage.add(ImageBlock.builder().imageUrl("").imageHeight())
//		payloadMessage.add((LayoutBlock) DividerBlock.builder().build());
							
 //		String s
//	    String s = "[{\"1\":{\"2\":{\"3\":{\"4\":[5,{\"6\":7}]}}}}]";
//	    String s1 ="{\r\n"
//	    		+ "	\"blocks\": [\r\n"
//	    		+ "		{\r\n"
//	    		+ "			\"type\": \"header\",\r\n"
//	    		+ "			\"text\": {\r\n"
//	    		+ "				\"type\": \"plain_text\",\r\n"
//	    		+ "				\"text\": \"Today leave list :\",\r\n"
//	    		+ "				\"emoji\": true\r\n"
//	    		+ "			}\r\n"
//	    		+ "		},\r\n"
//	    		+ "		{\r\n"
//	    		+ "			\"type\": \"section\",\r\n"
//	    		+ "			\"text\": {\r\n"
//	    		+ "				\"type\": \"mrkdwn\",\r\n"
//	    		+ "				\"text\": \"*No one take leave Today!*\"\r\n"
//	    		+ "			},\r\n"
//	    		+ "			\"accessory\": {\r\n"
//	    		+ "				\"type\": \"image\",\r\n"
//	    		+ "				\"image_url\": \"https://uat.tcube.io/assets/images/App_slack_logo1.png\",\r\n"
//	    		+ "				\"alt_text\": \"Tcube logo\"\r\n"
//	    		+ "			}\r\n"
//	    		+ "		}\r\n"
//	    		+ "	]\r\n"
//	    		+ "}";
//	    try {
//	    	Object obj = parser.parse(s1);
////	    	array = (JSONArray)obj;
//	    	obj2 = (JSONObject) obj;
//	    	System.out.println("object 2"+obj2);
//			process(s);
//	    } catch(Exception e) {
//	    	e.printStackTrace();
//	    }
//		JSONObject s1 = {
//				   			"accessory": {
//				   				"type": "image",
//				   				"image_url": "https://uat.tcube.io/assets/images/App_slack_logo1.png",
//				   				"alt_text": "Tcube logo"
//				   			}
//				   		}
//				   	]
//		};
		
//		messageBuilder.append("*my message:* "+messge+ NEW_LINE);
//		messageBuilder.append("*message:* "+"HI"+ NEW_LINE);
		
	}

	public void sendMessageToSlackLeaveUserList(String url, List<LeaveTrackerDetails> leaveDetails)
			throws ParseException {
		logger.info("TodayLeaveUserListSendToSlack(sendMessageToSlackLeaveUserList) >> Entry");
		urlSlackWebHook = url;
		ArrayList<LayoutBlock> payloadMessage = new ArrayList<LayoutBlock>();
		JSONArray array = new JSONArray();
		String halfAndFullDay = "";
		List<LayoutBlock> layoutMessage = new ArrayList();
		Date d1 = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date_str = simpleDateFormat.format(d1);
//		final JSONObject jsonObject = new JSONObject();	
//		JSONParser parser = new JSONParser();
		layoutMessage.add(SectionBlock.builder()
				.text(MarkdownTextObject.builder().text("*Staff on PTO Today:*").build()).build());
//		payloadMessage.add(DividerBlock
//			    .builder()
//			    .build());
		for (int i = 0; i < leaveDetails.size(); i++) {

//			long value1 = leaveDetails.get(i).getOrgDetails().getOrg_id();

//			System.out.println((leaveDetails.get(i).getHalf_full_day()));
			String string1 = leaveDetails.get(i).getHalf_full_day();
			JSONParser jsonParser = new JSONParser();
			JSONArray jsonArray = (JSONArray) jsonParser.parse(string1);
			
			for (int k = 0; k < jsonArray.size(); k++) {
				final JSONObject newJsonObject = new JSONObject((Map) jsonArray.get(k));
//				System.out.println(newJsonObject.get("date"));
				if (date_str.equals(newJsonObject.get("date"))) {
					
					if (newJsonObject.get("full_half").equals("Full Day")) {
						
						halfAndFullDay = (String) newJsonObject.get("full_half");
						
						layoutMessage.add(SectionBlock.builder()
								.text(MarkdownTextObject.builder().text("> *" + leaveDetails.get(i).getEmp_name().trim()
										+ " : " + "* " + "`" + leaveDetails.get(i).getLeave_type() + "`").build())
								.build());
					} else {
						
						if (newJsonObject.get("full_half").equals("Half Day")) {
							
							halfAndFullDay = newJsonObject.get("full_half") + " - " + newJsonObject.get("first_second") ;
							layoutMessage.add(SectionBlock.builder()
									.text(MarkdownTextObject.builder()
											.text("> *" + leaveDetails.get(i).getEmp_name().trim() + " : " + "* " + "`"
													+ leaveDetails.get(i).getLeave_type() + " - " + "(" + halfAndFullDay
													+ ")" + "`")
											.build())
									.build());

						}

					}
				}
			}
		}
//		payloadMessage.add((LayoutBlock) DividerBlock
//			    .builder()
//			    .build());
		payloadMessage.addAll(layoutMessage);

		process(payloadMessage);
//		}

	}
	public void sendDummyData(){
		logger.info("TodayLeaveUserListSendToSlack(sendMessageToSlackLeaveUserList) >> Entry");
		urlSlackWebHook ="https://hooks.slack.com/services/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";
		ArrayList<LayoutBlock> payloadMessage = new ArrayList<LayoutBlock>();
		JSONArray array = new JSONArray();
		String halfAndFullDay="";
//		List<LayoutBlock> layoutMessage = new ArrayList();
		payloadMessage.add(SectionBlock
			    .builder()
			    .text(MarkdownTextObject
			      .builder()
			      .text("*Hi All*")
			      .build())
			    .build());
		process(payloadMessage);
		
	}
	
	
	
	private void process(ArrayList<LayoutBlock> payloadMessage) {
//		ArrayList<LayoutBlock> payloadMessage = new ArrayList<LayoutBlock>();
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
//	 List<LayoutBlock> message = new ArrayList<LayoutBlock>();
	 
//	 message1
//	  message.add(SectionBlock
//	    .builder()
//	    .text(MarkdownTextObject
//	      .builder()
//	      .text("*A co-worker signed up for the office potluck!*")
//	      .build())
//	    .build());
//	  message.add(SectionBlock
//	    .builder()
//	    .fields(Arrays.asList(
//	      MarkdownTextObject
//	        .builder()
//	        .text("*CO-WORKER:* " + coworker)
//	        .build(),
//	      MarkdownTextObject
//	        .builder()
//	        .text("*DEPARTMENT:* " + department)
//	        .build(),
//	      MarkdownTextObject
//	        .builder()
//	        .text("*FOOD ITEM:* " + foodItem)
//	        .build(),
//	      MarkdownTextObject
//	        .builder()
//	        .text("*VEGAN?:* " + isVeganString)
//	        .build(),
//	      MarkdownTextObject
//	        .builder()
//	        .text("*HOT OR COLD?:* " + isHotString)
//	        .build(),
//	      MarkdownTextObject
//	        .builder()
//	        .text("*RETURN THE CONTAINER?:* " + isContainerReturnableString)
//	        .build()
//	    ))
//	    .build());
//	  message.add(DividerBlock
//	    .builder()
//	    .build());
//	   ChatPostMessageRequest request = ChatPostMessageRequest.builder()
//	    .channel("CXXXXXXX")
//	    .blocks(message)
//	    .build();

//	   this.sendMessage(request);
		
	}