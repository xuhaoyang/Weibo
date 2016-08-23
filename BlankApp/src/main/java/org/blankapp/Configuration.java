/**
 * Copyright (C) 2015 JianyingLi <lijy91@foxmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.blankapp;

public class Configuration {

    private boolean mLoggingEnabled = false;

    public Configuration() {
    }

    public boolean isLoggingEnabled() {
        return mLoggingEnabled;
    }

    public void setLoggingEnabled(boolean enabled) {
        this.mLoggingEnabled = enabled;
    }

    public static class Builder {
        private boolean mLoggingEnabled = false;

        public Builder setLoggingEnabled(boolean enabled) {
            this.mLoggingEnabled = enabled;
            return this;
        }

        public Configuration create() {
            Configuration configuration = new Configuration();

            configuration.mLoggingEnabled = mLoggingEnabled;

            return configuration;
        }

    }
}
