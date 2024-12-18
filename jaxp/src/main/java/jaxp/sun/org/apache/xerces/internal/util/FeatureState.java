

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
 * $Id: FeatureState.java 3024 2011-03-01 03:46:13Z joehw $
 */

package jaxp.sun.org.apache.xerces.internal.util;

public class FeatureState {

    public final Status status;
    public final boolean state;

    public static final FeatureState SET_ENABLED = new FeatureState(Status.SET, true);
    public static final FeatureState SET_DISABLED = new FeatureState(Status.SET, false);
    public static final FeatureState UNKNOWN = new FeatureState(Status.UNKNOWN, false);
    public static final FeatureState RECOGNIZED = new FeatureState(Status.RECOGNIZED, false);
    public static final FeatureState NOT_SUPPORTED = new FeatureState(Status.NOT_SUPPORTED, false);
    public static final FeatureState NOT_RECOGNIZED = new FeatureState(Status.NOT_RECOGNIZED, false);
    public static final FeatureState NOT_ALLOWED = new FeatureState(Status.NOT_ALLOWED, false);

    public FeatureState(Status status, boolean state) {
        this.status = status;
        this.state = state;
    }

    public static FeatureState of(Status status) {
        return new FeatureState(status, false);
    }

    public static FeatureState is(boolean value) {
        return new FeatureState(Status.SET, value);
    }

    public boolean isExceptional() {
        return this.status.isExceptional();
    }
}
