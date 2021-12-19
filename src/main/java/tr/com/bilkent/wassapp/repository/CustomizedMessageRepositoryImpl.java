package tr.com.bilkent.wassapp.repository;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import tr.com.bilkent.wassapp.collection.Message;
import tr.com.bilkent.wassapp.collection.enums.MessageStatus;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@AllArgsConstructor
public class CustomizedMessageRepositoryImpl implements CustomizedMessageRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public long markAllAsRead(String email, String otherEmail) {
        Query query = new Query().addCriteria(
                where("receiver").is(email).and("sender").is(otherEmail).and("status").ne(MessageStatus.READ));
        return mongoTemplate.updateMulti(query, new Update().set("status", MessageStatus.READ), "message")
                .getModifiedCount();
    }

    @Override
    public long deleteChatHistory(String email, String otherEmail) {
        Query query = new Query().addCriteria(new Criteria().orOperator(
                where("receiver").is(email).and("sender").is(otherEmail),
                where("receiver").is(otherEmail).and("sender").is(email)));

        return mongoTemplate.remove(query, Message.class).getDeletedCount();
    }
}
