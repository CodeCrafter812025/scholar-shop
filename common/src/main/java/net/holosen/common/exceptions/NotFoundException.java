package net.holosen.common.exceptions;

public class NotFoundException extends Exception {
    public NotFoundException() {
        super("Data Not Found!");
    }
}
