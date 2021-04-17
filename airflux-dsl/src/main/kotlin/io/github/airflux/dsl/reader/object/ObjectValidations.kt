package io.github.airflux.dsl.reader.`object`

class ObjectValidations(val before: List<ObjectValidator.Before>, val after: List<ObjectValidator.After>) {

    @ObjectReaderMarker
    class Builder private constructor(
        val before: Validators<ObjectValidator.Before.Builder>,
        val after: Validators<ObjectValidator.After.Builder>
    ) {

        constructor(other: Builder = Default) : this(
            before = Validators<ObjectValidator.Before.Builder>()
                .apply { other.before.forEach { add(it) } },
            after = Validators<ObjectValidator.After.Builder>()
                .apply { other.after.forEach { add(it) } }
        )

        fun build(configuration: ObjectReaderConfiguration, attributes: List<Attribute<*>>): ObjectValidations =
            ObjectValidations(
                before = before.map { validator -> validator.build(configuration, attributes) },
                after = after.map { validator -> validator.build(configuration, attributes) }
            )

        class Validators<T : ObjectValidator.Identifier> : Iterable<T> {

            private val items: MutableMap<String, T> = LinkedHashMap()

            operator fun contains(name: String): Boolean = items.containsKey(name)

            operator fun contains(validator: T): Boolean = items.containsKey(validator.key)

            operator fun get(name: String): T? = items[name]

            fun add(validator: T) {
                items[validator.key] = validator
            }

            fun remove(name: String) {
                items.remove(name)
            }

            override fun iterator(): Iterator<T> = items.values.iterator()
        }

        companion object {
            val Default = Builder(before = Validators(), after = Validators())
        }
    }
}