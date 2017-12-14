package venkman

import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.NORTH
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextArea

class ResponseView(val app: VenkmanApp) : JPanel(BorderLayout(), false) {

    val statusCodeView: JLabel = JLabel()
    val bodyView: JTextArea = JTextArea(20, 20)

    init {
        app.addListener(this::modelChanged)
        add(statusCodeView, NORTH)
        add(bodyView, CENTER)
    }

    fun modelChanged(model: ResponseModel) {
        statusCodeView.text = model.protocolVersion + ' ' + model.statusCode.toString() + ' ' + model.statusReasonPhrase
        bodyView.text = model.body
    }
}