package org.michelin.filemanager.parser;

import org.michelin.filemanager.exception.ValidationException;

public class PositionalParseException extends ValidationException {
    public PositionalParseException(String message) { super(message); }
    public PositionalParseException(String message, Throwable cause) { super(message, cause); }
}
