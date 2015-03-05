package com.vote.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import com.vote.objects.Moderator;
import com.vote.objects.Poll;

import flexjson.JSONSerializer;
public class VoteService {

	private  static HashMap<Integer,Moderator> moderatorMap = new HashMap<Integer,Moderator>();
	//private  static HashMap<Integer, HashMap<String,Poll>> modPollMap=new HashMap<Integer,HashMap<String,Poll>>();
	private  static HashMap<Integer, ArrayList<String>> modPollMap=new HashMap<Integer,ArrayList<String>>();

	private static HashMap<String,Poll> pollMap=new HashMap<String,Poll>();
	
	public boolean validateModId(int modId){
		if(!(moderatorMap.containsKey(modId)))
				return false;
		return true;
	}
	public boolean validatePollId(String pollId){
		if(!pollMap.containsKey(pollId))
			return false;
		return true;
	}
	public boolean validateModPollMap(int modId,String pollId){
		ArrayList<String> pollListOfMod= modPollMap.get(modId);	
		if(pollListOfMod.contains(pollId))
			return true;
		return false;
	}
	private int generateID(){
		Random r = new Random();
		return r.nextInt(9000) + 1000; 
	}
	
	public Moderator createModerator(Moderator mod) {
		Moderator moderator = new Moderator();
		int id = generateID();
		while(moderatorMap.containsKey(id))
			id = generateID();
		moderator.id = id;
		moderator.name = mod.name;
		DateTime dt = new DateTime(DateTimeZone.UTC);
		
		moderator.created_at =  dt.toString(ISODateTimeFormat.dateTime().withZoneUTC());
		moderator.password = mod.password;
		moderator.email = mod.email;
		moderatorMap.put(id,moderator);

		return moderator;

	}
	public Moderator viewModerator(int moderatorId)  {
		return moderatorMap.get(moderatorId);
	}
	public Moderator updateModerator(int moderator_id,Moderator moderator) {
		
		Moderator oldModerator=moderatorMap.get(moderator_id);
	
		if(moderator.name!=null)
			oldModerator.name = moderator.name;
		if(moderator.email!=null)
			oldModerator.email = moderator.email;
		if(moderator.password!=null)
			oldModerator.password=moderator.password;
		moderatorMap.put(moderator_id, oldModerator);
		return oldModerator;
		
	}
	public Poll createPoll(int moderator_id, Poll pollBody) {
		
		

		Poll poll=new Poll();
		String pollID = Integer.toString(generateID(),36);

		while(pollMap.containsKey(pollID)){
			pollID = Integer.toString(generateID(),36);
		}
		ArrayList<String> pollsOfMod=modPollMap.get(moderator_id);
		if(pollsOfMod==null){
			pollsOfMod=new ArrayList<String>();
			
		}
		pollsOfMod.add(pollID);
		
		modPollMap.put(moderator_id, pollsOfMod);

		poll.id=pollID;
		poll.question=pollBody.question;
		poll.started_at=pollBody.started_at;
		poll.expired_at=pollBody.expired_at;
		poll.choice=pollBody.choice;
		pollMap.put(pollID, poll);
		return poll;
	}

	public String viewPoll(String pollId)  {
	
		return new JSONSerializer().include("choice").exclude("results").exclude("*.class").serialize(pollMap.get(pollId));
	
	}

	public ArrayList<Poll> listAllPolls(int moderator_id) {
		
		ArrayList<String> pollIDs= modPollMap.get(moderator_id);
		ArrayList<Poll> pollList= new ArrayList<Poll>();
		for(String pollId :pollIDs){
			pollList.add(pollMap.get(pollId));
		}	
		return pollList;
	}
	
	

	public Poll viewPollOfMod(int moderator_id, String poll_id) {
		
		ArrayList<String> pollListOfMod= modPollMap.get(moderator_id);	
		Poll poll = pollMap.get(poll_id);
		return pollMap.get(poll_id);
	}

	public void deletePoll(int moderator_id, String poll_id)  {
		ArrayList<String> pollList= modPollMap.get(moderator_id);
		/*if(pollMap==null)
			throw new Exception("No polls for given Moderator!");
		if(!(pollList.contains(poll_id)))
			throw new Exception(String.format("Poll for moderator %s does not exist!",moderator_id));	*/	
		pollList.remove(poll_id);
		if(pollList.size()==0)
			modPollMap.remove(moderator_id);

		modPollMap.put(moderator_id, pollList);
		
		
	}

	public void votePoll(String poll_id, int choice_index)  {
		int voteCount=0;
		Poll updatePoll=pollMap.get(poll_id);
		if(updatePoll.results.size()==0){
			updatePoll.results=new ArrayList<Integer>(updatePoll.choice.size());
			for(int i=0;i<updatePoll.choice.size();i++){
				updatePoll.results.add(0);
			}
		}
		voteCount=updatePoll.results.get(choice_index);
		updatePoll.results.set(choice_index, voteCount+1);
		pollMap.put(poll_id, updatePoll);
	}

	
		
		

	

}
