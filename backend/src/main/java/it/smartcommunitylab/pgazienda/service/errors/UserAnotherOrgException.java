package it.smartcommunitylab.pgazienda.service.errors;

public class UserAnotherOrgException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserAnotherOrgException() {
        super("User exists in another company!");
    }

}
