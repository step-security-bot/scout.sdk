/*
 * Copyright (c) 2010, 2023 BSI Business Systems Integration AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.scout.sdk.s2i.model.typescript

import com.intellij.lang.javascript.psi.JSArrayLiteralExpression
import com.intellij.lang.javascript.psi.JSElement
import com.intellij.lang.javascript.psi.JSLiteralExpression
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import org.eclipse.scout.sdk.core.typescript.model.api.IConstantValue
import org.eclipse.scout.sdk.core.typescript.model.api.IObjectLiteral
import java.math.BigInteger
import java.util.*

open class IdeaConstantValue(protected val ideaModule: IdeaNodeModule, internal val element: JSElement?) : IConstantValue {

    override fun <T : Any> convertTo(expectedType: Class<T>?): Optional<T> {
        if (expectedType == null) return Optional.empty()

        val converted: Any?
        if (expectedType.isArray) {
            converted = tryConvertToArray(expectedType)
        } else {
            converted = when (expectedType) {
                IObjectLiteral::class.java -> tryConvertToObjectLiteral()

                String::class.java,
                java.lang.String::class.java -> tryConvertToString()

                Boolean::class.java,
                java.lang.Boolean::class.java -> tryConvertToBoolean()

                java.lang.Integer::class.java,
                Int::class.java,
                java.lang.Float::class.java,
                Float::class.java,
                java.lang.Long::class.java,
                Long::class.java,
                java.lang.Double::class.java,
                Double::class.java,
                java.math.BigDecimal::class.java,
                java.math.BigInteger::class.java -> tryConvertToNumber(expectedType)

                else -> null // unknown type conversion
            }
        }

        @Suppress("UNCHECKED_CAST") // do not use expectedType.cast() here
        return Optional.ofNullable(converted as T?)
    }

    override fun type(): IConstantValue.ConstantValueType {
        if (element is JSObjectLiteralExpression) return IConstantValue.ConstantValueType.ObjectLiteral
        if (element is JSArrayLiteralExpression) return IConstantValue.ConstantValueType.Array
        if (element !is JSLiteralExpression) return IConstantValue.ConstantValueType.Unknown
        if (element.isStringLiteral) return IConstantValue.ConstantValueType.String
        if (element.isBooleanLiteral) return IConstantValue.ConstantValueType.Boolean
        if (element.isNumericLiteral) return IConstantValue.ConstantValueType.Numeric
        return IConstantValue.ConstantValueType.Unknown
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <T> tryConvertToArray(expectedType: Class<T>): Array<Any?>? {
        val arrayLiteral = element as? JSArrayLiteralExpression ?: return null
        val expressions = arrayLiteral.expressions
        val componentType = expectedType.componentType
        val result = java.lang.reflect.Array.newInstance(componentType, expressions.size) as Array<Any?>
        for (i in expressions.indices) {
            val value = ideaModule.spiFactory.createConstantValue(expressions[i])
            if (IConstantValue::class.java == componentType) {
                result[i] = value
            } else {
                result[i] = value.convertTo(componentType).orElse(null)
            }
        }
        return result
    }

    protected fun tryConvertToString(): String? {
        val literal = element as? JSLiteralExpression ?: return null
        return literal.takeIf { it.isStringLiteral }?.stringValue
    }

    protected fun tryConvertToNumber(requestedNumberType: Class<*>): Any? {
        val literal = element as? JSLiteralExpression ?: return null
        val value = literal.takeIf { it.isNumericLiteral }?.value ?: return null

        // currently number can only return BigInteger, Long or Double.
        // com.intellij.lang.javascript.psi.impl.JSLiteralExpressionImpl.getValue
        if (value is BigInteger) {
            return when (requestedNumberType) {
                Int::class.java, java.lang.Integer::class.java -> value.intValueExact()
                Float::class.java, java.lang.Float::class.java -> value.toFloat()
                Long::class.java, java.lang.Long::class.java -> value.toLong()
                Double::class.java, java.lang.Double::class.java -> value.toDouble()
                java.math.BigDecimal::class.java -> value.toBigDecimal()
                java.math.BigInteger::class.java -> value
                else -> null
            }
        }
        if (value is Long) {
            return when (requestedNumberType) {
                Int::class.java, java.lang.Integer::class.java -> value.toInt()
                Float::class.java, java.lang.Float::class.java -> value.toFloat()
                Long::class.java, java.lang.Long::class.java -> value
                Double::class.java, java.lang.Double::class.java -> value.toDouble()
                java.math.BigDecimal::class.java -> value.toBigDecimal()
                java.math.BigInteger::class.java -> value.toBigInteger()
                else -> null
            }
        }
        if (value is Double) {
            return when (requestedNumberType) {
                Int::class.java, java.lang.Integer::class.java -> value.toInt()
                Float::class.java, java.lang.Float::class.java -> value.toFloat()
                Long::class.java, java.lang.Long::class.java -> value.toLong()
                Double::class.java, java.lang.Double::class.java -> value
                java.math.BigDecimal::class.java -> value.toBigDecimal()
                java.math.BigInteger::class.java -> value.toBigDecimal().toBigInteger()
                else -> null
            }
        }
        return null
    }

    protected fun tryConvertToBoolean(): Boolean? {
        val literal = element as? JSLiteralExpression ?: return null
        return literal.takeIf { it.isBooleanLiteral }?.value as? Boolean
    }

    protected fun tryConvertToObjectLiteral(): IObjectLiteral? {
        val literal = element as? JSObjectLiteralExpression ?: return null
        return ideaModule.spiFactory.createObjectLiteralExpression(literal).api()
    }
}