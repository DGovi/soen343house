package Exceptions;

import View.DashboardDriver;

/**
 * Exception for bad input in the UI.
 */
public class BadInputException extends Exception{

    /**
     * Constructor for BadInputException>
     * @param inputType the input type that caused the exception
     */
    public BadInputException(String inputType) {
        System.err.println("ERROR: Bad " + inputType + " input!");
    }
}
