package com.springProject;

import java.sql.Timestamp;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchData {
	private	String category;
	private String location;
	private int star;
	private String hashtag;
	private Timestamp startDate;
	private Timestamp endDate;
}
