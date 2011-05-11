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
package org.jboss.forge.validation;

import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.events.InstallFacets;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.validation.provider.BVProvider;
import org.jboss.forge.validation.api.ValidationDescriptor;
import org.jboss.forge.validation.api.ValidationFacet;
import org.jboss.forge.validation.provider.ValidationProvider;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;

/**
 * @author Kevin Pollet
 */
@Alias("validation")
@Help("A plugin that helps setting up bean validation")
@RequiresFacet(DependencyFacet.class)
@RequiresProject
public class ValidationPlugin implements Plugin
{
    private final Project project;
    private final DependencyFacet dependencyFacet;
    private final Event<InstallFacets> request;
    private final BeanManager beanManager;

    @Inject
    public ValidationPlugin(Project project, Event<InstallFacets> request, BeanManager beanManager)
    {
        this.project = project;
        this.dependencyFacet = project.getFacet(DependencyFacet.class);
        this.request = request;
        this.beanManager = beanManager;
    }

    @DefaultCommand
    public void status(PipeOut pipeOut)
    {
        if (project.hasFacet(ValidationFacet.class))
        {
            pipeOut.println("Validation is installed.");
        }
        else
        {
            pipeOut.println("Validation is not installed. Use 'validation setup' to get started.");
        }
    }

    @Command("setup")
    public void setup(@Option(name = "provider",
            help = "Specify the bean validation provider",
            defaultValue = "HIBERNATE_VALIDATOR", required = true) BVProvider provider,
                      @Option(name = "messageInterpolator") String messageInterpolator,
                      @Option(name = "traversableResolver") String traversableResolver,
                      @Option(name = "constraintValidatorFactory") String constraintValidatorFactory,
                      PipeOut pipeOut)
    {

        final ValidationProvider validationProvider = provider.getValidationProvider(beanManager);

        // install facet if needed
        installValidationFacet();

        // create descriptor if needed
        final ValidationFacet validationFacet = project.getFacet(ValidationFacet.class);

        if (createDescriptor(messageInterpolator, traversableResolver, constraintValidatorFactory))
        {
            final ValidationDescriptor defaultDescriptor = validationProvider.getDefaultDescriptor();

            final ValidationDescriptor descriptor = Descriptors.create(ValidationDescriptor.class)
                    .defaultProvider(defaultDescriptor.getDefaultProvider())
                    .messageInterpolator(messageInterpolator == null ? defaultDescriptor.getMessageInterpolator() : messageInterpolator)
                    .traversableResolver(traversableResolver == null ? defaultDescriptor.getTraversableResolver() : traversableResolver)
                    .constraintValidatorFactory(constraintValidatorFactory == null ? defaultDescriptor.getConstraintValidatorFactory() : constraintValidatorFactory);

            validationFacet.saveConfig(descriptor);
        }

        // add needed dependency
        for (Dependency oneDependency : validationProvider.getDependencies())
        {
            if (!dependencyFacet.hasDependency(oneDependency))
            {
                dependencyFacet.addDependency(oneDependency);
            }
        }

        pipeOut.println("Dependencies for provider [" + provider + "] has been added.");
    }

    private void installValidationFacet()
    {
        if (!project.hasFacet(ValidationFacet.class))
        {
            request.fire(new InstallFacets(ValidationFacet.class));
        }
    }

    private boolean createDescriptor(String userMessageInterpolator, String userTraversableResolver, String userConstraintValidatorFactory)
    {
        return userMessageInterpolator != null || userTraversableResolver != null || userConstraintValidatorFactory != null;
    }
}
