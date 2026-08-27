package org.michelin.filemanager.catalog;

import org.michelin.filemanager.exception.ValidationException;

public class CatalogValidationException extends ValidationException {
    public CatalogValidationException(String message) { super(message); }
    public CatalogValidationException(String message, Throwable cause) { super(message, cause); }
}
