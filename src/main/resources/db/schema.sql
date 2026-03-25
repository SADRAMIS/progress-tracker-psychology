-- =====================================================
-- Схема базы данных для PostgreSQL
-- =====================================================

-- Создание ENUM типов (вместо ENUM в таблицах)
CREATE TYPE goal_status AS ENUM ('ACTIVE', 'COMPLETED', 'ABANDONED');
CREATE TYPE question_difficulty AS ENUM ('EASY', 'MEDIUM', 'HARD');
CREATE TYPE recommendation_type AS ENUM ('MOTIVATION', 'FOCUS', 'CONSISTENCY', 'LEARNING_PATH');
CREATE TYPE priority_level AS ENUM ('LOW', 'MEDIUM', 'HIGH');

-- Таблица users
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    level INT DEFAULT 1,
    xp_total INT DEFAULT 0,
    motivation_score DECIMAL(5,2) DEFAULT 50.00,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица goals
CREATE TABLE goals (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    xp_target INT NOT NULL,
    current_xp INT DEFAULT 0,
    status goal_status DEFAULT 'ACTIVE',
    deadline DATE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Таблица questions
CREATE TABLE questions (
    id BIGSERIAL PRIMARY KEY,
    question_number INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    difficulty question_difficulty NOT NULL,
    xp_value INT NOT NULL,
    theory_content TEXT,
    code_example TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица answers
CREATE TABLE answers (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE,
    attempts INT DEFAULT 0,
    time_spent_minutes INT,
    level_completed INT DEFAULT 0,
    notes TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id)
);

-- Таблица psychometric_scores
CREATE TABLE psychometric_scores (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    motivation DECIMAL(5,2),   -- 0-100
    confidence DECIMAL(5,2),   -- 0-100
    resilience DECIMAL(5,2),   -- 0-100
    focus DECIMAL(5,2),        -- 0-100
    consistency DECIMAL(5,2),  -- 0-100
    overall_score DECIMAL(5,2),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
COMMENT ON COLUMN psychometric_scores.motivation IS '0-100: How motivated is user';
COMMENT ON COLUMN psychometric_scores.confidence IS '0-100: How confident in knowledge';
COMMENT ON COLUMN psychometric_scores.resilience IS '0-100: Ability to bounce back after mistakes';
COMMENT ON COLUMN psychometric_scores.focus IS '0-100: Concentration level';
COMMENT ON COLUMN psychometric_scores.consistency IS '0-100: Study consistency';

-- Таблица progress_records
CREATE TABLE progress_records (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    date DATE NOT NULL,
    xp_gained INT DEFAULT 0,
    hours_studied DECIMAL(4,2) DEFAULT 0,
    questions_solved INT DEFAULT 0,
    percentage_correct DECIMAL(5,2) DEFAULT 0,
    streak_days INT DEFAULT 0,
    mood_rating INT,   -- 1-5 scale
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Таблица error_patterns
CREATE TABLE error_patterns (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    error_type VARCHAR(100) NOT NULL,
    frequency INT DEFAULT 1,
    weak_topics VARCHAR(255),
    description TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Таблица recommendations
CREATE TABLE recommendations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    recommendation_type recommendation_type NOT NULL,
    content TEXT NOT NULL,
    priority priority_level DEFAULT 'MEDIUM',
    is_read BOOLEAN DEFAULT FALSE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Создание индексов
CREATE INDEX idx_answers_user_id ON answers(user_id);
CREATE INDEX idx_answers_question_id ON answers(question_id);
CREATE INDEX idx_progress_records_created_date ON progress_records(created_date);
CREATE INDEX idx_psychometric_scores_user ON psychometric_scores(user_id);
CREATE INDEX idx_users_email ON users(email);