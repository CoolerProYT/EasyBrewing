<script setup lang="ts">
import { computed, ref } from 'vue'
import { itemName, vanilla } from '../easybrewing'
import ItemSlot from './ItemSlot.vue'

/** Marks the recipes Minecraft 1.20.1 doesn't have, for the older versions' page. */
const props = defineProps<{ markNew?: boolean }>()

/** Potions added in Minecraft 1.21; their recipes don't exist in 1.20.1. */
const NEW_IN_121 = ['wind_charged', 'weaving', 'oozing', 'infested']
const isNew = (id: string) => NEW_IN_121.includes(id.split('/')[1] ?? '')

const FORMS = [
  { id: 'potion', label: 'Potion' },
  { id: 'splash_potion', label: 'Splash' },
  { id: 'lingering_potion', label: 'Lingering' },
]

const form = ref('potion')
const query = ref('')

const formOf = (id: string) => id.split(':')[1]?.split('/')[0]

/** Recipes that start from the chosen bottle form, matched on any of the three names. */
const rows = computed(() => {
  const q = query.value.trim().toLowerCase()
  return vanilla.recipes
    .filter((r) => formOf(r.input) === form.value)
    .filter((r) => !q || [r.input, r.reagent, r.output].some((id) => itemName(id).toLowerCase().includes(q)))
    .sort((a, b) => itemName(a.output).localeCompare(itemName(b.output)))
})
</script>

<template>
  <div class="eb-brewing">
    <div class="controls">
      <div class="tabs" role="tablist">
        <button
          v-for="f in FORMS"
          :key="f.id"
          role="tab"
          :aria-selected="form === f.id"
          :class="{ active: form === f.id }"
          @click="form = f.id"
        >
          {{ f.label }}
        </button>
      </div>
      <input v-model="query" class="search" type="search" placeholder="Search potions or ingredients" />
    </div>
    <table>
      <thead>
        <tr>
          <th>Potion in</th>
          <th>Ingredient</th>
          <th>Potion out</th>
          <th v-if="props.markNew">Since</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="r in rows" :key="r.input + r.reagent">
          <td><ItemSlot :id="r.input" label /></td>
          <td><ItemSlot :id="r.reagent" label /></td>
          <td><ItemSlot :id="r.output" label /></td>
          <td v-if="props.markNew"><span v-if="isNew(r.output)" class="badge">1.21</span></td>
        </tr>
        <tr v-if="rows.length === 0">
          <td :colspan="props.markNew ? 4 : 3" class="eb-muted">No recipes match.</td>
        </tr>
      </tbody>
    </table>
    <p class="eb-muted">{{ rows.length }} recipes</p>
  </div>
</template>

<style scoped>
.controls {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
  margin: 12px 0;
}

.tabs {
  display: inline-flex;
  padding: 3px;
  border-radius: 8px;
  background: var(--vp-c-bg-soft);
}

.tabs button {
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  color: var(--vp-c-text-2);
}

.tabs button.active {
  background: var(--vp-c-bg);
  color: var(--vp-c-brand-1);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.12);
}

.search {
  flex: 1;
  min-width: 200px;
  padding: 6px 10px;
  border: 1px solid var(--vp-c-divider);
  border-radius: 8px;
  background: var(--vp-c-bg);
  font-size: 14px;
}

table {
  display: table;
  width: 100%;
}

td {
  vertical-align: middle;
}

.badge {
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 600;
  background: var(--vp-c-brand-soft);
  color: var(--vp-c-brand-1);
}
</style>
