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

import android.content.Context;

import org.blankapp.util.Config;
import org.blankapp.util.Log;

/**
 *
 *    ___    __    _        __        _      ___  ___
 *   / __\  / /   /_\    /\ \ \/\ /\ /_\    / _ \/ _ \
 *  /__\// / /   //_\\  /  \/ / //_///_\\  / /_)/ /_)/
 * / \/  \/ /___/  _  \/ /\  / __ \/  _  \/ ___/ ___/
 * \_____/\____/\_/ \_/\_\ \/\/  \/\_/ \_/\/   \/
 * ===================================================
 *                                 http://blankapp.org
 */
public class BlankApp {

    public static void initialize(Context context) {
        initialize(context, new Configuration.Builder().create());
    }

    public static void initialize(Context context, boolean loggingEnabled) {
        initialize(context, new Configuration.Builder().setLoggingEnabled(loggingEnabled).create());
    }

    public static void initialize(Context context, Configuration configuration) {
        Log.e("    ___    __    _        __        _      ___  ___ ");
        Log.e("   / __\\  / /   /_\\    /\\ \\ \\/\\ /\\ /_\\    / _ \\/ _ \\");
        Log.e("  /__\\// / /   //_\\\\  /  \\/ / //_///_\\\\  / /_)/ /_)/");
        Log.e(" / \\/  \\/ /___/  _  \\/ /\\  / __ \\/  _  \\/ ___/ ___/ ");
        Log.e(" \\_____/\\____/\\_/ \\_/\\_\\ \\/\\/  \\/\\_/ \\_/\\/   \\/     ");
        Log.e(" ===================================================");
        Log.e("                                 http://blankapp.org");
        if (configuration != null) {
            Log.setEnabled(configuration.isLoggingEnabled());
        }
        Config.initialize(context);
    }

    public static void dispose() {
    }


}
