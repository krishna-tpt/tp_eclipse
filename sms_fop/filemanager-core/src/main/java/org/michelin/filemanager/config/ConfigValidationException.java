package org.michelin.filemanager.config;

import org.michelin.filemanager.exception.ValidationException;

public class ConfigValidationException extends ValidationException {
    public ConfigValidationException(String message) { super(message); }
    public ConfigValidationException(String message, Throwable cause) { super(message, cause); }
}
