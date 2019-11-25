package vn.elite.snatcher.examples;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

public enum ImageInfo {
    JPG("image/jpeg") {
        @Override
        protected ImageInfo check(InputStream input) throws IOException {
            byte[] data = new byte[12];
            while (true) {
                if (input.read(data, 0, 4) != 4) {
                    return null;
                }
                int marker = getShortBigEndian(data, 0);
                int size = getShortBigEndian(data, 2);
                if ((marker & 0xff00) != 0xff00) {
                    return null;// not a valid marker
                }
                if (marker == 0xffe0) { // APPx
                    if (size < 14) {
                        // not an APPx header as we know it, skip
                        skip(input, size - 2);
                        continue;
                    }
                    if (input.read(data, 0, 12) != 12) {
                        return null;
                    }
                    final byte[] APP0_ID = {0x4a, 0x46, 0x49, 0x46, 0x00};
                    if (equals(APP0_ID, 0, data, 0, 5)) {
                        if (data[7] == 1) {
                            setPhysicalWidthDpi(getShortBigEndian(data, 8));
                            setPhysicalHeightDpi(getShortBigEndian(data, 10));
                        } else if (data[7] == 2) {
                            int x = getShortBigEndian(data, 8);
                            int y = getShortBigEndian(data, 10);
                            setPhysicalWidthDpi((int) (x * 2.54f));
                            setPhysicalHeightDpi((int) (y * 2.54f));
                        }
                    }
                    skip(input, size - 14);
                } else if (collectComments && size > 2 && marker == 0xfffe) { // comment
                    size -= 2;
                    byte[] chars = new byte[size];
                    if (input.read(chars, 0, size) != size) {
                        return null;
                    }
                    String comment = new String(chars, StandardCharsets.ISO_8859_1);
                    if (comments == null) {
                        comments = new Vector<>();
                    }
                    comments.addElement(comment.trim());
                } else if (marker >= 0xffc0 && marker <= 0xffcf && marker != 0xffc4 && marker != 0xffc8) {
                    if (input.read(data, 0, 6) != 6) {
                        return null;
                    }
                    bitsPerPixel = (data[0] & 0xff) * (data[5] & 0xff);
                    progressive = marker == 0xffc2 || marker == 0xffc6 || marker == 0xffca || marker == 0xffce;
                    width = getShortBigEndian(data, 3);
                    height = getShortBigEndian(data, 1);
                    return this;
                } else {
                    skip(input, size - 2);
                }
            }
        }

        @Override
        public String extension() {
            return "jpg";
        }
    },

    GIF("image/gif") {
        @Override
        protected ImageInfo check(InputStream input) throws IOException {
            final byte[] GIF_MAGIC_87A = {0x46, 0x38, 0x37, 0x61};
            final byte[] GIF_MAGIC_89A = {0x46, 0x38, 0x39, 0x61};
            byte[] a = new byte[11]; // 4 from the GIF signature + 7 from the global header
            if (input.read(a) != 11) {
                return null;
            }
            if (!equals(a, 0, GIF_MAGIC_89A, 0, 4) && !equals(a, 0, GIF_MAGIC_87A, 0, 4)) {
                return null;
            }
            width = getShortLittleEndian(a, 4);
            height = getShortLittleEndian(a, 6);
            int flags = a[8] & 0xff;
            bitsPerPixel = (flags >> 4 & 0x07) + 1;
            if (!determineNumberOfImages) {
                return this;
            }
            // skip global color palette
            if ((flags & 0x80) != 0) {
                int tableSize = (1 << (flags & 7) + 1) * 3;
                skip(input, tableSize);
            }
            numberOfImages = 0;
            int blockType;
            do {
                switch (blockType = input.read()) {
                    case 0x2c: {
                        // image separator
                        if (input.read(a, 0, 9) != 9) {
                            return null;
                        }
                        flags = a[8] & 0xff;
                        progressive = (flags & 0x40) != 0;
                        int localBitsPerPixel = (flags & 0x07) + 1;
                        if (localBitsPerPixel > bitsPerPixel) {
                            bitsPerPixel = localBitsPerPixel;
                        }
                        if ((flags & 0x80) != 0) {
                            skip(input, (1 << localBitsPerPixel) * 3);
                        }
                        skip(input, 1); // initial code length
                        int n;
                        do {
                            if ((n = input.read()) > 0) {
                                skip(input, n);
                            } else if (n == -1) {
                                return null;
                            }
                        } while (n > 0);
                        numberOfImages++;
                        break;
                    }
                    case 0x21: // extension
                        int extensionType = input.read();
                        if (collectComments && extensionType == 0xfe) {
                            int n;
                            do {
                                if ((n = input.read()) == -1) {
                                    return null;
                                }
                                if (n > 0) {
                                    for (int i = 0; i < n; i++) {
                                        if (input.read() == -1) {
                                            return null;
                                        }
                                    }
                                }
                            } while (n > 0);
                        } else {
                            int n;
                            do {
                                if ((n = input.read()) > 0) {
                                    skip(input, n);
                                } else if (n == -1) {
                                    return null;
                                }
                            } while (n > 0);
                        }
                        break;
                    case 0x3b:
                        // end of file
                        break;
                    default:
                        return null;
                }
            } while (blockType != 0x3b);
            return this;
        }

        @Override
        public String extension() {
            return "gif";
        }
    },

