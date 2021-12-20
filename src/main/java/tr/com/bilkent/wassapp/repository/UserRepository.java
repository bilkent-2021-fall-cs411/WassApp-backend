package tr.com.bilkent.wassapp.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tr.com.bilkent.wassapp.collection.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("{\n" +
            "    $or: [\n" +
            "        {displayName: {$regex: ?0, $options: 'i'}},\n" +
            "        {email: {$regex: ?0, $options: 'i'}}\n" +
            "    ]\n" +
            "}")
    List<User> search(String searchTerm, Pageable pageable);

}
