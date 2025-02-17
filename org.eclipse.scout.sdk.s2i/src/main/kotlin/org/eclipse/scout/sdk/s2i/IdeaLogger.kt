/*
 * Copyright (c) 2010, 2023 BSI Business Systems Integration AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.scout.sdk.s2i

import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.ControlFlowException
import com.intellij.openapi.diagnostic.LogLevel
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.IndexNotReadyException
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.JBInsets
import org.eclipse.scout.sdk.core.log.ISdkConsoleSpi
import org.eclipse.scout.sdk.core.log.LogMessage
import org.eclipse.scout.sdk.core.log.SdkConsole
import org.eclipse.scout.sdk.core.util.Strings
import org.eclipse.scout.sdk.s2i.EclipseScoutBundle.message
import java.util.logging.Level
import javax.swing.ScrollPaneConstants

open class IdeaLogger : ISdkConsoleSpi, StartupActivity, DumbAware {

    private val m_textLog = Logger.getInstance(IdeaLogger::class.java)
    private val m_balloonLog = NotificationGroupManager.getInstance().getNotificationGroup("scout.notification")

    /**
     * Executed on [Project] open
     */
    override fun runActivity(project: Project) {
        SdkConsole.setConsoleSpi(this)
        if (EclipseScoutBundle.isRunningInSandbox()) {
            // set log level to DEBUG for plugin development
            m_textLog.setLevel(LogLevel.DEBUG)
        }
    }

    override fun clear() {
        // nop
    }

    override fun isEnabled(level: Level): Boolean {
        if (level == Level.OFF) {
            return false
        }
        if (level == Level.FINE) {
            return m_textLog.isDebugEnabled
        }
        if (level.intValue() < Level.FINE.intValue()) {
            return m_textLog.isTraceEnabled
        }
        return true
    }

    override fun println(msg: LogMessage) {
        logToTextFile(msg)
        if (msg.severity() == Level.SEVERE || msg.severity() == Level.WARNING) {
            logToEventLogWindow(msg)
        }
    }

    protected fun logToEventLogWindow(msg: LogMessage) {
        var text = msg.text()
        val primaryThrowable = msg.firstThrowable()
        if (Strings.isBlank(text) && primaryThrowable.isPresent) {
            text = Strings.notBlank(primaryThrowable.orElseThrow().message).orElse("Error")
        }
        if (Strings.isBlank(text)) {
            return // no balloon for empty log message
        }
        val notification = m_balloonLog.createNotification(text, levelToNotificationType(msg.severity()))
        primaryThrowable.ifPresent { notification.addAction(ShowThrowableAction(it)) }
        notification.isImportant = msg.severity() == Level.SEVERE
        notification.notify(null)
    }

    private class ShowThrowableAction(private val throwable: Throwable) : NotificationAction(message("details") + "...") {
        override fun actionPerformed(e: AnActionEvent, notification: Notification) {
            val pos = JBPopupFactory.getInstance().guessBestPopupLocation(e.dataContext)
            val detailsText = Strings.fromThrowable(throwable)
            val textArea = JBTextArea(detailsText, 40, 150)
            textArea.isEditable = false
            val scrollPane = JBScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED)
            val balloon = JBPopupFactory.getInstance().createBalloonBuilder(scrollPane)
                .setSmallVariant(true)
                .setBorderInsets(JBInsets.create(0, 0))
                .setHideOnClickOutside(true)
                .setHideOnKeyOutside(true)
                .setHideOnAction(false)
                .createBalloon()
            balloon.setAnimationEnabled(false)
            balloon.show(pos, Balloon.Position.atRight)
        }
    }

    protected fun shouldBeRethrown(t: Throwable) = t is ControlFlowException // ControlFlowExceptions should not be logged. See com.intellij.openapi.diagnostic.Logger.checkException
            || t is IndexNotReadyException

    protected fun logToTextFile(msg: LogMessage) {
        val toRethrow = msg.throwableList().firstOrNull { shouldBeRethrown(it) }
        val toLog = msg.throwableList().firstOrNull { !shouldBeRethrown(it) }
        if (toRethrow != null && toLog == null) {
            throw toRethrow
        }

        // do not log the prefix here as this information is already logged by the text logger
        when (msg.severity()) {
            Level.SEVERE -> m_textLog.warn(msg.text(), toLog) // do not use m_textLog.error() because this is registered as fatal plugin error
            Level.WARNING -> m_textLog.warn(msg.text(), toLog)
            Level.INFO -> m_textLog.info(msg.text(), toLog)
            Level.FINE -> m_textLog.debug(msg.text(), toLog)
            else -> {
                m_textLog.trace(msg.text())
                toLog?.let { m_textLog.trace(it) }
            }
        }
        if (toRethrow != null) {
            // there was a loggable throwable and an exception to rethrow. The loggable has already been reported.
            // now throw so that both have been handled
            throw toRethrow
        }
    }

    protected fun levelToNotificationType(level: Level) = when (level) {
        Level.WARNING -> NotificationType.WARNING
        Level.SEVERE -> NotificationType.ERROR
        else -> NotificationType.INFORMATION
    }
}
