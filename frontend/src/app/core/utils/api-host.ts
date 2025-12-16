const DEFAULT_HOST = 'localhost';
const DEFAULT_PROTOCOL = 'http:';

function getRuntimeLocation(): Location | undefined {
  if (typeof globalThis === 'undefined') {
    return undefined;
  }

  const runtime = globalThis as typeof globalThis & {
    location?: Location;
    window?: { location?: Location };
  };

  return runtime.location ?? runtime.window?.location;
}

export function resolveApiHost(): string {
  const location = getRuntimeLocation();
  return location?.hostname ?? DEFAULT_HOST;
}

export function resolveApiProtocol(): string {
  const location = getRuntimeLocation();
  return location?.protocol ?? DEFAULT_PROTOCOL;
}

export function resolveApiBase(port: number): string {
  const protocol = resolveApiProtocol().replace(/:$/, '');
  const host = resolveApiHost();
  return `${protocol}://${host}:${port}`;
}
