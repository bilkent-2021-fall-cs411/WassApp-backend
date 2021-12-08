package tr.com.bilkent.wassapp.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tr.com.bilkent.wassapp.model.HTTPResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public HTTPResponse<String> handleRuntimeException(RuntimeException e) {
        log.error(e.getMessage());
        return new HTTPResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }
}
