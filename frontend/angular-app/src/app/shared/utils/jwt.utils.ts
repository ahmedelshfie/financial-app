export interface JwtPayload {
  exp?: number;
  sub?: string;
  username?: string;
  [key: string]: unknown;
}

const JWT_PARTS = 3;

export function parseJwt(token: string): JwtPayload | null {
  if (!token) {
    return null;
  }

  const segments = token.split('.');
  if (segments.length !== JWT_PARTS) {
    return null;
  }

  try {
    const payload = atob(segments[1]);
    return JSON.parse(payload) as JwtPayload;
  } catch {
    return null;
  }
}

export function isJwtExpired(token: string, nowInSeconds = Math.floor(Date.now() / 1000)): boolean {
  const payload = parseJwt(token);
  if (!payload?.exp) {
    return false;
  }

  return payload.exp <= nowInSeconds;
}