    PNG("image/png") {
        @Override
        protected ImageInfo check(InputStream input) throws IOException {
            final byte[] PNG_MAGIC = {0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a};
            byte[] a = new byte[27];
            if (input.read(a) != 27) {
                return null;
            }
            if (!equals(a, 0, PNG_MAGIC, 0, 6)) {
                return null;
            }
            width = getIntBigEndian(a, 14);
            height = getIntBigEndian(a, 18);
            bitsPerPixel = a[22] & 0xff;
            int colorType = a[23] & 0xff;
            if (colorType == 2 || colorType == 6) {
                bitsPerPixel *= 3;
            }
            progressive = (a[26] & 0xff) != 0;
            return this;
        }

        @Override
        public String extension() {
            return "png";
        }
    },

    BMP("image/bmp") {
        @Override
        protected ImageInfo check(InputStream input) throws IOException {
            byte[] a = new byte[44];
            if (input.read(a) != a.length) {
                return null;
            }
            width = getIntLittleEndian(a, 16);
            height = getIntLittleEndian(a, 20);
            if (width < 1 || height < 1) {
                return null;
            }
            bitsPerPixel = getShortLittleEndian(a, 26);
            if (bitsPerPixel != 1 && bitsPerPixel != 4 &&
                bitsPerPixel != 8 && bitsPerPixel != 16 &&
                bitsPerPixel != 24 && bitsPerPixel != 32) {
                return null;
            }
            int dpiX = (int) (getIntLittleEndian(a, 36) * 2.54E-02);
            if (dpiX > 0) {
                setPhysicalWidthDpi(dpiX);
            }

            int dpiY = (int) (getIntLittleEndian(a, 40) * 0.0254);
            if (dpiY > 0) {
                setPhysicalHeightDpi(dpiY);
            }
            return this;
        }

        @Override
        public String extension() {
            return "bmp";
        }
    },

    PCX("image/pcx") {
        @Override
        protected ImageInfo check(InputStream input) throws IOException {
            byte[] a = new byte[64];
            if (input.read(a) != a.length) {
                return null;
            }
            if (a[0] != 1) { // encoding, 1=RLE is only valid value
                return null;
            }
            // width / height
            int x1 = getShortLittleEndian(a, 2);
            int y1 = getShortLittleEndian(a, 4);
            int x2 = getShortLittleEndian(a, 6);
            int y2 = getShortLittleEndian(a, 8);
            if (x1 < 0 || x2 < x1 || y1 < 0 || y2 < y1) {
                return null;
            }
            width = x2 - x1 + 1;
            height = y2 - y1 + 1;
            // color depth
            int bits = a[1];
            int planes = a[63];
            if (planes == 1 &&
                (bits == 1 || bits == 2 || bits == 4 || bits == 8)) {
                // paletted
                bitsPerPixel = bits;
            } else if (planes == 3 && bits == 8) {
                // RGB truecolor
                bitsPerPixel = 24;
            } else {
                return null;
            }
            setPhysicalWidthDpi(getShortLittleEndian(a, 10));
            setPhysicalHeightDpi(getShortLittleEndian(a, 10));
            return this;
        }

        @Override
        public String extension() {
            return "pcx";
        }
    },

