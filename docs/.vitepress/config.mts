import { defineConfig } from 'vitepress'

// GitHub Pages serves a project site from /<repository>/. For a custom domain or a user site, build with DOCS_BASE=/.
const base = process.env.DOCS_BASE ?? '/EasyBrewing/'
const STATION_ICON = 'https://storage.googleapis.com/coolerpromc/textures/easybrewing/item_brewing_station.png'

/** Both versions have the same pages; the older one lives under /old/. */
function sidebar(prefix: string) {
  return [
    {
      text: 'Guide',
      items: [
        { text: 'Getting started', link: `${prefix}guide/getting-started` },
        { text: 'Item Brewing Station', link: `${prefix}guide/brewing-station` },
        { text: 'Upgrades', link: `${prefix}guide/upgrades` },
        { text: 'Automation', link: `${prefix}guide/automation` },
        { text: 'Brewing recipes', link: `${prefix}guide/brewing-recipes` },
        { text: 'Crafting', link: `${prefix}guide/crafting` },
        { text: 'Other mods', link: `${prefix}guide/compat` },
      ],
    },
    { text: 'Config', link: `${prefix}config` },
    { text: 'FAQ', link: `${prefix}faq` },
  ]
}

export default defineConfig({
  title: 'Easy Brewing',
  description: 'An upgradable brewing station for Minecraft 1.20.1 to 26.3: faster brews, bigger batches and hopper automation.',
  base,
  cleanUrls: true,
  srcExclude: ['README.md', 'scripts/**'],
  head: [['link', { rel: 'icon', type: 'image/png', href: STATION_ICON }]],
  themeConfig: {
    logo: { src: STATION_ICON, alt: '' },
    nav: [
      { text: 'Guide', link: '/guide/getting-started', activeMatch: '^/guide/' },
      { text: 'Upgrades', link: '/guide/upgrades' },
      { text: 'Config', link: '/config' },
      { component: 'VersionSwitcher' },
    ],
    sidebar: {
      '/old/': sidebar('/old/'),
      // The current version lives at the root. VitePress picks the deepest matching key, so the folder above wins.
      '/': sidebar('/'),
    },
    search: { provider: 'local' },
    outline: { level: [2, 3] },
    socialLinks: [{ icon: 'github', link: 'https://github.com/CoolerProYT/EasyBrewing' }],
    footer: { message: 'Released under the MIT License.' },
  },
})
