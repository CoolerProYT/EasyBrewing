<script setup lang="ts">
import { computed } from 'vue'
import { data } from '../easybrewing'

const props = defineProps<{ section: string }>()

/** Plainer wording than the config file's comments. Options missing here show their comment. */
const DESCRIPTIONS: Record<string, string> = {
  potionStackSize: 'How many potions, splash potions and lingering potions fit in one stack, everywhere in the game.',
  maxSpeedUpgrade: 'How many speed upgrades in the speed slot take effect. Extra items in the stack do nothing.',
  allowAmountUpgrade: 'Turns amount upgrades on or off.',
  maxAmountUpgrade: 'How many amount upgrades in the amount slot take effect. Extra items in the stack do nothing.',
  potionCount: 'Potions brewed per ingredient before amount upgrades. Capped at potionStackSize.',
  processingTime: 'Ticks per brew before speed upgrades. 20 ticks is one second.',
  cobblemonPotionCount: 'Items brewed per ingredient by Cobblemon brewing recipes.',
}

const options = computed(() => data.config.filter((option) => option.section === props.section))
</script>

<template>
  <table>
    <thead>
      <tr>
        <th>Option</th>
        <th>Default</th>
        <th>Range</th>
        <th>What it does</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="o in options" :key="o.key">
        <td><code>{{ o.key }}</code></td>
        <td><code>{{ o.default }}</code></td>
        <td>{{ o.min === null ? 'true / false' : `${o.min} – ${o.max}` }}</td>
        <td>
          {{ DESCRIPTIONS[o.key] ?? o.comment }}
          <span v-if="o.restart" class="eb-muted">Needs a restart.</span>
        </td>
      </tr>
    </tbody>
  </table>
</template>
