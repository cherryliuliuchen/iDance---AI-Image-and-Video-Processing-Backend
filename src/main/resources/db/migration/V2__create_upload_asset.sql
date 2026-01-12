CREATE TABLE IF NOT EXISTS upload_asset (
                                            asset_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id UUID REFERENCES app_user(user_id) ON DELETE SET NULL,

    provider VARCHAR(50) NOT NULL DEFAULT 'dashscope',
    oss_url TEXT NOT NULL,

    file_name VARCHAR(255),
    content_type VARCHAR(100),
    file_size_bytes BIGINT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
    );

CREATE INDEX IF NOT EXISTS idx_upload_asset_user_id
    ON upload_asset(user_id);
