package com.vote.service;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;




import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vote.objects.Moderator;
import com.vote.objects.Poll;
import com.vote.repositories.ModeratorRepository;
import com.vote.repositories.PollRepository;

import flexjson.JSONSerializer;

@Service
public class VoteService {

	
	@Autowired
	private ModeratorRepository modRepo;
	@Autowired
	private PollRepository pollRepo;
	
	
	public boolean validateModId(int modId){
		if(!(modRepo.exists(modId)))
				return false;
		return true;
	}
	public boolean validatePollId(String pollId){
		if(pollId==null)
			return false;
		if(!(pollRepo.exists(pollId)))
			return false;
		return true;
	}
	public boolean validateModPollMap(int modId,String pollId){
		
		Poll poll=pollRepo.findById(pollId);
		if(poll.moderatorId==modId)
			return true;
		return false;
	}
	private Integer generateID(){
		Random r = new Random();
		return r.nextInt(9000) + 1000; 
	}
	
	public Moderator createModerator(Moderator mod) {
		Moderator moderator = new Moderator();
		Integer id = generateID();
		while(modRepo.exists(id))
			id = generateID();
		moderator.id = id;
		moderator.name = mod.name;
		DateTime dt = new DateTime(DateTimeZone.UTC);
		
		moderator.created_at =  dt.toString(ISODateTimeFormat.dateTime().withZoneUTC());
		moderator.password = mod.password;
		moderator.email = mod.email;
		
		if(modRepo==null){
			System.out.println("object is null here");
		}
		modRepo.save(moderator);
		return moderator;

	}
	public Moderator viewModerator(int moderatorId)  {
		
		return modRepo.findById(moderatorId);
	}
	public Moderator updateModerator(int moderator_id,Moderator moderator) {
		
		Moderator oldModerator=modRepo.findById(moderator_id);
	
		if(moderator.name!=null)
			oldModerator.name = moderator.name;
		if(moderator.email!=null)
			oldModerator.email = moderator.email;
		if(moderator.password!=null)
			oldModerator.password=moderator.password;
		modRepo.save(oldModerator);
		
		return oldModerator;
		
	}
	public Poll createPoll(int moderator_id, Poll pollBody) {
		
		

		Poll poll=new Poll();
		String pollID = Integer.toString(generateID(),36);

		while(pollRepo.exists(pollID)){
			pollID = Integer.toString(generateID(),36);
		}

		poll.id=pollID;
		poll.question=pollBody.question;
		poll.started_at=pollBody.started_at;
		poll.expired_at=pollBody.expired_at;
		poll.choice=pollBody.choice;
		poll.moderatorId=moderator_id;
		poll.results = new int[poll.choice.length];
		pollRepo.save(poll);
		return poll;
	}

	public String viewPoll(String pollId)  {
		return new JSONSerializer().include("choice").exclude("results").exclude("moderatorId").exclude("*.class").serialize(pollRepo.findById(pollId).toString());
	
	}

	public ArrayList<String> listAllPolls(int moderator_id) {
		
		List<Poll> pollsOfMod=pollRepo.findByModeratorId(moderator_id);
		ArrayList<String> pollArray=new ArrayList<String>();
		if(pollsOfMod==null)
			return null;
		else{
				Iterator<Poll> it=pollsOfMod.iterator();
				while(it.hasNext()){
					String jsonString = new JSONSerializer().include("choice").exclude("results").exclude("moderatorId").exclude("*.class").serialize(it.next());
					pollArray.add(jsonString);
				}
			}
		return pollArray;
			
		}
		
	
	public Poll viewPollOfMod(int moderator_id, String poll_id) {
		Poll poll=pollRepo.findById(poll_id);
		return poll;
	}

	public void deletePoll(int moderator_id, String poll_id)  {
		
		Poll poll=pollRepo.findById(poll_id);
		pollRepo.delete(poll);
		
		
	}

	public boolean votePoll(String poll_id, int choice_index)  {
		int voteCount=0;
		Poll updatePoll=pollRepo.findById(poll_id);
		if(updatePoll.results==null){
			updatePoll.results = new int[updatePoll.choice.length];
		}
		if(choice_index>=updatePoll.choice.length)
			return false;
		voteCount=updatePoll.results[choice_index];
		updatePoll.results[choice_index]= voteCount+1;
		pollRepo.save(updatePoll);
		
		return true;
	}
	public void findExpiredPolls(String scheduledDateTime){
		List<Poll> allPolls=pollRepo.findAll();
		//ArrayList<Poll> expiredPolls=new ArrayList<Poll>();
		if(allPolls==null)
			return;
		else{
				Iterator<Poll> it=allPolls.iterator();
				while(it.hasNext()){
					Poll poll=it.next();
					if(poll!=null){
						String dateString = poll.expired_at;
						org.joda.time.format.DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
						DateTime expiredDate = formatter.parseDateTime(dateString);
						if(expiredDate.isBefore(formatter.parseDateTime(scheduledDateTime))){
							System.out.println("Poll ID: " + poll.id);
							writeToKafka(poll);
						}
					}
				}
			}
		
		
	}
	private void writeToKafka(Poll poll) {
		Producer<Integer, String> producer;
	    final Properties properties = new Properties();
	        properties.put("metadata.broker.list", "54.68.83.161:9092");
	        properties.put("serializer.class", "kafka.serializer.StringEncoder");
	        properties.put("request.required.acks", "1");
	        producer = new Producer<>(new ProducerConfig(properties));
	    String topic="cmpe273-new-topic";
	    Moderator mod=modRepo.findById(poll.moderatorId);
	    String kafkaMessage = formatMessage(poll, mod);
	    KeyedMessage<Integer, String> data = new KeyedMessage<>(topic, kafkaMessage);
        producer.send(data);
        producer.close();
		
	}
	
	private String formatMessage(Poll poll, Moderator mod){
		String sjsuId="009994036";
	    StringBuilder kafkaMessage=new StringBuilder();
	    kafkaMessage.append(mod.email)
	    			.append(":")
	    			.append(sjsuId)
	    			.append(":")
	    			.append("Poll Result [");
	    for(int i =0;i<poll.choice.length;i++){
	    	kafkaMessage.append(poll.choice[i])
	    				.append("=")
	    				.append(poll.results[i]);
	    	if(i!=poll.choice.length-1){
	    		kafkaMessage.append(",");
	    	}
	    				
	    }
	    kafkaMessage.append("]");			
	    return kafkaMessage.toString();
	}
	
		
		

	

}
