package com.vote.objects;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;

public class Poll {
	@Id public String id;
	@NotNull
	public String question;
	@NotNull
	public String started_at;
	@NotNull
	public String expired_at;
	@NotNull
	public String[] choice;
	public int[] results;
	public Integer moderatorId;
	
	@Override
    public String toString() {
        return String.format(
                "Poll[id=%s, question=%s,started_at=%s,expired_at=%s]",
                id, question,started_at,expired_at);
    }
}
