package venkman

import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import javax.swing.*
import javax.swing.JFrame.EXIT_ON_CLOSE

class MainWindow(val app: VenkmanApp) {
    internal val frame: JFrame
    private val urlInput = JTextField()
    private val methodSelector = JComboBox(arrayOf<Any>("GET", "POST", "PUT", "HEAD", "DELETE"))
    private val sendButton = JButton(object : AbstractAction("Send") {
        override fun actionPerformed(e: ActionEvent) {
            val requestModel = RequestModel(urlInput.text, listOf(), methodSelector.selectedItem as String)
            app.send(requestModel)
        }
    })
    private val responseView: ResponseView = ResponseView(app)
    internal var loading: Boolean = false
        set(value) {
            urlInput.isEnabled = !value
            sendButton.isEnabled = !value
        }


    init {
        this.frame = JFrame("Venkman")
        frame.defaultCloseOperation = EXIT_ON_CLOSE
        val contentPane = frame.contentPane
        contentPane.add(createNavBar(), BorderLayout.NORTH)
        contentPane.add(responseView, BorderLayout.CENTER)
        val rootPane = frame.rootPane
        registerKeyBinding(rootPane, KeyEvent.VK_PLUS, InputEvent.CTRL_MASK, "Increase Font Size", { e -> changeFontSize(1.2) })
        registerKeyBinding(rootPane, KeyEvent.VK_MINUS, InputEvent.CTRL_MASK, "Decrease Font Size", { e -> changeFontSize(1.0 / 1.2) })
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
        urlInput.text = "http://blablabla.de:8080/main/entpunkt"
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


}
