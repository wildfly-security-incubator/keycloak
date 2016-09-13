/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
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
package org.keycloak.subsystem.adapter.extension;

import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.operations.common.GenericSubsystemDescribeHandler;
import org.jboss.as.controller.operations.validation.StringLengthValidator;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines attributes and operations for a secure-deployment.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2013 Red Hat Inc.
 */
abstract class AbstractAdapterConfigurationDefinition extends SimpleResourceDefinition {

    static final SimpleAttributeDefinition REALM =
            new SimpleAttributeDefinitionBuilder("realm", ModelType.STRING, true)
                    .setXmlName("realm")
                    .setAllowExpression(true)
                    .setValidator(new StringLengthValidator(1, Integer.MAX_VALUE, true, true))
                    .build();

    static final SimpleAttributeDefinition RESOURCE =
            new SimpleAttributeDefinitionBuilder("resource", ModelType.STRING, true)
                    .setXmlName("resource")
                    .setAllowExpression(true)
                    .setValidator(new StringLengthValidator(1, Integer.MAX_VALUE, true, true))
                    .build();

    static final SimpleAttributeDefinition USE_RESOURCE_ROLE_MAPPINGS =
            new SimpleAttributeDefinitionBuilder("use-resource-role-mappings", ModelType.BOOLEAN, true)
            .setXmlName("use-resource-role-mappings")
            .setAllowExpression(true)
            .setDefaultValue(new ModelNode(false))
            .build();

    static final SimpleAttributeDefinition BEARER_ONLY =
            new SimpleAttributeDefinitionBuilder("bearer-only", ModelType.BOOLEAN, true)
                    .setXmlName("bearer-only")
                    .setAllowExpression(true)
                    .setDefaultValue(new ModelNode(false))
                    .build();

    static final SimpleAttributeDefinition ENABLE_BASIC_AUTH =
            new SimpleAttributeDefinitionBuilder("enable-basic-auth", ModelType.BOOLEAN, true)
                    .setXmlName("enable-basic-auth")
                    .setAllowExpression(true)
                    .setDefaultValue(new ModelNode(false))
                    .build();

    static final SimpleAttributeDefinition PUBLIC_CLIENT =
            new SimpleAttributeDefinitionBuilder("public-client", ModelType.BOOLEAN, true)
                    .setXmlName("public-client")
                    .setAllowExpression(true)
                    .setDefaultValue(new ModelNode(false))
                    .build();

    static final SimpleAttributeDefinition TURN_OFF_CHANGE_SESSION =
            new SimpleAttributeDefinitionBuilder("turn-off-change-session-id-on-login", ModelType.BOOLEAN, true)
                    .setXmlName("turn-off-change-session-id-on-login")
                    .setAllowExpression(true)
                    .setDefaultValue(new ModelNode(false))
                    .build();

    static final List<SimpleAttributeDefinition> DEPLOYMENT_ONLY_ATTRIBUTES = new ArrayList<SimpleAttributeDefinition>();

    static {
        DEPLOYMENT_ONLY_ATTRIBUTES.add(REALM);
        DEPLOYMENT_ONLY_ATTRIBUTES.add(RESOURCE);
        DEPLOYMENT_ONLY_ATTRIBUTES.add(USE_RESOURCE_ROLE_MAPPINGS);
        DEPLOYMENT_ONLY_ATTRIBUTES.add(BEARER_ONLY);
        DEPLOYMENT_ONLY_ATTRIBUTES.add(ENABLE_BASIC_AUTH);
        DEPLOYMENT_ONLY_ATTRIBUTES.add(PUBLIC_CLIENT);
        DEPLOYMENT_ONLY_ATTRIBUTES.add(TURN_OFF_CHANGE_SESSION);
    }

    static final List<SimpleAttributeDefinition> ALL_ATTRIBUTES = new ArrayList();

    static {
        ALL_ATTRIBUTES.addAll(DEPLOYMENT_ONLY_ATTRIBUTES);
        ALL_ATTRIBUTES.addAll(SharedAttributeDefinitons.ATTRIBUTES);
    }

    static final Map<String, SimpleAttributeDefinition> XML_ATTRIBUTES = new HashMap<String, SimpleAttributeDefinition>();

    static {
        for (SimpleAttributeDefinition def : ALL_ATTRIBUTES) {
            XML_ATTRIBUTES.put(def.getXmlName(), def);
        }
    }

    private static final Map<String, SimpleAttributeDefinition> DEFINITION_LOOKUP = new HashMap<String, SimpleAttributeDefinition>();
    static {
        for (SimpleAttributeDefinition def : ALL_ATTRIBUTES) {
            DEFINITION_LOOKUP.put(def.getXmlName(), def);
        }
    }

    private final AbstractAdapterConfigurationWriteAttributeHandler attrWriteHandler;
    private final List<SimpleAttributeDefinition> attributes;

    protected AbstractAdapterConfigurationDefinition(String name, List<SimpleAttributeDefinition> attributes, AbstractAdapterConfigurationAddHandler addHandler, AbstractAdapterConfigurationRemoveHandler removeHandler, AbstractAdapterConfigurationWriteAttributeHandler attrWriteHandler) {
        super(PathElement.pathElement(name),
                KeycloakExtension.getResourceDescriptionResolver(name),
                addHandler,
                removeHandler);
        this.attributes = attributes;
        this.attrWriteHandler = attrWriteHandler;
    }

    @Override
    public void registerOperations(ManagementResourceRegistration resourceRegistration) {
        super.registerOperations(resourceRegistration);
        resourceRegistration.registerOperationHandler(GenericSubsystemDescribeHandler.DEFINITION, GenericSubsystemDescribeHandler.INSTANCE);
    }

    @Override
    public void registerAttributes(ManagementResourceRegistration resourceRegistration) {
        super.registerAttributes(resourceRegistration);
        for (AttributeDefinition attrDef : this.attributes) {
            resourceRegistration.registerReadWriteAttribute(attrDef, null, this.attrWriteHandler);
        }
    }

    public static SimpleAttributeDefinition lookup(String name) {
        return DEFINITION_LOOKUP.get(name);
    }
}
