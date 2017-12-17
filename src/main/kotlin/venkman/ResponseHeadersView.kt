package venkman

import java.awt.Font
import javax.swing.JTable
import javax.swing.JTextField
import javax.swing.SwingUtilities
import javax.swing.table.AbstractTableModel
import javax.swing.table.TableCellEditor
import javax.swing.DefaultCellEditor


class ResponseHeadersView : JTable() {
    val model = HeadersModel()
    private var myCellEditor: DefaultCellEditor? = null

    init {
        setModel(model)
        val textfield = JTextField()
        textfield.isEditable = false
        textfield.border = null
        myCellEditor = DefaultCellEditor(textfield)
    }

    override fun getCellEditor(row: Int, column: Int): TableCellEditor {
        return myCellEditor!!;
    }

    override fun changeSelection(row: Int, column: Int, toggle: Boolean, extend: Boolean) {
        super.changeSelection(row, column, toggle, extend);
        SwingUtilities.invokeLater {
            if ((getCellEditor(row, column) != null && !editCellAt(row, column))) {
                val textfield : JTextField = myCellEditor!!.component as JTextField;
                textfield.selectAll();
            }
        }
    }

    override fun setFont(font: Font?) {
        super.setFont(font)
        myCellEditor?.component?.setFont(font)
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
            return true
        }
    }

    fun modelChanged(responseModel: ResponseModel) {
        model.headers = responseModel.headers
    }
}