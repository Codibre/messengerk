package com.github.codibre.messengerk.messengerk_spring_boot_starter

import com.github.codibre.messengerk.core.handler.HandlerDescriptor
import com.github.codibre.messengerk.core.handler.MessageHandler
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.classreading.MetadataReader
import org.springframework.core.type.classreading.MetadataReaderFactory
import org.springframework.core.type.filter.TypeFilter
import java.lang.reflect.Method
import kotlin.reflect.KClass

open class AnnotationFinder {

    fun findHandlers(
        basePackage: String,
        context: ApplicationContext
    ): MutableMap<String, MutableList<HandlerDescriptor>> {
        val handlerDescriptors = mutableMapOf<String, MutableList<HandlerDescriptor>>()

        val handlerClasses =
            findAnnotations(basePackage, com.github.codibre.messengerk.core.annotations.MessageHandler::class)

        for (classes in handlerClasses) {
            for (handler in classes.value) {
                try {
                    try {
                        if (handler is Method) {
                            val handlerCallable: MessageHandler = {
                                val bean = context.getBean(handler::class.java)
                                handler.invoke(bean, it)
                            }

                            val handlerDescriptor = HandlerDescriptor(handler, handlerCallable)
                            handlerDescriptors.getOrPut(classes.key) { mutableListOf() }.add(handlerDescriptor)
                        }
                    } catch (e: Exception) {
                        println("Error on getting handler bean ${e.message}")
                        continue
                    }
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }
        }

        return handlerDescriptors
    }

    private fun findAnnotations(
        basePackage: String,
        annotationClass: KClass<out Annotation>
    ): MutableMap<String, MutableList<Any>> {
        val classNames = scanClasses(basePackage)
        val result = mutableMapOf<String, MutableList<Any>>()

        for (className in classNames) {
            try {
                val clazz = Class.forName(className)

                // Check if the class has the annotation
                if (clazz.isAnnotationPresent(annotationClass.java)) {
                    result.getOrPut(className) { mutableListOf() }.add(clazz)
                }

                // Iterate through the methods of the class
                for (method in clazz.methods) {
                    if (method.isAnnotationPresent(annotationClass.java)) {
                        result.getOrPut(className) { mutableListOf() }.add(method)
                    }
                }
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }

        return result
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
