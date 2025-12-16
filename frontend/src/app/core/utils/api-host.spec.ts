import { resolveApiBase, resolveApiHost, resolveApiProtocol, __setMockLocation } from './api-host';

describe('api host utilities', () => {
  afterEach(() => __setMockLocation());

  it('should fall back to defaults when no window available', () => {
    expect(resolveApiHost()).toBe('localhost');
    expect(resolveApiProtocol()).toBe('http:');
    expect(resolveApiBase(8080)).toBe('http://localhost:8080');
  });

  it('should read values from runtime location', () => {
    __setMockLocation({ hostname: 'app.local', protocol: 'https:' } as Location);

    expect(resolveApiHost()).toBe('app.local');
    expect(resolveApiProtocol()).toBe('https:');
    expect(resolveApiBase(4200)).toBe('https://app.local:4200');
  });
});
