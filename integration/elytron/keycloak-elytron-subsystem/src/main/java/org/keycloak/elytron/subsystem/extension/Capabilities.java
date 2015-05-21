/*
 * Copyright 2015 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.keycloak.elytron.subsystem.extension;

import org.jboss.as.controller.capability.RuntimeCapability;
import org.wildfly.security.auth.spi.SecurityRealm;

/**
 * Note: this class is copied from the Elytron subsystems.  The stuff in here
 * will eventually live in core somewhere.
 *
 * The capabilities provided by and required by this subsystem.
 *
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 */
class Capabilities {

    private static final String CAPABILITY_BASE = "org.wildfly.security.";


    static final String SECURITY_REALM_CAPABILITY = CAPABILITY_BASE + "security-realm";

    static final RuntimeCapability<Void> SECURITY_REALM_RUNTIME_CAPABILITY =
            RuntimeCapability.Builder
                             .of(SECURITY_REALM_CAPABILITY, true, SecurityRealm.class)
                             .build();

}
