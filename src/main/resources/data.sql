-- 1. Тестовый вопрос
INSERT INTO questions (question_number, title, category, difficulty, xp_value)
VALUES (999, 'Test Question for Tests', 'BASICJAVA', 'EASY', 50);

-- 2. Реальные вопросы для практики
INSERT INTO questions (question_number, title, category, difficulty, xp_value)
VALUES
(1, 'Spring Bean и как он создаётся', 'SPRINGBOOT', 'MEDIUM', 100),
(2, 'Почему Service слой отдельно от Controller', 'SPRINGBOOT', 'EASY', 50),
(3, 'Способы внедрения зависимостей в Spring', 'SPRINGBOOT', 'MEDIUM', 100),
(4, 'Как работает @Transactional', 'SPRINGBOOT', 'MEDIUM', 100),
(5, 'Что такое JPA и чем отличается от JDBC', 'SPRINGBOOT', 'HARD', 150),
(6, 'Что такое N+1 problem и как её избежать', 'SPRINGBOOT', 'HARD', 150),
(7, 'Что произойдёт при исключении в @Transactional', 'SPRINGBOOT', 'MEDIUM', 100),
(8, 'Как настроить CORS в Spring Boot', 'SPRINGBOOT', 'EASY', 50),
(9, 'HTTP протокол', 'BASICJAVA', 'EASY', 50),
(10, 'Для чего нужна JVM и чем она отличается от JRE', 'BASICJAVA', 'MEDIUM', 100);