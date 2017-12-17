package venkman

import java.awt.BorderLayout
import java.awt.BorderLayout.*
import java.awt.Color
import javax.swing.*

class ResponseView(app: VenkmanApp) : JPanel(BorderLayout(), false) {

    val statusCodeView: JLabel = JLabel()
    val headersView: ResponseHeadersView = ResponseHeadersView()
    val bodyView: JTextArea = JTextArea(20, 20)

    init {
        val northPanel = JPanel(BorderLayout())
        app.addListener(this::modelChanged)
        northPanel.add(statusCodeView, NORTH)
        northPanel.add(headersView, CENTER)
        add(northPanel, NORTH)
        add(JScrollPane(bodyView), CENTER)
    }

    fun modelChanged(model: ResponseModel) {
        statusCodeView.text = model.protocolVersion + ' ' + model.statusCode.toString() + ' ' + model.statusReasonPhrase
        statusCodeView.isOpaque = true
        if(model.statusCode < 300) {
            statusCodeView.background = Color.GREEN
        } else if(model.statusCode < 400) {
            statusCodeView.background = Color.YELLOW
        } else {
            statusCodeView.background = Color.RED
        }
        bodyView.text = model.body
        headersView.modelChanged(model)
    }
}