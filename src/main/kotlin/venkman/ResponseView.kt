package venkman

import java.awt.BorderLayout
import java.awt.BorderLayout.*
import java.awt.Color
import javax.swing.*
import javax.swing.text.DefaultCaret

class ResponseView(app: VenkmanApp) : JPanel(BorderLayout(), false) {

    val statusCodeView: JLabel = JLabel()
    val headersView: ResponseHeadersView = ResponseHeadersView()
    val bodyView: JTextArea = JTextArea(20, 20)

    init {
        bodyView.isEditable = false
        (bodyView.caret as DefaultCaret).updatePolicy = DefaultCaret.NEVER_UPDATE
        val northPanel = JPanel(BorderLayout())
        app.addListener(this::modelChanged)
        northPanel.add(statusCodeView, NORTH)
        northPanel.add(headersView, CENTER)
        add(northPanel, NORTH)
        add(JScrollPane(bodyView), CENTER)
    }

    fun modelChanged(model: ResponseModel) {
        if (model.loading) {
            statusCodeView.text = "Loading ..."
            statusCodeView.isOpaque = false
        } else {
            if (model.statusCode != 0) {
                statusCodeView.text = model.protocolVersion + ' ' + model.statusCode.toString() + ' ' + model.statusReasonPhrase

            } else {
                statusCodeView.text = "";
            }
            if (model.statusCode < 200) {
                statusCodeView.isOpaque = false
            } else if (model.statusCode < 300) {
                statusCodeView.background = Color.GREEN
                statusCodeView.isOpaque = true
            } else if (model.statusCode < 400) {
                statusCodeView.background = Color.YELLOW
                statusCodeView.isOpaque = true
            } else {
                statusCodeView.background = Color.RED
                statusCodeView.isOpaque = true
            }
        }
        bodyView.text = model.body
        headersView.modelChanged(model)
    }
}