package venkman

import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import javax.swing.*

class MainWindow(val app: VenkmanApp) : JFrame("Venkman") {
    private val urlInput = JTextField()
    private val methodSelector = JComboBox(arrayOf<Any>("GET", "POST", "PUT", "HEAD", "DELETE"))
    private val headersTable = HeadersTable()

    private val sendButton = JButton(object : AbstractAction("Send") {
        override fun actionPerformed(e: ActionEvent) {
            val requestModel = RequestModel(urlInput.text, headersTable.getHeaders(), methodSelector.selectedItem as String)
            app.send(requestModel)
        }
    })

    private val responseView: ResponseView = ResponseView(app)

    init {
        app.addListener(this::modelChanged)
        defaultCloseOperation = EXIT_ON_CLOSE
        val northPanel = JPanel(BorderLayout())
        northPanel.add(createNavBar(), BorderLayout.NORTH)
        northPanel.add(headersTable, BorderLayout.CENTER)
        contentPane.add(northPanel, BorderLayout.NORTH)
        contentPane.add(responseView, BorderLayout.CENTER)
        registerKeyBinding(rootPane, KeyEvent.VK_PLUS, InputEvent.CTRL_MASK, "Increase Font Size", { e -> changeFontSize(1.2) })
        registerKeyBinding(rootPane, KeyEvent.VK_MINUS, InputEvent.CTRL_MASK, "Decrease Font Size", { e -> changeFontSize(1.0 / 1.2) })
        pack()
        isVisible = true
    }

    fun modelChanged(model: ResponseModel) {
        urlInput.isEnabled = !model.loading
        sendButton.isEnabled = !model.loading
    }

    private fun changeFontSize(factor: Double) {
        val font = contentPane.font
        val fontSizePt = font.size.toFloat()
        var increasedFontSizePt = (fontSizePt * factor).toInt().toFloat()
        if (increasedFontSizePt == fontSizePt) {
            ++increasedFontSizePt
        }
        val increasedFont = font.deriveFont(increasedFontSizePt)
        setFontRecursive(contentPane, increasedFont, factor)
        pack()
    }

    private fun setFontRecursive(c: Component, font: Font, factor: Double) {
        if (Container::class.java.isAssignableFrom(c.javaClass)) {
            val children = (c as Container).components
            for (child in children) {
                setFontRecursive(child, font, factor)
            }
        }
        if (JTable::class.java.isAssignableFrom(c.javaClass)) {
            val table = c as JTable
            table.rowHeight = (table.rowHeight.toFloat() * factor).toInt()
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
