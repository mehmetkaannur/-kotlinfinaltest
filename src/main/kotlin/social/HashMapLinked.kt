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
            val valueList: MutableList<V> = mutableListOf()
            var currentNode: Node<K, V>? = head

            while (currentNode != null) {
                valueList.add(currentNode.value)
                currentNode = currentNode.next
            }
            return valueList
        }

    override fun containsKey(key: K): Boolean {
        return key in getBucket(key).map { it.key }
    }

    override fun remove(key: K): V? {
        if (!containsKey(key)) {
            return null
        }
        val targetBucket = getBucket(key)
        val nodeToReturn = targetBucket.first { it.key == key }

        if (nodeToReturn.prev == null && nodeToReturn.next == null) {
            this.head = null
            this.tail = null
        } else if (nodeToReturn.prev == null) {
            this.head = nodeToReturn.next
            this.head!!.prev = null
        } else if (nodeToReturn.next == null) {
            this.tail = nodeToReturn.prev
            this.tail!!.next = null
        } else {
            val prevNode = nodeToReturn.prev
            val nextNode = nodeToReturn.next
            prevNode!!.next = nextNode
            nextNode!!.prev = prevNode
        }
        targetBucket.remove(nodeToReturn)
        size--
        return nodeToReturn.value
    }

    override operator fun set(key: K, value: V): V? {
        val removedNodeValue = remove(key)
        val newNode = Node(key, value, null, null)
        val targetBucket = getBucket(key)

        if (size == 0) {
            this.head = newNode
            this.tail = newNode
        } else {
            newNode.prev = this.tail
            this.tail!!.next = newNode
            this.tail = newNode
        }
        targetBucket.add(newNode)
        size++
        resize()
        return removedNodeValue
    }

    override fun removeLongestStandingEntry(): Pair<K, V>? {
        if (size == 0) {
            return null
        }
        val headKey = head!!.key
        val headValue = this.remove(headKey)

        return Pair(headKey, headValue!!)
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
