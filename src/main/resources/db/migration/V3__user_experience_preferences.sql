CREATE TABLE gabgym.user_experience_preferences (
    user_id bigint PRIMARY KEY REFERENCES gabgym.users(id) ON DELETE CASCADE,
    experience_mode varchar(16) NOT NULL DEFAULT 'normal',
    food_enabled boolean NOT NULL DEFAULT true,
    workout_enabled boolean NOT NULL DEFAULT true,
    weight_enabled boolean NOT NULL DEFAULT true,
    coach_enabled boolean NOT NULL DEFAULT true,
    language_code varchar(5) NOT NULL DEFAULT 'pt',
    completed boolean NOT NULL DEFAULT false,
    updated_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT user_experience_mode CHECK (experience_mode IN ('simple','normal','complete')),
    CONSTRAINT user_experience_language CHECK (language_code IN ('pt','en','es','fr'))
);
