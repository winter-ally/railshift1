-- Run AFTER schema.sql on Supabase.
-- Supabase publishes every table in "public" through a REST API that anyone holding the
-- (public) anon key can call. RailShift's apps talk only to the FastAPI backend, so we turn on
-- Row Level Security with NO policies: the public API then sees nothing.
-- FastAPI connects as the database owner, which bypasses RLS, so it is unaffected.
-- Safe to re-run.
DO $$
DECLARE t record;
BEGIN
    FOR t IN SELECT tablename FROM pg_tables WHERE schemaname = 'public' LOOP
        EXECUTE format('ALTER TABLE public.%I ENABLE ROW LEVEL SECURITY', t.tablename);
    END LOOP;
END $$;
