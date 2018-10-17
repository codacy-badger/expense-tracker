CREATE TABLE reporter_v1.day_report (
  id UUID PRIMARY KEY,
  report_date DATE,
  data JSONB
);