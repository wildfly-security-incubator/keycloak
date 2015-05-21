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

import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.operations.common.GenericSubsystemDescribeHandler;
import org.jboss.as.controller.registry.ManagementResourceRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jboss.as.controller.ReloadRequiredWriteAttributeHandler;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.operations.validation.StringLengthValidator;
import org.jboss.as.controller.operations.validation.URIValidator;
import org.jboss.dmr.ModelType;

/**
 * Defines attributes and operations for the Realm
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2015 Red Hat Inc.
 */
public class RealmDefinition extends SimpleResourceDefinition {

    public static final String TAG_NAME = "realm";

    protected static final SimpleAttributeDefinition AUTH_SERVER_URI =
        new SimpleAttributeDefinitionBuilder("auth-server-uri", ModelType.STRING, true)
                .setXmlName("auth-server-uri")
                .setAllowExpression(true)
                .setValidator(new URIValidator(true, true))
                .build();

    protected static final SimpleAttributeDefinition CLIENT_NAME =
        new SimpleAttributeDefinitionBuilder("client-name", ModelType.STRING, true)
                .setXmlName("client-name")
                .setAllowExpression(true)
                .setValidator(new StringLengthValidator(1, Integer.MAX_VALUE, true, true))
                .build();

    protected static final SimpleAttributeDefinition CLIENT_SECRET =
        new SimpleAttributeDefinitionBuilder("client-secret", ModelType.STRING, true)
                .setXmlName("client-secret")
                .setAllowExpression(true)
                .setValidator(new StringLengthValidator(1, Integer.MAX_VALUE, true, true))
                .build();

    protected static final List<SimpleAttributeDefinition> ALL_ATTRIBUTES = new ArrayList<SimpleAttributeDefinition>();
    static {
        ALL_ATTRIBUTES.add(AUTH_SERVER_URI);
        ALL_ATTRIBUTES.add(CLIENT_NAME);
        ALL_ATTRIBUTES.add(CLIENT_SECRET);
    }

    private static final Map<String, SimpleAttributeDefinition> DEFINITION_LOOKUP = new HashMap<String, SimpleAttributeDefinition>();
    static {
        for (SimpleAttributeDefinition def : ALL_ATTRIBUTES) {
            DEFINITION_LOOKUP.put(def.getXmlName(), def);
        }
    }

    private static final ReloadRequiredWriteAttributeHandler realmAttrHandler = new ReloadRequiredWriteAttributeHandler(ALL_ATTRIBUTES.toArray(new SimpleAttributeDefinition[0]));

    public RealmDefinition() {
        super(PathElement.pathElement("realm"),
                KeycloakElytronExtension.getResourceDescriptionResolver("realm"),
                RealmAddHandler.INSTANCE,
                new RealmRemoveHandler(RealmAddHandler.INSTANCE));
    }

    @Override
    public void registerOperations(ManagementResourceRegistration resourceRegistration) {
        super.registerOperations(resourceRegistration);
        resourceRegistration.registerOperationHandler(GenericSubsystemDescribeHandler.DEFINITION, GenericSubsystemDescribeHandler.INSTANCE);
    }

    @Override
    public void registerAttributes(ManagementResourceRegistration resourceRegistration) {
        super.registerAttributes(resourceRegistration);

        for (AttributeDefinition attrDef : ALL_ATTRIBUTES) {
            //TODO: use subclass of realmAttrHandler that can call RealmDefinition.validateTruststoreSetIfRequired
            resourceRegistration.registerReadWriteAttribute(attrDef, null, realmAttrHandler);
        }
    }

    public static SimpleAttributeDefinition lookup(String name) {
        return DEFINITION_LOOKUP.get(name);
    }
}
