package Game.Menu.MenuPoints

import Engine.IGameScene

class OptionSelectorMenuPoint(
    val label: String,
    parent: IGameScene,
    val options: List<OptionValue>,
    var optionIdx: Int,
    val onChange: (old: OptionValue, new: OptionValue) -> Unit,
    initialFontSize : Int = 24
    ):
    MenuPointGameObject("$label: ${options[optionIdx].name}", parent, onActivate = {}, fontSize = initialFontSize) {

    override fun onSelected() {
        super.text = "$label: <${options[optionIdx].name}>"
    }

    override fun onDeselected() {
        super.text = "$label: ${options[optionIdx].name}"
    }

    fun increase() {
        val oldIdx = optionIdx
        if (optionIdx < options.size-1) optionIdx += 1
        super.text = "$label: <${options[optionIdx].name}>"
        onChange(options[oldIdx], options[optionIdx])
    }

    fun decrease() {
        val oldIdx = optionIdx
        if (optionIdx > 0) optionIdx -= 1
        super.text = "$label: <${options[optionIdx].name}>"
        onChange(options[oldIdx], options[optionIdx])
    }
}

data class OptionValue(val id: Int, val name: String) { }