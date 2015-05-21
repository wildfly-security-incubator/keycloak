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

import org.jboss.as.controller.AbstractAddStepHandler;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.ServiceRemoveStepHandler;
import org.jboss.as.controller.capability.RuntimeCapability;
import org.jboss.msc.service.ServiceName;
import org.wildfly.security.auth.spi.SecurityRealm;

/**
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2015 Red Hat Inc.
 */
public class RealmRemoveHandler extends ServiceRemoveStepHandler {

    RealmRemoveHandler(AbstractAddStepHandler addOperation) {
        super(addOperation, Capabilities.SECURITY_REALM_RUNTIME_CAPABILITY);
    }

    @Override
    protected ServiceName serviceName(String name, PathAddress pathAddr) {
        String realmName = pathAddr.getLastElement().getValue();
        RuntimeCapability<Void> runtimeCapability =
                RuntimeCapability.fromBaseCapability(Capabilities.SECURITY_REALM_RUNTIME_CAPABILITY, realmName);
        return runtimeCapability.getCapabilityServiceName(SecurityRealm.class);
    }
}
