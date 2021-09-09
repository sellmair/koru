package com.futuremind.koru.processor

import com.squareup.kotlinpoet.*


class WrapperInterfaceBuilder(
    originalTypeName: ClassName,
    originalTypeSpec: TypeSpec,
    private val newTypeName: String,
    generatedInterfaces: Map<TypeName, GeneratedInterface>,
) : WrapperBuilder(originalTypeName, originalTypeSpec, generatedInterfaces) {

    private val functions = originalTypeSpec.funSpecs
        .filter { !it.modifiers.contains(KModifier.PRIVATE) }
        .map { originalFuncSpec ->
            originalFuncSpec.toBuilder(name = originalFuncSpec.name)
                .clearBody()
                .setReturnType(originalFuncSpec)
                .apply {
                    modifiers.remove(KModifier.SUSPEND)
                    modifiers.add(KModifier.ABSTRACT)
                }
                .build()
        }

    private val properties = originalTypeSpec.propertySpecs
        .filter { !it.modifiers.contains(KModifier.PRIVATE) }
        .map { originalPropertySpec ->
            PropertySpec
                .builder(
                    name = originalPropertySpec.name,
                    type = originalPropertySpec.wrappedType
                )
                .mutable(false)
                .apply {
                    modifiers.add(KModifier.ABSTRACT)
                }
                .build()
        }


    private val modifiers: Set<KModifier> = originalTypeSpec.modifiers()

    fun build(): TypeSpec = TypeSpec
        .interfaceBuilder(newTypeName)
        .addModifiers(modifiers)
        .addSuperinterfaces(superInterfacesNames)
        .addFunctions(functions)
        .addProperties(properties)
        .build()

}
