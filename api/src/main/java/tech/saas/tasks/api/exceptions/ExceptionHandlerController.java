package tech.saas.tasks.api.exceptions;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;
import tech.saas.tasks.core.exceptions.BadRequestException;
import tech.saas.tasks.core.exceptions.ForbiddenException;

import java.net.URI;
import java.util.Objects;

@Slf4j
@Controller
@ControllerAdvice
@RequestMapping("${server.error.path:${error.path:/error}}")
public class ExceptionHandlerController extends ResponseEntityExceptionHandler implements ErrorController {

    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<Object> handleBadRequestException(BadRequestException ex, WebRequest request) throws Exception {
        return handleExceptionInternal(
                ex,
                ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, String.valueOf(ex.getMessage())),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @ExceptionHandler(ForbiddenException.class)
    public final ResponseEntity<Object> handleForbiddenException(ForbiddenException ex, WebRequest request) throws Exception {
        return handleExceptionInternal(
                ex,
                ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, String.valueOf(ex.getMessage())),
                new HttpHeaders(),
                HttpStatus.FORBIDDEN,
                request
        );
    }


    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            @NonNull Exception exception,
            Object body,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode statusCode,
            @NonNull WebRequest request) {

        if (body instanceof ProblemDetail data) {
            data.setType(URI.create("traffic:exception"));
            return super.handleExceptionInternal(exception, data, headers, statusCode, request);
        }

        ProblemDetail data = ProblemDetail.forStatusAndDetail(statusCode, exception.getMessage());
        data.setType(URI.create("traffic:exception"));
        return super.handleExceptionInternal(exception, data, headers, statusCode, request);
    }


    @RequestMapping(produces = "application/json")
    public ResponseEntity<?> errorJson(HttpServletRequest request) {

        HttpStatus status = getStatus(request);
        if (status == HttpStatus.NO_CONTENT) {
            return new ResponseEntity<>(status);
        }
        var message = getErrorMessage(request);

        var body = ProblemDetail.forStatusAndDetail(status, String.valueOf(message));
        body.setType(URI.create("traffic:exception"));
        body.setTitle(String.valueOf(status));

        return new ResponseEntity<>(body, status);
    }

    protected HttpStatus getStatus(HttpServletRequest request) {
        var statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        }
        catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }


    protected String getErrorMessage(HttpServletRequest request) {
        var message = request.getAttribute(WebUtils.ERROR_MESSAGE_ATTRIBUTE);
        return Objects.requireNonNullElse(
                String.valueOf(message),
                "что-то пошло не так =("
        );
    }
}