    IFF("image/iff") {
        @Override
        protected ImageInfo check(InputStream input) throws IOException {
            byte[] a = new byte[10];
            // read remaining 2 bytes of file id, 4 bytes file size and 4 bytes IFF sub-format
            if (input.read(a, 0, 10) != 10) {
                return null;
            }
            final byte[] IFF_RM = {0x52, 0x4d};
            if (!equals(a, 0, IFF_RM, 0, 2)) {
                return null;
            }
            int type = getIntBigEndian(a, 6);
            // type must be ILBM or PBM
            if (type != 0x494c424d && type != 0x50424d20) {
                return null;
            }
            // loop chunks to find BMHD chunk
            do {
                if (input.read(a, 0, 8) != 8) {
                    return null;
                }
                int chunkId = getIntBigEndian(a, 0);
                int size = getIntBigEndian(a, 4);
                if ((size & 1) == 1) {
                    size++;
                }
                if (chunkId == 0x424d4844) { // BMHD chunk
                    if (input.read(a, 0, 9) != 9) {
                        return null;
                    }
                    width = getShortBigEndian(a, 0);
                    height = getShortBigEndian(a, 2);
                    bitsPerPixel = a[8] & 0xff;
                    return width > 0 && height > 0 && bitsPerPixel > 0 && bitsPerPixel < 33 ? this : null;
                } else {
                    skip(input, size);
                }
            } while (true);
        }

        @Override
        public String extension() {
            return "iff";
        }
    },

    RAS("image/ras") {
        @Override
        protected ImageInfo check(InputStream input) throws IOException {
            byte[] a = new byte[14];
            if (input.read(a) != a.length) {
                return null;
            }
            final byte[] RAS_MAGIC = {0x6a, (byte) 0x95};
            if (!equals(a, 0, RAS_MAGIC, 0, 2)) {
                return null;
            }
            width = getIntBigEndian(a, 2);
            height = getIntBigEndian(a, 6);
            bitsPerPixel = getIntBigEndian(a, 10);
            return width > 0 && height > 0 && bitsPerPixel > 0 && bitsPerPixel <= 24 ? this : null;
        }

        @Override
        public String extension() {
            return "ras";
        }
    },

    PSD("image/psd") {
        @Override
        protected ImageInfo check(InputStream input) throws IOException {
            byte[] a = new byte[24];
            if (input.read(a) != a.length) {
                return null;
            }
            final byte[] PSD_MAGIC = {0x50, 0x53};
            if (!equals(a, 0, PSD_MAGIC, 0, 2)) {
                return null;
            }
            width = getIntBigEndian(a, 16);
            height = getIntBigEndian(a, 12);
            int channels = getShortBigEndian(a, 10);
            int depth = getShortBigEndian(a, 20);
            bitsPerPixel = channels * depth;
            return width > 0 && height > 0 && bitsPerPixel > 0 && bitsPerPixel <= 64 ? this : null;
        }

        @Override
        public String extension() {
            return "psd";
        }
    };

    private final String mimeType;
    protected int width = -1;
    protected int height = -1;
    protected int bitsPerPixel = -1;
    protected boolean progressive;
    protected boolean collectComments = true;
    protected Vector<String> comments = null;
    protected boolean determineNumberOfImages;
    protected int numberOfImages = 1;
    protected int physicalHeightDpi = -1;
    protected int physicalWidthDpi = -1;

    ImageInfo(String mimeType) {
        this.mimeType = mimeType;
    }

    public static ImageInfo analyze(InputStream input) throws IOException {
        int header = getSignature(input);
        switch (header) {
            case 0x8950:
                return PNG.check(input);
            case 0xffd8:
                return JPG.check(input);
            case 0x4749:
                return GIF.check(input);
            case 0x3842:
                return PSD.check(input);
            case 0x424d:
                return BMP.check(input);
            case 0x0a06:
                return PCX.check(input);
            case 0x464f:
                return IFF.check(input);
            case 0x59a6:
                return RAS.check(input);
            default:
                return null;
        }
    }

