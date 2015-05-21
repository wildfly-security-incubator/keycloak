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

import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.keycloak.elytron.realm.KeycloakSecurityRealm;
import org.wildfly.security.auth.spi.SecurityRealm;

/**
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2015 Red Hat Inc.
 */
public class KeycloakRealmService implements Service<SecurityRealm> {
    private volatile SecurityRealm securityRealm;

    public KeycloakRealmService(KeycloakSecurityRealm securityRealm) {
        this.securityRealm = securityRealm;
    }

    @Override
    public void start(StartContext context) throws StartException {
    }

    @Override
    public void stop(StopContext context) {
        securityRealm = null;
    }

    @Override
    public SecurityRealm getValue() throws IllegalStateException, IllegalArgumentException {
        return securityRealm;
    }

}
