<script setup lang="ts">
import { computed, reactive } from 'vue'
import { data, defaultSetup, round, seconds, stationStats, BREWS_PER_BLAZE_POWDER } from '../easybrewing'
import ItemSlot from './ItemSlot.vue'

const setup = reactive(defaultSetup())
setup.speed = data.speedUpgrades[0] ?? null
setup.speedCount = 1

const stats = computed(() => stationStats(setup))
const vanillaStand = computed(() => stationStats({ ...defaultSetup(), processingTime: 400, potionCount: 3 }))
const faster = computed(() => stats.value.perMinute / vanillaStand.value.perMinute)

function pick(kind: 'speed' | 'amount', id: string | null) {
  const list = kind === 'speed' ? data.speedUpgrades : data.amountUpgrades
  setup[kind] = list.find((u) => u.id === id) ?? null
  const countKey = kind === 'speed' ? 'speedCount' : 'amountCount'
  if (setup[kind] && setup[countKey] === 0) setup[countKey] = 1
}

function reset() {
  Object.assign(setup, defaultSetup())
}
</script>

<template>
  <div class="eb-calc">
    <div class="slots">
      <div class="slot-row">
        <span class="label">Speed slot</span>
        <div class="choices">
          <button :class="{ active: !setup.speed }" @click="pick('speed', null)">None</button>
          <button
            v-for="u in data.speedUpgrades"
            :key="u.id"
            :class="{ active: setup.speed?.id === u.id }"
            :title="`×${u.value} per item`"
            @click="pick('speed', u.id)"
          >
            <ItemSlot :id="u.id" />
          </button>
        </div>
        <label v-if="setup.speed" class="count">
          ×
          <input v-model.number="setup.speedCount" type="number" min="1" max="64" />
        </label>
      </div>
      <div class="slot-row">
        <span class="label">Amount slot</span>
        <div class="choices">
          <button :class="{ active: !setup.amount }" @click="pick('amount', null)">None</button>
          <button
            v-for="u in data.amountUpgrades"
            :key="u.id"
            :class="{ active: setup.amount?.id === u.id }"
            :title="`+${u.value} per item`"
            @click="pick('amount', u.id)"
          >
            <ItemSlot :id="u.id" />
          </button>
        </div>
        <label v-if="setup.amount" class="count">
          ×
          <input v-model.number="setup.amountCount" type="number" min="1" max="64" />
        </label>
      </div>
    </div>

    <div class="results">
      <div class="stat">
        <span class="value">{{ seconds(stats.ticks) }} s</span>
        <span class="name">per brew ({{ stats.ticks }} ticks)</span>
      </div>
      <div class="stat">
        <span class="value">{{ stats.perBrew }}</span>
        <span class="name">potions per brew</span>
      </div>
      <div class="stat">
        <span class="value">{{ round(stats.perMinute, 1) }}</span>
        <span class="name">potions per minute</span>
      </div>
      <div class="stat">
        <span class="value">{{ stats.perBlazePowder }}</span>
        <span class="name">potions per blaze powder</span>
      </div>
    </div>

    <p class="summary">
      Speed ×{{ round(stats.multiplier) }}
      <template v-if="setup.speed && setup.speedCount > setup.maxSpeedUpgrade">
        (only {{ setup.maxSpeedUpgrade }} of {{ setup.speedCount }} speed upgrades count)
      </template>
      <template v-if="setup.amount && !setup.allowAmountUpgrade">· amount upgrades are disabled in the config</template>
      <template v-else-if="setup.amount && setup.amountCount > setup.maxAmountUpgrade">
        · only {{ setup.maxAmountUpgrade }} of {{ setup.amountCount }} amount upgrades count
      </template>
      · {{ round(faster, 1) }}× the output of a vanilla brewing stand. Each brew uses 1 ingredient, and a blaze powder lasts
      {{ BREWS_PER_BLAZE_POWDER }} brews.
    </p>
    <p v-if="stats.stuck" class="warning">
      A brew needs {{ stats.perBrew }} potions, but potions only stack to {{ setup.potionStackSize }}. The station will never start.
      Raise <code>potionStackSize</code> or use fewer amount upgrades.
    </p>

    <details class="config">
      <summary>Server config</summary>
      <div class="config-grid">
        <label>processingTime <input v-model.number="setup.processingTime" type="number" min="20" max="4000" /></label>
        <label>potionCount <input v-model.number="setup.potionCount" type="number" min="1" max="64" /></label>
        <label>potionStackSize <input v-model.number="setup.potionStackSize" type="number" min="1" max="64" /></label>
        <label>maxSpeedUpgrade <input v-model.number="setup.maxSpeedUpgrade" type="number" min="1" max="64" /></label>
        <label>maxAmountUpgrade <input v-model.number="setup.maxAmountUpgrade" type="number" min="1" max="64" /></label>
        <label>allowAmountUpgrade <input v-model="setup.allowAmountUpgrade" type="checkbox" /></label>
      </div>
      <button class="reset" @click="reset">Reset to defaults</button>
    </details>
  </div>
</template>

<style scoped>
.eb-calc {
  margin: 16px 0;
  padding: 16px;
  border: 1px solid var(--vp-c-divider);
  border-radius: 12px;
  background: var(--vp-c-bg-soft);
}

.slots {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.slot-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
}

.label {
  width: 96px;
  font-weight: 600;
  font-size: 14px;
}

.choices {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.choices button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 44px;
  height: 44px;
  padding: 2px 8px;
  border: 2px solid transparent;
  border-radius: 8px;
  font-size: 13px;
  background: var(--vp-c-bg);
}

.choices button.active {
  border-color: var(--vp-c-brand-1);
  background: var(--vp-c-brand-soft);
}

.count input,
.config-grid input[type='number'] {
  width: 64px;
  padding: 2px 6px;
  border: 1px solid var(--vp-c-divider);
  border-radius: 6px;
  background: var(--vp-c-bg);
}

.results {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 10px;
  margin-top: 16px;
}

.stat {
  display: flex;
  flex-direction: column;
  padding: 10px 12px;
  border-radius: 8px;
  background: var(--vp-c-bg);
}

.value {
  font-size: 24px;
  font-weight: 700;
  color: var(--vp-c-brand-1);
  line-height: 1.2;
}

.name {
  font-size: 13px;
  color: var(--vp-c-text-2);
}

.summary {
  margin: 12px 0 0;
  font-size: 14px;
  color: var(--vp-c-text-2);
}

.warning {
  margin: 8px 0 0;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 14px;
  background: var(--vp-c-danger-soft);
}

.config {
  margin-top: 12px;
  font-size: 14px;
}

.config summary {
  cursor: pointer;
  color: var(--vp-c-text-2);
}

.config-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 8px;
  margin-top: 8px;
}

.config-grid label {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  font-family: var(--vp-font-family-mono);
  font-size: 13px;
}

.reset {
  margin-top: 10px;
  padding: 4px 10px;
  border: 1px solid var(--vp-c-divider);
  border-radius: 6px;
  font-size: 13px;
}
</style>
