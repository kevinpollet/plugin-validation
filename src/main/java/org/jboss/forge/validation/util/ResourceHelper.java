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
package org.jboss.forge.validation.util;

import java.io.FileNotFoundException;

import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.java.JavaMemberResource;
import org.jboss.forge.resources.java.JavaResource;

/**
 * @author Kevin Pollet
 */
public class ResourceHelper
{
    //disable instantiation
    private ResourceHelper()
    {

    }

    public static Annotation<JavaClass> addAnnotationTo(Resource<?> resource, Class<? extends java.lang.annotation.Annotation> annotationClass) throws FileNotFoundException
    {
        if (resource instanceof JavaResource)
        {
            final JavaClass clazz = getJavaClassFromResource(resource);
            return clazz.addAnnotation(annotationClass);
        }
        else if (resource instanceof JavaMemberResource)
        {
            final JavaMemberResource javaMemberResource = (JavaMemberResource) resource;
            return javaMemberResource.getUnderlyingResourceObject().addAnnotation(annotationClass);
        }
        return null;
    }

    public static JavaClass getJavaClassFromResource(Resource<?> resource) throws FileNotFoundException
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
}