    protected static int getSignature(InputStream input) throws IOException {
        return ((input.read() & 0xff) << 8) + (input.read() & 0xff);
    }

    public static ImageInfo analyze(File file) throws IOException {
        return analyze(new FileInputStream(file));
    }

    public static ImageInfo analyze(byte[] array) throws IOException {
        return analyze(new ByteArrayInputStream(array));
    }

    protected static boolean equals(byte[] a1, int offs1, byte[] a2, int offs2, int num) {
        while (num-- > 0) {
            if (a1[offs1++] != a2[offs2++]) {
                return false;
            }
        }
        return true;
    }

    protected static void skip(InputStream input, int num) throws IOException {
        while (num > 0) {
            long result = input.skip(num);
            if (result > 0) {
                num -= result;
            } else {
                result = input.read();
                if (result == -1) {
                    throw new IOException("Premature end of input.");
                }
                num--;
            }
        }
    }

    private static int getShortBigEndian(byte[] a, int offs) {
        return (a[offs] & 0xff) << 8 | a[offs + 1] & 0xff;
    }

    private static int getShortLittleEndian(byte[] a, int offs) {
        return a[offs] & 0xff | (a[offs + 1] & 0xff) << 8;
    }

    private static int getIntBigEndian(byte[] a, int offs) {
        return (a[offs] & 0xff) << 24 | (a[offs + 1] & 0xff) << 16 | (a[offs + 2] & 0xff) << 8 | a[offs + 3] & 0xff;
    }

    private static int getIntLittleEndian(byte[] a, int offs) {
        return (a[offs + 3] & 0xff) << 24 | (a[offs + 2] & 0xff) << 16 | (a[offs + 1] & 0xff) << 8 | a[offs] & 0xff;
    }

    public void print() {
        System.out.format("%25s: %s\n", "MIME type", mimeType());
        System.out.format("%25s: %s\n", "Progressive", isProgressive() ? "yes" : "no");
        System.out.format("%25s: %s\n", "Bits per pixel", bitsPerPixel());
        System.out.format("%25s: %s\n", "Width (pixels)", width());
        System.out.format("%25s: %s\n", "Height (pixels)", height());
        System.out.format("%25s: %s\n", "Number of images", numberOfImages());
        System.out.format("%25s: %s\n", "Physical width (DPI)", physicalWidthDpi());
        System.out.format("%25s: %s\n", "Physical height (DPI)", physicalHeightDpi());
        System.out.format("%25s: %s\n", "Physical width (inch)", physicalWidthInch());
        System.out.format("%25s: %s\n", "Physical height (inch)", physicalHeightInch());
        int numComments = getNumberOfComments();
        System.out.format("%25s: %s\n", "Textual comments number", numComments);
        if (numComments > 0) {
            for (int i = 0; i < numComments; i++) {
                System.out.format("%3d: %s\n", i, getComment(i));
            }
        }
    }

    /**
     * If {@link #check(InputStream)} was successful, returns the image's number of bits per pixel.
     * Does not include transparency information like the alpha channel.
     *
     * @return number of bits per image pixel
     */
    public int bitsPerPixel() {
        return bitsPerPixel;
    }

    /**
     * Returns the index'th comment retrieved from the file.
     *
     * @param index int index of comment to return
     * @throws IllegalArgumentException if index is smaller than 0 or larger than or equal
     *                                  to the number of comments retrieved
     * @see #getNumberOfComments
     */
    public String getComment(int index) {
        if (comments == null || index < 0 || index >= comments.size()) {
            throw new IllegalArgumentException("Not a valid comment index: " + index);
        }
        return comments.elementAt(index);
    }

    /**
     * If {@link #check(InputStream)} was successful, returns one the image's vertical
     * resolution input pixels.
     *
     * @return image height input pixels
     */
    public int height() {
        return height;
    }

    /**
     * If {@link #check(InputStream)} was successful, returns a String with the
     * MIME type of the format.
     *
     * @return MIME type, e.g. <code>image/jpeg</code>
     */
    public String mimeType() {
        return mimeType;
    }

