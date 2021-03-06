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

package org.keycloak.adapters.elytron;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.util.JsonSerialization;
import org.wildfly.security.authz.Attributes;
import org.wildfly.security.authz.Attributes.Entry;
import org.wildfly.security.authz.AuthorizationIdentity;
import org.wildfly.security.authz.RoleDecoder;
import org.wildfly.security.authz.Roles;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class KeycloakRoleDecoder implements RoleDecoder {
    @Override
    public Roles decodeRoles(AuthorizationIdentity identity) {
        Attributes attributes = identity.getAttributes();
        Entry realmAccess = attributes.get("realm_access");
        Set<String> roleSet = new HashSet<>();

        if (realmAccess != null) {
            String realmAccessValue = realmAccess.get(0);

            try {
                Map<String, List<String>> jsonNode = JsonSerialization.readValue(realmAccessValue, Map.class);
                List<String> roles = jsonNode.get("roles");

                if (roles != null) {
                    roleSet.addAll(roles);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Entry resourceAccess = attributes.get("resource_access");

        if (resourceAccess != null) {
            Iterator<String> iterator = resourceAccess.iterator();

            while (iterator.hasNext()) {
                try {
                    Map<String, Map<String, List<String>>> resources = JsonSerialization.readValue(iterator.next(), Map.class);

                    for (String resourceKey : resources.keySet()) {
                        List<String> roles = resources.get(resourceKey).get("roles");

                        if (roles != null) {
                            roleSet.addAll(roles);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Error while reading resource access role.", e);
                }
            }
        }

        return Roles.fromSet(roleSet);
    }
}
