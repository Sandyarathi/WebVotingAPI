package com.vote.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import com.vote.objects.Poll;
//@Repository
//@RepositoryRestResource(collectionResourceRel = "polls", path = "polls")
@Service
public interface PollRepository extends MongoRepository<Poll, String> {

	

	public Poll findById(String id);

	

	public List<Poll> findByModeratorId(Integer moderatorId);




	

	

	
	

}