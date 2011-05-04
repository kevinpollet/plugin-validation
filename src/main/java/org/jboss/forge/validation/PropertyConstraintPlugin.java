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

import java.io.FileNotFoundException;

import javax.inject.Inject;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresResource;
import org.jboss.forge.validation.api.ConstraintLocation;
import org.jboss.forge.validation.api.ValidationFacet;

import static org.jboss.forge.validation.api.ConstraintLocation.FIELD;
import static org.jboss.forge.validation.api.ConstraintLocation.METHOD;

//TODO add groups support
//TODO add class constraints
//TODO add all bean validation built-in constraints
//TODO constraint list

/**
 * @author Kevin Pollet
 */
@Alias("add-constraint")
@RequiresResource(JavaResource.class)
@RequiresFacet({ValidationFacet.class, JavaSourceFacet.class})
public class PropertyConstraintPlugin implements Plugin
{
    private final JavaSourceFacet javaSourceFacet;
    private final Shell shell;

    @Inject
    public PropertyConstraintPlugin(Project project, Shell shell)
    {
        this.javaSourceFacet = project.getFacet(JavaSourceFacet.class);
        this.shell = shell;
    }

    @Command(value = "Null")
    public void addNullConstraint(@Option(name = "onproperty", required = true) String property,
                                  @Option(name = "message") String message,
                                  @Option(name = "on", defaultValue = "FIELD", required = true) ConstraintLocation location) throws FileNotFoundException
    {
        final JavaClass javaClass = getJavaClassFromResource(shell.getCurrentResource());

        // add constraint to property
        final Annotation<JavaClass> constraintAnnotation = addAnnotationTo(javaClass, property, location, Null.class);
        addConstraintMessageTo(constraintAnnotation, message);

        javaSourceFacet.saveJavaSource(javaClass);
        shell.println("Added Null constraint on property '" + property + "'");
    }

    @Command(value = "NotNull")
    public void addNotNullConstraint(@Option(name = "onproperty", required = true) String property,
                                     @Option(name = "message") String message,
                                     @Option(name = "on", defaultValue = "FIELD", required = true) ConstraintLocation location) throws FileNotFoundException
    {
        final JavaClass javaClass = getJavaClassFromResource(shell.getCurrentResource());

        // add constraint to property
        final Annotation<JavaClass> constraintAnnotation = addAnnotationTo(javaClass, property, location, NotNull.class);
        addConstraintMessageTo(constraintAnnotation, message);

        javaSourceFacet.saveJavaSource(javaClass);
        shell.println("Added NotNull constraint on property '" + property + "'");
    }

    @Command(value = "AssertTrue")
    public void addAssertTrueConstraint(@Option(name = "onproperty", required = true) String property,
                                        @Option(name = "message") String message,
                                        @Option(name = "on", defaultValue = "FIELD", required = true) ConstraintLocation location) throws FileNotFoundException
    {
        final JavaClass javaClass = getJavaClassFromResource(shell.getCurrentResource());

        // add constraints to property
        final Annotation<JavaClass> constraintAnnotation = addAnnotationTo(javaClass, property, location, AssertTrue.class);
        addConstraintMessageTo(constraintAnnotation, message);

        javaSourceFacet.saveJavaSource(javaClass);
        shell.println("Added AssertTrue constraint on property '" + property + "'");
    }

    @Command(value = "AssertFalse")
    public void addAssertFalseConstraint(@Option(name = "onproperty", required = true) String property,
                                         @Option(name = "message") String message,
                                         @Option(name = "on", defaultValue = "FIELD", required = true) ConstraintLocation location) throws FileNotFoundException
    {
        final JavaClass javaClass = getJavaClassFromResource(shell.getCurrentResource());

        // add constraints to property
        final Annotation<JavaClass> constraintAnnotation = addAnnotationTo(javaClass, property, location, AssertFalse.class);
        addConstraintMessageTo(constraintAnnotation, message);

        javaSourceFacet.saveJavaSource(javaClass);
        shell.println("Added AssertFalse constraint on property '" + property + "'");
    }

    @Command(value = "Min")
    public void addMinConstraint(@Option(name = "onproperty", required = true) String property,
                                 @Option(name = "minValue", required = true) long min,
                                 @Option(name = "message") String message,
                                 @Option(name = "on", defaultValue = "FIELD", required = true) ConstraintLocation location) throws FileNotFoundException
    {
        final JavaClass javaClass = getJavaClassFromResource(shell.getCurrentResource());

        // add constraints to property
        final Annotation<JavaClass> constraintAnnotation = addAnnotationTo(javaClass, property, location, Min.class);
        addConstraintMessageTo(constraintAnnotation, message);
        constraintAnnotation.setLiteralValue(String.valueOf(min));

        javaSourceFacet.saveJavaSource(javaClass);
        shell.println("Added Min constraint on property '" + property + "'");
    }

    @Command(value = "Max")
    public void addMaxConstraint(@Option(name = "onproperty", required = true) String property,
                                 @Option(name = "maxValue", required = true) long max,
                                 @Option(name = "message") String message,
                                 @Option(name = "on", defaultValue = "FIELD", required = true) ConstraintLocation location) throws FileNotFoundException
    {
        final JavaClass javaClass = getJavaClassFromResource(shell.getCurrentResource());

        // add constraints to property
        final Annotation<JavaClass> constraintAnnotation = addAnnotationTo(javaClass, property, location, Max.class);
        addConstraintMessageTo(constraintAnnotation, message);
        constraintAnnotation.setLiteralValue(String.valueOf(max));

        javaSourceFacet.saveJavaSource(javaClass);
        shell.println("Added Min constraint on property '" + property + "'");
    }

    private Annotation<JavaClass> addAnnotationTo(JavaClass javaClass, String property, ConstraintLocation location, Class<? extends java.lang.annotation.Annotation> annotationClass)
    {
        if (location == FIELD)
        {
            final Field<JavaClass> field = javaClass.getField(property);
            if (field == null)
            {
                throw new IllegalStateException("The current class has no property named '" + property + "'");
            }
            return field.addAnnotation(annotationClass);
        }
        else if (location == METHOD)
        {
            Method<JavaClass> method = getPropertyAccessor(javaClass, property);
            if (method == null)
            {
                throw new IllegalStateException("The current class has no accessor method for property '" + property + "'");
            }
            return method.addAnnotation(annotationClass);
        }
        throw new RuntimeException("Only field or method can be annotated with constraint");
    }

    private JavaClass getJavaClassFromResource(Resource<?> resource) throws FileNotFoundException
    {
        if (resource instanceof JavaResource)
        {
            final JavaResource javaResource = (JavaResource) resource;
            final JavaSource<?> javaSource = javaResource.getJavaSource();
            if (!(javaSource.isClass() || javaSource.isInterface()))
            {
                throw new IllegalStateException("Constraint can only be added on interface method or class property");
            }

            return (JavaClass) javaResource.getJavaSource();
        }
        throw new RuntimeException("The current resource is not a Java Resource");
    }

    private Method<JavaClass> getPropertyAccessor(JavaClass clazz, String property)
    {
        final StringBuilder methodName = new StringBuilder();
        methodName.append("get");
        methodName.append(Character.toUpperCase(property.charAt(0)));
        if (property.length() > 1)
        {
            methodName.append(property.substring(1, property.length()));
        }
        return clazz.getMethod(methodName.toString());
    }

    private void addConstraintMessageTo(Annotation<JavaClass> constraintAnnotation, String message)
    {
        if (message != null)
        {
            constraintAnnotation.setStringValue("message", message);
        }
    }
}
