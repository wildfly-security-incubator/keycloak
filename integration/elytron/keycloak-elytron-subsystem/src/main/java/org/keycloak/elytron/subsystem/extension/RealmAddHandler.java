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

import java.net.URI;
import org.jboss.as.controller.AbstractAddStepHandler;
import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.capability.RuntimeCapability;
import org.jboss.dmr.ModelNode;

import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceTarget;
import org.keycloak.elytron.realm.KeycloakSecurityRealm;
import org.keycloak.elytron.realm.KeycloakSecurityRealmBuilder;
import org.wildfly.security.auth.spi.CredentialSupport;
import org.wildfly.security.auth.spi.SecurityRealm;
import org.wildfly.security.password.interfaces.ClearPassword;

/**
 * Add a new realm.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2015 Red Hat Inc.
 */
public final class RealmAddHandler extends AbstractAddStepHandler {

    public static RealmAddHandler INSTANCE = new RealmAddHandler();

    private RealmAddHandler() {
        super(Capabilities.SECURITY_REALM_RUNTIME_CAPABILITY, RealmDefinition.ALL_ATTRIBUTES);
    }

    @Override
    protected void performRuntime(OperationContext context, ModelNode operation, ModelNode model)
            throws OperationFailedException {
        String realmName = context.getCurrentAddress().getLastElement().getValue();
        KeycloakSecurityRealm kcRealm = KeycloakSecurityRealmBuilder.builder()
                                            .setRealmName(realmName)
                                            .setAuthServerUri(authServerUri(model))
                                            .setElytronClientName(clientSpec(model, RealmDefinition.CLIENT_NAME))
                                            .setElytronClientSecret(clientSpec(model, RealmDefinition.CLIENT_SECRET))
                                            .addCredentialSupport(ClearPassword.class, CredentialSupport.VERIFIABLE_ONLY)
                                            .build();
        KeycloakRealmService kcRealmService = new KeycloakRealmService(kcRealm);

        RuntimeCapability<Void> runtimeCapability = RuntimeCapability.fromBaseCapability(Capabilities.SECURITY_REALM_RUNTIME_CAPABILITY, context.getCurrentAddressValue());
        ServiceName serviceName = runtimeCapability.getCapabilityServiceName(SecurityRealm.class);

        ServiceTarget serviceTarget = context.getServiceTarget();
        ServiceBuilder<SecurityRealm> serviceBuilder = serviceTarget.addService(serviceName, kcRealmService);

        serviceBuilder.setInitialMode(ServiceController.Mode.LAZY);
        serviceBuilder.install();
    }

    private URI authServerUri(ModelNode model) throws OperationFailedException {
        ModelNode uriAsModel = model.get(RealmDefinition.AUTH_SERVER_URI.getName());
        String uriAsString = uriAsModel.asString();
        try {
            return new URI(uriAsString);
        } catch (Exception e) {
            throw new OperationFailedException(e);
        }
    }

    private String clientSpec(ModelNode model, AttributeDefinition attributeDef) {
        return model.get(attributeDef.getName()).asString();
    }
}
