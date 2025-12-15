const DEFAULT_HOST = 'localhost';
const DEFAULT_PROTOCOL = 'http:';

export function resolveApiHost(): string {
  if (typeof window !== 'undefined' && window.location?.hostname) {
    return window.location.hostname;
  }
  return DEFAULT_HOST;
}

export function resolveApiProtocol(): string {
  if (typeof window !== 'undefined' && window.location?.protocol) {
    return window.location.protocol;
  }
  return DEFAULT_PROTOCOL;
}

export function resolveApiBase(port: number): string {
  const protocol = resolveApiProtocol().replace(/:$/, '');
  const host = resolveApiHost();
  return `${protocol}://${host}:${port}`;
}
