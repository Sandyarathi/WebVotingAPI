package com.vote.objects;

import java.util.ArrayList;

import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonView;

public class Poll {
	public String id;
	@NotNull
	public String question;
	@NotNull
	public String started_at;
	@NotNull
	public String expired_at;
	@NotNull
	public ArrayList<String> choice;
	public ArrayList<Integer> results;
	
	
	public Poll(){
		results= new ArrayList<Integer>();
		choice= new ArrayList<String>();
	}

}
