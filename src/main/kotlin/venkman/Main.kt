package venkman

import javax.swing.UIManager
import javax.swing.UnsupportedLookAndFeelException

object Main {
    @Throws(ClassNotFoundException::class, UnsupportedLookAndFeelException::class, InstantiationException::class, IllegalAccessException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        System.setProperty("awt.useSystemAAFontSettings", "on")
        System.setProperty("swing.aatext", "true")
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        VenkmanApp()
    }
}