package com.mediapicker.gallery.domain.entity

data class Validation(val rules: List<Rule>) {

    fun getMinVideoSelectionRule() = rules.filterIsInstance<Rule.MinVideoSelection>().first()

    fun getMaxVideoSelectionRule() = rules.filterIsInstance<Rule.MaxVideoSelection>().first()

    fun getMinPhotoSelectionRule() = rules.filterIsInstance<Rule.MinPhotoSelection>().first()

    fun getMaxPhotoSelectionRule() = rules.filterIsInstance<Rule.MaxPhotoSelection>().first()

    fun getMinWidthRule() = rules.filterIsInstance<Rule.MinWidth>().first()

    fun getMinHeightRule() = rules.filterIsInstance<Rule.MinHeight>().first()

    fun getMaxRatioRule() = rules.filterIsInstance<Rule.MaxRatio>().first()

    /*override fun toString(): String {
        return "Max Selection For Photo "+getMaxPhotoSelectionRule().maxSelectionLimit
    }*/


    class ValidationBuilder {
        private val rule = HashSet<Rule>().apply {
            this.add(Rule.MinVideoSelection(minSelectionLimit = 0, message = "Please select at least one video"))
            this.add(Rule.MaxVideoSelection(maxSelectionLimit = 2, message = "Max video selection limit reached"))
            this.add(Rule.MinPhotoSelection(minSelectionLimit = 1, message = "Please select at least one photo"))
            this.add(Rule.MaxPhotoSelection(maxSelectionLimit = 4, message = "Max Photo Limit Reached "))
            this.add(Rule.MinHeight("Image is too small (min. is 100x100 px).", "100"))
            this.add(Rule.MinWidth("Image is too small (min. is 100x100 px).", "100"))
            this.add(Rule.MaxRatio("Image is too small (min. is 100x100 px).", "100"))
        }

        fun setMinPhotoSelection(minSelection: Rule): ValidationBuilder {
            val i = rule.filterIsInstance<Rule.MinPhotoSelection>()
            i.forEach { rule.remove(it) }
            rule.add(minSelection)
            return this
        }

        fun setMaxPhotoSelection(maxSelection: Rule): ValidationBuilder {
            val i = rule.filterIsInstance<Rule.MaxPhotoSelection>()
            i.forEach { rule.remove(it) }
            rule.add(maxSelection)
            return this
        }


        fun setMinVideoSelection(minSelection: Rule): ValidationBuilder {
            val i = rule.filterIsInstance<Rule.MinVideoSelection>()
            i.forEach { rule.remove(it) }
            rule.add(minSelection)
            return this
        }

        fun setMaxVideoSelection(maxSelection: Rule): ValidationBuilder {
            val i = rule.filterIsInstance<Rule.MaxVideoSelection>()
            i.forEach { rule.remove(it) }
            rule.add(maxSelection)
            return this
        }

        fun setMinWidth(minWidth: Rule): ValidationBuilder {
            val i = rule.filterIsInstance<Rule.MinWidth>()
            i.forEach { rule.remove(it) }
            rule.add(minWidth)
            return this
        }

        fun setMinHeight(minHeight: Rule): ValidationBuilder {
            val i = rule.filterIsInstance<Rule.MinHeight>()
            i.forEach { rule.remove(it) }
            rule.add(minHeight)
            return this
        }

        fun setMaxRatio(maxRatio: Rule): ValidationBuilder {
            val i = rule.filterIsInstance<Rule.MaxRatio>()
            i.forEach { rule.remove(it) }
            rule.add(maxRatio)
            return this
        }

        fun build(): Validation {
            return Validation(rules = rule.toList())
        }
    }
}


sealed class Rule(private val id: Int, open val message: String, open val value: String) {
    data class MinVideoSelection(val minSelectionLimit: Int, override val message: String) : Rule(1, message, "")
    data class MaxVideoSelection(val maxSelectionLimit: Int, override val message: String) : Rule(2, message, "")
    data class MinPhotoSelection(val minSelectionLimit: Int, override val message: String) : Rule(3, message, "")
    data class MaxPhotoSelection(val maxSelectionLimit: Int, override val message: String) : Rule(4, message, "")
    data class MinWidth(override val message: String, override val value: String) : Rule(5, message, value)
    data class MinHeight(override val message: String, override val value: String) : Rule(6, message, value)
    data class MaxRatio(override val message: String, override val value: String) : Rule(7, message, value)
}
