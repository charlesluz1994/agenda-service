package cluz.com.agenda.exception;

import cluz.com.agenda.util.ErrorConstants;
import cluz.com.agenda.util.ErrorUtility;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class AgendaControllerAdvice extends ResponseEntityExceptionHandler {

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Error> handleAccessDeniedException(Exception ex, WebRequest request) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Error.builder()
				.message(ErrorConstants.PERMISSION_DENIED_DESCRIPTION)
				.reason(ErrorConstants.PERMISSION_DENIED_REASON_CODE)
				.status(HttpStatus.FORBIDDEN.value()).build());
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Error> handleDataIntegrityViolationException(Exception ex, WebRequest request) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Error.builder()
				.message(ErrorConstants.CPF_ERROR_DESCRIPTION)
				.source(ErrorConstants.CPF_FIELD_ERROR)
				.reason(ErrorConstants.INVALID_ARGUMENT_REASON_CODE)
				.status(HttpStatus.BAD_REQUEST.value()).build());
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<Error> handleBusinessException(Exception ex, WebRequest request) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Error.builder()
				.message(ex.getMessage())
				.source("")
				.reason(ErrorConstants.INVALID_ARGUMENT_REASON_CODE)
				.status(HttpStatus.BAD_REQUEST.value()).build());
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<Error> handleNotFoundException(Exception ex, WebRequest request) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Error.builder()
				.message(ex.getMessage())
				.source("")
				.reason(ErrorConstants.INVALID_ARGUMENT_REASON_CODE)
				.status(HttpStatus.NOT_FOUND.value()).build());
	}

	@ExceptionHandler(ApiException.class)
	@ApiResponse(responseCode = "400", description = "Client error", content = @Content(schema = @Schema(implementation = Error.class, example = "{\n  \"message\": \"CPF validation message\",\n  \"reason\": \"The request contains an empty value for CPF\",\n  \"status\": 404\n}")))
	public ResponseEntity<Error> handleApiException(ApiException ex) {
		return ResponseEntity.status(ex.getHttpStatus()).body(ex.getError());
	}

	@ExceptionHandler(ResourceAccessException.class)
	public ResponseEntity<Error> resourceAccessUnavailable(ResourceAccessException ex, WebRequest request) {
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Error.builder()
				.message(ErrorConstants.DOWNSTREAM_TIMEOUT_ERROR_DESCRIPTION)
				.reason(ErrorConstants.DOWNSTREAM_TIMEOUT_ERROR_REASON_CODE)
				.status(HttpStatus.SERVICE_UNAVAILABLE.value()).build());
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		Error error = ErrorUtility.buildInvalidArgumentInvalidValueError();
		return new ResponseEntity<>(error, headers, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		Optional<FieldError> optionalFieldError = Optional.ofNullable(ex.getBindingResult().getFieldError());
		String source = optionalFieldError.map(FieldError::getField).orElse("");
		String code = optionalFieldError.map(FieldError::getCode).orElse("");

		Error error;
		switch (code) {
			case "NotNull":
			case "Blank":
				error = ErrorUtility.buildInvalidArgumentCannotBeNullError(source);
				break;
			case "Future":
			case "DateTimeFormat":
			case "Email":
			case "Size":
			case "CPF":
				error = ErrorUtility.buildInvalidArgumentError(source);
				break;
			case "Pattern":
			case "no-code-found":
			default:
				error = ErrorUtility.buildInvalidArgumentInvalidValueError();
				break;
		}

		return new ResponseEntity<>(error, headers, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		return ResponseEntity.status(status).body(Error.builder()
				.message(ErrorConstants.UNSUPPORTED_HTTP_METHOD_TYPE_DESCRIPTION)
				.reason(ErrorConstants.UNSUPPORTED_HTTP_METHOD_TYPE_REASON_CODE)
				.status(status.value()).build());
	}

	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		return ResponseEntity.status(status).body(Error.builder()
				.message(ErrorConstants.UNSUPPORTED_MEDIA_TYPE_DESCRIPTION)
				.reason(ErrorConstants.UNSUPPORTED_MEDIA_TYPE_REASON_CODE)
				.status(status.value()).build());
	}

	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
				Error.builder()
						.message(ErrorConstants.UNSUPPORTED_MEDIA_TYPE_DESCRIPTION)
						.reason(ErrorConstants.UNSUPPORTED_MEDIA_TYPE_REASON_CODE)
						.status(HttpStatus.NOT_ACCEPTABLE.value()).build());
	}

	@ExceptionHandler(Exception.class)
	@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = Error.class, example = "{\n  \"message\": \"Internal Server Error\",\n  \"reason\": \"Unexpected error occurred\",\n  \"status\": 500\n}")))
	public ResponseEntity<Error> handleUncaughtException(Exception ex, WebRequest request) {
		log.error("Unexpected error while processing the request", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Error.builder()
				.message(ErrorConstants.INTERNAL_SERVER_ERROR_DESCRIPTION)
				.reason(ErrorConstants.INTERNAL_SERVER_ERROR_REASON_CODE)
				.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build());
	}

}
