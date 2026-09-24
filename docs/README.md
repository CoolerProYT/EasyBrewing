# Easy Brewing wiki

VitePress site for the mod. Recipes, upgrade values, config options and item textures are read from the mod itself, so regenerate the mod's data before building when it changes.

```bash
./gradlew :neoforge:runData   # from the repository root, when mod recipes changed
cd docs
npm install
npm run dev                   # syncs data, then serves http://localhost:5173
npm run build                 # syncs data, then builds to .vitepress/dist
```

`npm run sync` (run automatically by `dev` and `build`) writes `.vitepress/data/data.json` and copies the station's GUI texture to `public/gui/`. Both are git-ignored. It reads:

- recipes from the datagen output in `common/src/generated/resources`
- item names from `en_us.json`
- upgrade strengths from `CommonClass.java`
- config options, defaults and ranges from `NeoForgeCommonConfig.java`

Item icons are not copied. Mod items load from `https://storage.googleapis.com/coolerpromc/textures/easybrewing/`, and vanilla items and potions from the hosted renders next to them, set in `.vitepress/theme/easybrewing.ts`. When you add an item, upload its texture there (1024x1024, nearest-neighbor) before syncing. The station has no flat item texture, so its icon is rendered from the block model with `scripts/render-block-icon.py`. The source render is kept in `public/icons/`.

## Vanilla brewing recipes

The [Brewing recipes](guide/brewing-recipes.md) page uses a snapshot of vanilla's brewing recipes and potion names in `.vitepress/vanilla/brewing.json`. It is committed, because the Minecraft jar isn't available in CI. After porting to a new Minecraft version, refresh it from any Minecraft jar that contains `data/` and `assets/`, then update the version named on that page:

```bash
npm run sync:vanilla -- ~/.gradle/caches/fabric-loom/minecraftMaven/net/minecraft/minecraft-merged-deobf/<version>/minecraft-merged-deobf-<version>.jar
```

## Versions

The site root documents the current version (Minecraft 26.3). Pages under `old/` document Minecraft 1.20.1 – 26.2, picked from the version menu (`.vitepress/theme/version.ts`, `components/VersionSwitcher.vue`). The station, upgrades, recipes and config are the same in every version, so most `old/` pages include the current page with `<!--@include: ...-->`. Keep those shared pages version-neutral. Only these pages are written separately for older versions:

- `getting-started.md`: loaders per Minecraft version (it includes the "first brew" region of the current page)
- `brewing-recipes.md`: recipes come from the mod loader instead of datapacks, and 1.20.1 lacks the 1.21 potions
- `compat.md`: JEI, REI, Create and Cobblemon differ per version and loader
- `config.md` and `faq.md`: Cobblemon and datapack details

Before 26.3, vanilla's brewing recipes are built in code by `PotionBrewing`. Versions 1.21 – 26.2 have the same recipes as the 26.3 snapshot, so the older pages reuse it and mark the 1.21 potions for 1.20.1.
