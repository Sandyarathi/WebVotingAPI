package com.vote.controllers;

import java.util.ArrayList;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vote.objects.Moderator;
import com.vote.objects.Poll;
import com.vote.service.VoteService;

import flexjson.JSONSerializer;

@RestController
@RequestMapping("/api/v1")
public class VoteController {
	
	@Autowired
	VoteService service ;
	
	@RequestMapping(value="/moderators", method = RequestMethod.POST, 
			produces = "application/json", consumes= "application/json")
	public ResponseEntity<Moderator> createModerator( @RequestBody @Valid Moderator moderator){
		return new ResponseEntity<Moderator>(service.createModerator(moderator),HttpStatus.CREATED);
	}
	
	@RequestMapping(value="/moderators/{moderator_id}", method = RequestMethod.GET, produces ="application/json")
    public ResponseEntity<Moderator> viewModerator(@PathVariable("moderator_id") int moderatorId ) {
	  if(service.validateModId(moderatorId))
		  return new ResponseEntity<Moderator>(service.viewModerator(moderatorId), HttpStatus.OK);
	  else{
		  
		  return new ResponseEntity<Moderator>(HttpStatus.NOT_FOUND);
	  }
    }
	
	@RequestMapping(value="/moderators/{moderator_id}", method = RequestMethod.PUT, produces ="application/json")
	public ResponseEntity<Moderator> updateModerator(@PathVariable int moderator_id, @RequestBody Moderator moderator) {
		if(service.validateModId(moderator_id))
			return new ResponseEntity<Moderator>(service.updateModerator(moderator_id,moderator), HttpStatus.OK);
		else
			 return new ResponseEntity<Moderator>(HttpStatus.NOT_FOUND);

	}
	
	@RequestMapping(value="/moderators/{moderator_id}/polls", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<String> createPoll(@PathVariable int moderator_id, @RequestBody @Valid Poll poll) {
		if(service.validateModId(moderator_id)){
			Poll resultPoll = service.createPoll(moderator_id,poll);
			String jsonString = new JSONSerializer().include("choice").exclude("results").exclude("*.class").serialize(resultPoll);

			return new ResponseEntity<String>(jsonString,HttpStatus.CREATED);
		}
		  else{
			  
			  return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		  }
	}
	
	@RequestMapping(value="/polls/{poll_id}", method = RequestMethod.GET, produces ="application/json")
	public ResponseEntity<String> viewPoll(@PathVariable String poll_id) {
		if(service.validatePollId(poll_id))
			  return new ResponseEntity<String>(service.viewPoll(poll_id),HttpStatus.OK);
		  else{
			  
			  return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		  }
		  
		//return new ResponseEntity<String>(service.viewPoll(poll_id), HttpStatus.OK);
	}
	@RequestMapping(value="/moderators/{moderator_id}/polls/{poll_id}", method = RequestMethod.GET, produces ="application/json")
	  public ResponseEntity<Poll> viewPollOfMod(@PathVariable int moderator_id,@PathVariable String poll_id){
		
		if(service.validateModId(moderator_id)&& service.validatePollId(poll_id)&& service.validateModPollMap(moderator_id, poll_id)){
			
				
				return new ResponseEntity<Poll>(service.viewPollOfMod(moderator_id,poll_id), HttpStatus.OK);
			
		}
		else
			return new ResponseEntity<Poll>( HttpStatus.NOT_FOUND);

	  }
	
	
	
	@RequestMapping(value="/polls/{poll_id}", method = RequestMethod.PUT, produces ="application/json")
	  public ResponseEntity<String> votePoll(@PathVariable String poll_id, @RequestParam("choice") int choice_index) {
		  
		if(service.validatePollId(poll_id)){
			boolean success = service.votePoll(poll_id,choice_index);
			if(success)
				return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
			else
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);

		}
		else
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
	  }
	
	@RequestMapping(value="/moderators/{moderator_id}/polls", method = RequestMethod.GET, produces ="application/json")
	public ResponseEntity<ArrayList<String>> listAllPolls(@PathVariable int moderator_id) {
		if(service.validateModId(moderator_id))
			return new ResponseEntity<ArrayList<String>>(service.listAllPolls(moderator_id), HttpStatus.OK);		  
		else{
			  
			  return new ResponseEntity<ArrayList<String>>(HttpStatus.NOT_FOUND);
		  }  
		
		
		//return new ResponseEntity<ArrayList<Poll>>(service.listAllPolls(moderator_id), HttpStatus.OK);
	
	}
	@RequestMapping(value="/moderators/{moderator_id}/polls/{poll_id}", method = RequestMethod.DELETE, produces ="application/json")
	  public ResponseEntity<String>  deletePoll(@PathVariable int moderator_id,@PathVariable String poll_id) {
		if(service.validateModId(moderator_id)&& service.validatePollId(poll_id)&& service.validateModPollMap(moderator_id, poll_id)){
				 service.deletePoll(moderator_id,poll_id);
				 return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
		}
		else
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		 
	  }
	


}
