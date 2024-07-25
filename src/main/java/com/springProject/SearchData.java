package com.springProject;

import java.sql.Timestamp;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchData {
	private	String category;
	private String location;
	private int star;
	private String hashtag;
	private String startDate;
	private String endDate;
}
