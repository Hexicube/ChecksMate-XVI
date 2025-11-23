import java.util.Arrays

class MoveList() {
    companion object {
        val MAX = 3000 // TODO: find an appropriate limit
    }

    private val data = Array<Move?>(MAX) { null }
    var used = 0

    fun asArray(): Array<Move> = Arrays.copyOf(data, used)

    fun add(move: Move) {
        //if (used >= MAX) throw IllegalStateException("Array full")
        data[used++] = move
    }

    operator fun plusAssign(move: Move) = add(move)

    operator fun get(idx: Int) = data[idx]!!

    fun reset() {
        used = 0
    }
}