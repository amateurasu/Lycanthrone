package vn.elite.fundamental.algorithm.ring.buffer;

import lombok.Getter;

/**
 * Insignificantly better performance
 *
 * @param <T>
 */
@Getter
public class RingBufferFillCount<T> {
    private T[] elements;

    private int capacity;
    private int writePos = 0;
    private int available = 0;

    @SuppressWarnings("unchecked")
    public RingBufferFillCount(int capacity) {
        this.capacity = capacity;
        elements = (T[]) new Object[capacity];
    }

    public void reset() {
        writePos = 0;
        available = 0;
    }

    public int remainingCapacity() {
        return capacity - available;
    }

    public boolean put(T element) {
        if (available >= capacity) {
            return false;
        }
        if (writePos >= capacity) {
            writePos = 0;
        }
        elements[writePos] = element;
        writePos++;
        available++;
        return true;
    }

    public int put(T[] newElements) {
        return put(newElements, newElements.length);
    }

    public int put(T[] newElements, int length) {
        int readPos = 0;
        if (writePos > available) {
            //space above writePos is all empty
            if (length <= capacity - writePos) {
                //space above writePos is sufficient to insert batch
                while (readPos < length) {
                    elements[writePos++] = newElements[readPos++];
                }
                available += readPos;
                return length;
            }

            // both space above writePos and below writePos is necessary to use to insert batch.
            while (writePos < capacity) {
                elements[writePos++] = newElements[readPos++];
            }

            //fill into bottom of array too.
            writePos = 0;

            int endPos = Math.min(length - readPos, capacity - available - readPos);
            while (writePos < endPos) {
                elements[writePos++] = newElements[readPos++];
            }
            available += readPos;
            return readPos;
        }

        int endPos = capacity - available + writePos;

        while (writePos < endPos) {
            elements[writePos++] = newElements[readPos++];
        }
        available += readPos;
        return readPos;
    }

    public T take() {
        if (available == 0) {
            return null;
        }
        int nextSlot = writePos - available;
        if (nextSlot < 0) {
            nextSlot += capacity;
        }
        T nextObj = elements[nextSlot];
        available--;
        return nextObj;
    }

    public int take(T[] into) {
        return take(into, into.length);
    }

    public int take(T[] into, int length) {
        int intoPos = 0;

        if (available <= writePos) {
            int nextPos = writePos - available;
            int endPos = nextPos + Math.min(available, length);

            while (nextPos < endPos) {
                into[intoPos++] = elements[nextPos++];
            }
            available -= intoPos;
            return intoPos;
        }
        int nextPos = writePos - available + capacity;

        int leftInTop = capacity - nextPos;
        if (length <= leftInTop) {
            //copy directly
            while (intoPos < length) {
                into[intoPos++] = elements[nextPos++];
            }
            available -= length;
            return length;
        }
        //copy top
        while (nextPos < capacity) {
            into[intoPos++] = elements[nextPos++];
        }

        //copy bottom - from 0 to writePos
        nextPos = 0;
        int leftToCopy = length - intoPos;
        int endPos = Math.min(writePos, leftToCopy);

        while (nextPos < endPos) {
            into[intoPos++] = elements[nextPos++];
        }
        available -= intoPos;
        return intoPos;
    }
}
