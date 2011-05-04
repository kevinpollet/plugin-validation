/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.validation.provider;

import java.util.HashSet;
import java.util.Set;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.validation.api.ValidationDescriptor;
import org.jboss.forge.validation.api.ValidationProvider;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;

import static java.util.Collections.unmodifiableSet;
import static org.jboss.forge.project.dependencies.DependencyBuilder.create;

/**
 * @author Kevin Pollet
 */
//TODO how to set to scope provided
public class ApacheBeanValidationProvider implements ValidationProvider
{
    private final ValidationDescriptor defaultDescriptor;
    private final Set<Dependency> dependencies;

    public ApacheBeanValidationProvider()
    {
        this.defaultDescriptor = Descriptors.create(ValidationDescriptor.class)
                .defaultProvider("org.apache.bval.jsr303.ApacheValidationProvider")
                .messageInterpolator("org.apache.bval.jsr303.DefaultMessageInterpolator")
                .traversableResolver("org.apache.bval.jsr303.resolver.DefaultTraversableResolver")
                .constraintValidatorFactory("org.apache.bval.jsr303.DefaultConstraintValidatorFactory");

        // add apache bean validation dependencies
        final Set<Dependency> tmpSet = new HashSet<Dependency>();
        tmpSet.add(create("org.apache.bval:org.apache.bval.bundle:0.2-incubating"));
        this.dependencies = unmodifiableSet(tmpSet);
    }

    @Override
    public ValidationDescriptor getDefaultDescriptor()
    {
        return defaultDescriptor;
    }

    @Override
    public Set<Dependency> getDependencies()
    {
        return dependencies;
    }
}
