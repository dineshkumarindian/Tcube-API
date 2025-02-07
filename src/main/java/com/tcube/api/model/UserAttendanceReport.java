package com.tcube.api.model;

import java.util.ArrayList;
import java.util.List;

public class UserAttendanceReport {
	
	private List<String> dates_x_axis;
	
	private List<ArrayList<Object>> chart_data;
	

	public List<String> getDates_x_axis() {
		return dates_x_axis;
	}

	public void setDates_x_axis(List<String> dates_x_axis) {
		this.dates_x_axis = dates_x_axis;
	}

	public List<ArrayList<Object>> getChart_data() {
		return chart_data;
	}

	public void setChart_data(List<ArrayList<Object>> chart_data) {
		this.chart_data = chart_data;
	}



}
