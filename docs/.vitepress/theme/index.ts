import DefaultTheme from 'vitepress/theme'
import type { Theme } from 'vitepress'
import BrewingExplorer from './components/BrewingExplorer.vue'
import ConfigTable from './components/ConfigTable.vue'
import ItemSlot from './components/ItemSlot.vue'
import RecipeCard from './components/RecipeCard.vue'
import SideConfig from './components/SideConfig.vue'
import StationGui from './components/StationGui.vue'
import UpgradeCalculator from './components/UpgradeCalculator.vue'
import UpgradeTable from './components/UpgradeTable.vue'
import VersionSwitcher from './components/VersionSwitcher.vue'
import './style.css'

export default {
  extends: DefaultTheme,
  enhanceApp({ app }) {
    app.component('BrewingExplorer', BrewingExplorer)
    app.component('ConfigTable', ConfigTable)
    app.component('ItemSlot', ItemSlot)
    app.component('RecipeCard', RecipeCard)
    app.component('SideConfig', SideConfig)
    app.component('StationGui', StationGui)
    app.component('UpgradeCalculator', UpgradeCalculator)
    app.component('UpgradeTable', UpgradeTable)
    app.component('VersionSwitcher', VersionSwitcher)
  },
} satisfies Theme
