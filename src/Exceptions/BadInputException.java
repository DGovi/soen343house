package Exceptions;

import View.DashboardDriver;

public class BadInputException extends Exception{

    public BadInputException(String inputType) {
        System.err.println("ERROR: Bad " + inputType + " input!");
    }
}
