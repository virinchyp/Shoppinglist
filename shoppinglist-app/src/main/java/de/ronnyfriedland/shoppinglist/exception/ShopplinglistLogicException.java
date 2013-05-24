package de.ronnyfriedland.shoppinglist.exception;

public class ShopplinglistLogicException extends Throwable {

    private static final long serialVersionUID = -8525290186840124014L;

    public ShopplinglistLogicException(final Throwable cause) {
        super(cause);
    }

    public ShopplinglistLogicException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
