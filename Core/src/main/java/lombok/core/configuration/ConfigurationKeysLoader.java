/*
 * Copyright (C) 2014 The Project Lombok Authors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package lombok.core.configuration;

import lombok.ConfigurationKeys;
import lombok.core.SpiLoadUtil;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public interface ConfigurationKeysLoader {
    class LoaderLoader {
        private static final AtomicBoolean alreadyLoaded = new AtomicBoolean(false);

        private LoaderLoader() {}

        public static void loadAllConfigurationKeys() {
            if (alreadyLoaded.get()) return;

            try {
                Class.forName(ConfigurationKeys.class.getName());
            } catch (Throwable ignore) {}

            try {
                Class<ConfigurationKeysLoader> loaderClass = ConfigurationKeysLoader.class;
                Iterable<ConfigurationKeysLoader> services = SpiLoadUtil.findServices(
                    loaderClass, loaderClass.getClassLoader());
                for (ConfigurationKeysLoader service : services) {
                    try { } catch (Exception ignore) {}
                }
            } catch (IOException e) {
                throw new RuntimeException("Can't load config keys; services file issue.", e);
            } finally {
                alreadyLoaded.set(true);
            }
        }
    }
}
