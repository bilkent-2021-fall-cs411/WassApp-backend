package tr.com.bilkent.wassapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "tr.com.bilkent.wassapp.repository")
public class MongoConfig {
}
