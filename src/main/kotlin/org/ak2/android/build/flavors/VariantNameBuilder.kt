package org.ak2.android.build.flavors

class VariantNameBuilder {

    private val buf = StringBuilder();

    fun append(flavor: Flavor?) = append(flavor?.name)

    fun append(segment: String?) = apply {
        segment?.let {
            buf.append(if (buf.isEmpty()) it else it.capitalize())
        }
    }

    fun build() = buf.toString()
}