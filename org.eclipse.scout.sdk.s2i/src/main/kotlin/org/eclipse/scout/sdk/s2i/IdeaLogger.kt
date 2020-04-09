package org.eclipse.scout.sdk.s2i

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.util.Disposer
import org.eclipse.scout.sdk.core.log.ISdkConsoleSpi
import org.eclipse.scout.sdk.core.log.LogMessage
import org.eclipse.scout.sdk.core.log.SdkConsole
import org.eclipse.scout.sdk.core.util.Strings
import java.util.logging.Level

open class IdeaLogger : ISdkConsoleSpi, StartupActivity, DumbAware, Disposable {

    private val m_textLog = Logger.getInstance(IdeaLogger::class.java)
    private val m_balloonLog = NotificationGroup("Scout", NotificationDisplayType.BALLOON, true)
    private var m_previousConsoleSpi: ISdkConsoleSpi? = null

    /**
     * Executed on [Project] open
     */
    override fun runActivity(project: Project) {
        val existingConsoleSpi = SdkConsole.getConsoleSpi()
        if (existingConsoleSpi == this) {
            // there is already a project open which registered the logger already
            return
        }

        Disposer.register(project, this)
        m_previousConsoleSpi = existingConsoleSpi
        SdkConsole.setConsoleSpi(this)

        if (isRunningInSandbox()) {
            m_textLog.setLevel(org.apache.log4j.Level.ALL)
        }
    }

    protected fun isRunningInSandbox(): Boolean {
        val sandbox = "sandbox"
        return Strings.countMatches(PathManager.getPluginsPath(), sandbox) > 0
                || Strings.countMatches(PathManager.getConfigPath(), sandbox) > 0
                || Strings.countMatches(PathManager.getSystemPath(), sandbox) > 0
    }

    /**
     * Executed on [Project] close
     */
    override fun dispose() {
        SdkConsole.setConsoleSpi(m_previousConsoleSpi)
        m_previousConsoleSpi = null
    }

    override fun clear() {
        // nop
    }

    override fun isRelevant(level: Level): Boolean {
        if (level == Level.OFF) {
            return false
        }
        if (m_textLog.isDebugEnabled || m_textLog.isTraceEnabled) {
            return true // accept all
        }
        return level.intValue() >= Level.INFO.intValue()
    }

    override fun println(msg: LogMessage) {
        logToTextFile(msg)
        if (msg.severity() == Level.SEVERE || msg.severity() == Level.WARNING) {
            logToEventLogWindow(msg)
        }
    }

    protected fun logToEventLogWindow(msg: LogMessage) {
        val notification = m_balloonLog.createNotification(msg.text(), levelToMessageType(msg.severity()))
        notification.isImportant = msg.severity() == Level.SEVERE
        notification.notify(null)
    }

    protected fun logToTextFile(msg: LogMessage) {
        val exception = msg.firstThrowable().orElse(null)
        // do not log the prefix here as this information is already logged by the text logger
        when (msg.severity()) {
            Level.SEVERE -> m_textLog.error(msg.text(), exception)
            Level.WARNING -> m_textLog.warn(msg.text(), exception)
            Level.INFO -> m_textLog.info(msg.text(), exception)
            else -> m_textLog.debug(msg.text(), exception)
        }
    }

    protected fun levelToMessageType(level: Level): MessageType = when (level) {
        Level.WARNING -> MessageType.WARNING
        Level.SEVERE -> MessageType.ERROR
        else -> MessageType.INFO
    }
}
