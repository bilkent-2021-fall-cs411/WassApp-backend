package tr.com.bilkent.wassapp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tr.com.bilkent.wassapp.collection.User;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {

    User findByEmail(String email);

}
