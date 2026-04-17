package com.example.finance.gateway.exception;

import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalErrorWebExceptionHandler.class);

  public GlobalErrorWebExceptionHandler(
      ErrorAttributes errorAttributes,
      ApplicationContext applicationContext,
      ServerCodecConfigurer serverCodecConfigurer) {
    super(errorAttributes, new WebProperties().getResources(), applicationContext);
    super.setMessageWriters(serverCodecConfigurer.getWriters());
    super.setMessageReaders(serverCodecConfigurer.getReaders());
  }

  @Override
  protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
    return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
  }

  @SuppressWarnings({"null", "deprecation"})
  private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
    Map<String, Object> errorAttributes =
        getErrorAttributes(request, ErrorAttributeOptions.defaults());
    Throwable throwable = getError(request);

    int status = (int) errorAttributes.getOrDefault("status", 500);
    String path = request.path();
    String traceId = resolveTraceId(request);

    String message;
    String error;
    String errorCode;
    String category;

    if (throwable instanceof ResponseStatusException responseStatusException) {
      // Use the explicit status and reason set by the caller — do NOT sanitize away an intentional
      // message.
      status = responseStatusException.getStatusCode().value();
      String reason = responseStatusException.getReason();
      message = (reason != null && !reason.isBlank()) ? reason : defaultMessageFor(status);
      error = responseStatusException.getStatusCode().toString();
      errorCode = mapErrorCode(status);
      category = mapCategory(status);
    } else {
      // Unknown/unexpected throwable — sanitize to avoid leaking internals.
      message = sanitizeMessage(status, (String) errorAttributes.get("message"));
      error =
          HttpStatus.resolve(status) != null
              ? HttpStatus.resolve(status).getReasonPhrase()
              : "Internal Server Error";
      errorCode = mapErrorCode(status);
      category = mapCategory(status);
    }

    if (status >= 500) {
      log.error(
          "Gateway error [{}] {} {} -> {}",
          traceId,
          request.methodName(),
          path,
          errorCode,
          throwable);
    } else {
      log.warn(
          "Gateway error [{}] {} {} -> {} ({})",
          traceId,
          request.methodName(),
          path,
          errorCode,
          throwable.getMessage());
    }

    ErrorResponse body =
        ErrorResponse.of(status, error, message, path, errorCode, category, traceId, null);

    return ServerResponse.status(status).contentType(MediaType.APPLICATION_JSON).bodyValue(body);
  }

  private String defaultMessageFor(int status) {
    return switch (status) {
      case 400 -> "The request could not be processed. Please verify your input.";
      case 401 -> "Authentication is required to access this resource.";
      case 403 -> "You do not have permission to perform this action.";
      case 404 -> "The requested resource was not found.";
      default ->
          status >= 500
              ? "An unexpected server error occurred. Please try again later."
              : "The request could not be processed.";
    };
  }

  private String mapErrorCode(int status) {
    return switch (status) {
      case 400 -> "BAD_REQUEST";
      case 401 -> "UNAUTHORIZED";
      case 403 -> "FORBIDDEN";
      case 404 -> "NOT_FOUND";
      case 405, 415 -> "UNSUPPORTED_OPERATION";
      case 409 -> "DUPLICATE_OR_CONFLICT";
      case 422 -> "BUSINESS_RULE_VIOLATION";
      default -> status >= 500 ? "INTERNAL_SERVER_ERROR" : "HTTP_ERROR";
    };
  }

  private String mapCategory(int status) {
    return switch (status) {
      case 400 -> "VALIDATION";
      case 401 -> "AUTHENTICATION";
      case 403 -> "AUTHORIZATION";
      case 404 -> "MISSING_DATA";
      case 409, 422 -> "BUSINESS_RULE";
      default -> status >= 500 ? "SERVER_FAILURE" : "BAD_REQUEST";
    };
  }

  private String sanitizeMessage(int status, String rawMessage) {
    if (status >= 500) {
      return "An unexpected server error occurred. Please try again later.";
    }
    if (rawMessage == null || rawMessage.isBlank()) {
      return switch (status) {
        case 400 -> "The request could not be processed. Please verify your input.";
        case 401 -> "Authentication is required to access this resource.";
        case 403 -> "You do not have permission to perform this action.";
        case 404 -> "The requested resource was not found.";
        default -> "The request could not be processed.";
      };
    }
    return rawMessage;
  }

  private String resolveTraceId(ServerRequest request) {
    String correlationId = request.headers().firstHeader("X-Correlation-Id");
    if (correlationId != null && !correlationId.isBlank()) {
      return correlationId;
    }
    String requestId = request.exchange().getRequest().getId();
    if (requestId != null && !requestId.isBlank()) {
      return requestId;
    }
    return UUID.randomUUID().toString();
  }
}
