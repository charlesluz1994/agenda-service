package cluz.com.agenda.util;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Constants used in error reporting responses.
 * @NoArgsConstructor add for sonar requirements : 'hide the implicit public constructor in our utility class'
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorConstants {
    public static final String MISSING_REQUIRED_BODY_REASON_CODE = "MISSING_REQUIRED_BODY";

    // Reason Codes
    public static final String INVALID_REQUEST_PARAMETER_REASON_CODE = "INVALID_REQUEST_PARAMETER";
    public static final String DOWNSTREAM_TIMEOUT_ERROR_REASON_CODE = "DOWNSTREAM_SERVICE_UNAVAILABLE";
    public static final String UNSUPPORTED_MEDIA_TYPE_REASON_CODE = "UNSUPPORTED_MEDIA_TYPE";
    public static final String UNSUPPORTED_HTTP_METHOD_TYPE_REASON_CODE = "UNSUPPORTED_HTTP_METHOD_TYPE";
    public static final String MISSING_REQUIRED_PARAMETER_REASON_CODE = "MISSING_REQUIRED_PARAMETER";
    public static final String INTERNAL_SERVER_ERROR_REASON_CODE = "INTERNAL";
    public static final String INVALID_ARGUMENT_REASON_CODE = "INVALID_ARGUMENT";
    public static final String INVALID_STATE_REASON_CODE = "INVALID_STATE";
    public static final String PERMISSION_DENIED_REASON_CODE = "PERMISSION_DENIED";
    public static final String FIRST_DATA_UNAVAILABLE_REASON_CODE = "UNAVAILABLE";

    // Descriptions
    public static final String BAD_REQUEST_DESCRIPTION = "Invalid request parameter";

    public static final String CPF_ERROR_DESCRIPTION = "CPF already Exists";
    public static final String MISSING_REQUIRED_PARAMETER_DESCRIPTION = "One of the request parameter is missing.";
    public static final String INTERNAL_SERVER_ERROR_DESCRIPTION = "Internal server error. Something went wrong while processing the request";
    public static final String DOWNSTREAM_TIMEOUT_ERROR_DESCRIPTION = "One of the downstream system has timeout.";
    public static final String UNSUPPORTED_MEDIA_TYPE_DESCRIPTION = "Requested media type is not supported.";
    public static final String UNSUPPORTED_HTTP_METHOD_TYPE_DESCRIPTION = "Requested http method type is not supported.";
    public static final String UNAUTHENTICATED_DESCRIPTION = "Server cannot verify your request.";
    public static final String INVALID_ARGUMENT_ERROR_MESSAGE = "Cannot process the request because it is malformed or has incorrect/missing fields or values.";
    public static final String RESOURCE_ACCESS_EXCEPTION = "Resource Access Exception";
    public static final String INVALID_FIELD_LENGTH = "Invalid Field Length";
    public static final String INVALID_STATE_DESCRIPTION = "Request cannot be executed due to the incorrect field value.";
    public static final String PERMISSION_DENIED_DESCRIPTION = "Access is denied to the requested resource. Client does not have sufficient permission or has been locked.";
    public static final String FIRST_DATA_UNAVAILABLE_DESCRIPTION = "Service unavailable. Typically the server not able to serve the request temporarily. Retry after some time";

    // Details
    public static final String AUTHENTICATION_FAILED = "Authentication failed";
    public static final String INVALID_ARGUMENT_CANNOT_BE_NULL_ERROR_MESSAGE = " is required.";


    // Source
    public static final String REQUEST_BODY_SOURCE = "Request body";

    public static final String CPF_FIELD_ERROR = "CPF";
}