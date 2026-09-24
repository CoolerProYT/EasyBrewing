/**
 * The documented versions. Each lives under its own folder; the current one is the site root.
 * A new entry is only needed when the mod's behaviour changes, not for every Minecraft port.
 * Older versions register brewing recipes through the mod loader instead of datapacks, and have different mod integrations.
 */
export const VERSIONS = [
  { key: 'current', label: 'Minecraft 26.3', path: '/' },
  { key: 'old', label: 'Minecraft 1.20.1 – 26.2', path: '/old/' },
] as const

export type Version = (typeof VERSIONS)[number]

// The most specific folder wins, so the root only matches pages outside the other folders.
const byDepth = [...VERSIONS].sort((a, b) => b.path.length - a.path.length)

export function versionOf(relativePath: string): Version {
  return byDepth.find((v) => `/${relativePath}`.startsWith(v.path)) ?? VERSIONS[0]
}
