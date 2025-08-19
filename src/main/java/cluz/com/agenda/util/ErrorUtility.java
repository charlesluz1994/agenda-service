package cluz.com.agenda.util;


import cluz.com.agenda.exception.Error;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Utilities to help with error reporting. Contains static methods for returning error reponse bodies that are common for multiple exception types.
 * Leverages the Lombok @NoArgsConstructor to create a private constructor that satisfies the Sonar requirement to 'hide the implicit public constructor in our utility class'.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorUtility {

	public static Error buildInvalidArgumentError(String source) {
		return getError(ErrorConstants.INVALID_ARGUMENT_ERROR_MESSAGE, source, ErrorConstants.INVALID_ARGUMENT_REASON_CODE, HttpStatus.BAD_REQUEST.value());
	}

	public static Error buildInvalidArgumentCannotBeNullError(String source) {
		return getError(ErrorConstants.INVALID_ARGUMENT_ERROR_MESSAGE, source, ErrorConstants.INVALID_ARGUMENT_REASON_CODE, HttpStatus.BAD_REQUEST.value());
	}

	public static Error buildInvalidArgumentInvalidValueError() {
		return getError(ErrorConstants.INVALID_ARGUMENT_ERROR_MESSAGE, ErrorConstants.REQUEST_BODY_SOURCE, ErrorConstants.INVALID_ARGUMENT_REASON_CODE, HttpStatus.BAD_REQUEST.value());
	}

	public static Error getError(String message, String source, String reason, Integer status) {
		return Error.builder()
				.message(message)
				.source(source)
				.reason(reason)
				.status(status)
				.build();
	}

}