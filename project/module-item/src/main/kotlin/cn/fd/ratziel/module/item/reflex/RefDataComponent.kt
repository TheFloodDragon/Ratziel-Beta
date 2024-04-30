package cn.fd.ratziel.module.item.reflex

import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.MinecraftVersion

/**
 * RefDataComponent
 * Only 1.20.5+
 *
 * @author TheFloodDragon
 * @since 2024/4/27 11:20
 */
object RefDataComponent {

    /**
     * [net.minecraft.core.component.DataComponents]
     */
    val componentsClass by lazy {
        net.minecraft.core.component.DataComponents::class.java
    }

    /**
     * [net.minecraft.core.component.DataComponentHolder]
     */
    val holderClass by lazy {
        net.minecraft.core.component.DataComponentHolder::class.java
    }

    /**
     * public static final DataComponentType<CustomData> b = a("custom_data", (v) -> {return v.a(CustomData.b);})
     */
    internal val componentsCustomDataFiled by lazy {
        if (MinecraftVersion.isLower(12005)) throw UnsupportedOperationException("RefItemStack#dataComponentsCustomDataFiled only support 1.20.5 or higher!")
        ReflexClass.of(componentsClass).structure.getField("b")
    }

    /**
     * @Nullable default <T> T a(DataComponentType<? extends T> var0)
     */
    internal val getComponentMethod by lazy {
        if (MinecraftVersion.isLower(12005)) throw UnsupportedOperationException("RefItemStack#dataComponentsCustomDataFiled only support 1.20.5 or higher!")
        ReflexClass.of(holderClass).structure.getField("a")
    }

}