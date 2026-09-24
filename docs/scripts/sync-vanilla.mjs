// Snapshots vanilla's brewing recipes and potion names from a Minecraft jar into .vitepress/vanilla/brewing.json.
// The jar is not available in CI, so the snapshot is committed. Re-run after porting to a new Minecraft version:
//
//   npm run sync:vanilla -- <path to a Minecraft jar containing data/ and assets/>
//
// e.g. ~/.gradle/caches/fabric-loom/minecraftMaven/net/minecraft/minecraft-merged-deobf/<version>/minecraft-merged-deobf-<version>.jar
import { mkdirSync, readFileSync, writeFileSync } from 'node:fs'
import { dirname, join } from 'node:path'
import { fileURLToPath } from 'node:url'
import { inflateRawSync } from 'node:zlib'

const docs = join(dirname(fileURLToPath(import.meta.url)), '..')
const jarPath = process.argv[2]
if (!jarPath) {
  console.error('Usage: npm run sync:vanilla -- <path to minecraft jar>')
  process.exit(1)
}

/** Reads the entries whose names pass `filter` from a zip file. */
function readZip(file, filter) {
  const buf = readFileSync(file)
  let eocd = buf.length - 22
  while (eocd >= 0 && buf.readUInt32LE(eocd) !== 0x06054b50) eocd--
  if (eocd < 0) throw new Error(`${file} is not a zip file`)
  const entries = new Map()
  let offset = buf.readUInt32LE(eocd + 16)
  while (buf.readUInt32LE(offset) === 0x02014b50) {
    const method = buf.readUInt16LE(offset + 10)
    const size = buf.readUInt32LE(offset + 20)
    const nameLength = buf.readUInt16LE(offset + 28)
    const extraLength = buf.readUInt16LE(offset + 30)
    const commentLength = buf.readUInt16LE(offset + 32)
    const local = buf.readUInt32LE(offset + 42)
    const name = buf.toString('utf8', offset + 46, offset + 46 + nameLength)
    if (filter(name)) {
      const start = local + 30 + buf.readUInt16LE(local + 26) + buf.readUInt16LE(local + 28)
      const raw = buf.subarray(start, start + size)
      entries.set(name, (method === 8 ? inflateRawSync(raw) : raw).toString('utf8'))
    }
    offset += 46 + nameLength + extraLength + commentLength
  }
  return entries
}

const RECIPES = 'data/minecraft/recipe/brewing/'
const LANG = 'assets/minecraft/lang/en_us.json'
const files = readZip(jarPath, (name) => name === LANG || (name.startsWith(RECIPES) && name.endsWith('.json')))
if (!files.has(LANG)) throw new Error(`${jarPath} has no ${LANG}`)
const lang = JSON.parse(files.get(LANG))

const path = (id) => id.replace(/^minecraft:/, '')
const potionForm = (item, potion) => `minecraft:${path(item)}/${path(potion)}`

const recipes = [...files]
  .filter(([name]) => name.startsWith(RECIPES))
  .map(([, text]) => JSON.parse(text))
  .filter((json) => json.type === 'minecraft:brewing')
  .map((json) => ({
    input: potionForm(json.input.item, json.input.potion_contents.potions),
    reagent: json.reagent.item,
    output: potionForm(json.output.id, json.output.components['minecraft:potion_contents'].potion),
  }))
  .sort((a, b) => a.input.localeCompare(b.input) || a.reagent.localeCompare(b.reagent))

// Long and strong variants share their base potion's name in game; the wiki marks them so they can be told apart.
const names = {}
for (const id of new Set(recipes.flatMap((r) => [r.input, r.output]))) {
  const [item, potion] = path(id).split('/')
  const base = potion.replace(/^(long|strong)_/, '')
  const name = lang[`item.minecraft.${item}.effect.${base}`] ?? id
  names[id] = potion.startsWith('long_') ? `${name} (long)` : potion.startsWith('strong_') ? `${name} II` : name
}
for (const reagent of new Set(recipes.map((r) => r.reagent))) {
  names[reagent] = lang[`item.minecraft.${path(reagent)}`] ?? lang[`block.minecraft.${path(reagent)}`] ?? reagent
}

mkdirSync(join(docs, '.vitepress/vanilla'), { recursive: true })
writeFileSync(join(docs, '.vitepress/vanilla/brewing.json'), JSON.stringify({ names, recipes }, null, 2) + '\n')
console.log(`Snapshotted ${recipes.length} vanilla brewing recipes.`)
