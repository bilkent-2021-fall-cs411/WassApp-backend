package tr.com.bilkent.wassapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tr.com.bilkent.wassapp.collection.Message;

import java.time.OffsetDateTime;

@Repository
public interface MessageRepository extends MongoRepository<Message, String>, CustomizedMessageRepository {

    @Query(value = "{\n" +
            "    $or: [{\n" +
            "        $and: [{\n" +
            "            sender: ?0\n" +
            "        }, {\n" +
            "            receiver: ?1\n" +
            "        }]\n" +
            "    }, {\n" +
            "        $and: [{\n" +
            "            sender: ?1\n" +
            "        }, {\n" +
            "            receiver: ?0\n" +
            "        }]\n" +
            "    }], sendDate: {\n" +
            "        $lte: ?2\n" +
            "    }\n" +
            "}", sort = "{ sendDate: -1 }")
    Page<Message> getChatMessages(String sender, String receiver, OffsetDateTime beforeDate, Pageable pageable);

    @Aggregation({"{ $match: { $or: [{sender: ?0}, {receiver: ?0}] } }",
            "    { $sort: { sendDate: -1 }}",
            "    {\n" +
                    "        $group: {\n" +
                    "            _id: {\n" +
                    "                receiver: {$min: [\"$receiver\", \"$sender\"]},\n" +
                    "                sender: {$max: [\"$receiver\", \"$sender\"]}\n" +
                    "            },\n" +
                    "            doc: {$first: \"$$ROOT\"}\n" +
                    "        }\n" +
                    "    }",
            "    {\n" +
                    "        $replaceRoot: {\n" +
                    "            newRoot: \"$doc\"\n" +
                    "        }\n" +
                    "    }",
            "    { $sort: { sendDate: -1 }}"})
    AggregationResults<Message> findChatsOfUser(String email);
}
