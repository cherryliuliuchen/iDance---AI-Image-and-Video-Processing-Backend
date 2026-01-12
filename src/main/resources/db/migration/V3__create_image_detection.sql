CREATE TABLE IF NOT EXISTS image_detection (
                                               detection_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    asset_id UUID NOT NULL
    REFERENCES upload_asset(asset_id)
    ON DELETE CASCADE,

    model VARCHAR(100) NOT NULL DEFAULT 'animate-anyone-detect-gen2',

    check_pass BOOLEAN NOT NULL,
    reason TEXT,
    request_id VARCHAR(100),

    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
    );

CREATE INDEX IF NOT EXISTS idx_image_detection_asset_id
    ON image_detection(asset_id);

CREATE INDEX IF NOT EXISTS idx_image_detection_created_at
    ON image_detection(created_at);
