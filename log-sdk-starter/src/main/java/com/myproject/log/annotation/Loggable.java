package com.myproject.log.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation pour marquer les méthodes dont l'exécution doit être tracée
 * et envoyée vers le collecteur de logs gRPC (log-ingestor).
 */
@Target(ElementType.METHOD) // On ne peut l'utiliser que sur des méthodes
@Retention(RetentionPolicy.RUNTIME) // Indispensable pour que l'Aspect puisse la lire à l'exécution
public @interface Loggable {
    /**
     * Le niveau de log (INFO, WARN, ERROR, etc.)
     * Par défaut à INFO.
     */
    String level() default "INFO";

    /**
     * Permet de catégoriser le log (ex: "DATABASE", "EXTERNAL_API", "AUTH")
     */
    String category() default "GENERAL";
}
