package venkman

import javax.swing.UIManager

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        System.setProperty("awt.useSystemAAFontSettings", "on")
        System.setProperty("swing.aatext", "true")
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        VenkmanApp()
    }
}