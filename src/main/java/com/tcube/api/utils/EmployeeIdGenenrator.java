package com.tcube.api.utils;

public class EmployeeIdGenenrator {
	public static String genarateId(String clientName, String username) {
		String idPrefix;

		char firstLetter = clientName.charAt(0);
		char secondLetter = clientName.charAt(1);
		StringBuilder sb = new StringBuilder();

		idPrefix = sb.append(firstLetter).append(secondLetter).toString().toUpperCase()+ "#";
		System.out.println(idPrefix);

		return idPrefix;
	}
}
