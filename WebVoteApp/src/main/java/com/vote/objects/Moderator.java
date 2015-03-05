package com.vote.objects;

import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;


public class Moderator {
	public Integer id;
	@NotNull
	public String name;
	@NotNull
	public String email;
	@NotNull
	public String password;
	public String created_at;
	

}
