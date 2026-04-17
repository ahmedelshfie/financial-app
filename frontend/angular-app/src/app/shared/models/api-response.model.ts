export interface ApiErrorResponse {
  timestamp?: string;
  status?: number;
  error?: string;
  message?: string;
  path?: string;
  errorCode?: string;
  category?: string;
  traceId?: string;
  details?: Record<string, string>;
}
