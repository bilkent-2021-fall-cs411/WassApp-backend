package tr.com.bilkent.wassapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import tr.com.bilkent.wassapp.config.converter.OffsetDateTimeToStringConverter;
import tr.com.bilkent.wassapp.config.converter.StringToOffsetDateTimeConverter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Configuration
@EnableMongoRepositories(basePackages = "tr.com.bilkent.wassapp.repository")
@EnableMongoAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
@RequiredArgsConstructor
public class MongoConfig {
    private final OffsetDateTimeToStringConverter offsetDateTimeToStringConverter;
    private final StringToOffsetDateTimeConverter stringToOffsetDateTimeConverter;

    @Bean
    public DateTimeProvider auditingDateTimeProvider() {
        return () -> Optional.of(OffsetDateTime.now());
    }

    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(List.of(stringToOffsetDateTimeConverter, offsetDateTimeToStringConverter));
    }
}
