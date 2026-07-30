package com.jeanfrancis.backendapispringboot.exception;

/**
 * ====================================================================================
 * ROLE : EXCEPTION MÉTIER TRANSVERSE (BUSINESS LOGIC EXCEPTION)
 * RESPONSIBILITY : Intercepte les violations de règles de gestion de l'ERP
 * (ex: tentative d'insertion d'un doublon, rupture de stock, règle financière enfreinte).
 * ARCHITECTURE : Hérite de RuntimeException pour s'intégrer au GlobalExceptionHandler.
 * ====================================================================================
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