    /**
     * If {@link #check(InputStream)} was successful and {@link #setCollectComments(boolean)} was called with
     * <code>true</code> as argument, returns the number of comments retrieved
     * from the input image stream / file.
     * Any number &gt;= 0 and smaller than this number of comments is then a
     * valid argument for the {@link #getComment(int)} method.
     *
     * @return number of comments retrieved from input image
     */
    public int getNumberOfComments() {
        return comments != null ? comments.size() : 0;
    }

    /**
     * Returns the number of images input the examined file.
     * Assumes that <code>determineImageNumber(true);</code> was called before
     * a successful call to {@link #check(InputStream)}.
     * This value can currently be only different from <code>1</code> for GIF images.
     *
     * @return number of images input file
     */
    public int numberOfImages() {
        return numberOfImages;
    }

    /**
     * Returns the physical height of this image input dots per inch (dpi).
     * Assumes that {@link #check(InputStream)} was successful.
     * Returns <code>-1</code> on failure.
     *
     * @return physical height (input dpi)
     * @see #physicalWidthDpi()
     * @see #physicalHeightInch()
     */
    public int physicalHeightDpi() {
        return physicalHeightDpi;
    }

    /**
     * If {@link #check(InputStream)} was successful, returns the physical width of this image input dpi (dots per inch)
     * or -1 if no value could be found.
     *
     * @return physical height (input dpi)
     * @see #physicalHeightDpi()
     * @see #physicalWidthDpi()
     * @see #physicalWidthInch()
     */
    public Float physicalHeightInch() {
        if (height > 0 && physicalHeightDpi > 0) {
            return (float) height / physicalHeightDpi;
        } else {
            return null;
        }
    }

    /**
     * If {@link #check(InputStream)} was successful, returns the physical width of this image input dpi (dots per inch)
     * or -1 if no value could be found.
     *
     * @return physical width (input dpi)
     * @see #physicalHeightDpi()
     * @see #physicalWidthInch()
     * @see #physicalHeightInch()
     */
    public int physicalWidthDpi() {
        return physicalWidthDpi;
    }

    /**
     * Returns the physical width of an image input inches, or
     * <code>-1.0f</code> if width information is not available.
     * Assumes that {@link #check} has been called successfully.
     *
     * @return physical width input inches or <code>-1.0f</code> on failure
     * @see #physicalWidthDpi
     * @see #physicalHeightInch
     */
    public Float physicalWidthInch() {
        if (width > 0 && physicalWidthDpi > 0) {
            return (float) width / physicalWidthDpi;
        } else {
            return null;
        }
    }

    /**
     * If {@link #check(InputStream)} was successful, returns one the image's horizontal
     * resolution input pixels.
     *
     * @return image width input pixels
     */
    public int width() {
        return width;
    }

    /**
     * Returns whether the image is stored input a progressive (also called: interlaced) way.
     *
     * @return true for progressive/interlaced, false otherwise
     */
    public boolean isProgressive() {
        return progressive;
    }

    /**
     * Specify whether textual comments are supposed to be extracted from input.
     * Default is <code>false</code>.
     * If enabled, comments will be added to an internal list.
     *
     * @param newValue if <code>true</code>, this class will read comments
     * @see #getNumberOfComments
     * @see #getComment
     */
    public void setCollectComments(boolean newValue) {
        collectComments = newValue;
    }

    /**
     * Specify whether the number of images input a file is to be
     * determined - default is <code>false</code>.
     * This is a special option because some file formats require running over
     * the entire file to find out the number of images, a rather time-consuming
     * task.
     * Not all file formats support more than one image.
     * If this method is called with <code>true</code> as argument,
     * the actual number of images can be queried via
     * {@link #numberOfImages()} after a successful call to
     * {@link #check(InputStream)}.
     *
     * @param newValue will the number of images be determined?
     * @see #numberOfImages
     */
    public void determineImageNumber(boolean newValue) {
        determineNumberOfImages = newValue;
    }

    protected abstract ImageInfo check(InputStream input) throws IOException;
    public abstract String extension();

    public ImageInfo check(InputStream input, boolean gotSignature) throws IOException {
        if (!gotSignature) {
            getSignature(input);
        }
        return check(input);
    }

    protected void setPhysicalHeightDpi(int dpi) {
        physicalHeightDpi = dpi;
    }

    protected void setPhysicalWidthDpi(int dpi) {
        physicalWidthDpi = dpi;
    }
}
