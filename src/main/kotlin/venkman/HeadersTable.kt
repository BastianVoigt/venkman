package venkman

import java.awt.BorderLayout
import java.util.*
import javax.swing.DefaultCellEditor
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.table.AbstractTableModel


class HeadersTable : JPanel(BorderLayout()) {
    private val headers: MutableList<Pair<String, String>> = mutableListOf(Pair("Accept", "application/json"))
    private val model = MyTableModel(headers)

    init {
        val table = object : JTable(model) {
            override fun editCellAt(row: Int, column: Int, eventObject: EventObject): Boolean {
                if (row == headers.size - 1) {
                    (model as MyTableModel).add(Pair("", ""))
                }
                return super.editCellAt(row, column, eventObject)
            }
        }
        for (i in 0 until table.columnCount) {
            (table.getDefaultEditor(table.getColumnClass(i)) as DefaultCellEditor).clickCountToStart = 1
        }
        add(table.tableHeader, BorderLayout.NORTH)
        add(table, BorderLayout.CENTER)
    }

    fun getHeaders(): List<Pair<String, String>> {
        return headers
                .filter({ h -> !(h.first.isEmpty() || h.second.isEmpty()) })
                .toList()
    }

    class MyTableModel(val data: MutableList<Pair<String, String>>) : AbstractTableModel() {
        override fun getRowCount(): Int {
            return data.size
        }

        override fun getColumnCount(): Int {
            return 2
        }

        override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
            when (columnIndex) {
                0 -> return data[rowIndex].first
                1 -> return data[rowIndex].second
            }
            throw IllegalArgumentException("Column index " + columnIndex + " out of bounds")
        }

        fun add(header: Pair<String, String>) {
            data.add(header)
            fireTableRowsInserted(data.size, data.size)
        }

        override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
            if (columnIndex == 0) {
                data[rowIndex] = data[rowIndex].copy(first = aValue as String)
            } else {
                data[rowIndex] = data[rowIndex].copy(second = aValue as String)
            }
            fireTableCellUpdated(rowIndex, columnIndex)
        }

        override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
            return true
        }

        override fun getColumnName(column: Int): String {
            if (column == 0) {
                return "Key"
            }
            return "Value"
        }
    }
}