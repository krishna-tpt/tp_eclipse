package org.michelin.filemanager.mapper;

import org.michelin.filemanager.exception.ValidationException;

public class FieldMappingException extends ValidationException {
    public FieldMappingException(String message) { super(message); }
    public FieldMappingException(String message, Throwable cause) { super(message, cause); }
}
