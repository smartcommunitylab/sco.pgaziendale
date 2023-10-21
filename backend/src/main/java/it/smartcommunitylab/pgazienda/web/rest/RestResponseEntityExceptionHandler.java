/*******************************************************************************
 * Copyright 2015 Fondazione Bruno Kessler
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 ******************************************************************************/

package it.smartcommunitylab.pgazienda.web.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import it.smartcommunitylab.pgazienda.service.errors.ImportDataException;
import it.smartcommunitylab.pgazienda.service.errors.InconsistentDataException;
import it.smartcommunitylab.pgazienda.service.errors.InvalidPasswordException;
import it.smartcommunitylab.pgazienda.service.errors.RepeatingSubscriptionException;
import it.smartcommunitylab.pgazienda.service.errors.UserAnotherOrgException;
import it.smartcommunitylab.pgazienda.service.errors.UsernameAlreadyUsedException;
import it.smartcommunitylab.pgazienda.web.rest.errors.AccountResourceException;
import it.smartcommunitylab.pgazienda.web.rest.errors.BadRequestAlertException;

/**
 * @author raman
 *
 */
@ControllerAdvice
public class RestResponseEntityExceptionHandler 
  extends ResponseEntityExceptionHandler {


    @ExceptionHandler(InvalidPasswordException.class)
    protected ResponseEntity<Object> handleInvalidPwd(
      Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, new ErrorMsg("Invalid password","INVALID_PASSWORD"), 
          new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(UsernameAlreadyUsedException.class)
    protected ResponseEntity<Object> handleLoginInUse(
      Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, new ErrorMsg("Login already in use", "LOGIN_IN_USE"), 
          new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
    @ExceptionHandler(UserAnotherOrgException.class)
    protected ResponseEntity<Object> handleLoginAnotherOrg(
      Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, new ErrorMsg("User exists in another company", "USER_ANOTHER_COMPANY"), 
          new HttpHeaders(), HttpStatus.CONFLICT, request);
    }
    @ExceptionHandler(AccountResourceException.class)
    protected ResponseEntity<Object> handleAccountError(
      Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, new ErrorMsg("Account data exception", "ACCONT_EXCEPTION"), 
          new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }    
    @ExceptionHandler(BadRequestAlertException.class)
    protected ResponseEntity<Object> handleBadRequest(
      Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, new ErrorMsg("Incorrect data: " + ex.getMessage(), "BAD_REQUEST_DATA"), 
          new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(RepeatingSubscriptionException.class)
    protected ResponseEntity<Object> handleRepeatingSubscription(
      Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, new ErrorMsg("Repeating subscription", "REPEATING_SUBSCRIPTION"), 
          new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(InconsistentDataException.class)
    protected ResponseEntity<Object> handleIllegalArgument(
    		InconsistentDataException ex, WebRequest request) {
        return handleExceptionInternal(ex, new ErrorMsg("Incorrect data: " + ex.getMessage(), ex.getDetails()), 
          new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ImportDataException.class)
    protected ResponseEntity<Object> handleImportError(
    		ImportDataException ex, WebRequest request) {
    	Map<String, Object> errorData =new HashMap<>();
    	errorData.put("col", ex.getCol());
    	errorData.put("row", ex.getRow());
        return handleExceptionInternal(ex, new ErrorMsg("Incorrect imported data row "+ex.getRow()+", col " + ex.getCol(), "INVALID_IMPORT_DATA", errorData), 
          new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<Object> handleSecurity(
      Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, new ErrorMsg("Incorrect credentials", "BAD_CREDENTIALS"), 
          new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(SecurityException.class)
    protected ResponseEntity<Object> handleAccess(
      Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, new ErrorMsg("Insufficient rights", "INSUFFICIENT_RIGHTS"), 
          new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<Object> handleAccessDenied(
      Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, new ErrorMsg("Insufficient rights", "INSUFFICIENT_RIGHTS"), 
          new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleGeneric(
      Exception ex, WebRequest request) {
    	ex.printStackTrace();
        return handleExceptionInternal(ex, new ErrorMsg("Generic error", "GENERIC_ERROR"), 
          new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
    
    
    public static class ErrorMsg {
    	public String message;
    	public String type;
    	public Map<String, Object> errorData;

		public ErrorMsg(String message, String type) {
			super();
			this.message = message;
			this.type = type;
		}
		public ErrorMsg(String message, String type, Map<String, Object> errorData) {
			super();
			this.message = message;
			this.errorData = errorData;
      this.type = type;
		}

		public ErrorMsg() {
		}
    }
}