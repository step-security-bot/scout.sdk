/*
 * Copyright (c) 2010, 2023 BSI Business Systems Integration AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.scout.sdk.s2i.util.compat

import org.eclipse.scout.sdk.core.log.SdkLog
import org.eclipse.scout.sdk.core.log.SdkLog.onTrace
import org.eclipse.scout.sdk.core.util.FinalValue
import org.eclipse.scout.sdk.core.util.SdkException
import java.lang.reflect.InvocationTargetException

@Suppress("unused")
class CompatibilityMethodCaller<T> {

    companion object {
        const val CONSTRUCTOR_NAME = "<init>"
    }

    private val m_descriptors = ArrayList<MethodDescriptor<T>>()
    private val m_selectedDescriptorWithCallable: Pair<MethodDescriptor<T>, (Any?, Array<out Any?>) -> T> by lazy {
        m_descriptors.asSequence()
                .mapNotNull { d -> d.resolvedCallable?.let { d to it } }
                .firstOrNull() ?: throw SdkException("No compatible API function found.")
    }

    fun withCandidate(d: MethodDescriptor<T>) = apply { m_descriptors.add(d) }

    fun withCandidate(declaringClass: Class<*>, methodName: String, vararg parameterTypes: Class<*>, handler: (ResolvedMethod<T>) -> T) =
            withCandidate(MethodDescriptor(declaringClass, methodName, parameterTypes, handler))

    fun withCandidate(declaringClassFqn: String, methodName: String, vararg parameterTypesFqn: String, handler: (ResolvedMethod<T>) -> T) =
            withCandidate(MethodDescriptor(declaringClassFqn, methodName, parameterTypesFqn, handler))

    fun invoke(): T {
        val (selectedDescriptor, methodCallable) = m_selectedDescriptorWithCallable
        return selectedDescriptor.handler(ResolvedMethod(selectedDescriptor, methodCallable))
    }

    class ResolvedMethod<R>(val descriptor: MethodDescriptor<R>, private val callable: (Any?, Array<out Any?>) -> R) {

        fun invoke(obj: Any?, vararg parameters: Any?) = try {
            callable(obj, parameters)
        } catch (e: InvocationTargetException) {
            throw e.targetException
        }

        fun invokeStatic(vararg parameters: Any?) = invoke(null, *parameters)
    }

    class MethodDescriptor<R>(val declaringClassFqn: String, val methodName: String, val parameterTypesFqn: Array<out String>, val handler: (ResolvedMethod<R>) -> R) {

        private val m_resolvedClass = FinalValue<Class<*>>()
        private val m_resolvedParameterTypes = FinalValue<Array<out Class<*>>>()
        internal val resolvedCallable: ((Any?, Array<out Any?>) -> R)? by lazy { resolveCallable() }

        constructor(declaringClass: Class<*>, methodName: String, parameterTypes: Array<out Class<*>>, handler: (ResolvedMethod<R>) -> R) : this(declaringClass.name, methodName, emptyArray(), handler) {
            m_resolvedClass.set(declaringClass)
            m_resolvedParameterTypes.set(parameterTypes)
        }

        @Suppress("UNCHECKED_CAST")
        private fun resolveCallable(): ((Any?, Array<out Any?>) -> R)? {
            try {
                val resolvedParameterTypes = resolvedParameterTypes()
                val resolvedClass = resolvedClass()
                if (CONSTRUCTOR_NAME == methodName) {
                    val constr = resolvedClass.getDeclaredConstructor(*resolvedParameterTypes)
                    constr.isAccessible = true
                    return { _, args -> constr.newInstance(*args) as R }
                }
                val method = resolvedClass.getDeclaredMethod(methodName, *resolvedParameterTypes)
                method.isAccessible = true
                return { obj, args -> method.invoke(obj, *args) as R }
            } catch (e: Throwable) {
                SdkLog.debug("Skipping method '{}'.", methodName, onTrace(e))
                return null
            }
        }

        private fun resolveClass(fqn: String): Class<*> = CompatibilityMethodCaller::class.java.classLoader.loadClass(fqn)

        fun resolvedClass(): Class<*> = m_resolvedClass.computeIfAbsentAndGet {
            resolveClass(declaringClassFqn)
        }

        fun resolvedParameterTypes(): Array<out Class<*>> = m_resolvedParameterTypes.computeIfAbsentAndGet {
            parameterTypesFqn
                    .map { resolveClass(it) }
                    .toTypedArray()
        }
    }
}