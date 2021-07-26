package uk.co.hsbc.recruitmenttest.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import uk.co.hsbc.recruitmenttest.model.ErrorMessage;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.ServiceUnavailableException;

@ControllerAdvice
public class RecruitmentTestExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecruitmentTestExceptionHandler.class);
    private static final String EXCEPTION_STR = "Handled exception: {}";

    @ExceptionHandler({InternalServerErrorException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorMessage handleInternalServerError(Exception e) {
        LOGGER.error(EXCEPTION_STR, e.getMessage());
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler({ServiceUnavailableException.class})
    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    @ResponseBody
    public ErrorMessage handleServiceUnavailableException(Exception e) {
        LOGGER.error(EXCEPTION_STR, e.getMessage());
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorMessage handleBadRequestException(Exception e) {
        LOGGER.error(EXCEPTION_STR, e.getMessage());
        return new ErrorMessage(e.getMessage());
    }
}
