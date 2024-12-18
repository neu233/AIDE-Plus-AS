

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * Copyright (c) 2009 by Oracle Corporation. All Rights Reserved.
 */

/*
 * $Id: PropertyState.java 3024 2011-03-01 03:46:13Z joehw $
 */
package jaxp.sun.org.apache.xerces.internal.util;

public class PropertyState {

    public final Status status;
    public final Object state;

    public static final PropertyState UNKNOWN = new PropertyState(Status.UNKNOWN, null);
    public static final PropertyState RECOGNIZED = new PropertyState(Status.RECOGNIZED, null);
    public static final PropertyState NOT_SUPPORTED = new PropertyState(Status.NOT_SUPPORTED, null);
    public static final PropertyState NOT_RECOGNIZED = new PropertyState(Status.NOT_RECOGNIZED, null);
    public static final PropertyState NOT_ALLOWED = new PropertyState(Status.NOT_ALLOWED, null);


    public PropertyState(Status status, Object state) {
        this.status = status;
        this.state = state;
    }

    public static PropertyState of(Status status) {
        return new PropertyState(status, null);
    }

    public static PropertyState is(Object value) {
        return new PropertyState(Status.SET, value);
    }

    public boolean isExceptional() {
        return this.status.isExceptional();
    }
}
