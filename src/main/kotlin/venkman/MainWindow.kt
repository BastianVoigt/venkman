package venkman

import org.apache.http.client.methods.*
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.io.IOException
import java.io.UncheckedIOException
import javax.swing.*
import javax.swing.JFrame.EXIT_ON_CLOSE

class MainWindow() {
    private val frame: JFrame
    private var responseView: JTextArea? = null

    init {
        this.frame = JFrame("Venkman")
        frame.defaultCloseOperation = EXIT_ON_CLOSE
        val contentPane = frame.contentPane
        contentPane.add(createNavBar(), BorderLayout.NORTH)
        contentPane.add(createResponseView(), BorderLayout.CENTER)
        val rootPane = frame.rootPane
        registerKeyBinding(rootPane, KeyEvent.VK_PLUS, InputEvent.CTRL_MASK, "Increase Font Size", {e -> changeFontSize(1.2)})
        registerKeyBinding(rootPane, KeyEvent.VK_MINUS, InputEvent.CTRL_MASK, "Decrease Font Size", {e -> changeFontSize(1.0 / 1.2)})
        frame.pack()
        frame.isVisible = true
    }

    private fun changeFontSize(factor: Double) {
        val contentPane = frame.contentPane
        val font = contentPane.font
        val fontSizePt = font.size.toFloat()
        var increasedFontSizePt = (fontSizePt * factor).toInt().toFloat()
        if (increasedFontSizePt == fontSizePt) {
            ++increasedFontSizePt
        }
        val increasedFont = font.deriveFont(increasedFontSizePt)
        setFontRecursive(contentPane, increasedFont)
        frame.pack()
    }

    private fun setFontRecursive(c: Component, font: Font) {
        if (Container::class.java.isAssignableFrom(c.javaClass)) {
            val children = (c as Container).components
            for (child in children) {
                setFontRecursive(child, font)
            }
        }
        c.font = font
    }

    private fun registerKeyBinding(rootPane: JRootPane, keyEvent: Int, modifier: Int, actionName: String, action: (ActionEvent) -> Unit) {
        val keyStroke = KeyStroke.getKeyStroke(keyEvent, modifier)
        rootPane.inputMap.put(keyStroke, actionName)
        rootPane.actionMap.put(actionName, object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent) {
                action(e)
            }
        })
    }

    private fun createNavBar(): JPanel {
        val methodSelector = JComboBox(arrayOf<Any>("GET", "POST", "PUT", "HEAD", "DELETE"))
        val urlInput = JTextField()
        urlInput.text = "http://blablabla.de:8080/main/entpunkt"
        val sendButton = JButton(SendAction(methodSelector, urlInput))
        val layout = GridBagLayout()
        val navBar = JPanel(layout)
        val constraints = GridBagConstraints()
        constraints.gridx = 0
        constraints.gridy = 0
        constraints.gridheight = 1
        constraints.gridwidth = 1
        constraints.weightx = 0.0
        navBar.add(methodSelector, constraints)
        constraints.gridx = 1
        constraints.weightx = 2.0
        constraints.fill = GridBagConstraints.HORIZONTAL
        navBar.add(urlInput, constraints)
        constraints.weightx = 0.0
        constraints.gridx = 2
        constraints.fill = GridBagConstraints.NONE
        navBar.add(sendButton, constraints)
        return navBar
    }

    private fun createRequest(method: String, url: String): HttpUriRequest {
        when (method) {
            "GET" -> return HttpGet(url)
            "POST" -> return HttpPost(url)
            "PUT" -> return HttpPut(url)
            "HEAD" -> return HttpHead(url)
            "DELETE" -> return HttpDelete(url)
            else -> throw IllegalArgumentException("Invalid method " + method)
        }
    }

    private fun createResponseView(): JComponent {
        responseView = JTextArea()
        responseView!!.rows = 20
        responseView!!.text = "Hier steht dann die Response"
        return JScrollPane(responseView)
    }

    private inner class SendAction(private val methodSelector: JComboBox<*>, private val urlInput: JTextField) : AbstractAction("Send") {
        override fun actionPerformed(e: ActionEvent) {
            responseView!!.text = ""
            try {
                HttpClientBuilder.create()
                        .disableRedirectHandling()
                        .build().use { httpClient ->
                    val request = createRequest(methodSelector.selectedItem as String, urlInput.text)
                    httpClient.execute(request).use { response ->
                        val responseString = EntityUtils.toString(response.entity)
                        responseView!!.text = responseString
                    }
                }
            } catch (e1: IOException) {
                throw UncheckedIOException(e1)
            }

        }
    }
}
