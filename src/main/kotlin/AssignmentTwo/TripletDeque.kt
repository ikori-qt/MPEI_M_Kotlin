package AssignmentTwo

import java.util.*

class TripletDeque<E>(
    private val containerMaxSize: Int = 5,
    private val queueMaxSize: Int = 1000
) : Deque<E> {

    constructor(queueMaxSize: Int) : this(containerMaxSize = 5, queueMaxSize = queueMaxSize)

    private class Container<E>(
        private val size: Int
    ) {
        val elements: Array<E?> = arrayOfNulls<Any>(size) as Array<E?>

        var next: Container<E>? = null
        var prev: Container<E>? = null

        var last: Int = 0
        var first: Int = 0
        var used: Int = 0

        /**
         * Циклическая индексация массива - прием, где, в массив фиксированной длинны, при желании добавить элемент
         * в начало или его конец, можно определить их положение индексом проходя круг
         */
        fun add(
            element: E?,
            isFirst: Boolean
        ): Boolean {
            if (used == size) return false

            if (used == 0) {
                elements[0] = element
            } else if (isFirst) {
                first = (first + size - 1) % size
                elements[first] = element
            } else {
                // Это нужно на случай если мы попнем первый элемент и добавим в конец
                last = (last + 1) % size
                elements[last] = element
            }

            used++
            return true
        }

        fun index(trueCounter: Int): Int = (first + trueCounter) % size
    }

    init {
        require(containerMaxSize > 0 && queueMaxSize > 0) {}
                //&& queueMaxSize.mod(containerMaxSize) == 0
    }

    // Сначала я думал сделать их nullable и не задавать сразу при создании TripletDeque, но потом подумал...
    // тут и так много методов, один пустой экземпляр малого класса в сравнении - мал.

    //А потом подумав еще раз, я подумал что при выполнении deque на последнем элементе нужно будет продумывать доп логику
    //оставления одного экземпляра. Палка о двух концах: или особая логика удаления или постоянная проверка на не null
    private var first: Container<E>? = null
    private var last: Container<E>? = null
    private var used: Int = 0

    fun initializeIfNeeded() {
        if (this.last == null && this.first == null) {
            this.last = Container<E>(containerMaxSize)
            this.first = this.last
        }
    }

    /**
     * Comments regarding methods are taken from Deque<E>
     */

    /**
     * Inserts the specified element at the front of this deque if it is possible to do so immediately
     * without violating capacity restrictions, throwing an IllegalStateException if no space
     * is currently available.
     * @throws IllegalStateException if there is no space left
     */
    override fun addFirst(e: E?) {
        if (!offerFirst(e)) throw IllegalStateException("TripletDeque: Deque's capacity is reached")
    }

    /**
     * Inserts the specified element at the end of this deque if it is possible to do so immediately
     * without violating capacity restrictions, throwing an IllegalStateException if no space
     * is currently available.
     * @throws IllegalStateException if there is no space left
     */
    override fun addLast(e: E?) {
        if (!offerLast(e)) throw IllegalStateException("TripletDeque: Deque's capacity is reached")
    }

    /**
     * Inserts the specified element at the front of this deque unless it would violate capacity
     * restrictions.
     *
     * @return true if an element was added, false otherwise
     */
    override fun offerFirst(e: E?): Boolean {
        if (this.used == this.queueMaxSize) return false
        initializeIfNeeded()

        val isEnough = this.first!!.add(e, true)
        if (!isEnough) {
            val tempContainer = Container<E>(containerMaxSize)
            this.first!!.prev = (tempContainer)
            tempContainer.next = this.first
            this.first = tempContainer
            this.first!!.add(e, true)
        }
        this.used++
        return true
    }

    /**
     * Inserts the specified element at the end of this deque unless it would violate capacity
     * restrictions.
     *
     * @return true if an element was added, false otherwise
     */
    override fun offerLast(e: E?): Boolean {
        if (this.used == this.queueMaxSize) return false
        initializeIfNeeded()

        val enough = this.last!!.add(e, false)
        if (!enough) {
            val tempContainer = Container<E>(containerMaxSize)
            this.last!!.next = (tempContainer)
            tempContainer.prev = this.last
            this.last = tempContainer
            this.last!!.add(e, false)
        }
        this.used++
        return true
    }

    /**
     * Retrieves and removes the first element of this deque.
     * This method differs from pollFirst only in that it throws an
     * exception if this deque is empty.
     *
     * @return the head of this deque
     * @throws NoSuchElementException if this deque is empty
     */
    override fun removeFirst(): E {
        if (this.used == 0) throw NoSuchElementException("TripletDeque: Deque is empty")
        return pollFirst() ?: throw NoSuchElementException("TripletDeque: Deque says it is NOT empty but [first] does not exist")
    }

    /**
     * Retrieves and removes the last element of this deque.
     * This method differs from pollLast only in that it throws an
     * exception if this deque is empty.
     *
     * @return the tail of this deque
     * @throws NoSuchElementException if this deque is empty
     */
    override fun removeLast(): E {
        if (this.used == 0) throw NoSuchElementException("TripletDeque: Deque is empty")
        return pollLast() ?: throw NoSuchElementException("TripletDeque: Deque says it is NOT empty but [last] does not exist")
    }

    private fun handleAnEmptyContainer(container: Container<E>) {
        val tempNext = container.next
        val tempPrev = container.prev

        //технически делает лишнюю операцию если вызвать при и так пустом листе

        if (tempPrev == null) {
            this.first = tempNext
        } else {
            tempPrev.next = tempNext
        }

        if (tempNext == null) {
            this.last = tempPrev
        }else {
            tempNext.prev = tempPrev
        }

        container.prev = null
        container.next = null
    }

    /**
     * Retrieves and removes the first element of this deque,
     * or returns null if this deque is empty.
     *
     * @return the head of this deque, or null if this deque is empty
     */
    override fun pollFirst(): E? {
        val linkCopy = this.first ?: return null

        val firstElement = linkCopy.elements[linkCopy.first]

        linkCopy.elements[linkCopy.first] = null
        linkCopy.first = (linkCopy.first + 1) % containerMaxSize
        linkCopy.used--
        this.used--

        if (linkCopy.used == 0) handleAnEmptyContainer(linkCopy)

        return firstElement
    }

    /**
     * Retrieves and removes the last element of this deque,
     * or returns null if this deque is empty.
     *
     * @return the tail of this deque, or null if this deque is empty
     */
    override fun pollLast(): E? {
        val linkCopy = this.last ?: return null

        val lastElement = linkCopy.elements[linkCopy.last]

        linkCopy.elements[linkCopy.last] = null
        linkCopy.last = (linkCopy.last - 1 + containerMaxSize) % containerMaxSize
        linkCopy.used--
        this.used--

        if (linkCopy.used == 0) handleAnEmptyContainer(linkCopy)

        return lastElement
    }

    /**
     * Retrieves, but does not remove, the first element of this deque.
     *
     * This method differs from peekFirst only in that it
     * throws an exception if this deque is empty.
     *
     * @return the head of this deque
     * @throws NoSuchElementException if this deque is empty
     */
    override fun getFirst(): E = peekFirst() ?: throw NoSuchElementException("TripletDeque: No elements available")

    /**
     * Retrieves, but does not remove, the last element of this deque.
     *
     * This method differs from peekLast only in that it
     * throws an exception if this deque is empty.
     *
     * @return the tail of this deque
     * @throws NoSuchElementException if this deque is empty
     */
    override fun getLast(): E = peekLast() ?: throw NoSuchElementException("TripletDeque: No elements available")

    /**
     * Retrieves, but does not remove, the first element of this deque,
     * or returns null if this deque is empty.
     *
     * @return the head of this deque, or null if this deque is empty
     */
    override fun peekFirst(): E? {
        if (this.used == 0) return null
        return this.first?.let { it.elements[it.first] }
    }

    /**
     * Retrieves, but does not remove, the last element of this deque,
     * or returns {@code null} if this deque is empty.
     *
     * @return the tail of this deque, or {@code null} if this deque is empty
     */
    override fun peekLast(): E? {
        if (this.used == 0) return null
        return this.last?.let { it.elements[it.last] }
    }

    /**
     * Supporting function meant to delete a position inside a container using an index
     */
    private fun targetedRemoval (container: Container<E>, position: Int) {
        //Эта часть берет все элементы с текущего до used и сдвигает их на 1ну назад. Таким образом решается цикличность
        for (i in position until container.used - 1) {
            container.elements[container.index(i)] =
                container.elements[container.index(i + 1)]
        }

        container.elements[container.last] = null
        container.used--
        this.used--

        container.last = (container.last + containerMaxSize - 1) % containerMaxSize

        if (container.used == 0) handleAnEmptyContainer(container)
    }

    /**
     * Removes the first occurrence of the specified element from this deque.
     * If the deque does not contain the element, it is unchanged.
     * More formally, removes the first element e such that
     * Objects.equals(o, e) (if such an element exists).
     * Returns true if this deque contained the specified element
     * (or equivalently, if this deque changed as a result of the call).
     *
     * @param o element to be removed from this deque, if present
     * @return true if an element was removed as a result of this call
     */
    override fun removeFirstOccurrence(o: Any?): Boolean {
        var tempContainer = this.first

        while (tempContainer != null) {
            for (i in 0 until tempContainer.used) {
                if (Objects.equals(o, tempContainer.elements[tempContainer.index(i)])) {
                    targetedRemoval(tempContainer, i)
                    return true
                }
            }
            tempContainer = tempContainer.next
        }
        return false
    }

    /**
     * Removes the last occurrence of the specified element from this deque.
     * If the deque does not contain the element, it is unchanged.
     * More formally, removes the last element e such that
     * Objects.equals(o, e) (if such an element exists).
     * Returns true if this deque contained the specified element
     * (or equivalently, if this deque changed as a result of the call).
     *
     * @param o element to be removed from this deque, if present
     * @return true if an element was removed as a result of this call
     */
    override fun removeLastOccurrence(o: Any?): Boolean {
        var tempContainer = this.last

        while (tempContainer != null) {
            for (i in tempContainer.used-1 downTo 0) {
                if (Objects.equals(o, tempContainer.elements[tempContainer.index(i)])) {
                    targetedRemoval(tempContainer, i)
                    return true
                }
            }
            tempContainer = tempContainer.prev
        }
        return false
    }

    /**
     * Inserts the specified element into the queue represented by this deque
     * (in other words, at the tail of this deque) if it is possible to do so
     * immediately without violating capacity restrictions, returning
     * true upon success and throwing an
     * IllegalStateException if no space is currently available.
     *
     * @param e the element to add
     * @return true (as specified by {@link Collection#add})
     * @throws IllegalStateException if the element cannot be added at this
     *         time due to capacity restrictions
     */
    override fun add(e: E?): Boolean {
        addLast(e)
        return true
    }

    /**
     * Inserts the specified element into the queue represented by this deque
     * (in other words, at the tail of this deque) if it is possible to do so
     * immediately without violating capacity restrictions, returning
     * true upon success and false if no space is currently
     * available.
     *
     * @param e the element to add
     * @return true if the element was added to this deque, else
     *         false
     */
    override fun offer(e: E?): Boolean = offerLast(e)

    /**
     * Retrieves and removes the head of the queue represented by this deque
     * (in other words, the first element of this deque).
     * This method differs from poll() only in that it
     * throws an exception if this deque is empty.
     *
     * @return the head of the queue represented by this deque
     * @throws NoSuchElementException if this deque is empty
     */
    override fun remove(): E = removeFirst()

    /**
     * Retrieves and removes the head of the queue represented by this deque
     * (in other words, the first element of this deque), or returns
     * null if this deque is empty.
     *
     * @return the first element of this deque, or null if
     *         this deque is empty
     */
    override fun poll(): E? = pollFirst()

    /**
     * Retrieves, but does not remove, the head of the queue represented by
     * this deque (in other words, the first element of this deque).
     *
     * @return the head of the queue represented by this deque
     * @throws NoSuchElementException if this deque is empty
     */
    override fun element(): E = getFirst()

    /**
     * Retrieves, but does not remove, the head of the queue represented by
     * this deque (in other words, the first element of this deque), or
     * returns null if this deque is empty.
     *
     * @return the head of the queue represented by this deque, or
     *         null if this deque is empty
     */
    override fun peek(): E? = peekFirst()

    /**
     * Adds all the elements in the specified collection at the end
     * of this deque, as if by calling offerLast on each one,
     * in the order that they are returned by the collection's iterator.
     *
     * An exception encountered while trying to add an element may result
     * in only some of the elements having been successfully added when
     * the associated exception is thrown.
     *
     * @param c the elements to be inserted into this deque
     * @return true if this deque changed as a result of the call
     * @throws IllegalStateException if not all the elements can be added at
     *         this time due to insertion restrictions
     */
    override fun addAll(c: Collection<E>): Boolean {
        if (this.queueMaxSize-this.used < c.size) throw IllegalStateException("TripletDeque: Attempting to add a Collection which size exceeds capacity")
        if (c.isEmpty()) return false
        for (i in c) {
            if (!offerLast(i)) break
        }
        return true
    }

    /**
     * Pushes an element onto the stack represented by this deque (in other
     * words, at the head of this deque) if it is possible to do so
     * immediately without violating capacity restrictions, throwing an
     * IllegalStateException if no space is currently available.
     *
     * @param e the element to push
     * @throws IllegalStateException if the element cannot be added at this
     *         time due to capacity restrictions
     */
    override fun push(e: E?) = addFirst(e)

    /**
     * Pops an element from the stack represented by this deque.  In other
     * words, removes and returns the first element of this deque.
     *
     * @return the element at the front of this deque (which is the top
     *         of the stack represented by this deque)
     * @throws NoSuchElementException if this deque is empty
     */
    override fun pop(): E = removeFirst()

    /**
     * Removes the first occurrence of the specified element from this deque.
     * If the deque does not contain the element, it is unchanged.
     * More formally, removes the first element e such that
     * Objects.equals(o, e) (if such an element exists).
     * Returns true if this deque contained the specified element
     * (or equivalently, if this deque changed as a result of the call).
     *
     * <p>This method is equivalent to {@link #removeFirstOccurrence(Object)}.
     *
     * @param o element to be removed from this deque, if present
     * @return true if an element was removed as a result of this call
     */
    override fun remove(o: E?): Boolean = removeFirstOccurrence(o)

    /**
     * Returns true if this deque contains the specified element.
     * More formally, returns true if and only if this deque contains
     * at least one element e such that Objects.equals(o, e).
     *
     * @param o element whose presence in this deque is to be tested
     * @return true if this deque contains the specified element
     */
    override fun contains(o: E?): Boolean {
        var tempLink = this.first
        while (tempLink != null) {
            for (i in 0 until tempLink.used) {
                if (Objects.equals(o, tempLink.elements[tempLink.index(i)])) return true
            }
            tempLink = tempLink.next
        }
        return false
    }

    /**
     * Returns an iterator over the elements in this deque in proper sequence.
     * The elements will be returned in order from first (head) to last (tail).
     *
     * @return an iterator over the elements in this deque in proper sequence
     */
    override fun iterator(): MutableIterator<E?> = object : MutableIterator<E?> {
        // Требуется такой синтакс почему?
        // MutableIterator == interface != object -> anonymous object implementing MutIt

        private var index = 0
        private var container = first
        //скорее всего при remove просят убрать то что только что получили при next, поэтому нужно хранить их данные
        private var prevIndex = -1
        private var prevContainer: Container<E>? = null

        // а в котлин builtin НЕТ ДОКУМЕНТАЦИИ, СПАСИБО!
        override fun remove() {
            val prevContainerLink = this.prevContainer ?: throw IllegalStateException("TripletDequeIterator: This method removes a seen (using next) entry. Are you removing nothing?")
            targetedRemoval(prevContainerLink, prevIndex)

            if (Objects.equals(prevContainerLink, this.container)) this.index--
            this.prevContainer = null
        }

        override fun next(): E? {
            val containerLink = this.container ?: throw NoSuchElementException("TripletDequeIterator: No elements available")
            val value = containerLink.elements[containerLink.index(index)]

            this.prevContainer = containerLink
            prevIndex = index
            index++

            if (this.index == containerLink.used) {
                this.container = containerLink.next
                this.index = 0
            }

            return value
        }

        override fun hasNext(): Boolean = container != null
    }

    override fun descendingIterator(): Iterator<E?> {
        throw UnsupportedOperationException()
    }

    override val size: Int
        get() = this.used

    override fun removeAll(elements: Collection<E?>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun retainAll(elements: Collection<E?>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun clear() {
        this.first = null
        this.last = null
        this.used = 0
    }

    override fun isEmpty(): Boolean {
        if (this.used == 0) {return true} else {return false}
    }

    override fun containsAll(elements: Collection<E?>): Boolean {
        throw UnsupportedOperationException()
    }
}