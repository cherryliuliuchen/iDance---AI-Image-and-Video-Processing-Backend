CREATE TABLE IF NOT EXISTS video_generation (
                                                generation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id UUID REFERENCES app_user(user_id)
    ON DELETE SET NULL,

    detection_id UUID NOT NULL
    REFERENCES image_detection(detection_id)
    ON DELETE RESTRICT,

    template_id VARCHAR(200) NOT NULL,
    model VARCHAR(100) NOT NULL DEFAULT 'animate-anyone-gen2',

    task_id VARCHAR(100) UNIQUE,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    -- PENDING / RUNNING / SUCCEEDED / FAILED

    video_url TEXT,
    request_id VARCHAR(100),

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
    );

CREATE INDEX IF NOT EXISTS idx_video_generation_user_id
    ON video_generation(user_id);

CREATE INDEX IF NOT EXISTS idx_video_generation_detection_id
    ON video_generation(detection_id);

CREATE INDEX IF NOT EXISTS idx_video_generation_status
    ON video_generation(status);
