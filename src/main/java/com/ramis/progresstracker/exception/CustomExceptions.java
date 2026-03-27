package com.ramis.progresstracker.exception;

public final class CustomExceptions {

    private CustomExceptions() {
        // utility class
    }

    public static class ProgressTrackerException extends RuntimeException {

        public ProgressTrackerException(String message) {
            super(message);
        }

        public ProgressTrackerException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Сущность не найдена (User, Goal, Question, etc.).
     */
    public static class ResourceNotFoundException extends ProgressTrackerException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * Ошибка валидации входных данных (невалидный XP, пустой title цели и т.п.).
     */
    public static class ValidationException extends ProgressTrackerException {
        public ValidationException(String message) {
            super(message);
        }
    }

    /**
     * Нарушение бизнес-правил (например, попытка завершить уже завершённую цель).
     */
    public static class BusinessRuleException extends ProgressTrackerException {
        public BusinessRuleException(String message) {
            super(message);
        }
    }
}
