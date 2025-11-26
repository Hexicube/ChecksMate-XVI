class MoveIterator(private val moves: MoveList) : Iterator<Move> {
    private var pos = 0
    override fun next() = Move(moves[pos++])
    override fun hasNext() = pos < moves.used
}

class MoveList() : Iterable<Move> {
    companion object {
        val MAX = 3000 // TODO: find an appropriate limit
    }

    private val data = ULongArray(MAX)
    var used = 0

    //fun asArray(): Array<Move> = Arrays.copyOf(data, used)

    operator fun plusAssign(move: Move){
        //if (used >= MAX) throw IllegalStateException("Array full")
        data[used++] = move.data
    }

    operator fun get(idx: Int) = data[idx]

    fun reset() {
        used = 0
    }

    override fun iterator() = MoveIterator(this)
}