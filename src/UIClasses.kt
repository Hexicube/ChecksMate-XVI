import java.awt.Color
import java.awt.Dimension
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

enum class UIIcon(val icon: ImageIcon, val col: Color) {
    COLLECTED(ImageIcon(ImageIO.read(File("images/icons/collected.png"))), Color(150, 220, 150)),
    AVAILABLE(ImageIcon(ImageIO.read(File("images/icons/available.png"))), Color(220, 220, 0)),
    HARD(ImageIcon(ImageIO.read(File("images/icons/hard.png"))), Color(220, 175, 175)),
    UNREACHABLE(ImageIcon(ImageIO.read(File("images/icons/unreachable.png"))), Color(125, 125, 125))
}

open class InfoEntry : JLabel() {
    init {
        isOpaque = true
        border = CompoundBorder(border, EmptyBorder(2, 2, 2, 0))
        setDisplay("", UIIcon.AVAILABLE)
    }

    override fun getPreferredSize() = Dimension(300, 24)
    override fun getMinimumSize() = preferredSize
    override fun getMaximumSize() = preferredSize

    fun setDisplay(str: String, img: UIIcon) {
        text = str
        icon = img.icon
        background = img.col
    }
}