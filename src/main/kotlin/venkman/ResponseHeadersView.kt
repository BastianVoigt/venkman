package venkman

import javax.swing.JTable
import javax.swing.table.AbstractTableModel

class ResponseHeadersView : JTable() {
    val model = HeadersModel()

    init {
        setModel(model)
    }

    class HeadersModel : AbstractTableModel() {
        var headers: List<Pair<String, String>> = listOf()
            set(value) {
                field = value
                fireTableDataChanged()
            }

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

    fun modelChanged(responseModel: ResponseModel) {
        model.headers = responseModel.headers
    }
}