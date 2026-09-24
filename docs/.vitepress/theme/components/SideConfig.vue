<script setup lang="ts">
import { reactive } from 'vue'

// Mirrors ItemBrewingStationBE.Slot and initCapabilityBySide; colors are the outlines drawn in game.
const SLOTS = [
  { id: 'FUEL', name: 'Fuel', color: '#dbbd0f' },
  { id: 'POTION', name: 'Potion', color: '#2c65f5' },
  { id: 'INPUT', name: 'Ingredient', color: '#1fff26' },
  { id: 'OUTPUT', name: 'Output', color: '#de5d07' },
] as const

type SlotId = (typeof SLOTS)[number]['id']

const DEFAULTS: Record<string, SlotId> = {
  TOP: 'FUEL',
  LEFT: 'POTION',
  FRONT: 'INPUT',
  RIGHT: 'POTION',
  BACK: 'INPUT',
  BOTTOM: 'OUTPUT',
}

/** Button positions in the station's GUI: a cross of six buttons. */
const LAYOUT = [
  { side: 'TOP', col: 2, row: 1 },
  { side: 'LEFT', col: 1, row: 2 },
  { side: 'FRONT', col: 2, row: 2 },
  { side: 'RIGHT', col: 3, row: 2 },
  { side: 'BACK', col: 1, row: 3 },
  { side: 'BOTTOM', col: 2, row: 3 },
]

const sides = reactive<Record<string, SlotId>>({ ...DEFAULTS })

const slot = (id: SlotId) => SLOTS.find((s) => s.id === id)!
const label = (side: string) => side.charAt(0) + side.slice(1).toLowerCase()

function cycle(side: string) {
  const index = SLOTS.findIndex((s) => s.id === sides[side])
  sides[side] = SLOTS[(index + 1) % SLOTS.length].id
}

function reset() {
  Object.assign(sides, DEFAULTS)
}
</script>

<template>
  <div class="eb-sides">
    <div class="cross">
      <button
        v-for="b in LAYOUT"
        :key="b.side"
        class="side"
        :style="{ gridColumn: b.col, gridRow: b.row, borderColor: slot(sides[b.side]).color }"
        :title="`${label(b.side)}: click to change slot`"
        @click="cycle(b.side)"
      >
        <span class="side-name">{{ label(b.side) }}</span>
        <span class="side-slot">{{ slot(sides[b.side]).name }}</span>
      </button>
    </div>
    <div class="legend">
      <p v-for="s in SLOTS" :key="s.id">
        <span class="swatch" :style="{ background: s.color }" />
        <strong>{{ s.name }}</strong>
        <span class="eb-muted">
          {{
            Object.keys(sides)
              .filter((side) => sides[side] === s.id)
              .map(label)
              .join(', ') || 'no side'
          }}
        </span>
      </p>
      <button class="reset" @click="reset">Reset to defaults</button>
    </div>
  </div>
</template>

<style scoped>
.eb-sides {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 24px;
  margin: 16px 0;
  padding: 16px;
  border: 1px solid var(--vp-c-divider);
  border-radius: 12px;
  background: var(--vp-c-bg-soft);
}

.cross {
  display: grid;
  grid-template-columns: repeat(3, 84px);
  grid-template-rows: repeat(3, 56px);
  gap: 6px;
}

.side {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 3px solid;
  border-radius: 6px;
  background: #8b8b8b;
  color: #fff;
  text-shadow: 1px 1px 0 #3f3f3f;
  line-height: 1.2;
}

.side:hover {
  filter: brightness(1.1);
}

.side-name {
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.side-slot {
  font-size: 14px;
  font-weight: 600;
}

.legend p {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 4px 0;
}

.swatch {
  width: 14px;
  height: 14px;
  border-radius: 3px;
  flex: none;
}

.reset {
  margin-top: 8px;
  padding: 4px 10px;
  border: 1px solid var(--vp-c-divider);
  border-radius: 6px;
  font-size: 13px;
}
</style>
