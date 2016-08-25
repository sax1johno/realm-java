/*
 * Copyright 2016 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.realm.exceptions;

import io.realm.internal.Keep;
import io.realm.internal.SharedRealm;

/**
 * Class for reporting problems when the accessing Realm related files.
 */
@Keep
public class RealmFileException extends RuntimeException {
    /**
     * The specific kind of this {@link RealmFileException}.
     */
    public enum Kind {
        /**
         * Thrown for any I/O related exception scenarios when a realm is opened.
         */
        ACCESS_ERROR,
        /**
         * Thrown if the user does not have permission to open or create the specified file in the specified access
         * mode when the realm is opened.
         */
        PERMISSION_DENIED,
        /**
         * Not used now.
         * Thrown if create_Always was specified and the file did already exist when the realm is opened.
         */
        EXISTS,
        /**
         * Thrown if relevant file cannot be found.
         */
        NOT_FOUND,
        /**
         * Thrown if the database file is currently open in another process which cannot share with the current process
         * due to an architecture mismatch.
         */
        INCOMPATIBLE_LOCK_FILE,
        /**
         * Not used now.
         * Thrown if the file needs to be upgraded to a new format, but upgrades have been explicitly disabled.
         */
        FORMAT_UPGRADE_REQUIRED;

        // Created from byte values by JNI.
        static Kind getKind(byte value) {
            switch (value) {
                case SharedRealm.FILE_EXCEPTION_KIND_ACCESS_ERROR:
                    return ACCESS_ERROR;
                case SharedRealm.FILE_EXCEPTION_KIND_PERMISSION_DENIED:
                    return PERMISSION_DENIED;
                case SharedRealm.FILE_EXCEPTION_KIND_EXISTS:
                    return EXISTS;
                case SharedRealm.FILE_EXCEPTION_KIND_NOT_FOUND:
                    return NOT_FOUND;
                case SharedRealm.FILE_EXCEPTION_KIND_IMCOMPATIBLE_LOCK_FILE:
                    return INCOMPATIBLE_LOCK_FILE;
                case SharedRealm.FILE_EXCEPTION_KIND_FORMAT_UPGRADE_REQUIRED:
                    return FORMAT_UPGRADE_REQUIRED;
                default:
                    throw new RuntimeException("Unknown value for RealmFileException kind.");
            }
        }
    }

    public final Kind kind;
    // Called by JNI
    @SuppressWarnings("unused")
    public RealmFileException(byte value, String message) {
        super(message);
        kind = Kind.getKind(value);
    }

    public RealmFileException(Kind kind, String message) {
        super(message);
        this.kind = kind;
    }

    public RealmFileException(Kind kind, Throwable cause) {
        super(cause);
        this.kind = kind;
    }

    public RealmFileException(Kind kind, String message, Throwable cause) {
        super(message, cause);
        this.kind = kind;
    }
}
