package com.github.codibre.messengerk.spring_boot_starter

import com.github.codibre.messengerk.core.handler.HandlerDescriptor
import com.github.codibre.messengerk.core.handler.MessageHandler
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.classreading.MetadataReader
import org.springframework.core.type.classreading.MetadataReaderFactory
import org.springframework.core.type.filter.TypeFilter
import kotlin.reflect.KClass

open class AnnotationHandlerFinder {

    fun findHandlers(
        basePackage: String,
        annotationClass: KClass<out Annotation>,
        context: ApplicationContext
    ): MutableMap<String, MutableList<HandlerDescriptor>> {
        val classNames = scanClasses(basePackage)
        val handlerDescriptors = mutableMapOf<String, MutableList<HandlerDescriptor>>()

        for (className in classNames) {
            try {
                val clazz = Class.forName(className)
                for (handler in clazz.methods) {
                    try {
                        if (handler.isAnnotationPresent(annotationClass.java)) {
                            val handlerCallable: MessageHandler = {
                                val bean = context.getBean(clazz)
                                handler.invoke(bean, it)
                            }

                            val handlerDescriptor = HandlerDescriptor(handler, handlerCallable)
                            handlerDescriptors.getOrPut(className) { mutableListOf() }.add(handlerDescriptor)
                        }
                    } catch (e: Exception) {
                        println("Error on getting handler bean ${e.message}")
                        continue
                    }
                }
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }

        return handlerDescriptors
    }

    private fun scanClasses(basePackage: String): List<String> {
        val scanner = ClassPathScanningCandidateComponentProvider(false)
        scanner.addIncludeFilter(AllClassesFilter())
        val candidateComponents = scanner.findCandidateComponents(basePackage)
        return candidateComponents.mapNotNull { it.beanClassName }
    }
}

class AllClassesFilter : TypeFilter {
    override fun match(metadataReader: MetadataReader, metadataReaderFactory: MetadataReaderFactory): Boolean {
        return true
    }
}
