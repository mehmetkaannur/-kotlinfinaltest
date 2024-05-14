package social

private const val INITIAL_BUCKETS = 8
private const val LOAD_FACTOR = 0.75

class HashMapLinked<K, V> : OrderedMap<K, V> {

    private class Node<K, V>(
        val key: K,
        var value: V,
        var prev: Node<K, V>?,
        var next: Node<K, V>? = null,
    )

    private var head: Node<K, V>? = null
    private var tail: Node<K, V>? = null

    private var buckets: MutableList<MutableList<Node<K, V>>>

    init {
        buckets = mutableListOf()
        for (i in 0..<INITIAL_BUCKETS) {
            buckets.add(mutableListOf())
        }
    }

    override var size = 0
        private set

    override val values: List<V>
        get() {
            TODO("To be implemented")
        }

    override fun containsKey(key: K): Boolean {
        TODO("To be implemented")
    }

    override fun remove(key: K): V? {
        TODO("To be implemented")
    }

    override fun set(key: K, value: V): V? {
        TODO("To be implemented")
    }

    override fun removeLongestStandingEntry(): Pair<K, V>? {
        TODO("To be implemented")
    }

    private fun getBucket(key: K) = buckets[key.hashCode().mod(buckets.size)]

    private fun resize() {
        if (size <= LOAD_FACTOR * buckets.size) {
            return
        }
        val allContent = mutableListOf<Node<K, V>>()
        for (bucket in buckets) {
            allContent.addAll(bucket)
        }

        val newNumBuckets = buckets.size * 2

        buckets = mutableListOf()
        for (i in 0..<newNumBuckets) {
            buckets.add(mutableListOf())
        }

        for (node in allContent) {
            getBucket(node.key).add(node)
        }
    }
}
