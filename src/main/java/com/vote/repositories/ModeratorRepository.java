package com.vote.repositories;



import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import com.vote.objects.Moderator;
//@Repository
//@RepositoryRestResource(collectionResourceRel = "moderators", path = "moderators")
@Service
public interface ModeratorRepository extends MongoRepository<Moderator, Integer> {

	public Moderator findById(Integer id);
	//public Moderator save(Moderator saved);

}


