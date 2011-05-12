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

import java.util.List;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.events.InstallFacets;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.validation.api.ValidationDescriptor;
import org.jboss.forge.validation.api.ValidationFacet;
import org.jboss.forge.validation.provider.BVProvider;
import org.jboss.forge.validation.provider.ValidationProvider;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;

import static org.jboss.forge.project.dependencies.ScopeType.PROVIDED;

/**
 * @author Kevin Pollet
 */
@Alias("validation")
@RequiresProject
@RequiresFacet(DependencyFacet.class)
public class ValidationPlugin implements Plugin
{
    private final Project project;
    private final BeanManager beanManager;
    private final Event<InstallFacets> request;
    private final DependencyFacet dependencyFacet;
    private final Dependency javaee6SpecAPI;
    private final Dependency beanValidationAPI;
    private final ShellPrompt shellPrompt;

    @Inject
    public ValidationPlugin(Project project, Event<InstallFacets> request, BeanManager beanManager, ShellPrompt shellPrompt)
    {
        this.project = project;
        this.beanManager = beanManager;
        this.request = request;
        this.dependencyFacet = project.getFacet(DependencyFacet.class);
        this.shellPrompt = shellPrompt;

        this.javaee6SpecAPI = DependencyBuilder.create()
                .setGroupId("org.jboss.spec")
                .setArtifactId("jboss-javaee-6.0")
                .setVersion("1.0.0.Final")
                .setScopeType(PROVIDED);

        this.beanValidationAPI = DependencyBuilder.create()
                .setGroupId("javax.validation")
                .setArtifactId("validation-api")
                .setVersion("1.0.0.GA")
                .setScopeType(PROVIDED);
    }

    @DefaultCommand(help = "Shows the status of the validation setup")
    public void status(PipeOut pipeOut)
    {
        if (project.hasFacet(ValidationFacet.class))
        {
            pipeOut.println("validation is installed.");
        }
        else
        {
            pipeOut.println("validation is not installed. Use 'validation setup' to get started.");
        }
    }

    @Command(value = "setup", help = "Setup validation for this project")
    public void setup(@Option(name = "provider", defaultValue = "HIBERNATE_VALIDATOR", required = true) BVProvider provider,
                      @Option(name = "messageInterpolator") String messageInterpolator,
                      @Option(name = "traversableResolver") String traversableResolver,
                      @Option(name = "constraintValidatorFactory") String constraintValidatorFactory,
                      PipeOut pipeOut)
    {

        final ValidationProvider validationProvider = provider.getValidationProvider(beanManager);

        installValidationFacet();
        installValidationDependencies();
        installValidationProviderDependencies(validationProvider.getDependencies());

        // create validation descriptor if needed
        if (shouldCreateDescriptor(messageInterpolator, traversableResolver, constraintValidatorFactory))
        {
            final ValidationDescriptor providerDescriptor = validationProvider.getDefaultDescriptor();
            final ValidationDescriptor descriptor = Descriptors.create(ValidationDescriptor.class)
                    .defaultProvider(providerDescriptor.getDefaultProvider())
                    .messageInterpolator(messageInterpolator == null ? providerDescriptor.getMessageInterpolator() : messageInterpolator)
                    .traversableResolver(traversableResolver == null ? providerDescriptor.getTraversableResolver() : traversableResolver)
                    .constraintValidatorFactory(constraintValidatorFactory == null ? providerDescriptor.getConstraintValidatorFactory() : constraintValidatorFactory);

            project.getFacet(ValidationFacet.class).saveConfig(descriptor);
            pipeOut.println("validation descriptor has been created successfully.");
        }
    }

    private void installValidationFacet()
    {
        if (!project.hasFacet(ValidationFacet.class))
        {
            request.fire(new InstallFacets(ValidationFacet.class));
        }
    }

    private void installValidationDependencies()
    {
        if (!dependencyFacet.hasDependency(javaee6SpecAPI) && !dependencyFacet.hasDependency(beanValidationAPI))
        {
            dependencyFacet.addDependency(beanValidationAPI);
        }
    }

    private void installValidationProviderDependencies(Set<Dependency> dependencies)
    {
        for (Dependency oneDependency : dependencies)
        {
            // let the user the choice of the version
            final List<Dependency> versions = dependencyFacet.resolveAvailableVersions(oneDependency);
            final Dependency version = shellPrompt.promptChoiceTyped("Which version of " + oneDependency.getArtifactId() + " would you like to use?", versions, versions.get(versions.size() - 1));

            if (!dependencyFacet.hasDependency(version))
            {
                dependencyFacet.addDependency(version);
            }
        }
    }

    private boolean shouldCreateDescriptor(String userMessageInterpolator, String userTraversableResolver, String userConstraintValidatorFactory)
    {
        return userMessageInterpolator != null || userTraversableResolver != null || userConstraintValidatorFactory != null;
    }
}
