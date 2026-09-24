<script setup lang="ts">
import { computed } from 'vue'
import { configDefault, data, round, seconds, stationStats, defaultSetup } from '../easybrewing'
import ItemSlot from './ItemSlot.vue'

const props = defineProps<{ kind: 'speed' | 'amount' }>()

const maxCount = computed(() => configDefault<number>(props.kind === 'speed' ? 'maxSpeedUpgrade' : 'maxAmountUpgrade'))

/** One row per upgrade, with what one item and a full set (up to the config limit) do. */
const rows = computed(() =>
  (props.kind === 'speed' ? data.speedUpgrades : data.amountUpgrades).map((upgrade) => {
    const one = stationStats({ ...defaultSetup(), [props.kind]: upgrade, [`${props.kind}Count`]: 1 })
    const full = stationStats({ ...defaultSetup(), [props.kind]: upgrade, [`${props.kind}Count`]: maxCount.value })
    return { upgrade, one, full }
  }),
)
</script>

<template>
  <table class="eb-upgrades">
    <thead>
      <tr>
        <th>Upgrade</th>
        <th>Per item</th>
        <th>1 installed</th>
        <th v-if="maxCount > 1">{{ maxCount }} installed</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="{ upgrade, one, full } in rows" :key="upgrade.id">
        <td><ItemSlot :id="upgrade.id" label /></td>
        <template v-if="kind === 'speed'">
          <td>×{{ upgrade.value }} speed</td>
          <td>{{ seconds(one.ticks) }} s per brew</td>
          <td v-if="maxCount > 1">
            {{ seconds(full.ticks) }} s per brew
            <span class="eb-muted">(×{{ round(full.multiplier) }})</span>
          </td>
        </template>
        <template v-else>
          <td>+{{ upgrade.value }} potion{{ upgrade.value === 1 ? '' : 's' }}</td>
          <td>{{ one.perBrew }} potions per brew</td>
          <td v-if="maxCount > 1">{{ full.perBrew }} potions per brew</td>
        </template>
      </tr>
    </tbody>
  </table>
</template>

<style scoped>
.eb-upgrades td {
  vertical-align: middle;
}
</style>
