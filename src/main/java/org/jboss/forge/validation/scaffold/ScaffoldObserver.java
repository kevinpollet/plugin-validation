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
package org.jboss.forge.validation.scaffold;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.scaffold.events.ScaffoldGeneratedResources;
import org.jboss.forge.validation.api.ValidationFacet;
import org.jboss.forge.validation.api.scaffold.ScaffoldConfigurator;

/**
 * @author Kevin Pollet
 */
public class ScaffoldObserver
{
    private final Project project;
    private Instance<ScaffoldConfigurator> configurators;

    @Inject
    public ScaffoldObserver(Project project, @Any Instance<ScaffoldConfigurator> configurators)
    {
        this.project = project;
        this.configurators = configurators;
    }

    public void observe(@Observes ScaffoldGeneratedResources event)
    {
        // check if validation facet is installed on the current project
        if (project.hasFacet(ValidationFacet.class))
        {
            for (ScaffoldConfigurator oneConfigurator : configurators)
            {
                oneConfigurator.addValidationConfiguration(event);
            }
        }
    }
}
