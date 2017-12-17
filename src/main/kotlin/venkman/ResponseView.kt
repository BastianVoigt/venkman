package venkman

import java.awt.BorderLayout
import java.awt.BorderLayout.*
import javax.swing.*
import javax.swing.table.AbstractTableModel

class ResponseView(app: VenkmanApp) : JPanel(BorderLayout(), false) {

    val statusCodeView: JLabel = JLabel()
    val headersView: HeadersModel = HeadersModel()
    val bodyView: JTextArea = JTextArea(20, 20)

    init {
        val northPanel = JPanel(BorderLayout())
        app.addListener(this::modelChanged)
        northPanel.add(statusCodeView, NORTH)
        northPanel.add(JTable(headersView), CENTER)
        add(northPanel, NORTH)
        add(JScrollPane(bodyView), CENTER)
    }

    fun modelChanged(model: ResponseModel) {
        statusCodeView.text = model.protocolVersion + ' ' + model.statusCode.toString() + ' ' + model.statusReasonPhrase
        bodyView.text = model.body
        headersView.headers.clear()
        headersView.headers.addAll(model.headers)
    }

    class HeadersModel : AbstractTableModel() {
        val headers: MutableList<Pair<String, String>> = mutableListOf()
        override fun getRowCount(): Int {
            return headers.size
        }

        override fun getColumnCount(): Int {
            return 2
        }

        override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
            if (columnIndex == 0)
                return headers[rowIndex].first
            return headers[rowIndex].second
        }

        override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
            return false
        }
    }
}