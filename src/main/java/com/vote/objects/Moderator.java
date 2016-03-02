package com.vote.objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "moderators")
public class Moderator {
	
	@Null
	@Id public Integer id;
	@NotNull
	public String name;
	@NotNull
	public String email;
	@NotNull
	public String password;
	@Null
	public String created_at;
	
	@Override
    public String toString() {
        return String.format(
                "Moderator[id=%d, name=%s, email=%s]",
                id, name,email);
    }
	public Moderator(Moderator moderator){
		this.id=moderator.id;
		this.created_at=moderator.created_at;
		this.email=moderator.email;
		this.name=moderator.name;
		this.password=moderator.password;
	}
	public Moderator() {
		// TODO Auto-generated constructor stub
	}
	

}
