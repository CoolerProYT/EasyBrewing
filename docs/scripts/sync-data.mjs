// Pulls wiki data straight from the mod so the docs never drift from the game:
// datagen output (recipes), lang, upgrade values from CommonClass, config options from the NeoForge config spec,
// and the mod's own textures. Run `./gradlew :neoforge:runData` first when the mod's recipes change.
import { copyFileSync, existsSync, mkdirSync, readdirSync, readFileSync, writeFileSync } from 'node:fs'
import { basename, dirname, join } from 'node:path'
import { fileURLToPath } from 'node:url'

const docs = join(dirname(fileURLToPath(import.meta.url)), '..')
const root = join(docs, '..')
const generated = join(root, 'common/src/generated/resources')
const assets = join(root, 'common/src/main/resources/assets/easybrewing')
const java = join(root, 'common/src/main/java/com/coolerpromc/easybrewing')
const data = join(generated, 'data/easybrewing')

if (!existsSync(generated)) {
  console.error(`No datagen output at ${generated}. Run ./gradlew :neoforge:runData first.`)
  process.exit(1)
}

const readJson = (file) => JSON.parse(readFileSync(file, 'utf8'))
const jsonFiles = (dir) => (existsSync(dir) ? readdirSync(dir).filter((f) => f.endsWith('.json')).sort() : [])

const lang = readJson(join(assets, 'lang/en_us.json'))

// Item names for mod items; vanilla names are prettified on the page.
const names = {}
for (const [key, value] of Object.entries(lang)) {
  const match = key.match(/^(item|block)\.easybrewing\.([a-z0-9_]+)$/)
  if (match) names[`easybrewing:${match[2]}`] = value
}

// Mod textures are hosted at 1024x1024 alongside the vanilla renders; upload new ones there before syncing.
// The station has no flat item texture, so its icon is rendered from the block model (source kept in public/icons/).
const HOSTED_TEXTURES = 'https://storage.googleapis.com/coolerpromc/textures/easybrewing'
const pngNames = (dir) => (existsSync(dir) ? readdirSync(dir).filter((f) => f.endsWith('.png')).map((f) => basename(f, '.png')) : [])
const textures = {}
for (const name of [...pngNames(join(assets, 'textures/item')), ...pngNames(join(docs, 'public/icons'))]) {
  textures[`easybrewing:${name}`] = `${HOSTED_TEXTURES}/${name}.png`
}
mkdirSync(join(docs, 'public/gui'), { recursive: true })
copyFileSync(join(assets, 'textures/gui/item_brewing_station.png'), join(docs, 'public/gui/item_brewing_station.png'))

const ingredient = (value) => {
  if (typeof value === 'string') return value
  if (Array.isArray(value)) return ingredient(value[0])
  if (value?.item) return value.item
  if (value?.tag) return `#${value.tag}`
  return '?'
}

const recipes = jsonFiles(join(data, 'recipe')).map((file) => {
  const json = readJson(join(data, 'recipe', file))
  const recipe = { id: `easybrewing:${basename(file, '.json')}`, type: json.type, result: { id: json.result?.id, count: json.result?.count ?? 1 } }
  if (json.type === 'minecraft:crafting_shaped') {
    recipe.pattern = json.pattern
    recipe.key = Object.fromEntries(Object.entries(json.key).map(([symbol, value]) => [symbol, ingredient(value)]))
  } else if (json.type === 'minecraft:crafting_shapeless') {
    recipe.ingredients = json.ingredients.map(ingredient)
  }
  return recipe
})

// Upgrade strengths are constructor arguments in CommonClass, e.g. `new SpeedUpgradeItem(p, 1.25f)`.
const commonClass = readFileSync(join(java, 'CommonClass.java'), 'utf8')
const upgrades = (type) =>
  [...commonClass.matchAll(new RegExp(`registerItem\\("([a-z0-9_]+)",\\s*\\w+\\s*->\\s*new ${type}\\(\\w+,\\s*([\\d.]+)f?\\)`, 'g'))].map(
    ([, name, value]) => ({ id: `easybrewing:${name}`, value: Number(value) }),
  )
const speedUpgrades = upgrades('SpeedUpgradeItem')
const amountUpgrades = upgrades('AmountUpgradeItem')

// Config options, read from the NeoForge spec (the Fabric loader writes the same file with the same defaults).
const spec = readFileSync(join(root, 'neoforge/src/main/java/com/coolerpromc/easybrewing/config/NeoForgeCommonConfig.java'), 'utf8')
const config = []
let section = ''
for (const line of spec.split('\n')) {
  const push = line.match(/builder\.push\("([^"]+)"\)/)
  if (push) section = push[1]
  const define = line.match(/\.define(InRange)?\("([^"]+)",\s*([^,)]+)(?:,\s*(-?\d+),\s*(-?\d+))?\)/)
  if (!define) continue
  const [, , key, def, min, max] = define
  config.push({
    section,
    key,
    default: def === 'true' || def === 'false' ? def === 'true' : Number(def),
    min: min === undefined ? null : Number(min),
    max: max === undefined ? null : Number(max),
    comment: line.match(/\.comment\("((?:[^"\\]|\\.)*)"\)/)?.[1] ?? '',
    restart: line.includes('.gameRestart()'),
  })
}

mkdirSync(join(docs, '.vitepress/data'), { recursive: true })
writeFileSync(
  join(docs, '.vitepress/data/data.json'),
  JSON.stringify({ names, textures, recipes, speedUpgrades, amountUpgrades, config }, null, 2),
)
console.log(
  `Synced ${recipes.length} recipes, ${speedUpgrades.length + amountUpgrades.length} upgrades, ${config.length} config options, ${Object.keys(textures).length} textures.`,
)
