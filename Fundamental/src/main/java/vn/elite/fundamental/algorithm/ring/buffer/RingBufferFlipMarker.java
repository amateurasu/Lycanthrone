package vn.elite.fundamental.algorithm.ring.buffer;

import lombok.Getter;

@Getter
public class RingBufferFlipMarker<T> {

    private T[] elements;
    private int capacity;
    private int writePos = 0;
    private int readPos = 0;
    private boolean flipped = false;

    @SuppressWarnings("unchecked")
    public RingBufferFlipMarker(int capacity) {
        this.capacity = capacity;
        elements = (T[]) new Object[capacity];
    }

    public void reset() {
        writePos = 0;
        readPos = 0;
        flipped = false;
    }

    public int available() {
        return flipped
            ? capacity - readPos + writePos
            : writePos - readPos;
    }

    public int remainingCapacity() {
        return flipped
            ? readPos - writePos
            : capacity - writePos;
    }

    public boolean put(T element) {
        if (!flipped) {
            if (writePos == capacity) {
                writePos = 0;
                flipped = true;

                if (writePos < readPos) {
                    elements[writePos++] = element;
                    return true;
                }
                return false;
            } else {
                elements[writePos++] = element;
                return true;
            }
        }
        if (writePos < readPos) {
            elements[writePos++] = element;
            return true;
        }
        return false;
    }

    public int put(T[] newElements, int length) {
        int newElementsReadPos = 0;
        if (!flipped) {
            //readPos lower than writePos - free sections are:
            //1) from writePos to capacity
            //2) from 0 to readPos

            if (length <= capacity - writePos) {
                //new elements fit into top of elements array - copy directly
                while (newElementsReadPos < length) {
                    elements[writePos++] = newElements[newElementsReadPos++];
                }
            } else {
                //new elements must be divided between top and bottom of elements array

                //writing to top
                while (writePos < capacity) {
                    elements[writePos++] = newElements[newElementsReadPos++];
                }

                //writing to bottom
                writePos = 0;
                flipped = true;
                int endPos = Math.min(readPos, length - newElementsReadPos);
                while (writePos < endPos) {
                    elements[writePos++] = newElements[newElementsReadPos++];
                }
            }
        } else {
            //readPos higher than writePos - free sections are:
            //1) from writePos to readPos

            int endPos = Math.min(readPos, writePos + length);
            while (writePos < endPos) {
                elements[writePos++] = newElements[newElementsReadPos++];
            }
        }
        return newElementsReadPos;
    }

    public T take() {
        if (!flipped) {
            return readPos < writePos ? elements[readPos++] : null;
        }
        if (readPos != capacity) {
            return elements[readPos++];
        }
        readPos = 0;
        flipped = false;

        return readPos < writePos ? elements[readPos++] : null;
    }

    public int take(T[] into, int length) {
        int intoWritePos = 0;
        if (!flipped) {
            //writePos higher than readPos - available section is writePos - readPos
            int endPos = Math.min(writePos, readPos + length);
            while (readPos < endPos) {
                into[intoWritePos++] = elements[readPos++];
            }
            return intoWritePos;
        }

        //readPos higher than writePos - available sections are top + bottom of elements array
        if (length <= capacity - readPos) {
            //length is lower than the elements available at the top of the elements array - copy directly
            while (intoWritePos < length) {
                into[intoWritePos++] = elements[readPos++];
            }
        } else {
            //length is higher than elements available at the top of the elements array
            //split copy into a copy from both top and bottom of elements array.

            //copy from top
            while (readPos < capacity) {
                into[intoWritePos++] = elements[readPos++];
            }

            //copy from bottom
            readPos = 0;
            flipped = false;
            int endPos = Math.min(writePos, length - intoWritePos);
            while (readPos < endPos) {
                into[intoWritePos++] = elements[readPos++];
            }
        }
        return intoWritePos;
    }
}
