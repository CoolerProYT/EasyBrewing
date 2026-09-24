// @ts-ignore
import raw from '../data/data.json'
// @ts-ignore
import vanillaRaw from '../vanilla/brewing.json'

export interface Recipe {
  id: string
  type: string
  result: { id: string; count: number }
  pattern?: string[]
  key?: Record<string, string>
  ingredients?: string[]
}

export interface Upgrade {
  id: string
  /** Speed multiplier per item for speed upgrades, extra potions per item for amount upgrades. */
  value: number
}

export interface ConfigOption {
  section: string
  key: string
  default: number | boolean
  min: number | null
  max: number | null
  comment: string
  restart: boolean
}

export interface BrewingRecipe {
  /** Potions are `minecraft:<form>/<potion>`, e.g. `minecraft:splash_potion/swiftness`. */
  input: string
  reagent: string
  output: string
}

export const data = raw as unknown as {
  names: Record<string, string>
  textures: Record<string, string>
  recipes: Recipe[]
  speedUpgrades: Upgrade[]
  amountUpgrades: Upgrade[]
  config: ConfigOption[]
}

export const vanilla = vanillaRaw as unknown as {
  names: Record<string, string>
  recipes: BrewingRecipe[]
}

export function configDefault<T extends number | boolean>(key: string): T {
  return data.config.find((option) => option.key === key)?.default as T
}

/** Fuel from one blaze powder, in brews. Same as the vanilla brewing stand. */
export const BREWS_PER_BLAZE_POWDER = 20

const TAG_NAMES: Record<string, string> = {
  '#c:gems/diamond': 'Any diamond',
  '#c:ingots/gold': 'Any gold ingot',
  '#c:ingots/iron': 'Any iron ingot',
  '#c:dusts/redstone': 'Any redstone dust',
  '#c:storage_blocks/lapis': 'Any block of lapis',
  '#c:storage_blocks/redstone': 'Any block of redstone',
  '#minecraft:stone_crafting_materials': 'Cobblestone, blackstone or cobbled deepslate',
}

/** Item shown for a tag ingredient. */
const TAG_ICONS: Record<string, string> = {
  '#c:gems/diamond': 'minecraft:diamond',
  '#c:ingots/gold': 'minecraft:gold_ingot',
  '#c:ingots/iron': 'minecraft:iron_ingot',
  '#c:dusts/redstone': 'minecraft:redstone',
  '#c:storage_blocks/lapis': 'minecraft:lapis_block',
  '#c:storage_blocks/redstone': 'minecraft:redstone_block',
  '#minecraft:stone_crafting_materials': 'minecraft:cobblestone',
}

/** Mod items use their in-game name, potions and reagents their vanilla name; other ids are turned into readable names. */
export function itemName(id: string): string {
  if (data.names[id]) return data.names[id]
  if (vanilla.names[id]) return vanilla.names[id]
  if (TAG_NAMES[id]) return TAG_NAMES[id]
  const path = id.replace(/^#/, '').split(':').pop() ?? id
  return path
    .split('_') // @ts-ignore
    .map((word) => (['of', 'the'].includes(word) ? word : word.charAt(0).toUpperCase() + word.slice(1)))
    .join(' ')
}

/** Hosted renders of vanilla items, one PNG per item id; potions live under potion/, splash_potion/ and lingering_potion/. */
const VANILLA_ICONS = 'https://storage.googleapis.com/coolerpromc/textures'

/**
 * Where to load an item's icon from. Mod items use the hosted textures listed by the sync script;
 * vanilla items use the hosted renders.
 */
export function itemIcon(id: string): string | null {
  const itemId = TAG_ICONS[id] ?? id
  if (data.textures[itemId]) return data.textures[itemId]
  // @ts-ignore
  const [namespace, path] = itemId.includes(':') ? itemId.split(':') : ['minecraft', itemId]
  if (namespace !== 'minecraft') return null
  return `${VANILLA_ICONS}/${namespace}/${path}.png`
}

export interface StationSetup {
  processingTime: number
  potionCount: number
  potionStackSize: number
  maxSpeedUpgrade: number
  maxAmountUpgrade: number
  allowAmountUpgrade: boolean
  speed: Upgrade | null
  speedCount: number
  amount: Upgrade | null
  amountCount: number
}

export function defaultSetup(): StationSetup {
  return {
    processingTime: configDefault('processingTime'),
    potionCount: configDefault('potionCount'),
    potionStackSize: configDefault('potionStackSize'),
    maxSpeedUpgrade: configDefault('maxSpeedUpgrade'),
    maxAmountUpgrade: configDefault('maxAmountUpgrade'),
    allowAmountUpgrade: configDefault('allowAmountUpgrade'),
    speed: null,
    speedCount: 0,
    amount: null,
    amountCount: 0,
  }
}

/** Mirrors ItemBrewingStationBE: speed upgrades multiply together, amount upgrades add potions to every brew. */
export function stationStats(setup: StationSetup) {
  const speedCount = setup.speed ? Math.min(setup.speedCount, setup.maxSpeedUpgrade) : 0
  const multiplier = setup.speed ? Math.pow(setup.speed.value, speedCount) : 1
  // maxProgress = (int) (processingTime / multiplier); a brew finishes on the tick progress reaches it, and takes at least one tick.
  const ticks = Math.max(Math.floor(setup.processingTime / multiplier), 1)
  const amountCount = setup.allowAmountUpgrade && setup.amount ? Math.min(setup.amountCount, setup.maxAmountUpgrade) : 0
  const extra = setup.amount ? setup.amount.value * amountCount : 0
  const base = Math.min(setup.potionCount, setup.potionStackSize)
  const perBrew = base + extra
  return {
    speedCount,
    multiplier,
    ticks,
    amountCount,
    extra,
    perBrew,
    /** The potion slot and the output slot hold one stack, so a brew bigger than a stack never starts. */
    stuck: perBrew > setup.potionStackSize,
    perMinute: (perBrew * 1200) / ticks,
    perBlazePowder: perBrew * BREWS_PER_BLAZE_POWDER,
  }
}

export function seconds(ticks: number): string {
  const value = ticks / 20
  // @ts-ignore
  return Number.isInteger(value) ? `${value}` : value.toFixed(2).replace(/0$/, '')
}

export function round(value: number, digits = 2): string {
  const factor = Math.pow(10, digits)
  return `${Math.round(value * factor) / factor}`
}
