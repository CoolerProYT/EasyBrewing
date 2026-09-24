<script setup lang="ts">
import { withBase } from 'vitepress'

// The station's screen is the top-left 176x166 of its 256x256 GUI texture; positions come from ItemBrewingStationMenu/Screen.
const WIDTH = 176
const HEIGHT = 166
const TEXTURE = 256

const AREAS = [
  { n: 1, name: 'Fuel', x: 16, y: 16, w: 18, h: 18, text: 'Blaze powder. One powder lasts 20 brews; the bar underneath shows what is left.' },
  { n: 2, name: 'Potion', x: 78, y: 16, w: 18, h: 18, text: 'The potions to brew, as a stack. Water bottles, awkward potions and so on.' },
  { n: 3, name: 'Ingredient', x: 58, y: 36, w: 18, h: 18, text: 'The brewing ingredient: nether wart, sugar, gunpowder and so on. One is used per brew.' },
  { n: 4, name: 'Output', x: 78, y: 56, w: 18, h: 18, text: 'Finished potions. It must be empty or hold the same potion for a brew to start.' },
  { n: 5, name: 'Speed upgrade', x: 153, y: 5, w: 18, h: 18, text: 'Speed upgrades. A stack counts, up to the configured limit.' },
  { n: 6, name: 'Amount upgrade', x: 153, y: 23, w: 18, h: 18, text: 'Amount upgrades. Each one adds potions to every brew.' },
  { n: 7, name: 'Side settings', x: 144, y: 49, w: 24, h: 24, text: 'Choose which slot hoppers and pipes reach from each side of the block.' },
]

const pct = (value: number, of: number) => `${(value / of) * 100}%`
</script>

<template>
  <div class="eb-gui">
    <div
      class="screen pixelated"
      :style="{
        backgroundImage: `url(${withBase('/gui/item_brewing_station.png')})`,
        backgroundSize: `${(TEXTURE / WIDTH) * 100}% auto`,
        aspectRatio: `${WIDTH} / ${HEIGHT}`,
      }"
      role="img"
      aria-label="Item Brewing Station screen"
    >
      <span
        v-for="a in AREAS"
        :key="a.n"
        class="area"
        :title="a.name"
        :style="{ left: pct(a.x, WIDTH), top: pct(a.y, HEIGHT), width: pct(a.w, WIDTH), height: pct(a.h, HEIGHT) }"
      >
        <span class="badge">{{ a.n }}</span>
      </span>
    </div>
    <ol class="legend">
      <li v-for="a in AREAS" :key="a.n">
        <strong>{{ a.name }}</strong>
        <span class="eb-muted"> {{ a.text }}</span>
      </li>
    </ol>
  </div>
</template>

<style scoped>
.eb-gui {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  align-items: flex-start;
  margin: 16px 0;
}

.screen {
  position: relative;
  width: 100%;
  max-width: 352px;
  background-repeat: no-repeat;
  background-position: 0 0;
}

.area {
  position: absolute;
  border: 2px solid var(--vp-c-brand-1);
  border-radius: 2px;
}

.badge {
  position: absolute;
  top: -10px;
  left: -10px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: var(--vp-c-brand-1);
  color: #fff;
  font: 700 11px/1 var(--vp-font-family-base);
}

.legend {
  flex: 1;
  min-width: 240px;
  margin: 0;
}

.legend li {
  margin: 4px 0;
}
</style>
