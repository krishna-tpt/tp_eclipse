package org.michelin.filemanager.ingest;

import org.michelin.filemanager.exception.ValidationException;

public class EnvelopeValidationException extends ValidationException {
    public EnvelopeValidationException(String message) { super(message); }
    public EnvelopeValidationException(String message, Throwable cause) { super(message, cause); }
}
