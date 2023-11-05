package cluz.com.agenda.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ApiException extends RuntimeException{

    final HttpStatus httpStatus;
    final Error error;

}
