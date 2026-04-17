import { isJwtExpired, parseJwt } from './jwt.utils';

describe('jwt utils', () => {
  it('returns null when token format is invalid', () => {
    expect(parseJwt('invalid-token')).toBeNull();
  });

  it('parses a valid jwt payload', () => {
    const payload = btoa(JSON.stringify({ sub: 'demo', exp: 9999999999 }));
    const token = `header.${payload}.signature`;

    expect(parseJwt(token)?.sub).toBe('demo');
  });

  it('detects expiration', () => {
    const payload = btoa(JSON.stringify({ exp: 5 }));
    const token = `header.${payload}.signature`;

    expect(isJwtExpired(token, 10)).toBe(true);
    expect(isJwtExpired(token, 1)).toBe(false);
  });
});
